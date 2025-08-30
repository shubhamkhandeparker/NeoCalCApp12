package com.shubham.neocal.presentation.components

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Calculator Button Component
 * Creates the beautiful rounded buttons with perfect colors
 * Matches the reference design exactly
 */

@Composable
fun CalculatorButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    onPressRelease: (() -> Unit)? = null
) {

    Box(
        modifier = modifier
            .size(70.dp)    //perfect button size
            .clip(RoundedCornerShape(16.dp)) //Rounded Corner
            .background(backgroundColor)
            .pointerInput(Unit){
                detectTapGestures (
                    onPress = {
                        onLongPress?.invoke()       //call long press if provided
                        tryAwaitRelease()           //wait for release
                        onPressRelease?.invoke()    //call release if provided
                    },
                    onTap = {onClick()}
                )
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = NeoCalcColors.ButtonText, //White Text
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium

        )
    }

}