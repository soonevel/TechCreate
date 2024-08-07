package org.example

class Record(private val name: String, private val gender: String, private val age: String) {
    override fun toString(): String {
        return "Record {name='$name', gender='$gender', age='$age'}"
    }
}