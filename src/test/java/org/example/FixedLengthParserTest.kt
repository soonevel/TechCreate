package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FixedLengthParserTest {
    @Test
    fun parseFile_valid1_data1() {
        parseFile_valid1("src/test/resources/valid1_data1.txt")
    }

    @Test
    fun parseFile_valid1_data2() {
        parseFile_valid1("src/test/resources/valid1_data2.txt")
    }

    private fun parseFile_valid1(filePath: String) {
        try {
            val generator = Generator()
            generator.generateRecordAndFLP("src/test/resources/valid1.schema")

            val testedParser = FixedLengthParser()
            val actualRecords = testedParser.parseFile(filePath)

            val expectedRecords = mutableListOf<Record>()
            val rec1 = Record("John Doe", "M", "25")
            val rec2 = Record("Jane Smith", "F", "30")
            expectedRecords.add(rec1)
            expectedRecords.add(rec2)

            assertEquals(
                expectedRecords.toString(),
                actualRecords.toString(),
                "parseFile_valid1 should return the expected records."
            )
            assertEquals(2, actualRecords.size, "There should be 2 records.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

//    @Test
//    fun parseFile_valid2_data() {
//        try {
//            val generator = Generator()
//            generator.generateRecordAndFLP("src/test/resources/valid2.schema")
//
//            val testedParser = FixedLengthParser()
//            val actualRecords = testedParser.parseFile("src/test/resources/valid2_data.txt")
//
//            val expectedRecords = mutableListOf<Record>()
//            val rec1 = Record("2500", "12-12-2012")
//            val rec2 = Record("100", "01-01-2024")
//            expectedRecords.add(rec1)
//            expectedRecords.add(rec2)
//
//            assertEquals(
//                expectedRecords.toString(),
//                actualRecords.toString(),
//                "parseFile_valid2 should return the expected records."
//            )
//            assertEquals(2, actualRecords.size, "There should be 2 records.")
//        } catch (e: Exception) {
//            // Fail the test if an exception is thrown
//            fail("Exception should not have been thrown: " + e.message)
//        }
//    }
}