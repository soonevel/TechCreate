package org.example;

public class Column {
    private String columnName;
    private int startIndex;
    private int endIndex;

    /**
     * Constructor
     */
    public Column(String columnName, int startIndex, int endIndex) {
        this.columnName = columnName;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * Getters and Setters
     */
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    /**
     * toString()
     */
    @Override
    public String toString() {
        return "Column{" +
                "columnName='" + columnName + '\'' +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}