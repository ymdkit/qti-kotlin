package com.example.qti.resource

data class QtiItemBody(
    val prompt: QtiPrompt = QtiPrompt(),
    val interaction: QtiInteraction = EmptyQtiInteraction
)