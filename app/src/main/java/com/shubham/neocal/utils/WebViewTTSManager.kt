package com.shubham.neocal.utils

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient


class WebViewTTSManager(private val context: Context) {
    private var webView: WebView? = null
    private var isReady = false

    fun initialize(onInitComplete: (Boolean) -> Unit = {}) {
        try {
            Log.d("WebViewTTS", "Initializing webView TTS")

            webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                //Adding interface for communication

                addJavascriptInterface(TTSInterface(), "Android")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("WebViewTTS", "WebView loaded successfully")
                        isReady = true
                        onInitComplete(true)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        Log.e("WebViewTTS", "webView error:${description}")
                        onInitComplete(false)

                    }
                }
                //Loading ResponsiveVoice
                val htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                    <meta charset ="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    </head>
                    <body>
                    <script>
                    console.log('Loading ResponsiveVoice...');
                    
                    //Loading ResponsiveVoice script dynamically
                    
                   var script=document.createElement('script');
                   script.src='https://code.responsivevoice.org/responsivevoice.js?key=FREE';
                   script.onload = function(){
                   console.log('ResponsiveVoice script loaded waiting for initialization...');
                   waitForRV();
                   };
                   script.onerror=function(){
                   console.log('Failed to load ResponsiveVoice Script');
                   Android.onResponsiveVoiceError();
                   };
                   document.head.appendChild(script);
                   
                   function waitForRV(){
                   var attempts =0;
                   var maxAttempts =50; //5 seconds max
                   
                   function checkRV(){
                   attempts++;
                   console.log('Checking ResponsiveVoice... attempt' +attempts);
                   
                    
                    if (window.responsiveVoice && responsiveVoice.voiceSupport) {
                        console.log('ResponsiveVoice is ready!');
                        Android.onResponsiveVoiceReady();
                    } else if (attempts < maxAttempts) {
                        setTimeout(checkRV, 100);
                    } else {
                        console.log('ResponsiveVoice failed to load after ' + attempts + ' attempts');
                        Android.onResponsiveVoiceError();
                    }
                }
                checkRV();
            }
            
            function speakText(text) {
                console.log('Attempting to speak: ' + text);
                
                if (window.responsiveVoice && responsiveVoice.voiceSupport) {
                    responsiveVoice.speak(text, "US English Female", {
                        rate: 0.8,
                        pitch: 1.1,
                        volume: 1.0,
                        onend: function() {
                            console.log('Speech completed');
                        },
                        onerror: function(e) {
                            console.log('Speech error: ' + e);
                        }
                    });
                    return true;
                } else {
                    console.log('ResponsiveVoice still not ready');
                    return false;
                }
            }
        </script>
    </body>
    </html>
""".trimIndent()

                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        } catch (e: Exception) {
            Log.e("WebViewTTS", "Failed to initialize :${e.message}")
            onInitComplete(false)
        }
    }

    fun speak(text: String) {
        if (isReady && webView != null) {
            try {
                Log.d("WebViewTTS", "Speaking '$text'")

                //Enhance text for calculator
                val enhancedText = enhanceCalculatorText(text)

                //calling javaScript function
                val jsCode = "speakText('$enhancedText');"
                webView?.evaluateJavascript(jsCode) { result ->
                    Log.d("WebViewTTS", "Speech result :$result")
                }
            } catch (e: Exception) {
                Log.e("WebViewTTS", "speak error :${e.message}")
            }
        } else {
            Log.w("WebViewTTS", "TTS not ready yet")
        }
    }

    private fun enhanceCalculatorText(text: String): String {
        return when (text.lowercase()) {
            "0" -> "zero"
            "1" -> "one"
            "2" -> "two"
            "3" -> "three"
            "4" -> "four"
            "5" -> "five"
            "6" -> "six"
            "7" -> "seven"
            "8" -> "eight"
            "9" -> "nine"
            "+" -> "plus"
            "-" -> "minus"
            "x", "*" -> "times"
            "÷", "/" -> "divided by"
            "=" -> "equals"
            "." -> "point"
            "clear" -> "clear"
            "all clear " -> "all clear"
            "delete" -> "delete"
            else -> {
                if (text.startsWith("Result is")) {
                    text.replace("Result is ", "The result is ")
                } else {
                    text
                }
            }
        }
    }

    fun stop() {
        webView?.evaluateJavascript("responsiveVoice.cancel();", null)
    }

    fun shutdown() {
        webView?.destroy()
        webView = null
        isReady = false
    }

    fun isReady(): Boolean = isReady

    //javaScript Interface for communication
    inner class TTSInterface {
        @JavascriptInterface
        fun onResponsiveVoiceReady() {
            Log.d("WebViewTTS", "ResponsiveVoice is ready!")
            isReady =true
        }

        @JavascriptInterface
        fun onResponsiveVoiceError(){
            Log.e("WebViewTTS","ResponsiveVoice failed to load ")
            isReady=false
        }
    }

}