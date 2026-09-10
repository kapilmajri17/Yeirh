package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GroceryGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GroceryGreenContainer,
    onPrimaryContainer = GroceryOnGreenContainer,
    secondary = GroceryGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GroceryGoldContainer,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = GroceryGreenLight,
    onTertiary = Color.White,
    background = GroceryBackground,
    onBackground = GroceryTextPrimary,
    surface = GrocerySurface,
    onSurface = GroceryTextPrimary,
    surfaceVariant = GrocerySurfaceVariant,
    onSurfaceVariant = GroceryTextSecondary,
    outline = GroceryOutline,
    error = GroceryRedDiscount,
    errorContainer = GroceryRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF052E16),
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = Color(0xFFBBF7D0),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF451A03),
    background = Color(0xFF111827),
    onBackground = Color(0xFFF9FAFB),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF4B5563),
    error = Color(0xFFF87171)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
