package com.expenseiq.app.ui.util

object Localization {

    fun get(key: String, lang: String): String {
        val isBn = lang.equals("bn", ignoreCase = true)
        return when (key) {
            // General & Nav
            "app_name" -> if (isBn) "এক্সপেন্সআইকিউ" else "ExpenseIQ"
            "nav_home" -> if (isBn) "হোম" else "Home"
            "nav_expenses" -> if (isBn) "খরচ" else "Expenses"
            "nav_analytics" -> if (isBn) "অ্যানালিটিক্স" else "Analytics"
            "nav_budget" -> if (isBn) "বাজেট" else "Budget"

            // Auth
            "sign_in" -> if (isBn) "সাইন ইন" else "Sign In"
            "register" -> if (isBn) "নিবন্ধন" else "Register"
            "sign_in_subtitle" -> if (isBn) "আপনার অ্যাকাউন্টে লগইন করুন" else "Sign in to your private account"
            "register_subtitle" -> if (isBn) "নতুন অ্যাকাউন্ট তৈরি করুন" else "Create your personal tracker"
            "full_name" -> if (isBn) "পূর্ণ নাম" else "Full Name"
            "email_address" -> if (isBn) "ইমেইল ঠিকানা" else "Email Address"
            "password" -> if (isBn) "পাসওয়ার্ড" else "Password"
            "create_account" -> if (isBn) "অ্যাকাউন্ট তৈরি করুন" else "Create Account"
            "saved_accounts" -> if (isBn) "সংরক্ষিত অ্যাকাউন্টসমূহ" else "Saved Accounts on Device"
            "tap_to_select_account" -> if (isBn) "লগইন করতে অ্যাকাউন্ট ট্যাপ করুন" else "Tap an account to sign in"
            "already_registered_switch" -> if (isBn) "এই ইমেইলটি নিবন্ধিত। সাইন ইন করতে এখানে চাপুন।" else "This email is already registered. Tap here to Sign In."
            "back_to_login" -> if (isBn) "লগইনে ফিরে যান" else "Back to Login"
            "switch_to_bangla" -> "বাংলা"
            "switch_to_english" -> "English"

            // Initial Setup
            "setup_title" -> if (isBn) "প্রাথমিক আর্থিক সেটআপ" else "Initial Financial Setup"
            "setup_subtitle" -> if (isBn) "সঠিক বাজেট ও খরচের হিসাব রাখতে আপনার তথ্য দিন" else "Set up your monthly baseline to enable smart tracking and budget limits."
            "monthly_income" -> if (isBn) "মাসিক আয় / ভাতা" else "Monthly Allowance / Income"
            "house_rent" -> if (isBn) "বাড়ি ভাড়া" else "Rent"
            "utility_bills" -> if (isBn) "মাসিক ইউটিলিটি বিল (বিদ্যুৎ, গ্যাস, ইন্টারনেট)" else "Monthly Bills (Electricity, Gas, Internet)"
            "other_fixed" -> if (isBn) "অন্যান্য নির্দিষ্ট মাসিক খরচ" else "Other Fixed Monthly Expenses"
            "monthly_budget_target" -> if (isBn) "মাসিক ব্যয়ের বাজেট সীমা" else "Total Monthly Spending Budget"
            "fixed_total" -> if (isBn) "মোট নির্দিষ্ট খরচ" else "Total Fixed Obligations"
            "setup_complete_button" -> if (isBn) "সেটআপ সম্পন্ন করে শুরু করুন" else "Save & Get Started"

            // Dashboard
            "hello" -> if (isBn) "স্বাগতম" else "Hello"
            "monthly_spending" -> if (isBn) "মাসিক খরচ" else "MONTHLY SPENDING"
            "budget" -> if (isBn) "বাজেট" else "Budget"
            "budget_used" -> if (isBn) "ব্যবহৃত বাজেট" else "Budget Used"
            "spent" -> if (isBn) "খরচ" else "Spent"
            "remaining" -> if (isBn) "অবশিষ্ট" else "Remaining"
            "today_spending" -> if (isBn) "আজকের খরচ" else "Today"
            "add_button" -> if (isBn) "যোগ করুন" else "Add"
            "recent_expenses" -> if (isBn) "সাম্প্রতিক খরচ" else "Recent Expenses"
            "view_all" -> if (isBn) "সব দেখুন" else "View All"
            "no_expenses_yet" -> if (isBn) "এখনো কোনো খরচ যোগ করা হয়নি" else "No Expenses Yet"
            "add_first_expense" -> if (isBn) "প্রথম খরচ যোগ করতে '+' চাপুন।" else "Tap 'Add' to track your first expense."

            // Expense History
            "expense_history" -> if (isBn) "খরচের ইতিহাস" else "Expense History"
            "search_placeholder" -> if (isBn) "শিরোনাম বা নোট খুঁজুন..." else "Search by title, notes, or category..."
            "all_filter" -> if (isBn) "সব" else "All"
            "delete_confirm_title" -> if (isBn) "খরচ মুছে ফেলতে চান?" else "Delete Expense?"
            "delete_confirm_msg" -> if (isBn) "আপনি কি নিশ্চিত যে এই খরচটি মুছে ফেলতে চান?" else "Are you sure you want to delete this expense?"
            "delete" -> if (isBn) "মুছুন" else "Delete"
            "cancel" -> if (isBn) "বাতিল" else "Cancel"

            // Analytics
            "spending_analytics" -> if (isBn) "ব্যয়ের পরিসংখ্যান" else "Spending Analytics"
            "analytics_subtitle" -> if (isBn) "মাসিক প্রবণতা ও তুলনামূলক চিত্র" else "Monthly trends & spending distribution"
            "total_spending" -> if (isBn) "সর্বমোট ব্যয়" else "Total Spending"
            "avg_monthly" -> if (isBn) "মাসিক গড়" else "Avg Monthly"
            "top_category" -> if (isBn) "শীর্ষ বিভাগ" else "Top Category"
            "highest_month" -> if (isBn) "সর্বোচ্চ ব্যয়ের মাস" else "Highest Month"
            "monthly_trend" -> if (isBn) "মাসিক ব্যয়ের প্রবণতা" else "MONTHLY SPENDING TREND"
            "category_breakdown" -> if (isBn) "বিভাগভিত্তিক ব্যয়ের ভাগ" else "CATEGORY BREAKDOWN"

            // Budget
            "budget_and_recurring" -> if (isBn) "বাজেট ও পুনরাবৃত্ত খরচ" else "Budget & Recurring"
            "budget_status" -> if (isBn) "মাসিক বাজেটের অবস্থা" else "MONTHLY BUDGET STATUS"
            "set_budget" -> if (isBn) "বাজেট নির্ধারণ" else "Set Budget"
            "recurring_expenses" -> if (isBn) "পুনরাবৃত্ত খরচ" else "Recurring Expenses"
            "add_recurring" -> if (isBn) "যোগ করুন" else "Add"

            // Settings
            "settings" -> if (isBn) "সেটিংস" else "Settings"
            "app_language" -> if (isBn) "ভাষা (Language)" else "App Language"
            "app_theme" -> if (isBn) "থিম" else "App Theme"
            "currency_symbol" -> if (isBn) "মুদ্রা প্রতীক" else "Currency Symbol"
            "sign_out" -> if (isBn) "লগআউট" else "Sign Out"

            // Month names
            "month_1" -> if (isBn) "জানুয়ারি" else "January"
            "month_2" -> if (isBn) "ফেব্রুয়ারি" else "February"
            "month_3" -> if (isBn) "মার্চ" else "March"
            "month_4" -> if (isBn) "এপ্রিল" else "April"
            "month_5" -> if (isBn) "মে" else "May"
            "month_6" -> if (isBn) "জুন" else "June"
            "month_7" -> if (isBn) "জুলাই" else "July"
            "month_8" -> if (isBn) "আগস্ট" else "August"
            "month_9" -> if (isBn) "সেপ্টেম্বর" else "September"
            "month_10" -> if (isBn) "অক্টোবর" else "October"
            "month_11" -> if (isBn) "নভেম্বর" else "November"
            "month_12" -> if (isBn) "ডিসেম্বর" else "December"

            else -> key
        }
    }

    fun getMonthName(month: Int, lang: String): String {
        return get("month_$month", lang)
    }
}
