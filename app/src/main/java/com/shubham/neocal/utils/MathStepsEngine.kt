package com.shubham.neocal.utils

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.util.Log
import okhttp3.internal.format
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import kotlin.math.exp


class MathStepsEngine(private val context: Context) {
    private var webView: WebView? = null


    private val wolframApi: WolframApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.wolframalpha.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(WolframApi::class.java)
    }

    private val appId = ""

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


    suspend fun solveWithWolfram(expression: String): List<String> {
        try {
            // --- STEP 1: MAKE THE INITIAL QUERY ---
            Log.d("MathSteps", "--- Step 1: Making initial query for '$expression' ---")
            val initialResponse = wolframApi.getSteps(
                appId = appId,
                input = "expand $expression",
                format = "plaintext"
            )
            // Using the safe ?. operator and the elvis ?: operator for logging
            Log.d("MathSteps", "Initial query got ${initialResponse.pods?.size ?: 0} pods.")

            // Using the standard library's isNullOrEmpty() check, which is safe for nullable lists
            if (!initialResponse.success || initialResponse.pods.isNullOrEmpty()) {
                return listOf("Error: Wolfram Alpha could not understand the expression.")
            }

            // --- STEP 2: FIND A POD THAT OFFERS STEPS ---
            var stepByStepState: String? = null

            // Looping over the list, providing an empty list as a default if it's null
            for (pod in initialResponse.pods ?: emptyList()) {
                pod.states.forEach { state ->
                    if (state.name == "Step-by-step solution") {
                        stepByStepState = state.input
                        Log.d("MathSteps", "Found step-by-step state in pod '${pod.title}'. Input required: '$stepByStepState'")
                        return@forEach
                    }
                }
                if (stepByStepState != null) break
            }

            // --- STEP 3: MAKE THE SECOND QUERY ---
            if (stepByStepState != null) {
                Log.d("MathSteps", "--- Step 2: Making follow-up query for steps ---")
                val stepsResponse = wolframApi.getSteps(
                    appId = appId,
                    input = expression,
                    format = "plaintext",
                    podstate = stepByStepState
                )
                Log.d("MathSteps", "Steps query got ${stepsResponse.pods?.size ?: 0} pods.")

                val stepsPod = stepsResponse.pods?.find { it.title == "Step-by-step solution" }
                if (stepsPod != null) {
                    val steps = mutableListOf<String>()
                    // Safely iterating over subpods
                    stepsPod.subpods?.forEach { subPod ->
                        val currentText = subPod.plaintext
                        if (!currentText.isNullOrEmpty()) {
                            steps.add(currentText)
                        }
                    }
                    return steps
                }
            }

            // --- FALLBACK ---
            Log.d("MathSteps", "No step-by-step state found. Displaying final result.")
            val resultPod = initialResponse.pods?.find { it.primary == true || it.title == "Result" }
            val result = resultPod?.subpods?.firstOrNull()?.plaintext ?: "Could not find result."
            return listOf("Step 1: $expression", "Result: $result", "(No steps available)")

        } catch (e: Exception) {
            Log.e("MathSteps", "Wolfram API error: ${e.message}", e)
            return listOf("Step 1: $expression", "Error: ${e.message}")
        }
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
        
        //Enhanced step generation based on expression type
        //Check for any power pattern :^2,^3,^4...etc.
        var powerPattern =/\^(\d+)/;
        var powerMatch = expression.match(powerPattern);
        
        
        if(powerMatch && expression.includes("(")){
        var exponent = powerMatch[1];
        var baseExpression = expression.substring(0,expression.indexOf("^"));
        
        //Handle different exponents 
        if(exponent == "2"){
        steps.push("Step 2 : Rewrite as multiplication :" + baseExpression +"×" + baseExpression);
        steps.push("Steps 3 : Apply FOIL method (First, Outer,Inner,Last)");
        steps.push("Step 4 : Formula used : (a+b)² = a² + 2ab + b²");
        
        } 
        else if (exponent =="3"){
        steps.push("Step 2 :Rewrite as :" + baseExpression + "×" + baseExpression + "×" + baseExpression );
        steps.push("Step 3 :First expand  :" + baseExpression + "² than multiply by " + baseExpression );
        steps.push("Step 4 : Formula used :(a+b)³ = a³ + 3a²b + 3ab² + b³");
        }
        else {
        steps.push("Step 2 : Rewrite as repeated multiplication :" + baseExpression + "multiplied" + exponent + "times");
        steps.push ("Step 3 : use binominal theorem for expansion ");
        steps.push("Step 4 : Apply the formula : (a+b)^n = Σ(nCk × a^(n-k) × b^k)");
        }
        
        var expanded = Algebrite.run ("expand(" + expression + ")");   
        if(expanded && expanded !== expression ) {
        steps.push("Step " + (steps.length + 1 ) + " : Simplify :" + expanded );
        }
        } else if ( expression.includes ("*") && expression.includes(")")){
        
        //Handles Multiplications of binominals 
        steps.push("Step 2 : use distributive property (FOIL method )");
         steps.push("Step 3 : Multiply each term in first bracket by each term in second bracket");
         
         var expanded = Algebrite.run("expand("+ expression + ")");
         
         if(expanded && expanded !==expression ) {
         
         steps.push("Step 4 : Combine like terms : " + expanded );
         }
        } else {
        //Default Algebrite expression 
        var expanded = Algebrite.run("expand("+ expression + ")");
        if( expanded && expanded !== expression ){
        steps.push ("Step 2 : Expand: " + expanded);
        }
        }
        
        return steps;
        }catch(error){
        return ["Step 1: "+ expression, "Error: " + error.toString()]; 
       }
       }
       
       window.onload = function(){
       console.log("Window loaded !");
       setTimeout(checkLibraries,500);
       };
        
       
        </script>
        </body>
        </html>
        """.trimIndent()
            }


        }