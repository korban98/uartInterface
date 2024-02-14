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
import com.example.uartinterface.composable.SerialPort_Composable
import com.example.uartinterface.controls.SerialPortController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

var i: Int = 0

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@SuppressLint("MutableCollectionMutableState")
@ExperimentalMaterial3Api
class SerialPort: AppCompatActivity() {

    companion object {
        const val OPEN: Int = 1
        const val CLOSE: Int = 0
    }

    private val serialPortController: SerialPortController = SerialPortController.getInstance()

    private var isConnected by mutableStateOf(false)
    private var uartMessages by mutableStateOf(mutableListOf<String>())
    private var stopLoop by mutableStateOf(false)       //TODO: remove this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SerialPort_Composable(
                messages = uartMessages,
                Connected = isConnected,
                onConnClickListener = { buttonId ->
                    when (buttonId) {
                        OPEN -> {
                            isConnected = serialPortController.startConnection { newMessage ->
                                    uartMessages = uartMessages.toMutableList().apply { add(newMessage) }
                            }
                            if(isConnected) {
                                showToast("Open Success")
                                //TODO: remove this
                                stopLoop = false
                                GlobalScope.launch(Dispatchers.IO) {
                                    for (i in 0 until 1500) {
                                        if (stopLoop) break
                                        val inputMsg: String = generateRandomJson()
                                        serialPortController.sendMessage(inputMsg+'\u0079')
                                        delay(100)
                                    }
                                }
                            } else {
                                showToast("Open Fail")
                            }
                        }
                        CLOSE -> {
                            serialPortController.stopConnection()
                            uartMessages = mutableListOf()
                            isConnected = false
                            stopLoop = true
                        }
                    }
                },
                onSendClicked = { inputMsg ->
                    serialPortController.sendMessage(inputMsg)
                }
            )
        }
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