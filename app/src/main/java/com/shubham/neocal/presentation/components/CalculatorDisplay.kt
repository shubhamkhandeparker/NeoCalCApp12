package com.shubham.neocal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow


/**
 * Calculator Display Component
 * show the current calculation and result
 * Matches the dark navy display from reference image
 */

@Composable
fun CalculatorDisplay (
    displayText : String,
    modifier : Modifier= Modifier
){
    Box(
        modifier=modifier.fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(NeoCalcColors.DisplayBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ){
        Text(
            text=displayText,
            color= NeoCalcColors.DisplayText,
            fontSize = if(displayText.length>10)24.sp else 32.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            softWrap = false
        )
    }
}