package com.shubham.neocal.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Calculator Button Grid
 * Create the exact layout from reference image :
 * ROW 1 : AC  C  ÷  x
 * ROW 2 : 7   8  9  -
 * ROW 3 : 4   5  6  +
 * ROW 4 : 1   2  3 🖊️
 * ROW 5 :0(Wide) .
 * ROW 6 :=(Wide)
 */


@Composable
fun CalculatorButtonGrid(
    onNumberClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onAllClearClick: () -> Unit,
    onDecimalClick: () -> Unit,
    onBackSpaceClick: () -> Unit,
    onVoiceStart: () -> Unit,
    onVoiceStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //Row 1 :AC,A,÷,x
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "AC",
                backgroundColor = NeoCalcColors.RedButton,
                onClick = onAllClearClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "C",
                backgroundColor = NeoCalcColors.RedButton,
                onClick = onClearClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "÷",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("÷") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "x",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("x") },
                modifier = Modifier.weight(1f)
            )

        }

        //Row 2 :7,8,9,-

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "7",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("7") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "8",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("8") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "9",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("9") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "-",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("-") },
                modifier = Modifier.weight(1f)
            )
        }


        //ROW 3 :4,5,6,+
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "4",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("4") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "5",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("5") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "6",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("6") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "+",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("+") },
                modifier = Modifier.weight(1f)
            )
        }

        //Row 4: 1,2,3,🖊️ (edit Button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "1",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("1") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "2",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("2") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "3",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("3") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "⌫",
                backgroundColor = NeoCalcColors.PurpleButton,
                onClick = {
                    android.util.Log.d(
                        "NeoCalc",
                        "BUTTON GRID :onBackSpaceClick about to be called!"
                    )
                    onBackSpaceClick()
                    android.util.Log.d("NeoCalc", "BUTTON GRID :onBackSpaceClick call Completed!")
                },
                modifier = Modifier.weight(1f)
            )
        }

        //Row 5 :0 (Wide),.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "0",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("0") },
                modifier = Modifier.weight(2f)  //Double width
            )
            CalculatorButton(
                text = ".",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = onDecimalClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "\uD83C\uDFA4",
                backgroundColor = Color(0xFFF59E0B),
                onClick = {
                    android.util.Log.d("NeoCalc", "Mic tapped - use long press for voice input ")
                },
                onLongPress = {
                    android.util.Log.d(
                        "NeoCalc",
                        "Mic long pressed started - beginning voice input"
                    )
                    onVoiceStart()
                },
                onPressRelease = {
                    android.util.Log.d("NeoCalc", "Mic released-ending voice input")
                    onVoiceStop()
                },
                modifier = Modifier.weight(1f)
            )
        }

        //Row 6: =(Wide Green Button)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CalculatorButton(
                text = "=",
                backgroundColor = NeoCalcColors.GreenButton,
                onClick = onEqualsClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }

}

@Composable
fun AlgebraicButtonGrid(
    onNumberClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onAllClearClick: () -> Unit,
    onDecimalClick: () -> Unit,
    onBackSpaceClick: () -> Unit,
    onVoiceStart: () -> Unit,
    onVoiceStop: () -> Unit,
    onVariableClick: (String) -> Unit,
    onFunctionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        //Row 1: Variables row - x,y,z,(
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text="x",
                backgroundColor = NeoCalcColors.PurpleButton,
                onClick = {onVariableClick("x")},
                modifier=Modifier.weight(1f)
            )
            CalculatorButton(
                text="y",
                backgroundColor = NeoCalcColors.PurpleButton,
                onClick = {onVariableClick("y")},
                modifier=Modifier.weight(1f)
            )
            CalculatorButton(
                text="z",
                backgroundColor = NeoCalcColors.PurpleButton,
                onClick = {onVariableClick("z")},
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text="(",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = {onOperatorClick("(")},
                modifier=Modifier.weight(1f)
            )
            CalculatorButton(
                text=")",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = {onOperatorClick(")")},
                modifier=Modifier.weight(1f)
            )

        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "AC",
                backgroundColor = NeoCalcColors.RedButton,
                onClick = onAllClearClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "C",
                backgroundColor = NeoCalcColors.RedButton,
                onClick = onClearClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "÷",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("÷") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "x",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("x") },
                modifier = Modifier.weight(1f)
            )

        }

        //Row 2 :7,8,9,-

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "7",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("7") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "8",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("8") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "9",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("9") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "-",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("-") },
                modifier = Modifier.weight(1f)
            )
        }


        //ROW 3 :4,5,6,+
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "4",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("4") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "5",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("5") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "6",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("6") },
                modifier = Modifier.weight(1f)
            )

            CalculatorButton(
                text = "+",
                backgroundColor = NeoCalcColors.PinkButton,
                onClick = { onOperatorClick("+") },
                modifier = Modifier.weight(1f)
            )
        }

        //Row 4: 1,2,3,🖊️ (edit Button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "1",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("1") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "2",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("2") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "3",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("3") },
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "⌫",
                backgroundColor = NeoCalcColors.PurpleButton,
                onClick = {
                    android.util.Log.d(
                        "NeoCalc",
                        "BUTTON GRID :onBackSpaceClick about to be called!"
                    )
                    onBackSpaceClick()
                    android.util.Log.d("NeoCalc", "BUTTON GRID :onBackSpaceClick call Completed!")
                },
                modifier = Modifier.weight(1f)
            )


        }

        //Row 5 :0 (Wide),.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorButton(
                text = "0",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = { onNumberClick("0") },
                modifier = Modifier.weight(2f)  //Double width
            )
            CalculatorButton(
                text = ".",
                backgroundColor = NeoCalcColors.BlueButton,
                onClick = onDecimalClick,
                modifier = Modifier.weight(1f)
            )
            CalculatorButton(
                text = "\uD83C\uDFA4",
                backgroundColor = Color(0xFFF59E0B),
                onClick = {
                    android.util.Log.d("NeoCalc", "Mic tapped - use long press for voice input ")
                },
                onLongPress = {
                    android.util.Log.d(
                        "NeoCalc",
                        "Mic long pressed started - beginning voice input"
                    )
                    onVoiceStart()
                },
                onPressRelease = {
                    android.util.Log.d("NeoCalc", "Mic released-ending voice input")
                    onVoiceStop()
                },
                modifier = Modifier.weight(1f)
            )
        }

        //Row 6: =(Wide Green Button)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CalculatorButton(
                text = "=",
                backgroundColor = NeoCalcColors.GreenButton,
                onClick = onEqualsClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}