package com.example.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी")
}

class LanguageState {
    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun isHindi(): Boolean = currentLanguage == AppLanguage.HINDI

    fun getText(en: String, hi: String): String {
        return if (isHindi() && hi.isNotBlank()) hi else en
    }
}

val LocalAppLanguage = compositionLocalOf { LanguageState() }

@Composable
fun tr(en: String, hi: String): String {
    return LocalAppLanguage.current.getText(en, hi)
}
