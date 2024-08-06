package org.example.exceptions;

/**
 * Enumeration of schema validation errors, each associated with a specific error message template.
 * These errors are used to indicate various issues that can occur during the validation of schema files.
 */
public enum SchemaValidationError {
    INVALID_START_INDEX("Invalid startIndex for line %d: '%s'. Note that current startIndex must be greater than or equal to previous endIndex."),
    INVALID_SCHEMA_FILE("Invalid schema format for line %d: '%s'. Please ensure the format of 'columnStr startInt endInt'."),
    INVALID_INDEX("Invalid startIndex and/or endIndex for line %d: '%s'. Note that endIndex must be greater than or equal to startIndex, and they should be positive integers."),
    INVALID_COLUMN_NAME("Invalid columnName as '%s' for line %d: '%s'. Note that columnName should not be a reserved keyword in Java."),
    DUPLICATE_COLUMN_NAME("Invalid columnName as '%s' for line %d: '%s'. Note that there should not be duplicated columnName.");

    private final String messageTemplate;

    /**
     * Constructor
     */
    SchemaValidationError(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    /**
     * Methods to get the formatted message with the %variables replaced
     */
    public String getMessage(int lineNo, String lineContent) {
        return String.format(messageTemplate, lineNo, lineContent);
    }

    public String getMessage(String col, int lineNo, String lineContent) {
        return String.format(messageTemplate, col, lineNo, lineContent);
    }
}
