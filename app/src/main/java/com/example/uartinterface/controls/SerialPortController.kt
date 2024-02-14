package com.example.uartinterface.controls

import android.os.Handler
import java.nio.charset.StandardCharsets

@Suppress("DEPRECATION")
class SerialPortController {

    private var handler: Handler? = null
    private var fd = 0

    companion object{
        const val BAUD: Int = 115200
        const val SERIAL_PORT: String = "/dev/ttyS0"        // change the value with the serial port used

        private var instance: SerialPortController? = null

        @JvmStatic
        fun getInstance(): SerialPortController {
            if(instance == null)
                instance = SerialPortController()
            return instance as SerialPortController
        }
    }

    fun startConnection(uartMessagesCallback: (String) -> Unit): Boolean {
        fd = WpiControl.serialOpen(SERIAL_PORT, BAUD)
        if (fd > 0) {
            handler = Handler()
            handler!!.postDelayed(object : Runnable {
                override fun run() {
                    var mess = ""
                    if(WpiControl.serialDataAvail(fd) > 0) {
                        var found = false
                        while (!found) {
                            val outputSize: Int = WpiControl.serialDataAvail(fd)
                            if (outputSize > 0) {
                                val outputByte = ByteArray(outputSize)
                                var char: Int = -1
                                var index: Int = -1
                                repeat(outputSize) {
                                    char = WpiControl.serialGetchar(fd)
                                    if (char == '\u0000'.code || char == '\u0079'.code) return@repeat
                                    outputByte[it] = char.toByte()
                                    index = it
                                }
                                val str = String(outputByte.copyOf(index + 1), StandardCharsets.UTF_8)
                                mess += str
                                if (char == '\u0079'.code) found=true
                            }
                        }
                        uartMessagesCallback(mess + " " + mess.length)
                    }
                    handler!!.postDelayed(this, 10)
                }
            }, 10)      //10
            return true
        } else { return false }
    }

    fun stopConnection() {
        if (fd >= 0) {
          //  WpiControl.serialFlush(fd)
            WpiControl.serialClose(fd)
        }
        fd = -1
    }

    fun sendMessage(msg: String) {
        if (fd >= 0) {
            WpiControl.serialPuts(fd, msg)
        }
    }

}