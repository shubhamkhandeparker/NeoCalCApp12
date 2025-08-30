package com.shubham.neocal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TTSToggleButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        //Main toggle button

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isEnabled) Color(0xFF10B981) else Color(0xFFE5E7EB)
                )
                .clickable{onClick()}
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isEnabled) "\uD83D\uDD0A" else "\uD83D\uDD07",
                fontSize = 16.sp,
                color = if (isEnabled) Color.White else Color(0xFF9CA3AF)
            )
        }

        if(isEnabled){
            Box(
                modifier=Modifier
                    .size(12.dp)
                    .offset(x=28.dp,y=(-2).dp)
                    .background(Color(0xFF10B981),CircleShape)
                    .border(2.dp,Color.White,CircleShape)
            )
        }

    }
}