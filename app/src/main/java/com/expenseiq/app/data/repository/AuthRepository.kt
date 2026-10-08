package com.expenseiq.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.expenseiq.app.security.PasswordHasher
import com.expenseiq.app.security.SecureSessionStore
import com.expenseiq.app.data.local.UserDao
import com.expenseiq.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("expenseiq_auth_prefs", Context.MODE_PRIVATE)
    private val secureSessionStore = SecureSessionStore(context)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentLanguage = MutableStateFlow(prefs.getString(KEY_APP_LANGUAGE, "en") ?: "en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    companion object {
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
        private const val KEY_APP_LANGUAGE = "app_language"
        private const val KEY_SAVED_EMAIL = "saved_login_email"
    }

    fun getSavedEmail(): String {
        return secureSessionStore.get(KEY_SAVED_EMAIL) ?: ""
    }

    suspend fun initialize() {
        migrateLegacySession()
        val savedUserId = secureSessionStore.get(KEY_ACTIVE_USER_ID)?.toLongOrNull() ?: -1L
        if (savedUserId != -1L) {
            val user = userDao.getUserById(savedUserId)
            if (user != null) {
                _currentUser.value = user
                _currentLanguage.value = user.language
                return
            }
        }
        // If not logged in, user remains null so Login page is shown on launch
        _currentUser.value = null
    }

    private fun migrateLegacySession() {
        val legacyUserId = prefs.getLong(KEY_ACTIVE_USER_ID, -1L)
        if (legacyUserId != -1L && secureSessionStore.get(KEY_ACTIVE_USER_ID) == null) {
            secureSessionStore.put(KEY_ACTIVE_USER_ID, legacyUserId.toString())
        }

        val legacyEmail = prefs.getString(KEY_SAVED_EMAIL, null)
        if (!legacyEmail.isNullOrBlank() && secureSessionStore.get(KEY_SAVED_EMAIL) == null) {
            secureSessionStore.put(KEY_SAVED_EMAIL, legacyEmail)
        }

        if (legacyUserId != -1L || !legacyEmail.isNullOrBlank()) {
            prefs.edit()
                .remove(KEY_ACTIVE_USER_ID)
                .remove(KEY_SAVED_EMAIL)
                .apply()
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val cleanPassword = password.trim()
        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            val msg = if (_currentLanguage.value == "bn") {
                "এই ইমেইলের একটি অ্যাকাউন্ট ইতিমধ্যে সংরক্ষিত আছে। অনুগ্রহ করে সাইন ইন করুন।"
            } else {
                "An account with this email is already registered. Please sign in."
            }
            return Result.failure(Exception(msg))
        }

        val user = User(
            name = name.trim(),
            email = trimmedEmail,
            passwordHash = PasswordHasher.hash(cleanPassword),
            currencySymbol = "৳",
            monthlyIncome = 0.0,
            hasCompletedSetup = false,
            language = _currentLanguage.value
        )
        val id = userDao.insertUser(user)
        val loggedInUser = user.copy(id = id)
        _currentUser.value = loggedInUser
        secureSessionStore.put(KEY_ACTIVE_USER_ID, id.toString())
        secureSessionStore.put(KEY_SAVED_EMAIL, trimmedEmail)
        return Result.success(loggedInUser)
    }

    suspend fun login(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val cleanPassword = password.trim()
        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(
                Exception(
                    if (_currentLanguage.value == "bn") "কোনো সংরক্ষিত অ্যাকাউন্ট পাওয়া যায়নি। অনুগ্রহ করে প্রথমে নিবন্ধন করুন।" else "No registered account found for this email. Please register first."
                )
            )

        if (!PasswordHasher.verify(cleanPassword, user.passwordHash)) {
            return Result.failure(
                Exception(
                    if (_currentLanguage.value == "bn") "ভুল পাসওয়ার্ড। আবার চেষ্টা করুন।" else "Incorrect password. Please try again."
                )
            )
        }

        // Upgrade older local password hashes after a successful login.
        val authenticatedUser = if (PasswordHasher.isModern(user.passwordHash)) {
            user
        } else {
            val upgraded = user.copy(passwordHash = PasswordHasher.hash(cleanPassword))
            userDao.updateUser(upgraded)
            upgraded
        }

        _currentUser.value = authenticatedUser
        _currentLanguage.value = user.language
        secureSessionStore.put(KEY_ACTIVE_USER_ID, user.id.toString())
        secureSessionStore.put(KEY_SAVED_EMAIL, trimmedEmail)
        prefs.edit().putString(KEY_APP_LANGUAGE, user.language).apply()
        return Result.success(authenticatedUser)
    }

    fun logout() {
        _currentUser.value = null
        secureSessionStore.remove(KEY_ACTIVE_USER_ID)
    }

    suspend fun completeInitialSetup(
        monthlyIncome: Double,
        currencySymbol: String,
        language: String
    ) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            monthlyIncome = monthlyIncome,
            currencySymbol = currencySymbol,
            hasCompletedSetup = true,
            language = language
        )
        userDao.updateUser(updated)
        _currentUser.value = updated
        _currentLanguage.value = language
        secureSessionStore.put(KEY_ACTIVE_USER_ID, updated.id.toString())
        secureSessionStore.put(KEY_SAVED_EMAIL, updated.email)
        prefs.edit().putString(KEY_APP_LANGUAGE, language).apply()
    }

    suspend fun updateCurrencySymbol(symbol: String) {
        val current = _currentUser.value ?: return
        val updated = current.copy(currencySymbol = symbol)
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun setLanguage(lang: String) {
        _currentLanguage.value = lang
        prefs.edit().putString(KEY_APP_LANGUAGE, lang).apply()
        val current = _currentUser.value
        if (current != null) {
            val updated = current.copy(language = lang)
            userDao.updateUser(updated)
            _currentUser.value = updated
        }
    }

}