package com.example.uartinterface.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

const val OPEN: Int = 1
const val CLOSE: Int = 0
const val TEST: Int = 2

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@SuppressLint("MissingPermission", "ProduceStateDoesNotAssignValue")
@Composable
fun SerialPort_Composable(
    messages: List<String>,
    Connected: Boolean,
    len: Int,
    err: Int,
    onConnClickListener: (Int) -> Unit,
    onSendClicked: (String) -> Unit,
    onDelayChanged: (String) -> Unit
)
{

    var isConnected by remember { mutableStateOf(false) }           // true = connection uart opened; false = connection uart closed;
    var messagesList by remember { mutableStateOf<List<String>?>(mutableListOf()) }
    var isBtnOpenEnabled by remember { mutableStateOf(true) }
    var isBtnCloseEnabled by remember { mutableStateOf(false) }
    var isBtnSendEnabled by remember { mutableStateOf(false) }
    var inputMsgValue by remember { mutableStateOf("") }
    var delayMillis by remember { mutableStateOf("10") }
    var msgCount by remember { mutableStateOf(0) }
    var errCount by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Connected) {
        isConnected = Connected
        if(Connected) {
            isBtnOpenEnabled = false
            isBtnCloseEnabled = true
            isBtnSendEnabled = true
        } else {
            isBtnOpenEnabled = true
            isBtnCloseEnabled = false
            isBtnSendEnabled = false
        }
    }

    LaunchedEffect(messages) {
        messagesList = messages
    }

    LaunchedEffect(len) {
        msgCount = len
    }

    LaunchedEffect(err) {
        errCount = err
    }

    // to scroll to the last item of the lazyColumn
    DisposableEffect(messagesList) {
        if (messagesList?.isNotEmpty() == true) {
            val lastIndex = messagesList!!.size - 1
            coroutineScope.launch {
                listState.animateScrollToItem(index = lastIndex)
            }
        }
        onDispose { }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black,
                    ),
                    title = {},
                    actions = {
                        Text(text = "Err: ")
                        Text(text = errCount.toString())
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = "Msg: ")
                        Text(text = msgCount.toString())
                        TextField(
                            value = delayMillis,
                            onValueChange = { newText ->
                                if (newText.isEmpty()) {
                                    delayMillis = "0"
                                } else {
                                    val parsedValue = newText.toIntOrNull()
                                    if (parsedValue != null && parsedValue > 0) {
                                        delayMillis = if (delayMillis == "0") {
                                            // remove 0
                                            newText.substring(1)
                                        } else {
                                            newText
                                        }
                                        onDelayChanged(delayMillis)
                                    }
                                }
                            },
                            enabled = isBtnOpenEnabled,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp),
                            label = { Text("read millis") }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = { onConnClickListener(OPEN) },
                            enabled = isBtnOpenEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("OPEN")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(
                            onClick = { onConnClickListener(CLOSE) },
                            enabled = isBtnCloseEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("CLOSE")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        )
        { innerPadding ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .background(Color.White)
            )
            {
                Divider(
                    color = Color.Black,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(space = 5.dp),   // gap between items
                contentPadding = PaddingValues(all = 5.dp),
                state = listState
                ) {
                    if(isConnected) {
                        items(messagesList.orEmpty()) {
                            MsgItem(msg = it)
                        }
                    } else {
                        messagesList = emptyList()
                        inputMsgValue = ""
                    }
                }
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Campo di input
                    TextField(
                        value = inputMsgValue,
                        onValueChange = { newText ->
                            inputMsgValue = newText
                        },
                        enabled = isBtnSendEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        label = { Text("Write message") }
                    )
                    Button(
                        onClick = {
                            onSendClicked(inputMsgValue)
                            inputMsgValue = ""
                        },
                        modifier = Modifier.padding(start = 8.dp),
                        enabled = isBtnSendEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
}

@Composable
private fun MsgItem(msg: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = msg)
        }
    }
}