package com.shubham.neocal.utils

import android.speech.tts.Voice
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*


class TTSManager(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    fun initialize(onInitComplete: (Boolean) -> Unit = {}) {

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                isInitialized = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED

                if (isInitialized) {
                    //Enhanced voice setting for more human sound
                    setupBetterVoice()
                }


                Log.d("TTSManager", "TTS initialize successfully :$isInitialized")
                onInitComplete(isInitialized)
            } else {
                Log.e("TTSManager", "TTS initialization failed ")
                onInitComplete(false)
            }
        }
    }


    //Speak text with different priorities

    fun speak(text: String, priority: Int = TextToSpeech.QUEUE_FLUSH) {
        if (isInitialized && !text.isBlank()) {
            textToSpeech?.speak(text, priority, null, "utterance_${System.currentTimeMillis()}")
        }
    }

    //Stop current Speech
    fun stop() {
        textToSpeech?.stop()
    }

    //Clean up resources
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }

    //Check if TTS is ready
    fun isReady(): Boolean = isInitialized


    private fun setupBetterVoice() {

        textToSpeech?.let { tts ->
            Log.d("TTSManager", "\uD83C\uDFA4 Starting Voice Optimization...")

            //Get all available voices
            val voices = tts.voices
            Log.d("TTSManager", "Found ${voices?.size} voice on device")

            analyzeVoiceQualities(voices)

            //find the best voice for Calculator
            val preferredVoice = findBestVoice(voices)
            if (preferredVoice != null) {
                tts.voice=preferredVoice

                Log.d("TTSManager", "✅  Selected voice :${preferredVoice.name}")
                Log.d("TTSManager", "Voice Quality :${preferredVoice.quality}")

            } else {
                Log.d("TTSManager", "⚠\uFE0F Using default Voice")

            }

            //Optimized Speech parameters for Calculator use
            tts.setSpeechRate(0.85f) //Slower = more clear
            tts.setPitch(1.15f) //slightly higher = more pleasant

            Log.d("TTSManager", "voice setting optimized!")


        }
    }

    private fun findBestVoice(voices:Set<Voice>?): Voice?{
        if(voices==null) return null

        //Priority 1:High quality female voices
        val highQualityFemale = voices.find { voice ->
            voice.locale == Locale.US &&
                    voice.quality>= Voice.QUALITY_HIGH &&
                    (voice.name.contains("female",ignoreCase = true) ||
                    voice.name.contains("woman",ignoreCase = true))
        }
        if(highQualityFemale !=null){
            Log.d("TTSManager","found high-quality female voice")
            return highQualityFemale
        }

        //Priority 2 :Any high quality voice
        val highQuality = voices.find { voice->
            voice.locale==Locale.US &&
                    voice.quality>= Voice.QUALITY_HIGH
        }

        if(highQuality !=null){
            Log.d("TTSManager","Found high-quality voice")
            return highQuality
        }

        //Priority 3 :Normal quality ,prefer female
        val normalFemale =voices.find { voice ->
            voice.locale==Locale.US &&
                    voice.quality>= Voice.QUALITY_NORMAL &&
                    (voice.name.contains("female",ignoreCase = true)||
                            voice.name.contains("woman",ignoreCase = true))
        }

        if(normalFemale!=null){
            Log.d("TTSManager","Found Normal female voice")
            return normalFemale
        }

        //Priority 4 : Any normal quality voice
        val normalVoice = voices.find { voice ->
            voice.locale==Locale.US &&
                    voice.quality==Voice.QUALITY_NORMAL
        }
        Log.d("TTSManager","Using fallback voice")
        return normalVoice
    }


    private fun analyzeVoiceQualities(voices:Set<Voice>?){
        if(voices == null)return

        Log.d("TTSManager","DETAILED VOICE ANALYSIS:")

        val usVoices = voices.filter { it.locale==Locale.US }

        //Group by quality
        val qualityGroups = usVoices.groupBy { it.quality }
        qualityGroups.forEach { (quality,voiceList) ->
            val qualityName = when(quality){
                Voice.QUALITY_VERY_LOW -> "VERY_LOW(100)"
                Voice.QUALITY_LOW ->"LOW (200)"
                Voice.QUALITY_NORMAL->"NORMAL (300)"
                Voice.QUALITY_HIGH->"HIGH (400)"
                Voice.QUALITY_VERY_HIGH->"VERY_HIGH(500)"
                else -> "UNKNOWN ($quality)"
            }
            Log.d("TTSManager","$qualityName:${voiceList.size} Voices")

            //Showing first 5 voices names for each quality
            voiceList.take(5).forEach { voice ->
                Log.d("TTSManager","${voice.name}")

            }
        }
        //Looking for natural sounding voices specifically
        val naturalVoices=usVoices.filter { voice ->
            voice.name.contains("neural",ignoreCase = true)||
            voice.name.contains("enhanced",ignoreCase = true)||
            voice.name.contains("premium",ignoreCase = true)||
            voice.name.contains("wavenet",ignoreCase = true)||
            voice.name.contains("studio",ignoreCase = true)||
            voice.name.contains("natural",ignoreCase = true)
        }
        Log.d("TTSManager","NATURAL VOICES FOUND :${naturalVoices.size}")

        naturalVoices.forEach { voice ->
            Log.d("TTSManager","${voice.name} (Quality:${voice.quality}")
        }

    }

}
