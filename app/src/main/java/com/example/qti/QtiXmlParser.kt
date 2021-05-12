package com.example.qti

import com.example.qti.resource.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.lang.Exception

class QtiXmlParser {

    fun parse(inputStream: InputStream): QtiAssessmentItem {
        inputStream.use {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readQtiAssessmentItems(parser)
        }
    }

    private fun readQtiAssessmentItems(parser: XmlPullParser): QtiAssessmentItem {

        val builder = QtiAssessmentItemBuilder()

        parser.require(XmlPullParser.START_TAG, null, "qti-assessment-item")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "qti-response-declaration" -> builder.setResponse(
                    readQtiAssessmentItemResponse(
                        parser
                    )
                )
                "qti-outcome-declaration" -> builder.setOutCome(
                    readQtiAssessmentItemOutcome(
                        parser
                    )
                )
                "qti-item-body" -> builder.setItemBody(
                    readQtiAssessmentItemBody(
                        parser
                    )
                )
                else -> skip(parser)
            }
        }
        return builder.build()
    }

    private fun readQtiAssessmentItemResponse(parser: XmlPullParser): QtiResponse {
        parser.require(XmlPullParser.START_TAG, null, "qti-response-declaration")
        val answers = arrayListOf<String>()

        parser.nextTag()
        parser.nextTag()

        while (parser.name == "qti-value") {
            answers.add(parser.nextText())
            parser.nextTag()
            parser.nextTag()
        }

        parser.require(XmlPullParser.END_TAG, null, "qti-response-declaration")
        return QtiResponse(answers.toList())
    }

    private fun readQtiAssessmentItemOutcome(parser: XmlPullParser): QtiOutcome {
        parser.require(XmlPullParser.START_TAG, null, "qti-outcome-declaration")
        var value = 0f
        parser.nextTag()
        parser.nextTag()

        if (parser.name == "qti-value") {
            value = parser.nextText().toFloat()
        }

        parser.nextTag()
        parser.nextTag()

        parser.require(XmlPullParser.END_TAG, null, "qti-outcome-declaration")
        return QtiOutcome(value = value)
    }

    private fun readQtiAssessmentItemBody(parser: XmlPullParser): QtiItemBody {
        parser.require(XmlPullParser.START_TAG, null, "qti-item-body")

        var itemBody = QtiItemBody()

        var prompt = ""
        parser.next()
        while (parser.name != "qti-choice-interaction") {
            parser.name?.let {
                if(it.isNotBlank()){
                    prompt += "<${if (parser.eventType == XmlPullParser.END_TAG) "/" else ""}${it}>"
                }
            }
            parser.text?.let {
                if(it.isNotBlank()){
                    prompt += it
                }
            }
            parser.next()
        }
        itemBody = itemBody.copy(prompt = QtiPrompt(prompt))

        val maxChoices = parser.getAttributeValue(null, "max-choices").toInt()
        val minChoices = parser.getAttributeValue(null, "min-choices").toInt()
        parser.nextTag()
        val choices = arrayListOf<Pair<String, String>>()
        while (parser.name == "qti-simple-choice") {
            choices.add(parser.getAttributeValue(null, "identifier") to parser.nextText())
            parser.nextTag()
        }
        val interaction = SimpleChoiceQtiInteraction(
            maxChoices = maxChoices,
            minChoices = minChoices,
            choices = choices.toList()
        )
        itemBody = itemBody.copy(interaction = interaction)

        parser.nextTag()

        parser.require(XmlPullParser.END_TAG, null, "qti-item-body")
        return itemBody
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}