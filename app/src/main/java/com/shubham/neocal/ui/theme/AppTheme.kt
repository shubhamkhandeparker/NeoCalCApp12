package com.shubham.neocal.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.shubham.neocal.viewmodel.CalculatorViewModel
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize


data class NeoCalcColors(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val calculatorBackground: Color,
    val displayBackground: Color,
    val displayText: Color,
    val titleText: Color,
    val subtitleText: Color,
    val buttonText: Color,
    val redButton: Color,
    val blueButton: Color,
    val pinkButton: Color,
    val greenButton: Color,
    val purpleButton: Color,
    val backgroundGradientStart: Color,
    val backgroundGradientEnd: Color

)

enum class AppTheme {
    LIGHT, DARK
}

//light theme color
val LightNeoCalcColors = NeoCalcColors(
    primary = Color(0xFF14B8A6),
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onSurface = Color(0xFF374151),
    calculatorBackground = Color.White,
    displayBackground = Color(0xFF374151),
    displayText = Color.White,
    titleText = Color(0xFF14B8A6),
    subtitleText = Color.Gray,
    buttonText = Color.White,
    redButton = Color(0xFFEF4444),
    blueButton = Color(0xFF3B82F6),
    pinkButton = Color(0xFFEC4899),
    greenButton = Color(0xFF10B981),
    purpleButton = Color(0xFF8B5CF6),
    backgroundGradientStart = Color(0xFF8B5CF6),
    backgroundGradientEnd = Color(0xFFA855F7)
)

//Dark theme color
val DarkNeoCalcColors = NeoCalcColors(
    primary = Color(0xFF14B8A6),
    background = Color(0xFF1F2937),
    surface = Color(0xFF374151),
    onSurface = Color(0xFFF9FAFB),
    calculatorBackground = Color(0xFF111827),
    displayBackground = Color(0xFF000000),
    displayText = Color(0xFF14B8A6),
    titleText = Color(0xFF14B8A6),
    subtitleText = Color(0xFF9CA3AF),
    buttonText = Color.White,
    redButton = Color(0xFFDC2626),
    blueButton = Color(0xFF2563EB),
    pinkButton = Color(0xFFDB2777),
    greenButton = Color(0xFF059669),
    purpleButton = Color(0xFF7C3AED),
    backgroundGradientStart = Color(0xFF1F2937),
    backgroundGradientEnd = Color(0xFF111827)
)

//Composition local for accessing theme color
val LocalNeoCalcColors = staticCompositionLocalOf { LightNeoCalcColors }

@Composable
fun NeoCalcTheme(
    viewModel: CalculatorViewModel,
    content: @Composable () -> Unit
) {
    val currentTheme by viewModel.currentTheme.collectAsState()

    //CHAMELEON CROSSFADE EFFECT
    Crossfade(
        targetState = currentTheme,
        animationSpec = tween(
            durationMillis = 800,   //Almost perfect chameleon timing
            easing = FastOutSlowInEasing //Natural color flow
        ), label = "theme_crossfade"
    ) { theme ->

        val targetColors = when (theme) {
            AppTheme.LIGHT -> LightNeoCalcColors
            AppTheme.DARK -> DarkNeoCalcColors

        }
        //Animated colors with Chameleon timing
        val animatedColors = NeoCalcColors(
            primary = animateColorAsState(
                targetValue = targetColors.primary,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "primary"
            ).value,
            background = animateColorAsState(
                targetValue = targetColors.background,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "background"
            ).value,
            surface = animateColorAsState(
                targetValue = targetColors.surface,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "surface"
            ).value,
            onSurface = animateColorAsState(
                targetValue = targetColors.onSurface,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "onSurface"
            ).value,
            calculatorBackground = animateColorAsState(
                targetValue = targetColors.calculatorBackground,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "calculatorBackground"
            ).value,
            displayBackground = animateColorAsState(
                targetValue = targetColors.displayBackground,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "displayBackground"
            ).value,
            displayText = animateColorAsState(
                targetValue = targetColors.displayText,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "displayText"
            ).value,
            titleText = animateColorAsState(
                targetValue = targetColors.titleText,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "titleText"
            ).value,
            subtitleText = animateColorAsState(
                targetValue = targetColors.subtitleText,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "subtitleText"
            ).value,
            buttonText = animateColorAsState(
                targetValue = targetColors.buttonText,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "buttonText"
            ).value,
            redButton = animateColorAsState(
                targetValue = targetColors.redButton,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "red button"
            ).value,
            blueButton = animateColorAsState(
                targetValue = targetColors.blueButton,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "blue button"
            ).value,
            pinkButton = animateColorAsState(
                targetValue = targetColors.pinkButton,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "PinkButton "
            ).value,
            greenButton = animateColorAsState(
                targetValue = targetColors.greenButton,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "greenButton"
            ).value,
            purpleButton = animateColorAsState(
                targetValue = targetColors.purpleButton,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "purpleButton"
            ).value,
            backgroundGradientStart = animateColorAsState(
                targetValue = targetColors.backgroundGradientStart,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "backgroundGradientStart"
            ).value,
            backgroundGradientEnd = animateColorAsState(
                targetValue = targetColors.backgroundGradientEnd,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ), label = "backgroundGradientEnd"
            ).value
        )

        //fullscreen animated container
        Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize()
        ) {
            CompositionLocalProvider(LocalNeoCalcColors provides animatedColors) {
                content()
            }
        }
    }


}
