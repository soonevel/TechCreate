package org.example

import org.example.exceptions.SchemaValidationError
import org.example.exceptions.SchemaValidationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

internal class GeneratorTest {
    private var testedGenerator = Generator()

    companion object {
        private val expectedValidCols1 = mutableListOf<Column>()
        private val expectedValidCols2 = mutableListOf<Column>()

        @JvmStatic
        @BeforeAll
        fun setUpBeforeClass(): Unit {
            val col1 = Column("name", 1, 20)
            val col2 = Column("gender", 20, 21)
            val col3 = Column("age", 22, 25)
            expectedValidCols1.add(col1)
            expectedValidCols1.add(col2)
            expectedValidCols1.add(col3)

            val colA = Column("balance", 1, 10)
            val colB = Column("date", 11, 20)
            expectedValidCols2.add(colA)
            expectedValidCols2.add(colB)
        }
    }

    @Test
    fun splitLine_valid1() {
        try {
            val actualCol = testedGenerator.splitLine("firstName 1 10")
            val expectedCol = listOf("firstName", "1", "10")

            assertEquals(expectedCol, actualCol, "splitLine_valid1 should return the expected list.")
            assertEquals(3, actualCol.size, "There should be 3 strings.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun splitLine_valid2() {
        try {
            val actualCol = testedGenerator.splitLine("first name 1 10")
            val expectedCol = listOf("first name", "1", "10")

            assertEquals(expectedCol, actualCol, "splitLine_valid2 should return the expected list.")
            assertEquals(3, actualCol.size, "There should be 3 strings.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun splitLine_invalid1() {
        try {
            val actualCol = testedGenerator.splitLine("firstName 123 1 10")

            assertNotEquals(3, actualCol.size, "splitLine_invalid1 should not return a list with size of 3.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun splitLine_invalid2() {
        try {
            val actualCol = testedGenerator.splitLine("firstName  1 10")

            assertNotEquals(3, actualCol.size, "splitLine_invalid2 should not return a list with size of 3.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun splitLine_invalid3() {
        try {
            val actualCol = testedGenerator.splitLine("firstName 1  10")

            assertNotEquals(3, actualCol.size, "splitLine_invalid3 should not return a list with size of 3.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun parseSchemaFile_valid1() {
        try {
            val actualCols = testedGenerator.parseSchemaFile("src/test/resources/valid1.schema")

            assertEquals(
                expectedValidCols1.toString(),
                actualCols.toString(),
                "parseSchemaFile_valid1 should return the expected columns."
            )
            assertEquals(3, actualCols.size, "There should be 3 columns.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun parseSchemaFile_valid2() {
        try {
            val actualCols = testedGenerator.parseSchemaFile("src/test/resources/valid2.schema")

            assertEquals(
                expectedValidCols2.toString(),
                actualCols.toString(),
                "parseSchemaFile_valid2 should return the expected columns."
            )
            assertEquals(2, actualCols.size, "There should be 2 columns.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun parseSchemaFile_random() {
        try {
            val actualCols = testedGenerator.parseSchemaFile("src/test/resources/random.schema")

            val expectedCols = mutableListOf<Column>()
            val col1 = Column("student_name", 1, 20)
            val col2 = Column("student1", 21, 22)
            val col3 = Column("student", 23, 24)
            val col4 = Column("abc", 25, 26)
            val col5 = Column("student123", 27, 28)
            val col6 = Column("unknown", 29, 30)
            expectedCols.add(col1)
            expectedCols.add(col2)
            expectedCols.add(col3)
            expectedCols.add(col4)
            expectedCols.add(col5)
            expectedCols.add(col6)

            assertEquals(
                expectedCols.toString(),
                actualCols.toString(),
                "parseSchemaFile_random should return the expected columns."
            )
            assertEquals(6, actualCols.size, "There should be 6 columns.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun parseSchemaFile_unknown() {
        try {
            val actualCols = testedGenerator.parseSchemaFile("src/test/resources/unknown.schema")

            val expectedCols = mutableListOf<Column>()
            val col1 = Column("valid", 1, 2)
            val col2 = Column("unknown", 3, 4)
            expectedCols.add(col1)
            expectedCols.add(col2)

            assertEquals(
                expectedCols.toString(),
                actualCols.toString(),
                "parseSchemaFile_unknown should return the expected columns."
            )
            assertEquals(2, actualCols.size, "There should be 2 columns.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun parseSchemaFile_spaceInName() {
        try {
            val actualCols = testedGenerator.parseSchemaFile("src/test/resources/spaceInName.schema")

            val expectedCols = mutableListOf<Column>()
            val col1 = Column("remainingBalance", 1, 5)
            val col2 = Column("date", 6, 16)
            val col3 = Column("dailyTransactionLimit", 17, 20)
            expectedCols.add(col1)
            expectedCols.add(col2)
            expectedCols.add(col3)

            assertEquals(
                expectedCols.toString(),
                actualCols.toString(),
                "parseSchemaFile_spaceInName should return the expected columns."
            )
            assertEquals(3, actualCols.size, "There should be 3 columns.")
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun parseSchemaFile_extraspace1() {
        // Test that the correct exception is thrown
        val thrown = assertThrows(
            SchemaValidationException::class.java
        ) {
            testedGenerator.parseSchemaFile("src/test/resources/extraspace1.schema")
        }

        // Verify the exception message
        assertEquals(
            SchemaValidationError.INVALID_SCHEMA_FILE.getMessage(1, "remaining balance 1  20"),
            thrown.message
        )
    }

    @Test
    fun parseSchemaFile_extraspace2() {
        // Test that the correct exception is thrown
        val thrown = assertThrows(
            SchemaValidationException::class.java
        ) {
            testedGenerator.parseSchemaFile("src/test/resources/extraspace2.schema")
        }

        // Verify the exception message
        assertEquals(
            SchemaValidationError.INVALID_SCHEMA_FILE.getMessage(1, "remaining balance    1 20"),
            thrown.message
        )
    }

    @Test
    fun parseSchemaFile_number() {
        // Test that the correct exception is thrown
        val thrown = assertThrows(
            SchemaValidationException::class.java
        ) {
            testedGenerator.parseSchemaFile("src/test/resources/number.schema")
        }

        // Verify the exception message
        assertEquals(
            SchemaValidationError.INVALID_SCHEMA_FILE.getMessage(1, "remaining balance 1.0 5"),
            thrown.message
        )
    }

    @Test
    fun parseSchemaFile_contIndex() {
        // Test that the correct exception is thrown
        val thrown = assertThrows(
            SchemaValidationException::class.java
        ) {
            testedGenerator.parseSchemaFile("src/test/resources/contIndex.schema")
        }

        // Verify the exception message
        assertEquals(SchemaValidationError.INVALID_START_INDEX.getMessage(2, "today 4 8"), thrown.message)
    }

    @Test
    fun parseSchemaFile_index() {
        // Test that the correct exception is thrown
        val thrown = assertThrows(
            SchemaValidationException::class.java
        ) {
            testedGenerator.parseSchemaFile("src/test/resources/index.schema")
        }

        // Verify the exception message
        assertEquals(SchemaValidationError.INVALID_INDEX.getMessage(1, "age 2 1"), thrown.message)
    }

    @Test
    fun parseSchemaFile_keyword() {
        // Test that the correct exception is thrown
        val thrown = assertThrows(
            SchemaValidationException::class.java
        ) {
            testedGenerator.parseSchemaFile("src/test/resources/keyword.schema")
        }

        // Verify the exception message
        assertEquals(
            SchemaValidationError.INVALID_COLUMN_NAME.getMessage("fun", 1, "fun 1 2"),
            thrown.message
        )
    }

    @Test
    fun parseSchemaFile_duplicate() {
        // Test that the correct exception is thrown
        val thrown = assertThrows(
            SchemaValidationException::class.java
        ) {
            testedGenerator.parseSchemaFile("src/test/resources/duplicate.schema")
        }

        // Verify the exception message
        assertEquals(
            SchemaValidationError.DUPLICATE_COLUMN_NAME.getMessage("student", 2, "student 21 40"),
            thrown.message
        )
    }

    @Test
    fun writeRecordClass_valid1() {
        try {
            testedGenerator.writeRecordClass(expectedValidCols1, "src/test/resources/Record.txt")
            assertTrue(
                areFilesEqual(
                    "src/test/resources/ExampleRecord_valid1.txt",
                    "src/test/resources/Record.txt"
                )
            )
        } catch (e: IOException) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun writeRecordClass_valid2() {
        try {
            testedGenerator.writeRecordClass(expectedValidCols2, "src/test/resources/Record.txt")
            assertTrue(
                areFilesEqual(
                    "src/test/resources/ExampleRecord_valid2.txt",
                    "src/test/resources/Record.txt"
                )
            )
        } catch (e: IOException) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun writeFLPClass_valid1() {
        try {
            testedGenerator.writeFLPClass(expectedValidCols1, "src/test/resources/FixedLengthParser.txt")
            assertTrue(
                areFilesEqual(
                    "src/test/resources/ExampleFLP_valid1.txt",
                    "src/test/resources/FixedLengthParser.txt"
                )
            )
        } catch (e: IOException) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun writeFLPClass_valid2() {
        try {
            testedGenerator.writeFLPClass(expectedValidCols2, "src/test/resources/FixedLengthParser.txt")
            assertTrue(
                areFilesEqual(
                    "src/test/resources/ExampleFLP_valid2.txt",
                    "src/test/resources/FixedLengthParser.txt"
                )
            )
        } catch (e: IOException) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

    @Test
    fun generateRecordAndFLP_valid1() {
        try {
            testedGenerator.generateRecordAndFLP("src/test/resources/valid1.schema")
            assertTrue(
                areFilesEqual(
                    "src/test/resources/ExampleRecord_valid1.txt",
                    "src/main/java/org/example/Record.kt"
                )
            )
            assertTrue(
                areFilesEqual(
                    "src/test/resources/ExampleFLP_valid1.txt",
                    "src/main/java/org/example/FixedLengthParser.kt"
                )
            )
        } catch (e: Exception) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.message)
        }
    }

//    @Test
//    fun generateRecordAndFLP_valid2() {
//        try {
//            testedGenerator.generateRecordAndFLP("src/test/resources/valid2.schema")
//            assertTrue(
//                areFilesEqual(
//                    "src/test/resources/ExampleRecord_valid2.txt",
//                    "src/main/java/org/example/Record.kt"
//                )
//            )
//            assertTrue(
//                areFilesEqual(
//                    "src/test/resources/ExampleFLP_valid2.txt",
//                    "src/main/java/org/example/FixedLengthParser.kt"
//                )
//            )
//        } catch (e: Exception) {
//            // Fail the test if an exception is thrown
//            fail("Exception should not have been thrown: " + e.message)
//        }
//    }

    @Throws(IOException::class)
    private fun areFilesEqual(filePath1: String, filePath2: String): Boolean {
        val path1 = Paths.get(filePath1)
        val path2 = Paths.get(filePath2)
        val file1Bytes = Files.readAllBytes(path1)
        val file2Bytes = Files.readAllBytes(path2)

        // Compare the byte arrays
        return file1Bytes.contentEquals(file2Bytes)
    }
}