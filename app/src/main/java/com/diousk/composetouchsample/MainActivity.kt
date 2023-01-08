package com.diousk.composetouchsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diousk.composetouchsample.ui.theme.ComposeTouchSampleTheme
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(DebugTree())
        setContent {
            ComposeTouchSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyScreen()
                }
            }
        }
    }
}

@Composable
fun MyScreen() {
    Column {
        var logs: List<String> by remember {
            mutableStateOf(emptyList())
        }
        TouchScreen {
            logs = logs.toMutableList().apply { add(it) }
        }

        Logs(logs)
    }
}

@Composable
fun TouchScreen(onLog: (String) -> Unit = {}) {
    var refCount by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .size(400.dp)
            .background(Color.Green),
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.Cyan)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    val currentContext = currentCoroutineContext()
                    awaitPointerEventScope {
                        while (currentContext.isActive) {
                            val initEvent = awaitPointerEvent(PointerEventPass.Initial)
                            if (initEvent.type == PointerEventType.Press) {
                                onLog("blue get Press event from INIT, isConsumed ${initEvent.changes.first().consumed.downChange}")
                            }

                            val mainEvent = awaitPointerEvent(PointerEventPass.Main)
                            if (mainEvent.type == PointerEventType.Press) {
                                onLog("blue get Press event from MAIN, isConsumed ${mainEvent.changes.first().consumed.downChange}")
                            }

                            val finalEvent = awaitPointerEvent(PointerEventPass.Final)
                            if (finalEvent.type == PointerEventType.Press) {
                                onLog("blue get Press event from FINAL, isConsumed ${finalEvent.changes.first().consumed.downChange}")
                            }
                        }
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.Yellow)
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        val currentContext = currentCoroutineContext()
                        awaitPointerEventScope {
                            while (currentContext.isActive) {
                                val initEvent = awaitPointerEvent(PointerEventPass.Initial)
                                if (initEvent.type == PointerEventType.Press) {
                                    onLog("yellow get Press event from INIT, isConsumed ${initEvent.changes.first().consumed.downChange}")
                                }

                                val event = awaitPointerEvent(PointerEventPass.Main)
                                if (event.type == PointerEventType.Press) {
                                    onLog("yellow get Press event from MAIN")
                                    event.changes.first().consumeDownChange()
                                    onLog("yellow consume down")
                                    refCount++
                                }

                                val finalEvent = awaitPointerEvent(PointerEventPass.Final)
                                if (finalEvent.type == PointerEventType.Press) {
                                    onLog("yellow get Press event from FINAL, isConsumed ${finalEvent.changes.first().consumed.downChange}")
                                }
                            }
                        }
                    }
            ) {
                Text(text = "ref: $refCount")
            }
        }
    }
}


@Composable
fun Logs(logs: List<String>) {
    Column {
        logs.forEach {
            Text(text = it)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Timber.plant(DebugTree())
    ComposeTouchSampleTheme {
        MyScreen()
    }
}