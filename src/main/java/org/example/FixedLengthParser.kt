package org.example

import java.io.File
import java.io.IOException

class FixedLengthParser {
    @Throws(IOException::class)
    fun parseFile(filePath: String): List<Record> {
        val records = mutableListOf<Record>()

        File(filePath).bufferedReader().useLines { lines ->
            lines.forEach { line ->
                if (line.length == 25) {
                    // Extract fields based on fixed positions
                    val name = extractField(line, 1, 20).trim()
                    val gender = extractField(line, 20, 21).trim()
                    val age = extractField(line, 22, 25).trim()

                    // Create a new Record object
                    val record = Record(name, gender, age)
                    records.add(record)
                } else {
                    // Handle lines with unexpected length
                    println("Data '$line' is not parsed as it does not follow the schema.")
                }
            }
        }
        return records
    }

    private fun extractField(line: String, start: Int, end: Int): String {
        return line.substring(start - 1, end)
    }
}