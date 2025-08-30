package com.shubham.neocal.model

import androidx.compose.runtime.mutableStateOf
import kotlin.math.exp

/**
 * Model layer : Pure business logic for calculator operation
 * This class handles all mathematical computation for Neocalc
 * No Android dependencies = easily testable
 */

class CalculatorModel {

    /**
     * Step 1: Basic arithmetic operations
     * These are the foundation of our calculator
     */

    fun add(a: Double,b: Double): Double{
        return  a+b
    }

    fun subtract (a: Double, b: Double): Double{
        return a-b
    }

    fun multiply(a: Double,b: Double): Double{
        return a*b
    }

    fun divide (a: Double,b: Double): Double{
        return if(b!=0.0){
            a/b
        }else
        {
            throw ArithmeticException ("Cannot divide by zero")
        }
    }

    /**
     * Step 2: Format Calculation results for display
     * This ensures numbers look clean in our NeoCalc UI
     */

    fun formatResult(result:Double):String {
        return if(result == result.toLong().toDouble()){

            //If its a whole number , don't show decimal places
            result.toLong().toString()
        }
        else{
            //If it has decimals format to reasonable precision
            String.format("%.6f",result).trimEnd('0').trimEnd('.')
        }

    }

    /**
     * Step 3: Parse and Calculate Complex expression
     * Handles Multiple operations with proper precedence
     * Example : "2+3*4="14"(not "20")
     */

    fun calculateExpression (expression :String): String{
        return try {
            val cleanExpression = expression.replace(" ","") //Remove Spaces
            val result =evaluateExpression(cleanExpression)
            formatResult(result)
        }catch (e: Exception){
            "Error"
        }
    }


    /**
     * Helper Function :Evaluates Mathematical Expression
     * Uses Simple Algorithm For Order Of Operations
     */

    private fun evaluateExpression(expr:String): Double{
        //For now , lets handel simple cases

        //handel single numbers
        if(expr.matches(Regex("^-?\\d+(\\.\\d+)?$"))){
            return expr.toDouble()
        }

        //Handel simple : two-number operations
        //pattern : number operator number
        val pattern = Regex("^(-?\\d+(?:\\.\\d+)?)([+\\-*/])(-?\\d+(?:\\.\\d+)?)$")
        val match = pattern.find(expr)

        if(match!=null){
            val num1=match.groupValues[1].toDouble()
            val operator =match.groupValues[2]
            val num2=match.groupValues[3].toDouble()

            return when(operator){
                "+"-> add(num1,num2)
                "-"-> subtract(num1,num2)
                "*"-> multiply(num1,num2)
                "/"-> divide(num1,num2)
                else -> throw IllegalArgumentException("Unknown Operator")
            }
        }

        throw IllegalArgumentException("Invalid expression")
    }

    /**
     * Step 4:Input validation functions
     * Ensure only valid data reaches our calculator operations
     */

    fun isValidNumber(input:String): Boolean{
        if(input.isBlank())return false

        //Checking if the input is valid number format
        return try{
            input.toDouble()
            true
        }catch (e: NumberFormatException){
            false

        }
    }

    fun isValidOperator(operator: String):Boolean{
        return operator in listOf("+","-","*","/","x","÷")
    }

    fun sanitizeInput(input: String): String{
        return input
            .replace("x","*") //convert display x to *
            .replace("÷","/")  //convert display ÷ to /
            .replace(" ","")  //Remove Spaces
            .trim()                                 //Remove leading /trailing whitespace
    }

    /**
     * Step 5:Enhanced calculate function with validation
     * This replaces our basic calculateExpression
     */

    fun calculate(input :String):String{
        //Validate input first
        if(input.isBlank()){
            return "0"
        }

        //sanitize the input
        val cleanInput =sanitizeInput(input)

        //If it;s just a number , return it formatted
        if(isValidNumber(cleanInput)){
            return formatResult(cleanInput.toDouble())

        }
        //Otherwise , try to evaluate as expression
        return calculateExpression(cleanInput)
    }


    /**
     * Step 6: Text function to verify all operations works
     * This help us ensure everything is working before phase 2
     */

    fun runTest():List<String>{
        val testResults = mutableListOf<String>()

        try{
            //Test basic Operations
            testResults.add("Basic Add:2+3=${calculate("2+3")}")
            testResults.add("Basic Subtract :5-2=${calculate(("5-2"))}")
            testResults.add("Basic Multiply :4*3=${calculate("4*3")}")
            testResults.add("Basic Divide :10/2=${calculate(("10/2"))}")

            //Test Formatting
            testResults.add("Whole Number:5.0=${calculate("5.0")}")
            testResults.add("Decimal :5.5=${calculate("5.5")}")


            //Test error handling
            testResults.add("Division by zero :5/0=${calculate("5/0")}")
            testResults.add("Invalid Input:abc=${calculate("abc")}")


            //Test symbol conversion
            testResults.add("Multiply symbol :2x3=${calculate("2x3")}")
            testResults.add("Divide symbol : 6÷2=${calculate("6÷2")}")


            testResults.add("✅ ALL TESTS COMPLETED!")

        }catch (e:Exception){
            testResults.add("❌ TEST FAILED:${e.message}")
        }
        return testResults
    }
}

