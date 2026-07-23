package com.expensetracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Indigo80,
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondary = Sky40,
    onSecondary = Color.White,
    secondaryContainer = Sky80,
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = Amber40,
    onTertiary = Color.White,
    tertiaryContainer = Amber80,
    onTertiaryContainer = Color(0xFF78350F),
    error = Rose40,
    onError = Color.White,
    errorContainer = Rose80,
    onErrorContainer = Color(0xFF4C0519),
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant80
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Indigo80,
    secondary = Sky80,
    onSecondary = Color(0xFF0C4A6E),
    secondaryContainer = Color(0xFF0369A1),
    onSecondaryContainer = Sky80,
    tertiary = Amber80,
    onTertiary = Color(0xFF78350F),
    tertiaryContainer = Color(0xFFB45309),
    onTertiaryContainer = Amber80,
    error = Rose80,
    onError = Color(0xFF4C0519),
    errorContainer = Color(0xFFBE123C),
    onErrorContainer = Rose80,
    background = Color(0xFF121212),
    onBackground = Neutral90,
    surface = Color(0xFF121212),
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant90,
    outline = NeutralVariant80
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
