package org.example

import org.apache.commons.text.CaseUtils
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.example.exceptions.SchemaValidationError
import org.example.exceptions.SchemaValidationException
import java.io.*

class Generator {
    /**
     * Parses a schema file and convert it into Column object(s).
     * Each line in the schema file should have the format of "<columnName> <startIndex> <endIndex>" where:
     * - columnName is a non-empty string
     * - followed by a space
     * - followed by startIndex which is a positive integer
     * - followed by a space
     * - followed by endIndex which is a positive integer > startIndex
     * The columnName should be a valid variable name in Kotlin and there should not be duplicated columnName in the schema file.
     *
     * An example of a valid schema file:
     * columnName1 0 1
     * columnName2 2 3
     *
     * @param filePath path to the schema file.
     * @return a list of Column objects, each consists (String)columnName, (int)startIndex, and (int)endIndex as specified in the schema file.
     * @throws IOException if an I/O error occurs while parsing the file.
     * @throws SchemaValidationException if the content in the schema file does not follow valid format.
     */
    @Throws(IOException::class, SchemaValidationException::class)
    fun parseSchemaFile(filePath: String): List<Column> {
        val cols = mutableListOf<Column>()

        File(filePath).bufferedReader().useLines { lines ->
            val names = mutableSetOf<String>()

            var end = -1
            var l = 0
            lines.forEach { line ->
                l++

                val schema = splitLine(line)
                if (schema.size != 3) {
                    throw SchemaValidationException(SchemaValidationError.INVALID_SCHEMA_FILE.getMessage(l, line))
                }

                val start: Int
                try {
                    start = schema[1].trim().toInt()
                    if (start < end) {
                        throw SchemaValidationException(SchemaValidationError.INVALID_START_INDEX.getMessage(l, line))
                    }
                    end = schema[2].trim().toInt()
                } catch (e: NumberFormatException) {
                    throw SchemaValidationException(SchemaValidationError.INVALID_SCHEMA_FILE.getMessage(l, line))
                }

                if (start > end || start < 0 || end < 0) {
                    throw SchemaValidationException(SchemaValidationError.INVALID_INDEX.getMessage(l, line))
                }

                var name = CaseUtils.toCamelCase(schema[0].trim(), false, ' ')
                //remove any special characters
                name = name.replace("[^a-zA-Z0-9_]".toRegex(), "")
                //remove any leading numbers
                name = name.replace("^\\d+".toRegex(), "")
                if (name.isBlank()) {
                    name = "unknown"
                    println(
                        "WARNING: '" + schema[0].trim() + "' is not a valid variable name in Kotlin so it is renamed to 'unknown'. "
                                + "Please provide a better columnName instead."
                    )
                }
                if (RESERVED_KEYWORDS.contains(name)) {
                    throw SchemaValidationException(SchemaValidationError.INVALID_COLUMN_NAME.getMessage(name, l, line))
                }

                if (names.contains(name)) {
                    throw SchemaValidationException(SchemaValidationError.DUPLICATE_COLUMN_NAME.getMessage(name, l, line))
                }
                names.add(name)

                val col = Column(name, start, end)
                cols.add(col)
                println(col)
            }
        }
        return cols
    }

    /**
     * Generates a Record class in Kotlin, with each Column being a member variable of the Record class.
     *
     * @param cols a list of Column objects.
     * @param dstPath path to the Kotlin file where the Record class will be written to.
     * @throws IOException if an I/O error occurs while writing the Record class to file.
     */
    @Throws(IOException::class)
    fun writeRecordClass(cols: List<Column>, dstPath: String) {
        Velocity.init()

        // Create a context and add data
        val context = VelocityContext()
        context.put("cols", cols)

        // Load the template
        val template = Velocity.getTemplate("src/main/resources/record.vm")

        // Merge the template with the context
        val writer = StringWriter()
        template.merge(context, writer)

        // Output the result
        writeToKotlinFile(writer.toString(), dstPath)
    }

    /**
     * Generates a FixedLengthParser class in Kotlin, which will be used to parse each line of a txt file according to the Column objects passed in
     * that return the corresponding Record object(s).
     *
     * @param cols a list of Column objects.
     * @param dstPath path to the Kotlin file where the FixedLengthParser class will be written to.
     * @throws IOException if an I/O error occurs while writing the FixedLengthParser class to file.
     */
    @Throws(IOException::class)
    fun writeFLPClass(cols: List<Column>, dstPath: String) {
        Velocity.init()

        // Create a context and add data
        val context = VelocityContext()
        context.put("cols", cols)
        context.put("lineEnd", cols.last().endIndex)

        // Load the template
        val template = Velocity.getTemplate("src/main/resources/fixedLengthParser.vm")

        // Merge the template with the context
        val writer = StringWriter()
        template.merge(context, writer)

        // Output the result
        writeToKotlinFile(writer.toString(), dstPath)
    }

    /**
     * A wrapper function that calls parseSchemaFile, writeRecordClass, and writeFLPClass,
     * which results in generating the corresponding Record.kt and FixedLengthParser.kt according to the schema file passed in.
     *
     * @param filePath path to the schema file.
     * @throws IOException if an I/O error occurs while parsing the schema file or writing the Record and FixedLengthParser classes to files.
     * @throws SchemaValidationException if the content in the schema file does not follow valid format.
     */
    @Throws(IOException::class, SchemaValidationException::class)
    fun generateRecordAndFLP(filePath: String) {
        val cols = parseSchemaFile(filePath)

        writeRecordClass(cols, "src/main/java/org/example/Record.kt")
        writeFLPClass(cols, "src/main/java/org/example/FixedLengthParser.kt")
    }

    /**
     * Splits a String into three to-be-sanitized tokens, where token 1 = columnName, token 2 = startIndex, token 3 = endIndex.
     * @param line a line in the schema file.
     * @return a list of String after splitting accordingly.
     */
    fun splitLine(line: String): List<String> {
        val regex = "(?<!\\s) (?=\\d+)".toRegex()
        return line.split(regex)
    }

    @Throws(IOException::class)
    private fun writeToKotlinFile(classString: String, filePath: String) {
        File(filePath).writeText(classString)
    }

    companion object {
        private val RESERVED_KEYWORDS: Set<String> = setOf(
            "as", "as?", "abstract", "annotation", "break", "class", "companion", "const",
            "continue", "data", "delegate", "do", "dynamic", "else", "enum", "expect",
            "external", "false", "final", "finally", "for", "fun", "if", "import", "in",
            "inline", "interface", "is", "lateinit", "mutable", "noinline", "null",
            "object", "open", "operator", "out", "override", "package", "private",
            "protected", "public", "reified", "return", "sealed", "super", "suspend",
            "tailrec", "throw", "true", "try", "typealias", "val", "var", "when", "while"
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val generator = Generator()
            try {
                generator.generateRecordAndFLP("src/main/resources/FT.schema")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}