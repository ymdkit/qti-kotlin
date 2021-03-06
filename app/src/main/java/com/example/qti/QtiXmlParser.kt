package com.example.qti

import com.example.qti.constant.QtiAttribute
import com.example.qti.constant.QtiTag
import com.example.qti.ext.moveTo
import com.example.qti.ext.skip
import com.example.qti.resource.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class QtiXmlParser {

    fun parse(inputStream: InputStream): QtiAssessmentItem {
        inputStream.use {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readQtiAssessmentItems(parser)
        }
    }

    private fun readQtiAssessmentItems(parser: XmlPullParser): QtiAssessmentItem {

        val builder = QtiAssessmentItemBuilder()

        parser.require(XmlPullParser.START_TAG, null, QtiTag.QTI_ASSESSMENT_ITEM)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                QtiTag.QTI_RESPONSE_DECLARATION -> builder.setResponse(
                    readQtiAssessmentItemResponse(
                        parser
                    )
                )
                QtiTag.QTI_OUTCOME_DECLARATION -> builder.setOutCome(
                    readQtiAssessmentItemOutcome(
                        parser
                    )
                )
                QtiTag.QTI_ITEM_BODY -> builder.setItemBody(
                    readQtiAssessmentItemBody(
                        parser
                    )
                )
                else -> parser.skip()
            }
        }
        return builder.build()
    }

    private fun readQtiAssessmentItemResponse(parser: XmlPullParser): QtiResponse {
        parser.require(XmlPullParser.START_TAG, null, QtiTag.QTI_RESPONSE_DECLARATION)
        val answers = arrayListOf<String>()

        parser.moveTo(listOf(QtiTag.QTI_VALUE))
        while (parser.name == QtiTag.QTI_VALUE) {
            answers.add(parser.nextText())
            parser.nextTag()
            parser.nextTag()
        }

        parser.require(XmlPullParser.END_TAG, null, QtiTag.QTI_RESPONSE_DECLARATION)
        return QtiResponse(answers.toList())
    }

    private fun readQtiAssessmentItemOutcome(parser: XmlPullParser): QtiOutcome {
        parser.require(XmlPullParser.START_TAG, null, QtiTag.QTI_OUTCOME_DECLARATION)

        parser.moveTo(listOf(QtiTag.QTI_VALUE))
        val value = parser.nextText().toFloat()
        parser.moveTo(listOf(QtiTag.QTI_OUTCOME_DECLARATION))

        parser.require(XmlPullParser.END_TAG, null, QtiTag.QTI_OUTCOME_DECLARATION)
        return QtiOutcome(value = value)
    }

    private fun readQtiAssessmentItemBody(parser: XmlPullParser): QtiItemBody {
        parser.require(XmlPullParser.START_TAG, null, QtiTag.QTI_ITEM_BODY)

        var itemBody = QtiItemBody()

        var prompt = ""
        parser.next()
        while (parser.name != QtiTag.QTI_CHOICE_INTERACTION) {
            parser.name?.let {
                if (it.isNotBlank()) {
                    prompt += "<${if (parser.eventType == XmlPullParser.END_TAG) "/" else ""}${it}>"
                }
            }
            parser.text?.let {
                if (it.isNotBlank()) {
                    prompt += it
                }
            }
            parser.next()
        }
        itemBody = itemBody.copy(prompt = QtiPrompt(prompt))

        val maxChoices = parser.getAttributeValue(null, QtiAttribute.MAX_CHOICES).toInt()
        val minChoices = parser.getAttributeValue(null, QtiAttribute.MIN_CHOICES).toInt()
        parser.nextTag()
        val choices = arrayListOf<Pair<String, String>>()
        while (parser.name == QtiTag.QTI_SIMPLE_CHOICE) {
            choices.add(
                parser.getAttributeValue(
                    null,
                    QtiAttribute.IDENTIFIER
                ) to parser.nextText()
            )
            parser.nextTag()
        }
        val interaction = SimpleChoiceQtiInteraction(
            maxChoices = maxChoices,
            minChoices = minChoices,
            choices = choices.toList()
        )
        itemBody = itemBody.copy(interaction = interaction)

        parser.nextTag()

        parser.require(XmlPullParser.END_TAG, null, QtiTag.QTI_ITEM_BODY)
        return itemBody
    }
}