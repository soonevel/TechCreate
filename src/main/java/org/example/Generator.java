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

    /**
     * Parses a schema file and convert it into Column object(s).
     * Each line in the schema file should have the format of "<columnName> <startIndex> <endIndex>" where:
     * - columnName is a non-empty string
     * - followed by a space
     * - followed by startIndex which is a positive integer
     * - followed by a space
     * - followed by endIndex which is a positive integer > startIndex
     * The columnName should be a valid variable name in Java and there should not be duplicated columnName in the schema file.
     *
     * An example of a valid schema file:
     *  columnName1 0 1
     *  columnName2 2 3
     *
     * @param filePath path to the schema file.
     * @return a list of Column objects, each consists (String)columnName, (int)startIndex, and (int)endIndex as specified in the schema file.
     * @throws IOException if an I/O error occurs while parsing the file.
     * @throws SchemaValidationException if the content in the schema file does not follow valid format.
     */
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

    /**
     * Generates a Record class in Java, with each Column being a member variable of the Record class.
     *
     * @param cols a list of Column objects.
     * @param dstPath path to the Java file where the Record class will be written to.
     * @throws IOException if an I/O error occurs while writing the Record class to file.
     */
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

    /**
     * Generates a FixedLengthParser class in Java, which will be used to parse each line of a txt file according to the Column objects passed in
     * that return the corresponding Record object(s).
     *
     * @param cols a list of Column objects.
     * @param dstPath path to the Java file where the FixedLengthParser class will be written to.
     * @throws IOException if an I/O error occurs while writing the FixedLengthParser class to file.
     */
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


    /**
     * A wrapper function that calls parseSchemaFile, writeRecordClass, and writeFLPClass,
     * which results in generating the corresponding Record.java and FixedLengthParser.java according to the schema file passed in.
     *
     * @param filePath path to the schema file.
     * @throws IOException if an I/O error occurs while parsing the schema file or writing the Record and FixedLengthParser classes to files.
     * @throws SchemaValidationException if the content in the schema file does not follow valid format.
     */
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