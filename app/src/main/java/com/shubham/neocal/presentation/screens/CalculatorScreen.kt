package com.shubham.neocal.presentation.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubham.neocal.presentation.components.*
import com.shubham.neocal.viewmodel.CalculatorViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.shubham.neocal.ui.theme.AppTheme
import com.shubham.neocal.ui.theme.LocalNeoCalcColors
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


/**
 * Main Calculator screen
 * Creates the beautiful Neocalc interface with :
 * -Purple gradient background
 * -white rounded card
 * -Title and subtitle
 * -Display and button grid
 */


@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {


    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 2 })
    var showSettings by remember { mutableStateOf(false) }
    val displayText by viewModel.displayText.collectAsState()
    val colors = LocalNeoCalcColors.current

    LaunchedEffect(Unit) {
        viewModel.initializeMathSteps(context)
    }

    //show setting or  calculator based on state
    if (showSettings) {
        SettingsScreen(
            onBackClick = { showSettings = false },
            viewModel = viewModel
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.backgroundGradientStart,
                            colors.backgroundGradientStart
                        )
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colors.calculatorBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {

                        Spacer(modifier = Modifier.width(40.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {

                            //Title:NeoCalc

                            Text(
                                text = "NeoCalc",
                                color = colors.titleText,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center

                            )

                            //Subtitle : ✨ Kawaii computing Experience ✨

                            Text(
                                text = if (pagerState.currentPage == 0) "Basic Mode" else "Algebraic Mode",
                                color = colors.subtitleText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }

                        //Right:Control Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            //The toggle button
                            val currentTheme by viewModel.currentTheme.collectAsState()
                            Button(
                                onClick = { viewModel.toggleTheme() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentTheme == AppTheme.LIGHT)
                                        Color(0xFF7C3AED) else Color(0xFF14B8A6)
                                ),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (currentTheme == AppTheme.LIGHT) "\uD83C\uDF19" else "☀\uFE0F",
                                    fontSize = 16.sp
                                )
                            }
                            //setting button
                            Button(
                                onClick = { showSettings = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF6B7280
                                    )
                                ),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {

                                Text("⚙\uFE0F", fontSize = 16.sp)
                            }

                            val isTTSEnabled by viewModel.isTTSEnabled.collectAsState()
                            TTSToggleButton(
                                isEnabled = isTTSEnabled,
                                onClick = { viewModel.toggleTTS() }
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))

                    //Calculator Display
                    CalculatorDisplay(
                        displayText = displayText
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    //Swipeable Content
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        when (page) {
                            0 -> BasicCalculatorContent(viewModel)
                            1 -> AlgebraicCalculatorContent(viewModel)
                        }
                    }


                    val showStepsModal by viewModel.showStepsModal.collectAsState()
                    val steps by viewModel.steps.collectAsState()

                    android.util.Log.d(
                        "NeoCalc",
                        "UI: showSteps=$showStepsModal,step.size=${steps.size},steps=$steps"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.onStepsClick() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show Steps ", color = colors.titleText)
                    }

                    //steps display
                    if (showStepsModal) {
                        StepsModal(
                            steps = steps,
                            onDismiss = { viewModel.hideStepsModal() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BasicCalculatorContent(viewModel: CalculatorViewModel) {
    CalculatorButtonGrid(
        onNumberClick = { number -> viewModel.onNumberClick(number) },
        onOperatorClick = { operator -> viewModel.onOperatorClick(operator) },
        onEqualsClick = { viewModel.onEqualClick() },
        onClearClick = { viewModel.onClearClick() },
        onAllClearClick = { viewModel.onAllClear() },
        onDecimalClick = { viewModel.onDecimalClick() },
        onBackSpaceClick = { viewModel.onBackSpaceClick() },
        onVoiceStart = { viewModel.startVoiceInput() },
        onVoiceStop = { viewModel.stopVoiceInput() }
    )
}


@Composable
fun AlgebraicCalculatorContent(viewModel: CalculatorViewModel) {
    val showSteps by viewModel.showStepsModal.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val colors = LocalNeoCalcColors.current

    Column {
        //Extended button grid with algebraic functions
        AlgebraicButtonGrid(
            onNumberClick = { number -> viewModel.onNumberClick(number) },
            onOperatorClick = { operator -> viewModel.onOperatorClick(operator) },
            onEqualsClick = { viewModel.onEqualClick() },
            onClearClick = { viewModel.onClearClick() },
            onAllClearClick = { viewModel.onAllClear() },
            onDecimalClick = { viewModel.onDecimalClick() },
            onBackSpaceClick = { viewModel.onBackSpaceClick() },
            onVoiceStart = { viewModel.startVoiceInput() },
            onVoiceStop = { viewModel.stopVoiceInput() },
            onVariableClick = { variable -> viewModel.onVariableClick(variable) },
            onFunctionClick = { function -> viewModel.onFunctionClick(function) }
        )


    }


}

@Composable
fun StepsModal(
    steps: List<String>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeoCalcColors.current

    //Full Screen Overlay with blur background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.calculatorBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)

            ) {
                //Header with title and with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Step-by-Step :",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.titleText
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.titleText.copy(alpha = 0.1f)
                        ),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("✕", color = colors.titleText, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                //Steps content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    steps.forEach { step ->
                        Text(
                            text = step,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = colors.titleText,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}