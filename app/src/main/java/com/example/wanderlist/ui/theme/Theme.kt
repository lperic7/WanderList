package com.example.wanderlist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF673AB7)
private val PurpleDark = Color(0xFF4527A0)
private val PurpleContainerLight = Color(0xFFEDE7F6)
private val PurpleContainerDark = Color(0xFF31264A)

private val LightColors = lightColorScheme(
    primary = Purple,
    secondary = PurpleDark,
    primaryContainer = PurpleContainerLight,
    onPrimaryContainer = Purple
)

private val DarkColors = darkColorScheme(
    primary = Purple,
    secondary = PurpleDark,
    primaryContainer = PurpleContainerDark,
    onPrimaryContainer = Color(0xFFD1C4E9)
)

object AppColors {
    val Success = Color(0xFF4CAF50)
}

@Composable
fun WanderListTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}