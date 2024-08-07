package org.example

class Column(val columnName: String, val startIndex: Int, val endIndex: Int) {
    /**
     * toString()
     */
    override fun toString(): String {
        return "Column(columnName='$columnName', startIndex=$startIndex, endIndex=$endIndex)"
    }
}