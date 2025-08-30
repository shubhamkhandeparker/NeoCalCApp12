package com.shubham.neocal.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import java.util.*


class EnhancedTTSManager(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    fun initialize(onInitComplete: (Boolean) -> Unit = {}) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                isInitialized = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED

                if (isInitialized) {
                    setupBestVoiceForCalculator()
                }
                Log.d("EnhancedTTS", "TTS initialized :${isInitialized}")
                onInitComplete(isInitialized)
            } else {
                Log.e("EnhancedTTS", "TTS initialization failed")
                isInitialized = false
                onInitComplete(false)
            }
        }
    }

    private fun setupBestVoiceForCalculator() {
        textToSpeech?.let { tts ->

            //Finding the best voice for calculator

            val voices = tts.voices
            val bestVoice = findBestCalculatorVoice(voices)

            if (bestVoice != null) {
                tts.voice = bestVoice
                Log.d("EnhancedTTS", "Selected Voice :${bestVoice.name}")
            }

            //Optimize for Calculator speech
            tts.setSpeechRate(0.8f)     //slower,clearer
            tts.setPitch(1.2f)          //Slightly higher, more pleasant

        }

    }

    private fun findBestCalculatorVoice(voices: Set<Voice>?): Voice? {
        if (voices == null) return null

        val usVoices = voices.filter { it.locale == Locale.US }

        //Priority 1: Looking for female neural voices
        val femaleNeural = usVoices.find { voice ->
            voice.quality >= Voice.QUALITY_HIGH &&
                    (voice.name.contains("female", ignoreCase = true) ||
                            voice.name.contains("neural", ignoreCase = true))

        }

        if (femaleNeural != null) return femaleNeural

        //Priority 2 : Any high quality voice
        return usVoices.find { it.quality >= Voice.QUALITY_HIGH }
            ?: usVoices.firstOrNull()
    }

    fun speak(text: String) {
        if (isInitialized && textToSpeech != null) {
            val enhancedText = enhanceCalcualtorText(text)
            textToSpeech?.speak(enhancedText, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun enhanceCalcualtorText(text: String): String{
        return when (text.lowercase()){
            //Number with better pronunciation
            "0"->"zero"
            "1"->"one"
            "2"->"two"
            "3"->"three"
            "4"->"four"
            "5"->"five"
            "6"->"six"
            "7"->"seven"
            "8"->"eight"
            "9"->"nine"

            //Operator with pauses for clarity
            "+"-> "plus"
            "-"-> "minus"
            "x","*"-> "times"
            "÷","/"-> "divided by"
            "="-> "equals"
            "."-> "point"

            //Enhanced result announcement
            else -> {
                if(text.startsWith("Result is")){
                    text.replace("Result is ","The result is ")
                }else{
                    text
                }
            }
        }
    }
    fun stop(){
        textToSpeech?.stop()
    }

    fun shutdown(){
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech=null
        isInitialized=false
    }
    fun isReady():Boolean = isInitialized
}