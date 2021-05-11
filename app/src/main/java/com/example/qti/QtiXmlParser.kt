package com.example.qti

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.lang.Exception

class QtiXmlParser {

    fun parse(inputStream: InputStream): List<Quiz> {
        inputStream.use {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readQti(parser)
        }
    }

    private fun readQti(parser: XmlPullParser): List<Quiz> {
        val quizzes = mutableListOf<Quiz>()

        parser.require(XmlPullParser.START_TAG, null, "questestinterop")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "item") {
                quizzes.add(
                    readQuiz(parser)
                )
            } else if(parser.name == "assessment" || parser.name == "section") {
                continue
            } else {
                skip(parser)
            }
        }
        return quizzes
    }

    private fun readQuiz(parser: XmlPullParser): Quiz {
        val title = parser.getAttributeValue(null, "title") ?: throw Exception("title not found")
        return Quiz(title = title)
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