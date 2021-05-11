package com.example.qti

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ExampleUnitTest {
    @Test
    fun parseQti() {

        val parser = QtiXmlParser()
        val file = File("src/test/resources/test_qti.txt")
        val questions = parser.parse(FileInputStream(file))

        assertEquals(questions[0].title, "Question")
    }
}