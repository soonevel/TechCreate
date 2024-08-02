package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.text.CaseUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import org.example.exceptions.SchemaValidationException;

public class Generator {
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while"));

    public List<Column> parseSchemaFile(String filePath) throws IOException, SchemaValidationException {
        List<Column> cols = new ArrayList<>();
        List<String> names = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int end = -1;

            while ((line = reader.readLine()) != null) {
                var preSchema = line.splitWithDelimiters("\\s+\\d+", -1);
                List<String> postSchema = new ArrayList<>();
                for (String s : preSchema) {
                    if (s.isBlank()) {
                        continue;
                    }
                    postSchema.add(s.trim());
                }

                if (postSchema.size() != 3) {
                    throw new SchemaValidationException("Schema is not properly defined. "
                            + "Please ensure the format of 'columnStr startInt endInt' for '" + line + "'.");
                }

                int start = Integer.parseInt(postSchema.get(1));
                if (start < end) {
                    throw new SchemaValidationException("Invalid startIndex for '" + line + "'. "
                            + "Note that current startIndex must be greater than or equal to previous endIndex.");
                }
                end = Integer.parseInt(postSchema.get(2));

                if (start > end || start < 0 || end < 0) {
                    throw new SchemaValidationException("Invalid startIndex and/or endIndex for '" + line + "'. "
                            + "Note that endIndex must be greater than or equal to startIndex, and they should be positive integers.");
                }

                String name = CaseUtils.toCamelCase(postSchema.get(0), false, ' ');
                //remove any special characters
                name = name.replaceAll("[^a-zA-Z0-9_$]", "");
                //remove any leading numbers
                name = name.replaceAll("^\\d+", "");
                if (name.isBlank()) {
                    name = "unknown";
                }
                if (RESERVED_KEYWORDS.contains(name)) {
                    throw new SchemaValidationException("Invalid columnName as '" + name + "'. "
                            + "Note that columnName should not be a reserved keyword in Java.");
                }

                if (names.contains(name)) {
                    throw new SchemaValidationException("Invalid columnName as '" + name + "'. "
                            + "Note that there should not be duplicated columnName.");
                }
                names.add(name);

                Column col = new Column(name, start, end);
                cols.add(col);

                System.out.println(col);
            }
        }
        return cols;
    }

    public void writeRecordClass(List<Column> cols, String dstPath) throws IOException {
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
        writeToJavaFile(writer.toString(), dstPath);
    }

    public void writeFLPClass(List<Column> cols, String dstPath) throws IOException {
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
        writeToJavaFile(writer.toString(), dstPath);
    }

    public void generateRecordAndFLP(String filePath) throws IOException, SchemaValidationException {
        List<Column> cols = parseSchemaFile(filePath);

        writeRecordClass(cols, "src/main/java/org/example/Record.java");
        writeFLPClass(cols, "src/main/java/org/example/FixedLengthParser.java");
    }

    private void writeToJavaFile(String classString, String filePath) throws IOException {
        var path = Paths.get(filePath);
        Files.write(path, classString.getBytes());
    }

    public static void main(String[] args) {
        Generator generator = new Generator();
        try {
            generator.generateRecordAndFLP("src/main/resources/FT.schema");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}