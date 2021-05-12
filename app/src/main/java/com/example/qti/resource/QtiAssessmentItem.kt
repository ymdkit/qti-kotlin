package com.example.qti.resource

data class QtiAssessmentItem(
    val response: QtiResponse,
    val outcome: QtiOutcome,
    val itemBody: QtiItemBody,
)