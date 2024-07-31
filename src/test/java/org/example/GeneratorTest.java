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
    void writeRecordClassInString() {
    }

    @Test
    void writeFLPClassInString() {
    }

    @Test
    void main() {
    }
}