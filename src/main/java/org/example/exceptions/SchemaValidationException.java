package org.example.exceptions;

/**
 * Exception thrown when a user provides invalid schema file.
 * <p>
 * This exception is used to indicate that the content in the schema file provided does not meet the required criteria or format
 * and further action is needed to handle or correct the schema file.
 * </p>
 */
public class SchemaValidationException extends Exception {
    public SchemaValidationException(String message) {
        super(message);
    }

    public SchemaValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}