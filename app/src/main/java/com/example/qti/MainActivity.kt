package com.example.qti

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.qti.resource.QtiAssessmentItem
import com.example.qti.resource.SimpleChoiceQtiInteraction
import com.example.qti.ui.theme.QTITheme

private val LocalQuizViewModelContext = staticCompositionLocalOf<QuizViewModel> {
    error("error")
}

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<QuizViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state = viewModel.uiState.collectAsState().value

            QTITheme {
                Surface(color = MaterialTheme.colors.background) {
                    CompositionLocalProvider(LocalQuizViewModelContext provides viewModel) {
                        when (state) {
                            is QuizUiState.Initial -> {
                            }
                            is QuizUiState.Processing -> {
                                Quiz(state.quiz)
                            }
                            is QuizUiState.Result -> {
                                QtiResult(state.isCorrect)
                            }
                            is QuizUiState.Finish -> {
                                Finish()
                            }
                        }
                    }
                }
            }
        }

        viewModel.quizzes = listOf(parse(), parse(), parse())
        viewModel.loadNextQuiz()
    }

    fun parse(): QtiAssessmentItem {
        val parser = QtiXmlParser()
        return parser.parse(assets.open("test_qti.xml"))
    }
}

@Composable
fun Quiz(item: QtiAssessmentItem) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = {
                WebView(it).apply {
                    loadData(item.itemBody.prompt.text, "text/html", "UTF-8")
                }
            }
        )

        SimpleChoiceQtiInteraction(
            item = item
        )
    }
}

@Composable
fun SimpleChoiceQtiInteraction(item: QtiAssessmentItem) {
    val viewModel: QuizViewModel = LocalQuizViewModelContext.current
    (item.itemBody.interaction as SimpleChoiceQtiInteraction).choices.map {
        Button(
            onClick = {
                viewModel.onReceiveAnswer(item, it.first)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
        ) {
            Text(it.second)
        }
    }
}

@Composable
fun QtiResult(isCorrect: Boolean) {
    val viewModel: QuizViewModel = LocalQuizViewModelContext.current
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = if (isCorrect) "Correct" else "Incorrect")
        Button(
            onClick = {
                viewModel.loadNextQuiz()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
        ) {
            Text(text = "Next")
        }
    }
}

@Composable
fun Finish() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "finish!!", modifier = Modifier.fillMaxWidth())

    }

}
