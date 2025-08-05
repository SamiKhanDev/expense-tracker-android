package com.codewithfk.expensetracker.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpenseTrackerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize locale from saved preferences
        val prefs = getSharedPreferences("LocaleManager", MODE_PRIVATE)
        val languageCode = prefs.getString("language_code", "en") ?: "en"
        val localeList = androidx.core.os.LocaleListCompat.forLanguageTags(languageCode)
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(localeList)
    }
}