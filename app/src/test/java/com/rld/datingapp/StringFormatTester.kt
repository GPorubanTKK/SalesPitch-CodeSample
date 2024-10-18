package com.rld.datingapp

import com.rld.datingapp.util.formatLines
import org.apache.commons.lang3.RandomStringUtils
import org.junit.Test

class StringFormatTester {
    @Test fun normalConditions() {

    }

    @Test fun reallyLongWords1() {
        val lineLength = 20
        val str = "This sentence is about microspectrophotometers."
        val formatted = str.formatLines(lineLength)
        require(formatted == "This sentence is about\nmicrospectrophotometers.") { "Got: $formatted" }
    }

    @Test fun reallyLongWords2() {
        val lineLength = 50
        val sentence = randSentence(45, 70)
        val formatted = sentence.formatLines(lineLength)
        val formattedLines = formatted.lines()
        for(line in formattedLines) {
            val words = line.split(" ".toRegex())
            var numGreaterThanLen = 0
            for(word in words) if(word.length >= lineLength) numGreaterThanLen++
            require(numGreaterThanLen < 2)
        }
        println("Got [$formatted] from [$sentence].")
    }

    private fun randSentence(maxWordLength: Int, length: Int): String {
        val strUtils = RandomStringUtils.insecure()
        val words = mutableListOf<String>()
        for(i in 0 ..< length) words += strUtils.nextAlphanumeric(1, maxWordLength + 1)
        return words.joinToString(" ") { it }.trim()
    }
}