package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class Column {
    public String column_name;
    public int start_i;
    public int end_i;

    @Override
    public String toString() {
        return "Column{" +
                "column_name='" + column_name + '\'' +
                ", start_i=" + start_i +
                ", end_i=" + end_i +
                '}';
    }
}

public class Generator {
    public List<Column> parseSchemaFile(String filePath) throws Exception {
        List<Column> cols = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int i = -1;

            while ((line = reader.readLine()) != null) {
                var schema = line.split(" ", -1);

                if (schema.length != 3) {
                    throw new Exception("Schema is not properly defined.");
                }

                Column col = new Column();
                col.column_name = schema[0];
                col.start_i = Integer.parseInt(schema[1]);

                col.end_i = Integer.parseInt(schema[2]);

                cols.add(col);

                System.out.println(col);
            }
        }
        return cols;
    }

    public List<Record> parseFile(String filePath) throws IOException {
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<Column> cols = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                var schema = line.split(" ", -1);

                Column col = new Column();
                col.column_name = schema[0];
                col.start_i = Integer.valueOf(schema[1]);
                col.end_i = Integer.valueOf(schema[2]);

                cols.add(col);

                System.out.println(col);
            }
            String className = "Record";

            String classBlueprint = """
                    public class
                    """ + className + "{";

            for (var col : cols) {
                classBlueprint += "private String " + col.column_name + ";";
            }

            classBlueprint += "public Record(";


            for (var col : cols) {
                classBlueprint += "String " + col.column_name + ", ";
            }

            classBlueprint = classBlueprint.substring(0, classBlueprint.length() - 2);


            classBlueprint += ") {";

            for (var col : cols) {
                classBlueprint += "this." + col.column_name + " = " + col.column_name + ";";
            }
            classBlueprint += "}}";
            System.out.println(classBlueprint);

            var path = Paths.get("src/main/java/org/example/Record.java");
            Files.write(path, classBlueprint.getBytes());
        }
        return records;
    }

    private String extractField(String line, int start, int end) {
        return line.substring(start - 1, end);
    }

    public static void main(String[] args) {
        Generator parser = new Generator();
        try {
            List<Record> records = parser.parseFile("schema/FT.schema");
//            for (Record record : records) {
//                System.out.println(record);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}