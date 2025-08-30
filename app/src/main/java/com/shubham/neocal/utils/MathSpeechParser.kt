package com.shubham.neocal.utils

import android.util.Log

class MathSpeechParser {

    fun parseToMathExpression(spokenText: String): String? {
        Log.d("MathParser", "Input:$spokenText'")

        //Clean up the input - remove fillers words and Make lowercase
        val cleanText = cleanInput(spokenText)
        Log.d("MathParser", "Cleaned :$cleanText")

        //try to extract math expression
        val mathExpression = extractMathExpression(cleanText)
        Log.d("MathParser", "Result :'$mathExpression'")

        return mathExpression
    }

    private fun cleanInput(input: String): String {
        return input.lowercase()
            .replace("what's", "")
            .replace("what is", "")
            .replace("calculate", "")
            .replace("okay", "")
            .trim()
    }

    private fun extractMathExpression(cleanText: String): String? {


        //handle number + symbol format ("25"  + "1","3+2")
        if(cleanText.matches(Regex("\\d+\\s*[+\\-Xx÷*]\\s*\\d+"))){
            return cleanText.replace("X","x").replace("/","÷").replace("X","x")
        }

        //Handle simple pattern first
        return when {
            cleanText.contains("plus") -> handleAddition(cleanText)
            cleanText.contains("minus") -> handleSubtraction(cleanText)
            cleanText.contains("times") || cleanText.contains("multiply") -> handleMultiplication(
                cleanText
            )

            cleanText.contains("divide") -> handleDivision(cleanText)
            else -> null
        }
    }

    private fun handleAddition(text: String): String? {
        val parts = text.split("plus")
        if (parts.size == 2) {
            val num1 = convertWordsToNumber(parts[0].trim())
            val num2 = convertWordsToNumber(parts[1].trim())
            return if (num1 != null && num2 != null) "$num1+$num2" else null
        }
        return null
    }

    private fun handleSubtraction(text: String): String? {
        val parts = text.split("minus")
        if (parts.size == 2) {
            val num1 = convertWordsToNumber(parts[0].trim())
            val num2 = convertWordsToNumber(parts[1].trim())
            return if (num1 != null && num2 != null) "$num1-$num2" else null
        }
        return null
    }

    private fun handleMultiplication(text: String): String? {
        val parts = text.split("times", "multiply")
        if (parts.size == 2) {
            val num1 = convertWordsToNumber(parts[0].trim())
            val num2 = convertWordsToNumber(parts[1].trim())
            return if (num1 != null && num2 != null) "$num1*$num2" else null
        }
        return null
    }


    private fun handleDivision(text: String): String? {
        val parts = text.split("divide", "divide by ")
        if (parts.size == 2) {
            val num1 = convertWordsToNumber(parts[0].trim())
            val num2 = convertWordsToNumber(parts[1].trim())
            return if (num1 != null && num2 != null) "$num1÷$num2" else null
        }
        return null
    }

    private fun convertWordsToNumber(words: String): String? {

        val cleanWords = words.trim().lowercase()
        Log.d("MathParser", "Converting words:$words")

        //Handle direct number words
        return when (cleanWords) {
            "zero" -> "0"
            "one" -> "1"
            "two" -> "2"
            "three" -> "3"
            "four" -> "4"
            "five" -> "5"
            "size" -> "6"
            "seven" -> "7"
            "eight" -> "8"
            "nine" -> "9"
            "ten" -> "10"
            "eleven" -> "11"
            "twelve" -> "12"
            "thirteen" -> "13"
            "fourteen" -> "14"
            "fifteen" -> "15"
            "sixteen" -> "16"
            "seventeen" -> "17"
            "eighteen" -> "18"
            "nineteen" -> "19"
            "twenty" -> "20"
            "thirty" -> "30"
            "forty" -> "40"
            "fifty" -> "50"
            "sixty" -> "60"
            "seventy" -> "70"
            "eighty" -> "80"
            "ninety" -> "90"
            "hundred" -> "100"
            else -> handleComplexNumber(cleanWords)
        }
    }

    private fun handleComplexNumber(words: String): String? {
        //Handle "twenty five","thirty two",etc

        return when {
            words.contains("twenty") -> handleTwenties(words)
            words.contains("thirty") -> handleThirties(words)
            words.contains("forty") -> handleForties(words)
            words.contains("fifty") -> handleFifties(words)

            //IF it's already a number ,return it
            words.matches(Regex("\\d+")) -> words
            else -> {
                Log.w("MathParse", "Cloud not convert:'$words'")
                null
            }
        }
    }

    private fun handleTwenties(words: String): String? {
        return when {
            words.contains("twenty one") -> "21"
            words.contains("twenty two") -> "22"
            words.contains("twenty three") -> "23"
            words.contains("twenty four") -> "24"
            words.contains("twenty five") -> "25"
            words.contains("twenty six") -> "26"
            words.contains("twenty seven") -> "27"
            words.contains("twenty eight") -> "28"
            words.contains("twenty nine") -> "29"
            words == "twenty" -> "20"
            else -> null
        }
    }

    private fun handleThirties(words: String): String? {
        return when {
            words.contains("thirty one") -> "31"
            words.contains("thirty two") -> "32"
            words.contains("thirty three") -> "33"
            words.contains("thirty four") -> "34"
            words.contains("thirty five") -> "35"
            words.contains("thirty six") -> "36"
            words.contains("thirty seven") -> "37"
            words.contains("thirty eight") -> "38"
            words.contains("thirty nine") -> "39"
            words == "thirty" -> "30"
            else -> null
        }
    }

    private fun handleForties(words: String): String? {
        return when {
            words.contains("forty one") -> "41"
            words.contains("forty two") -> "42"
            words.contains("forty three") -> "43"
            words.contains("forty four") -> "44"
            words.contains("forty five") -> "45"
            words.contains("forty six") -> "46"
            words.contains("forty seven") -> "47"
            words.contains("forty eight") -> "48"
            words.contains("forty nine") -> "49"
            words == "forty" -> "40"
            else -> null
        }
    }

    private fun handleFifties(words: String): String? {
        return when {
            words.contains("fifty one") -> "51"
            words.contains("fifty two") -> "52"
            words.contains("fifty three") -> "53"
            words.contains("fifty four") -> "54"
            words.contains("fifty five") -> "55"
            words.contains("fifty six") -> "56"
            words.contains("fifty seven") -> "57"
            words.contains("fifty eight") -> "58"
            words.contains("fifty nine") -> "59"
            words == "fifty" -> "50"
            else -> null
        }
    }
}
