package com.example.proyectointegrador.ui.theme

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    primaryContainer = AzulSuave,
    onPrimaryContainer = AzulPrincipal,
    secondary = AzulPrincipal,
    onSecondary = Blanco,
    secondaryContainer = AzulSuave,
    onSecondaryContainer = AzulPrincipal,
    tertiary = AzulSuave,
    onTertiary = AzulPrincipal,
    background = Superficie,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = CamposYTarjetas,
    onSurfaceVariant = TextoSecundario,
    surfaceContainer = CamposYTarjetas,
    surfaceContainerHigh = FondoSuave,
    outline = TextoSecundario,
    outlineVariant = CamposYTarjetas
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    primaryContainer = AzulSuave,
    onPrimaryContainer = AzulPrincipal,
    secondary = AzulPrincipal,
    onSecondary = Blanco,
    secondaryContainer = AzulSuave,
    onSecondaryContainer = AzulPrincipal,
    tertiary = AzulSuave,
    onTertiary = AzulPrincipal,
    background = TextoPrincipal,
    onBackground = Superficie,
    surface = TextoPrincipal,
    onSurface = Superficie,
    surfaceVariant = TextoSecundario,
    onSurfaceVariant = CamposYTarjetas,
    surfaceContainer = TextoSecundario,
    surfaceContainerHigh = FondoSuave,
    outline = CamposYTarjetas,
    outlineVariant = TextoSecundario
)

@Composable
fun ProyectoIntegradorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivado por defecto para garantizar la paleta oficial U-TAD (#2563EB)
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= 31 -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? ComponentActivity)?.window ?: (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
