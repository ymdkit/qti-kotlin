package com.example.qti

import com.example.qti.resource.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ExampleUnitTest {
    @Test
    fun parseQti() {

        val parser = QtiXmlParser()
        val file = File("src/test/resources/test_qti.xml")
        val actual = parser.parse(FileInputStream(file))

        val expect =
            QtiAssessmentItem(
                response = QtiResponse(listOf("A")),
                outcome = QtiOutcome(1f),
                itemBody = QtiItemBody(
                    prompt = QtiPrompt("<p>Of the following hormones, which is produced by the adrenal glands?</p>"),
                    interaction = SimpleChoiceQtiInteraction(
                        choices = listOf(
                            "A" to "Epinephrine",
                            "B" to "Glucagon",
                            "C" to "Insulin",
                            "D" to "Oxytocin",
                        ),
                        maxChoices = 1,
                        minChoices = 1
                    )
                )
            )

        assertEquals(expect.response, actual.response)
        assertEquals(expect.outcome, actual.outcome)
        assertEquals(expect.itemBody, actual.itemBody)
    }
}