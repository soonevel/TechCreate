### Interview Question: Fixed-Length Column Parser

You are given a file with fixed-length columns. The schema describing the fields in the file is as follows:

```
name 1 20
gender 20 21
age 22 25
```

Each record in the file follows this schema, where:
- The `name` field starts at position 1 and ends at position 20 (inclusive).
- The `gender` field starts at position 20 and ends at position 21.
- The `age` field starts at position 22 and ends at position 25.

#### Task:
1. **Write a Java class `FixedLengthParser`** that takes a file and parses it based on the schema provided.

2. **Define a `Record` class** with fields for `name`, `gender`, and `age`, and ensure that the `FixedLengthParser` correctly populates an instance of this `Record` class for each line in the file.

3. **Implement the following functionality:**
    - Read the file line by line.
    - Extract values for `name`, `gender`, and `age` based on the given positions.
    - Create `Record` instances with the extracted values.
    - Print out the records for verification.

#### Example:

Given the following content in a file:

```
John Doe           M  25
Jane Smith         F  30
```

The parser should produce:

```
Record{name='John Doe           ', gender='M', age='25'}
Record{name='Jane Smith         ', gender='F', age='30'}
```

### Instructions:
- **Create the `FixedLengthParser` class** with a method `parseFile(String filePath)` that returns a list of `Record` objects.
- **Create the `Record` class** with appropriate fields, constructors, and `toString()` method.
- **Consider edge cases**, such as fields with unexpected lengths or missing values.

### Follow-Up Questions:
1. How would you handle cases where the file format might not strictly adhere to the schema (e.g., fields might be shorter or longer than expected)?
2. How would you extend the parser to handle different schemas dynamically?
3. How would you ensure the parser is efficient and can handle large files?

---

This question tests the candidate's ability to work with file I/O, string manipulation, and object-oriented design in Java. It also evaluates their problem-solving skills related to parsing and handling fixed-length data.

## Sample Solution
Certainly! Below is a Java solution for the problem. This includes a `FixedLengthParser` class to parse the file and a `Record` class to represent the parsed data.

### `Record` Class

```java
public class Record {
    private String name;
    private String gender;
    private String age;

    public Record(String name, String gender, String age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Record{name='" + name + "', gender='" + gender + "', age='" + age + "'}";
    }
    
    // Getters and Setters (if needed)
}
```

### `FixedLengthParser` Class

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FixedLengthParser {

    // Schema configuration
    private static final int NAME_START = 1;
    private static final int NAME_END = 20;
    private static final int GENDER_START = 20;
    private static final int GENDER_END = 21;
    private static final int AGE_START = 22;
    private static final int AGE_END = 25;

    public List<Record> parseFile(String filePath) throws IOException {
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() < AGE_END) {
                    // Handle lines that are shorter than expected
                    continue;
                }
                
                // Extract fields based on fixed positions
                String name = extractField(line, NAME_START, NAME_END).trim();
                String gender = extractField(line, GENDER_START, GENDER_END).trim();
                String age = extractField(line, AGE_START, AGE_END).trim();
                
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

    public static void main(String[] args) {
        FixedLengthParser parser = new FixedLengthParser();
        try {
            List<Record> records = parser.parseFile("path/to/your/file.txt");
            for (Record record : records) {
                System.out.println(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Explanation:

1. **`Record` Class:**
    - Holds the fields `name`, `gender`, and `age`.
    - Provides a `toString()` method for easy display of the record.

2. **`FixedLengthParser` Class:**
    - **Schema Configuration:** Defines constants for the start and end positions of each field.
    - **`parseFile(String filePath)`:** Reads the file line by line, extracts the fields using the fixed positions, and creates `Record` instances.
    - **`extractField(String line, int start, int end)`:** Helper method to extract a substring based on the given start and end positions.
    - **`main(String[] args)`:** Example main method to demonstrate how to use the parser. Adjust the file path accordingly.

This code assumes that the file follows the schema closely. If more robust error handling is required (for example, dealing with unexpected line lengths or missing fields), you can add additional checks and validations as needed.