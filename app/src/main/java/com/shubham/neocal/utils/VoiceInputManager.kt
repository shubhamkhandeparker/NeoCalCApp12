package com.shubham.neocal.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class VoiceInputManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    //callback function - we'll use this to send back result back
    private var onResultCallback: ((String) -> Unit)? = null

    fun initialize(onResult: (String) -> Unit) {
        onResultCallback = onResult

        if (SpeechRecognizer.isRecognitionAvailable(context)) {

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(recognitionListener)

            Log.d("VoiceInput", "Speech recognizer initialized successfully")
        } else {
            Log.e("VoiceInput", "Speech recognition not available on this device ")
        }
    }

    fun startListening() {
        android.util.Log.d("VoiceInput", "startListening called")

        if (!isListening && speechRecognizer != null) {
            isListening = true

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your calculation...")
            }
            try {
                speechRecognizer?.startListening(intent)
                android.util.Log.d("VoiceInput", "Started Listening...")
            } catch (e: Exception) {
                android.util.Log.e("VoiceInput", "Error Starting speech recognition :${e.message}")
                isListening = false
            }
        } else {
            android.util.Log.w(
                "VoiceInput",
                "Cannot start listening -  isListening:$isListening,speechRecognizer:$speechRecognizer"
            )

        }
    }

    fun stopListening() {
        if (isListening) {
            isListening = false
            speechRecognizer?.stopListening()
            Log.d("VoiceInput", "Stopped listening")
        }
    }


    private val recognitionListener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0] //Get best results
                Log.d("VoiceInput", "Recognized :$spokenText")
                onResultCallback?.invoke(spokenText)
            }
            isListening = false
        }

        override fun onError(error: Int) {
            Log.e("VoiceInput", "Speech recognition error :$error")
            isListening = false
        }


        //Required empty functions
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}


    }
}