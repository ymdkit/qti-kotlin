package com.example.qti.resource

data class SimpleChoiceQtiInteraction(
    val choices: List<Pair<String, String>>,
    val minChoices: Int,
    val maxChoices: Int,
): QtiInteraction