package com.shubham.neocal.presentation.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.collectAsState
import com.shubham.neocal.viewmodel.CalculatorViewModel
import androidx.compose.foundation.layout.statusBarsPadding
import com.shubham.neocal.ui.theme.AppTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        //Header with back button
        SettingsHeader(onBackClick = onBackClick)

        //settings content
        SettingsContent(viewModel = viewModel)
    }
}

@Composable
fun SettingsHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //back button
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF14B8A6)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(0.dp)
        ) {


            Text("←", fontSize = 20.sp, color = Color.White)
        }

        //title
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14B8A6)
        )
    }
}

@Composable
fun SettingsContent(viewModel: CalculatorViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Voice Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151)
            )

            //TTS Toggle
            TTSSettingToggle(viewModel=viewModel)

            Divider(color = Color(0xFFE5E7EB))

            Text(
                text = "Appearance",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151)
            )


            //Theme selection
            ThemeSettingSelection(viewModel=viewModel)

        }
    }
}


@Composable
fun TTSSettingToggle(viewModel: CalculatorViewModel) {
    val isTTSEnabled by viewModel.isTTSEnabled.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Voice Feedback",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151)
            )
            Text(
                text = "Hear button pressed and results",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }

        Switch(
            checked = isTTSEnabled,
            onCheckedChange = {viewModel.toggleTTS()},
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF10B981),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB)
            )

        )

    }
}

@Composable
fun ThemeSettingSelection(viewModel: CalculatorViewModel) {

  val currentTheme by viewModel.currentTheme.collectAsState()

    Row(
        modifier=Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column{
            Text(
                text="Theme",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color=Color(0xFF374151)
            )
            Text(
                text = if(currentTheme== AppTheme.LIGHT) "Light Mode" else "Dark Mode",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            //Chameleon theme toggle button
            Button(onClick = {viewModel.toggleTheme()},
                colors = ButtonDefaults.buttonColors(containerColor = if(currentTheme== AppTheme.LIGHT)
                Color(0xFF14B8A6)else Color(0xFF7C3AED)
                ),
                shape = RoundedCornerShape(12.dp)
            ){
                Text(
                    text=if(currentTheme== AppTheme.LIGHT)"\uD83C\uDF19  Dark" else "☀\uFE0F Light",
                    color=Color.White
                )
            }

        }
    }
}