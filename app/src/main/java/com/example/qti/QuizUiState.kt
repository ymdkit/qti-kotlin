package com.example.qti

import com.example.qti.resource.QtiAssessmentItem

sealed class QuizUiState {
    object Initial : QuizUiState()
    data class Processing(val quiz: QtiAssessmentItem) : QuizUiState()
    data class Result(val quiz: QtiAssessmentItem,val isCorrect: Boolean) : QuizUiState()
    object Finish : QuizUiState()
}