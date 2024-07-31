package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;

public class Generator {
    public List<Column> parseSchemaFile(String filePath) throws Exception {
        List<Column> cols = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                var schema = line.split(" ", -1);

                if (schema.length != 3) {
                    throw new Exception("Schema is not properly defined.");
                }

                //I wonder if need to check when the previous end index is larger than current start index?
                int start = Integer.parseInt(schema[1]);
                int end = Integer.parseInt(schema[2]);

                if (start > end) {
                    throw new Exception("Cannot parse when end index is greater than start index.");
                }

                Column col = new Column(schema[0], start, end);
                cols.add(col);

                System.out.println(col);
            }
        }
        return cols;
    }

    public String writeRecordClassInString(List<Column> cols) {
        Velocity.init();

        // Create a context and add data
        VelocityContext context = new VelocityContext();
        context.put("cols", cols);

        // Load the template
        Template template = Velocity.getTemplate("src/main/resources/record.vm");

        // Merge the template with the context
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Output the result
        String recordString = writer.toString();
        System.out.println(recordString);

        return recordString;
    }

    public String writeFLPClassInString(List<Column> cols) {
        Velocity.init();

        // Create a context and add data
        VelocityContext context = new VelocityContext();
        context.put("cols", cols);
        int lineEnd = cols.getLast().getEndIndex();
        context.put("lineEnd", lineEnd);

        // Load the template
        Template template = Velocity.getTemplate("src/main/resources/fixedLengthParser.vm");

        // Merge the template with the context
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Output the result
        String recordString = writer.toString();
        System.out.println(recordString);

        return recordString;
    }

    public void writeToJavaFile(String classString, String filePath) throws IOException {
        var path = Paths.get(filePath);
        Files.write(path, classString.getBytes());
    }

    public static void main(String[] args) {
        Generator generator = new Generator();
        try {
            List<Column> cols = generator.parseSchemaFile("src/main/resources/FT.schema");

            String recordClass = generator.writeRecordClassInString(cols);
            String FLPClass = generator.writeFLPClassInString(cols);

            generator.writeToJavaFile(recordClass, "src/main/java/org/example/Record.java");
            generator.writeToJavaFile(FLPClass, "src/main/java/org/example/FixedLengthParser.java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}