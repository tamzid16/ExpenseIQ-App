package com.expenseiq.app.security

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Password hashing kept local to the device.
 *
 * New passwords use PBKDF2-HMAC-SHA256 with a unique random salt.
 * Legacy SHA-256 hashes can still be verified so existing local accounts
 * can be upgraded automatically after a successful sign-in.
 */
object PasswordHasher {
    private const val PREFIX = "v1"
    private const val SALT_BYTES = 16
    private const val KEY_BITS = 256
    private const val ITERATIONS = 120_000

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_BITS)
        val derived = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded
        return "$PREFIX$DELIMITER${encode(salt)}$DELIMITER${encode(derived)}"
    }

    fun verify(password: String, storedHash: String): Boolean {
        if (!storedHash.startsWith("$PREFIX$DELIMITER")) {
            return verifyLegacy(password, storedHash)
        }

        val parts = storedHash.split(DELIMITER)
        if (parts.size != 3) return false

        return runCatching {
            val salt = decode(parts[1])
            val expected = decode(parts[2])
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, expected.size * 8)
            val actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
            MessageDigest.isEqual(expected, actual)
        }.getOrDefault(false)
    }

    fun isModern(storedHash: String): Boolean =
        storedHash.startsWith("$PREFIX$DELIMITER")

    private fun verifyLegacy(password: String, storedHash: String): Boolean {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("ExpenseIQ_Salt_${password.trim()}".toByteArray(Charsets.UTF_8))
        val legacy = bytes.joinToString("") { "%02x".format(it) }
        return MessageDigest.isEqual(
            legacy.toByteArray(Charsets.UTF_8),
            storedHash.toByteArray(Charsets.UTF_8)
        )
    }

    private fun encode(value: ByteArray): String =
        Base64.encodeToString(value, Base64.NO_WRAP or Base64.NO_PADDING)

    private fun decode(value: String): ByteArray =
        Base64.decode(value, Base64.DEFAULT)

    private const val DELIMITER = "$"
}
