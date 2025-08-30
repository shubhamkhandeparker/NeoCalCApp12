package com.shubham.neocal.utils

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.util.Log


class MathStepsEngine(private val context: Context) {
    private var webView: WebView? = null

    fun initialize() {
        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            webViewClient = WebViewClient()
            addJavascriptInterface(JavaScriptInterface(), "Android")

            //Loading HTML with MathSteps library
            loadDataWithBaseURL(
                "file:///android_asset/",
                getHtmlContent(),
                "text/html",
                "UTF-8",
                null
            )
        }
    }

    inner class JavaScriptInterface {
        @JavascriptInterface
        fun onStepsCalculated(stepsJson: String) {
            Log.d("MathSteps", "Received steps:$stepsJson ")

            //parse the JSON and update UI on main threads

            android.os.Handler(android.os.Looper.getMainLooper()).post {
                val steps = parseStepsFromJson(stepsJson)
                onStepsReady?.invoke(steps)
            }

        }

        private fun parseStepsFromJson(stepsJson: String): List<String> {
            return try {
                //Removing the outer brackets and splitting by comma
                val cleanJson = stepsJson.trim().removeSurrounding("[", "]")
                val steps = cleanJson.split("\",\"")
                    .map { it.trim().removeSurrounding("\"") }
                steps
            } catch (e: Exception) {
                Log.e("MathSteps", "Error parsing JSON:$e")
                listOf("Error Parsing Steps ")
            }
        }

        @JavascriptInterface
        fun onError(error: String) {
            Log.e("MathSteps", "Math Error :$error")
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onErrorCallback?.invoke(error)
            }
        }
    }


    //Adding Callback proprieties
    private var onStepsReady: ((List<String>) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null

    fun setCallbacks(
        onSteps: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        this.onStepsReady = onSteps
        this.onErrorCallback = onError
    }

    fun solveExpression(expression: String) {
        val jsCode = """
            try{
            
            console.log("Solving:$expression");
            var steps = generateSteps("$expression");
            
            var stepsJson = JSON.stringify(steps);
            console.log("Generated steps :" +stepsJson);
            Android.onStepsCalculated(stepsJson);
            
            } catch(error){
            console.log("Error:"+error);
            Android.onError(error.toString());
            
            }
    """
        webView?.evaluateJavascript(jsCode, null)
    }

    fun testBridge() {
        Log.d("MathSteps", "Testing bridge...")
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            solveExpression("(a+b)^2")
        }, 1500)

    }

    private fun getHtmlContent(): String {
        return """
        <!DOCTYPE html >
        <html >
        <head >
       <script src="js/algebrite.bundle-for-browser.js"></script>
        </head>
        <body >
        <div id ="result"></div>
        <script >
                console.log("Loading libraries...");

        function checkLibraries(){
        console.log("Algebrite available :",typeof Algebrite !=='undefined');
        return  typeof Algebrite!=='undefined';
        }
         
        function generateSteps(expression){
        if(!checkLibraries()){
        return ["step 1 :"+expression,"Error: Libraries not loaded"];
        }
        
        try{
        var steps=[];
        steps.push("Steps 1:"+expression);
        
        //using Algebrite for symbolic expression
        var expanded = Algebrite.run("expand("+expression +")");
        if(expanded && expanded !==expression){
        
        steps.push("Step 2 :Expand ->"+expanded);
        }
        
        
        //adding more steps based on expression type
        if(expression.includes("^2") || expression.includes("²")){
        steps.push("Step 3 : Apply(a+b)² = a² +2ab + b² formula");
        }
        
        return steps;
        } catch(error){
        return["Step 1 :" + expression,"Error:"+error.toString()];
        }
        }
              
        window.onload=function(){
        console.log("Window loaded !");
       setTimeout(checkLibraries,500);
      
        };
        </script>
        </body>
        </html>
        """.trimIndent()
    }


}