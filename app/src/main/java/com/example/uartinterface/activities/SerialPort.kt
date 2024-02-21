package com.example.uartinterface.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uartinterface.composable.CLOSE
import com.example.uartinterface.composable.OPEN
import com.example.uartinterface.composable.SerialPort_Composable
import com.example.uartinterface.composable.TEST
import com.example.uartinterface.controls.SerialPortController
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject

var i: Int = 0

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@SuppressLint("MutableCollectionMutableState")
@ExperimentalMaterial3Api
class SerialPort: AppCompatActivity() {

    private val serialPortController: SerialPortController = SerialPortController.getInstance()

    private var isConnected by mutableStateOf(false)
    private var uartMessages by mutableStateOf(mutableListOf<String>())
    private var len by mutableStateOf(0)
    private var errors by mutableStateOf(0)
    private var stopLoop by mutableStateOf(false)       //TODO: remove this
    var delaymil: Long = 10

    var expectedValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SerialPort_Composable(
                messages = uartMessages,
                Connected = isConnected,
                len = len,
                err = errors,
                onConnClickListener = { buttonId ->
                    when (buttonId) {
                        OPEN -> {
                            isConnected = serialPortController.startConnection { newMessage, size ->
                                var str = processReceivedMessage(newMessage)
                                uartMessages = uartMessages.toMutableList().apply { add(str + " ${newMessage.length}") }
                                // uartMessages = uartMessages.toMutableList().apply { add(newMessage+" ${newMessage.length}") }
                                len += newMessage.length
                                //TODO: remove this
                                /*        stopLoop = false
                                if(uartMessages.toMutableList().size == 1){
                                    GlobalScope.launch(Dispatchers.IO) {
                                        for (i in 0 until 1500) {
                                            if (stopLoop) break
                                            val inputMsg: String = generateRandomJson()
                                            serialPortController.sendMessage(inputMsg+inputMsg+SerialPortController.TERMINATOR_CHAR)
                                            delay(100)
                                        }
                                    }
                                }*/
                            }
                            if(isConnected) {
                                showToast("Open Success")
                            } else {
                                showToast("Open Fail")
                            }
                        }
                        CLOSE -> {
                            serialPortController.stopConnection()
                            uartMessages = mutableListOf()
                            isConnected = false
                            stopLoop = true
                            len = 0
                            errors = 0
                        }
                        TEST -> {}
                    }
                },
                onSendClicked = { inputMsg ->
                    //serialPortController.sendMessage(inputMsg+SerialPortController.TERMINATOR_CHAR)
                    serialPortController.sendMessage(inputMsg)
                    len = 0
                },
                onDelayChanged = { delay ->
                    serialPortController.delayMillis = delay.toLong()
                  //  delaymil = delay.toLong()
                }
            )
        }
    }

    private fun processReceivedMessage(receivedMessage: String): String {
        var str = receivedMessage
        var strPosition = 0
        for(i in receivedMessage.indices) {
            val receivedValue = receivedMessage[i].toString().toIntOrNull()
            if (receivedValue != null && receivedValue == expectedValue) {
                expectedValue = (expectedValue + 1) % 10
            } else {
                errors++
                str = str.substring(0, strPosition) + "*---*" + str.substring(strPosition+1)
                strPosition += 5
            }
            strPosition++
        }
        return str
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // TODO: remove this
    private fun generateRandomJson(): String {
        val json = JSONObject()

        json.put("name", "Person")
        json.put("age", i++)
        json.put("email", "person@example.com")
        json.put("pronto", "aaa")
        json.put("ciao", "fff")

        return json.toString()
    }

}