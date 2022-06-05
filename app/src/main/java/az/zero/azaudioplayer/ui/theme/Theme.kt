package az.zero.azaudioplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = SurfaceBlack,
    primaryVariant = Purple700,
    secondary = Teal200,
    onPrimary = Color.White,
    background = Color.DarkGray
)

private val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = Purple700,
    secondary = Teal200,
    onPrimary = Color.Black,
    background = Color.LightGray


    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private object AZAudioPlayerTheme : RippleTheme {
    // Here you should return the ripple color you want
    // and not use the defaultRippleColor extension on RippleTheme.
    // Using that will override the ripple color set in DarkMode
    // or when you set light parameter to false
    @Composable
    override fun defaultColor(): Color = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.DarkGray
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        Color.Black,
        lightTheme = !isSystemInDarkTheme()
    )
}

@Composable
fun AZAudioPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
    ) {
        CompositionLocalProvider(
            LocalRippleTheme provides AZAudioPlayerTheme,
            content = content
        )
    }
}