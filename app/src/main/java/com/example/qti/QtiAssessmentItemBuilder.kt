package com.example.qti

import com.example.qti.resource.QtiAssessmentItem
import com.example.qti.resource.QtiItemBody
import com.example.qti.resource.QtiOutcome
import com.example.qti.resource.QtiResponse

class QtiAssessmentItemBuilder {
    private var response = QtiResponse()
    private var itemBody = QtiItemBody()
    private var outcome = QtiOutcome()

    fun setOutCome(oc: QtiOutcome) {
        outcome = oc
    }

    fun setItemBody(ib: QtiItemBody) {
        itemBody = ib
    }

    fun setResponse(res: QtiResponse) {
        response = res
    }

    fun build() = QtiAssessmentItem(
        response = response,
        itemBody = itemBody,
        outcome = outcome
    )
}