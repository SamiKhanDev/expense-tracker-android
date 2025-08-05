package com.codewithfk.expensetracker.android.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var activity: android.app.Activity? = null

    fun attachActivity(activity: android.app.Activity) {
        this.activity = activity
    }
    private val prefs: SharedPreferences = context.getSharedPreferences("LocaleManager", Context.MODE_PRIVATE)
    private val _currentLocale = MutableStateFlow(getStoredLocale())
    val currentLocale: StateFlow<Locale> = _currentLocale

    private fun getStoredLocale(): Locale {
        val languageCode = prefs.getString("language_code", "en") ?: "en"
        return Locale(languageCode)
    }

    init {
        // Set initial locale
        setLocale(getStoredLocale().language)
    }
    fun setLocale(languageCode: String) {
        if (languageCode == getLocale().language) {
            return
        }
        // Save to preferences
        prefs.edit().putString("language_code", languageCode).apply()
        
        // Update locale
        val newLocale = Locale(languageCode)
        Locale.setDefault(newLocale)
        
        val config = context.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(newLocale))
        } else {
            config.locale = newLocale
        }
        
        // Update app delegate
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
        
        // Update state flow
        _currentLocale.value = newLocale
        
        // Update configuration and recreate activity
        val newContext = context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        // Recreate the activity to apply changes
        activity?.let { currentActivity ->
            currentActivity.runOnUiThread {
                currentActivity.recreate()
            }
        }
    }

    fun getLocale(): Locale {
        val configuration = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            configuration.locale
        }
    }

    fun getAvailableLanguages(): List<Language> = listOf(
        Language("en", "English"),
        Language("hi", "हिंदी"),
        Language("ur", "اردو")
    )
}

data class Language(
    val code: String,
    val displayName: String
)