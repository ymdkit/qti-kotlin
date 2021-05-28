package com.example.qti

import androidx.lifecycle.ViewModel
import com.example.qti.resource.QtiAssessmentItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuizViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Initial)
    val uiState: StateFlow<QuizUiState> = _uiState

    var index = -1
    var quizzes = emptyList<QtiAssessmentItem>()

    fun onReceiveAnswer(item: QtiAssessmentItem, response: String ){
        val isCorrect = item.response.answers[0] == response
        _uiState.value = QuizUiState.Result(item, isCorrect)
    }

    fun setQuiz(quiz: QtiAssessmentItem){
        _uiState.value = QuizUiState.Processing(quiz)
    }

    fun loadNextQuiz(){
        index++
        if(quizzes.size > index){
            setQuiz(quizzes[index])
        }else{
            _uiState.value = QuizUiState.Finish
        }
    }
}