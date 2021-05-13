package com.example.qti.ext

import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.skip() {
    if (eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}

fun XmlPullParser.moveTo(destinations: List<String>) {
    while (!destinations.contains(name)) nextTag()
}