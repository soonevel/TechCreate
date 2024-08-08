package org.example

class Record(private var name: String, private var gender: String, private var age: String) {
    override fun toString(): String {
        return "Record(name='$name', gender='$gender', age='$age')"
    }
}