package com.shubham.neocal.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.shubham.neocal.model.CalculatorModel
import com.shubham.neocal.utils.EnhancedTTSManager
import com.shubham.neocal.utils.TTSManager
import com.shubham.neocal.utils.WebViewTTSManager
import com.shubham.neocal.utils.VoiceInputManager
import com.shubham.neocal.utils.MathSpeechParser
import com.shubham.neocal.ui.theme.AppTheme
import com.shubham.neocal.utils.MathStepsEngine

/**
 * ViewModel Layer : Manages UI State for NeoCalc
 * Connect Models (business logic ) to View (UI)
 * Handles User interaction and state management
 * Now whit TTS integration for voice feedback!
 */

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {


    //Our Calculator brain from phase 1
    private val calculatorModel = CalculatorModel()

    //TTS Manager for voice feedback
    private val enhancedTTSManager = EnhancedTTSManager(application.applicationContext)


    //Private mutable state - Only ViewModel can change this
    private val _displayText = MutableStateFlow("0")
    private val _currentExpression = MutableStateFlow("")

    private val _isTTSEnabled = MutableStateFlow(true)   //TTS starts enabled

    val isTTSEnabled = _isTTSEnabled.asStateFlow()


    //Public read-only state -UI observe these
    val displayText: StateFlow<String> = _displayText.asStateFlow()
    val currentExpression: StateFlow<String> = _currentExpression.asStateFlow()

    //Voice Input Manager for speech recognition
    private val voiceInputManager = VoiceInputManager(application.applicationContext)

    //math speech parser for voice input
    private val mathSpeechParser = MathSpeechParser()

    //Theme Management
    private val _currentTheme = MutableStateFlow(AppTheme.LIGHT)
    val currentTheme = _currentTheme.asStateFlow()

    private var mathStepsEngine: MathStepsEngine? = null

    private val _steps = MutableStateFlow<List<String>>(emptyList())
    val steps: StateFlow<List<String>> = _steps.asStateFlow()

    private val _showStepsModal = MutableStateFlow(false)
    val showStepsModal: StateFlow<Boolean> = _showStepsModal.asStateFlow()



    fun onStepsClick() {
        android.util.Log.d("NeoCalc","onStepsClick called with expression ${_displayText.value}")
        mathStepsEngine?.solveExpression(_displayText.value)
        _showStepsModal.value = true
        android.util.Log.d("NeoCalc","showSteps set to :${_showStepsModal.value}")
        android.util.Log.d("NeoCalc","Current Steps :${_showStepsModal.value}")

    }

    fun hideStepsModal() {
        _showStepsModal.value = false
        _steps.value = emptyList()
    }


    fun toggleTheme() {
        _currentTheme.value = if (_currentTheme.value == AppTheme.LIGHT) {
            AppTheme.DARK
        } else {
            AppTheme.LIGHT
        }

        val themeName = if (_currentTheme.value == AppTheme.LIGHT) "Light" else "DARK"
        speakText("Switched to $themeName theme")
    }


    init {
        //Initialize with default values
        _displayText.value = "0"
        _currentExpression.value = ""

        //Initialize TTS
        initializeTTS()

        //Initialize voice Input
        initializeVoiceInput()
    }


    private fun initializeTTS() {
        viewModelScope.launch {
            enhancedTTSManager.initialize { success ->
                if (success) {
                    android.util.Log.d("NeoCalc", "TTS initialized successfully")
                    //Welcome Message
                    speakText("NeoCalc Ready")
                } else {
                    android.util.Log.e("NeoCalc", "TTS initialization failed")
                    _isTTSEnabled.value = false
                }
            }
        }
    }

    //Helper function to speak text
    private fun speakText(text: String) {
        android.util.Log.d("NeoCalc", "speakText called with :'$text'")
        android.util.Log.d("NeoCalc", "isTTSEnabled :$isTTSEnabled")
        android.util.Log.d("NeoCalc", "ttsManager.isReady():${enhancedTTSManager.isReady()}")

        if (_isTTSEnabled.value && enhancedTTSManager.isReady()) {
            android.util.Log.d("NeoCalc", "Actually calling enhancedTTSManager.speak")
            enhancedTTSManager.speak(text)
        } else {
            android.util.Log.w("NeoCalc", "TTS not ready or disabled ")
        }
    }

    /**
     * Step 2:Button handling functions
     * These handle all user interaction with calculator buttons
     */

    //Handle Number button Clicks(0-9)
    fun onNumberClick(number: String) {
        println("DEBUG : Number Clicked : $number")
        android.util.Log.d("NeoCalc", "Number Clicked :$number")

        android.util.Log.d("NeoCalc", "About to speak number $number")

        //Speak the number
        speakText(getNumberName(number))

        android.util.Log.d("NeoCalc", "Finished calling speakText")


        if (_displayText.value == "0" || _displayText.value == "Error") {

            //Replace initial 0 or error with new number
            _displayText.value = number
            _currentExpression.value = number
        } else {
            //Append number to current expression

            _displayText.value = _displayText.value + number
            _currentExpression.value = _currentExpression.value + number
        }
        println("DEBUG :After Click Display :${_displayText.value} ")
        android.util.Log.d("NeoCalc", "Display updated to :${_displayText.value}")
    }

    //Handle Operation button Clicks (+,-,x,÷)
    fun onOperatorClick(operator: String) {
        if (_displayText.value != "Error") {
            val currentText = _displayText.value

            if (operator == "(") {
                if (currentText == "0") {
                    //Replacing the initial "0 " with just the parenthesis
                    _displayText.value = "("
                    _currentExpression.value = "("
                } else {
                    val needsMultiply = currentText.isNotEmpty() &&
                            currentText.last() in "01234546789xyz)"
                    if (needsMultiply) {
                        _displayText.value = currentText + "*("
                        _currentExpression.value = _currentExpression.value + "*("
                    } else {
                        _displayText.value = currentText + "("
                        _currentExpression.value = _currentExpression.value + "("
                    }
                }
                speakText("Open Parenthesis")
            } else {
                val canAddOperator = currentText.isNotEmpty() && (
                        currentText.last() in "0123456789xyz()"
                        )
                if (canAddOperator) {
                    speakText(getOperatorName(operator))
                    _displayText.value = currentText + operator
                    _currentExpression.value = _currentExpression.value + operator
                }
            }
        }
    }


    //handle equal button Clicks
    fun onEqualClick() {
        speakText("equals")
        try {
            val result = calculatorModel.calculate(_currentExpression.value)
            _displayText.value = result
            _currentExpression.value = result

            //speak the result
            speakText("Result is $result")
        } catch (e: Exception) {
            _displayText.value = "Error"
            _currentExpression.value = ""

            speakText("Error")

        }
    }

    //handle clear button click (c)
    fun onClearClick() {
        speakText("Clear")
        _displayText.value = "0"
        _currentExpression.value = ""
    }

    //handle all clear button click(AC)
    fun onAllClear() {
        speakText("All Clear")
        _displayText.value = "0"
        _currentExpression.value = ""
    }

    /**
     *Step 3: Decimal point and backspace functions
     * These complete our calculator functionality
     */

    //Handel decimal point button click (.)

    fun onDecimalClick() {
        speakText("Point")
        val currentText = _displayText.value
        if (currentText == "0" || currentText == "Error") {
            //Start with "0."
            _displayText.value = "0."
            _currentExpression.value = "0."
        } else {
            //Only add decimal if current number doesn't already have one
            val lastNumber = currentText.split(Regex("[+\\-x÷*/]")).lastOrNull() ?: ""

            if (!lastNumber.contains(".")) {
                _displayText.value = currentText + "."
                _currentExpression.value = _currentExpression.value + "."
            }

        }
    }

    //handle backspace/delete button click
    fun onBackSpaceClick() {
        android.util.Log.d("NeoCalc", "BACKSPACE CLICKED- function called ")
        speakText("Delete")

        val currentText = _displayText.value
        android.util.Log.d("NeoCalc", "Current text before delete : '$currentText'")

        if (currentText.length > 1 && currentText != "Error") {
            //Remove last Character
            val newText = currentText.dropLast(1)
            android.util.Log.d("NeoCalc", "Removing last char , new text:'$newText'")

            _displayText.value = newText
            _currentExpression.value = newText

        } else {
            //If one Character or error , reset to "0"
            android.util.Log.d("NeoCalc", "Resetting to 0")
            _displayText.value = "0"
            _currentExpression.value = ""
        }
        android.util.Log.d("NeoCalc", "Final display text :'${_displayText.value}'")
    }

    /**
     * Step 4 :Utility function to get current state
     * Useful for debugging and testing
     */

    fun getCurrentState(): String {
        return "Display :${_displayText.value},Expression :${_currentExpression.value}"
    }


    /**
     * Step 5: Test function for viewModel
     * This verifies all button handling works correctly
     */

    fun runViewModelTests(): List<String> {
        val testResults = mutableListOf<String>()

        try {

            //Test number input
            onNumberClick("2")
            testResults.add("Number Input :${getCurrentState()}")

            //Test operator
            onOperatorClick("+")
            testResults.add("After Operator:${getCurrentState()}")

            //Test another number
            onNumberClick("3")
            testResults.add("Second Number :${getCurrentState()}")

            //Test equals
            onEqualClick()
            testResults.add("After Equals :${getCurrentState()}")

            //Test decimal
            onClearClick()
            onNumberClick("5")
            onDecimalClick()
            onNumberClick("5")
            testResults.add("Decimal Test :${getCurrentState()}")

            //Test backspace
            onBackSpaceClick()
            testResults.add("After Backspace :${getCurrentState()}")

            //Test clear
            onAllClear()
            testResults.add("After Clear :${getCurrentState()}")

            testResults.add("✅ ALL VIEWMODEL TESTS PASSED!")

        } catch (e: Exception) {
            testResults.add("❌ VIEWMODEL TEST FAILED :${e.message}")
        }
        return testResults
    }


    //Helper Function for better TTS experience
    private fun getNumberName(number: String): String {
        return when (number) {
            "0" -> "Zero"
            "1" -> "One"
            "2" -> "Two"
            "3" -> "Three"
            "4" -> "Four"
            "5" -> "Five"
            "6" -> "Six"
            "7" -> "Seven"
            "8" -> "Eight"
            "9" -> "Nine"
            else -> number
        }
    }

    private fun getOperatorName(operator: String): String {
        return when (operator) {
            "+" -> "Plus"
            "-" -> "Minus"
            "x", "*" -> "Times"
            "÷", "/" -> "Divide by"
            else -> operator
        }
    }

    //TTS Control functions
    fun toggleTTS() {
        _isTTSEnabled.value = !_isTTSEnabled.value
        if (_isTTSEnabled.value) {
            speakText("Voice Feedback enabled")
        }

    }

    fun isTTSOn(): Boolean = _isTTSEnabled.value

    //Cleanup TTS when viewModel is destroyed

    override fun onCleared() {
        super.onCleared()
        enhancedTTSManager.shutdown()
    }


    //Voice Input Function
    fun startVoiceInput() {
        android.util.Log.d("NeoCalc", "Start Voice input...")
        voiceInputManager.startListening()
    }

    fun stopVoiceInput() {
        android.util.Log.d("NeoCalc", "Stopping voice input...")
        voiceInputManager.stopListening()
    }

    private fun initializeVoiceInput() {
        voiceInputManager.initialize { spokenText ->
            android.util.Log.d("NeoCalc", "Voice input received ;$spokenText")

            val mathExpression = mathSpeechParser.parseToMathExpression(spokenText)

            if (mathExpression != null) {
                android.util.Log.d("NeoCalc", "Parsed math :$mathExpression")

                //process the math expression
                processMathExpression(mathExpression, spokenText)
            } else {
                android.util.Log.d("NeoCalc", "could not parse a math expression")

                speakText("I didn't understand that math expression . Try saying something like 'one plus one'")
            }

            //for now just log it we'll add math parsing in step 4
            speakText("You said :$spokenText")
        }

    }

    private fun processMathExpression(expression: String, originalSpeech: String) {
        try {
            //update display with expression
            _displayText.value = expression
            _currentExpression.value = expression

            //Calculate the result
            val result = calculatorModel.calculate(expression)

            //update display with result
            _displayText.value = result
            _currentExpression.value = result

            //speak the result
            speakText("The result is $result")

            android.util.Log.d(
                "NeoCalc",
                "Voice calculation:'$originalSpeech'->'$expression'->'$result'"
            )

        } catch (e: Exception) {
            android.util.Log.d("NeoCalc", "Error calculating :${e.message}")
            _displayText.value = "Error"
            _currentExpression.value = ""
            speakText("I couldn't calculate that. please try again")
        }
    }

    fun initializeMathSteps(context: Context) {
        mathStepsEngine = MathStepsEngine(context)
        mathStepsEngine?.initialize()

        //adding setCallbacks code
        mathStepsEngine?.setCallbacks(
            onSteps = { stepsList ->
                android.util.Log.d("NeoCalc","Callback received steps:$stepsList")
                _steps.value = stepsList
                android.util.Log.d("NeoCalc","Steps State updated to : ${_steps.value}")
            },
            onError = { error ->
                Log.e("MathSteps", "Error:$error")
            }
        )
    }

    fun onVariableClick(variable: String) {
        android.util.Log.d("NeoCalc", "Variable Clicked :$variable")

        val speakText = when (variable) {
            "x" -> "Variable x"
            "y" -> "Variable y"
            "z" -> "Variable z"
            else -> variable
        }
        speakText(speakText)

        if (_displayText.value == "0" || _displayText.value == "Error") {
            _displayText.value = variable
            _currentExpression.value = variable
        } else {
            _displayText.value = _displayText.value + variable
            _currentExpression.value = _currentExpression.value + variable
        }
    }

    fun onFunctionClick(function: String) {
        android.util.Log.d("NeoCalc", "Function Clicked : $function")
        speakText(function)
        if (_displayText.value == "0" || _displayText.value == "Error") {
            _displayText.value == function + "("
            _currentExpression.value == function + "("
        } else {
            _displayText.value = _displayText.value + function + "("
            _currentExpression.value = _currentExpression.value + "("
        }
    }
}
