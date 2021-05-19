package com.example.qti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.qti.resource.QtiAssessmentItem
import com.example.qti.resource.SimpleChoiceQtiInteraction
import com.example.qti.ui.theme.QTITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val item = parse()

        setContent {
            QTITheme {
                Surface(color = MaterialTheme.colors.background) {
                    Quiz(item)
                }
            }
        }
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
        Text(text = item.itemBody.prompt.text)
        
        (item.itemBody.interaction as SimpleChoiceQtiInteraction).choices.map{
            Button(onClick = {
                println(it.first)
            },
                modifier = Modifier.fillMaxWidth().padding(0.dp,8.dp)
            ) {
                Text(it.second)
            }
        }

    }
}