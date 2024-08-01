package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FixedLengthParserTest {
    @Test
    void parseFile_valid1_data1() {
        parseFile_valid1("src/test/resources/valid1_data1.txt");
    }

    @Test
    void parseFile_valid1_data2() {
        parseFile_valid1("src/test/resources/valid1_data2.txt");
    }

    private void parseFile_valid1(String filePath) {
        try {
            Generator generator = new Generator();
            generator.generateRecordAndFLP("src/test/resources/valid1.schema");

            FixedLengthParser testedParser = new FixedLengthParser();

            List<Record> actualRecords = testedParser.parseFile(filePath);

            List<Record> expectedRecords = new ArrayList<>();
            Record rec1 = new Record("John Doe", "M", "25");
            Record rec2 = new Record("Jane Smith", "F", "30");
            expectedRecords.add(rec1);
            expectedRecords.add(rec2);

            assertEquals(expectedRecords.toString(), actualRecords.toString(), "parseFile_valid1_data1 should return the expected records.");
            assertEquals(2, actualRecords.size(), "There should be 2 records.");
        } catch (Exception e) {
            // Fail the test if an exception is thrown
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

//    @Test
//    void parseFile_valid2_data() {
//        try {
//            Generator generator = new Generator();
//            generator.generateRecordAndFLP("src/test/resources/valid2.schema");
//
//            FixedLengthParser testedParser = new FixedLengthParser();
//            List<Record> actualRecords = testedParser.parseFile("src/test/resources/valid2_data.txt");
//
//            List<Record> expectedRecords = new ArrayList<>();
//            Record rec1 = new Record("2500", "12-12-2012");
//            Record rec2 = new Record("100", "01-01-2024");
//            expectedRecords.add(rec1);
//            expectedRecords.add(rec2);
//
//            assertEquals(expectedRecords.toString(), actualRecords.toString(), "parseFile_valid2_data should return the expected records.");
//            assertEquals(2, actualRecords.size(), "There should be 2 records.");
//        } catch (Exception e) {
//            // Fail the test if an exception is thrown
//            fail("Exception should not have been thrown: " + e.getMessage());
//        }
//    }
}