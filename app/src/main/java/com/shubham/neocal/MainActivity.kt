package com.shubham.neocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shubham.neocal.presentation.screens.CalculatorScreen
import com.shubham.neocal.ui.theme.NeoCalTheme
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubham.neocal.ui.theme.NeoCalcTheme
import com.shubham.neocal.viewmodel.CalculatorViewModel


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Microphone permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "Microphone permission denied. Voice input  won't work",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkMicrophonePermission()

        enableEdgeToEdge()
        setContent {
            //creating viewModel instance
            val calculatorViewModel: CalculatorViewModel = viewModel()

            //using animated chameleon theme
            NeoCalcTheme(viewModel = calculatorViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CalculatorScreen(viewModel = calculatorViewModel)
                }
            }

        }

    }

    private fun checkMicrophonePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                //Permission already granted
                android.util.Log.d("NeoCalc", "Microphone permission already granted")
            }

            else -> {
                //Request permission
                android.util.Log.d("NeoCalc", "Requesting microphone permission")
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

            }
        }
    }

}

