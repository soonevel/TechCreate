package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {
    Generator testedGenerator = new Generator();
    static List<Column> expectedValidCols = new ArrayList<>();

    @BeforeAll
    public static void setUpBeforeClass() {
        Column col1 = new Column("name", 1, 20);
        Column col2 = new Column("gender", 20, 21);
        Column col3 = new Column("age", 22, 25);
        expectedValidCols.add(col1);
        expectedValidCols.add(col2);
        expectedValidCols.add(col3);
    }

    @Test
    void parseSchemaFile_valid() {
        try {
            List<Column> actualCols = testedGenerator.parseSchemaFile("src/test/resources/valid.schema");

            assertEquals(expectedValidCols.toString(), actualCols.toString(), "parseSchemaFile_valid should return the expected columns.");
            assertEquals(3, actualCols.size(), "There should be 3 columns.");
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
    void parseSchemaFile_duplicate() {
        // Test that the correct exception is thrown
        Exception thrown = assertThrows(Exception.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/duplicate.schema");
        });

        // Verify the exception message
        assertEquals("Duplicated columns in schema are not allowed.", thrown.getMessage());
    }

    @Test
    void parseSchemaFile_keyword() {
        // Test that the correct exception is thrown
        Exception thrown = assertThrows(Exception.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/keyword.schema");
        });

        // Verify the exception message
        assertEquals("Column should not be a reserved keyword.", thrown.getMessage());
    }

    @Test
    void parseSchemaFile_extraspace() {
        // Test that the correct exception is thrown
        Exception thrown = assertThrows(Exception.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/extraspace.schema");
        });

        // Verify the exception message
        assertEquals("Schema is not properly defined. Please separate columnName startIndex endIndex with a space.", thrown.getMessage());
    }

    @Test
    void parseSchemaFile_index() {
        // Test that the correct exception is thrown
        Exception thrown = assertThrows(Exception.class, () -> {
            testedGenerator.parseSchemaFile("src/test/resources/index.schema");
        });

        // Verify the exception message
        assertEquals("Invalid startIndex and/or endIndex.", thrown.getMessage());
    }

    @Test
    void writeRecordClassInString() {
    }

    @Test
    void writeFLPClassInString() {
    }

    @Test
    void main() {
    }
}