package org.example;

import org.example.exceptions.SchemaValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {
    Generator testedGenerator = new Generator();
    static List<Column> expectedValidCols1 = new ArrayList<>();
    static List<Column> expectedValidCols2 = new ArrayList<>();

    @BeforeAll
    public static void setUpBeforeClass() {
        Column col1 = new Column("name", 1, 20);
        Column col2 = new Column("gender", 20, 21);
        Column col3 = new Column("age", 22, 25);
        expectedValidCols1.add(col1);
        expectedValidCols1.add(col2);
        expectedValidCols1.add(col3);

        Column colA = new Column("balance", 1, 10);
        Column colB = new Column("date", 11, 20);
        expectedValidCols2.add(colA);
        expectedValidCols2.add(colB);
    }

    @Test
    void parseSchemaFile_valid1() {
        try {
            List<Column> actualCols = testedGenerator.parseSchemaFile("src/test/resources/valid1.schema");

            assertEquals(expectedValidCols1.toString(), actualCols.toString(), "parseSchemaFile_valid1 should return the expected columns.");
            assertEquals(3, actualCols.size(), "There should be 3 columns.");
        } catch (Exception e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void parseSchemaFile_valid2() {
        try {
            List<Column> actualCols = testedGenerator.parseSchemaFile("src/test/resources/valid2.schema");

            assertEquals(expectedValidCols2.toString(), actualCols.toString(), "parseSchemaFile_valid2 should return the expected columns.");
            assertEquals(2, actualCols.size(), "There should be 2 columns.");
        } catch (Exception e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void parseSchemaFile_random() {
        try {
            List<Column> actualCols = testedGenerator.parseSchemaFile("src/test/resources/random.schema");

            List<Column> expectedCols = new ArrayList<>();
            Column col1 = new Column("student_name", 1, 20);
            Column col2 = new Column("student1", 21, 22);
            Column col3 = new Column("student", 23, 24);
            Column col4 = new Column("abc", 25, 26);
            Column col5 = new Column("student$123", 27, 28);
            Column col6 = new Column("unknown", 29, 30);
            expectedCols.add(col1);
            expectedCols.add(col2);
            expectedCols.add(col3);
            expectedCols.add(col4);
            expectedCols.add(col5);
            expectedCols.add(col6);

            assertEquals(expectedCols.toString(), actualCols.toString(), "parseSchemaFile_random should return the expected columns.");
            assertEquals(6, actualCols.size(), "There should be 6 columns.");
        } catch (Exception e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void parseSchemaFile_extraspace() {
        try {
            List<Column> actualCols = testedGenerator.parseSchemaFile("src/test/resources/extraspace.schema");

            List<Column> expectedCols = new ArrayList<>();
            Column col1 = new Column("remainingBalance", 1, 5);
            Column col2 = new Column("date", 6, 16);
            Column col3 = new Column("dailyTransactionLimit", 17, 20);
            expectedCols.add(col1);
            expectedCols.add(col2);
            expectedCols.add(col3);

            assertEquals(expectedCols.toString(), actualCols.toString(), "parseSchemaFile_extraspace should return the expected columns.");
            assertEquals(3, actualCols.size(), "There should be 3 columns.");
        } catch (Exception e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void parseSchemaFile_number() {
        // Test that the correct exception is thrown
        SchemaValidationException thrown = assertThrows(SchemaValidationException.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/number.schema");
        });

        // Verify the exception message
        assertEquals("Schema is not properly defined. Please ensure the format of 'columnStr startInt endInt' for 'remaining balance       1.0   5'.", thrown.getMessage());
    }

    @Test
    void parseSchemaFile_contIndex() {
        // Test that the correct exception is thrown
        SchemaValidationException thrown = assertThrows(SchemaValidationException.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/contIndex.schema");
        });

        // Verify the exception message
        assertEquals("Invalid startIndex for 'today 4 8'. "
                + "Note that current startIndex must be greater than or equal to previous endIndex.", thrown.getMessage());
    }

    @Test
    void parseSchemaFile_index() {
        // Test that the correct exception is thrown
        SchemaValidationException thrown = assertThrows(SchemaValidationException.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/index.schema");
        });

        // Verify the exception message
        assertEquals("Invalid startIndex and/or endIndex for 'age 2 1'. "
                + "Note that endIndex must be greater than or equal to startIndex, and they should be positive integers.", thrown.getMessage());
    }

    @Test
    void parseSchemaFile_keyword() {
        // Test that the correct exception is thrown
        SchemaValidationException thrown = assertThrows(SchemaValidationException.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/keyword.schema");
        });

        // Verify the exception message
        assertEquals("Invalid columnName as 'int'. "
                + "Note that columnName should not be a reserved keyword in Java.", thrown.getMessage());
    }

    @Test
    void parseSchemaFile_duplicate() {
        // Test that the correct exception is thrown
        SchemaValidationException thrown = assertThrows(SchemaValidationException.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/duplicate.schema");
        });

        // Verify the exception message
        assertEquals("Invalid columnName as 'student'. "
                + "Note that there should not be duplicated columnName.", thrown.getMessage());
    }

    @Test
    void writeRecordClass_valid1() {
        try {
            testedGenerator.writeRecordClass(expectedValidCols1, "src/test/resources/Record.txt");
            assertTrue(areFilesEqual("src/test/resources/ExampleRecord_valid1.txt", "src/test/resources/Record.txt"));
        } catch (IOException e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void writeRecordClass_valid2() {
        try {
            testedGenerator.writeRecordClass(expectedValidCols2, "src/test/resources/Record.txt");
            assertTrue(areFilesEqual("src/test/resources/ExampleRecord_valid2.txt", "src/test/resources/Record.txt"));
        } catch (IOException e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void writeFLPClass_valid1() {
        try {
            testedGenerator.writeFLPClass(expectedValidCols1, "src/test/resources/FixedLengthParser.txt");
            assertTrue(areFilesEqual("src/test/resources/ExampleFLP_valid1.txt", "src/test/resources/FixedLengthParser.txt"));
        } catch (IOException e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void writeFLPClass_valid2() {
        try {
            testedGenerator.writeFLPClass(expectedValidCols2, "src/test/resources/FixedLengthParser.txt");
            assertTrue(areFilesEqual("src/test/resources/ExampleFLP_valid2.txt", "src/test/resources/FixedLengthParser.txt"));
        } catch (IOException e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    void generateRecordAndFLP_valid1() {
        try {
            testedGenerator.generateRecordAndFLP("src/test/resources/valid1.schema");
            assertTrue(areFilesEqual("src/test/resources/ExampleRecord_valid1.txt", "src/main/java/org/example/Record.java"));
            assertTrue(areFilesEqual("src/test/resources/ExampleFLP_valid1.txt", "src/main/java/org/example/FixedLengthParser.java"));
        } catch (Exception e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

//    @Test
//    void generateRecordAndFLP_valid2() {
//        try {
//            testedGenerator.generateRecordAndFLP("src/test/resources/valid2.schema");
//            assertTrue(areFilesEqual("src/test/resources/ExampleRecord_valid2.txt", "src/main/java/org/example/Record.java"));
//            assertTrue(areFilesEqual("src/test/resources/ExampleFLP_valid2.txt", "src/main/java/org/example/FixedLengthParser.java"));
//        } catch (Exception e) {
//            // Fail the test if an exception is thrown
//            fail("Exception should not have been thrown: " + e.getMessage());
//        }
//    }

    private boolean areFilesEqual(String filePath1, String filePath2) throws IOException {
        var path1 = Paths.get(filePath1);
        var path2 = Paths.get(filePath2);
        byte[] file1Bytes = Files.readAllBytes(path1);
        byte[] file2Bytes = Files.readAllBytes(path2);

        // Compare the byte arrays
        return Arrays.equals(file1Bytes, file2Bytes);
    }
}