package org.example.exceptions

/**
 * Exception thrown when a user provides invalid schema file.
 * This exception is used to indicate that the content in the schema file provided does not meet the required criteria or format,
 * and further action is needed to handle or correct the schema file.
 */
class SchemaValidationException(message: String) : Exception(message)