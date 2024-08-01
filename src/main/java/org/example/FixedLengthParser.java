package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FixedLengthParser {
    public List<Record> parseFile(String filePath) throws IOException {
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 25) {
                    // Handle lines with unexpected length
                    System.out.println("Data '" + line + "' is not parsed as it does not follow the schema.");
                    continue;
                }

                // Extract fields based on fixed positions
                String name = extractField(line, 1, 20).trim();
                String gender = extractField(line, 20, 21).trim();
                String age = extractField(line, 22, 25).trim();

                // Create a new Record object
                Record record = new Record(name, gender, age);
                records.add(record);
            }
        }
        return records;
    }

    private String extractField(String line, int start, int end) {
        return line.substring(start - 1, end);
    }
}