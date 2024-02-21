package com.example.uartinterface.controls

import android.os.Handler
import com.example.uartinterface.utilities.RootCmd
import java.nio.charset.StandardCharsets

@Suppress("DEPRECATION")
class SerialPortController {

    private var handler: Handler? = null
    var fd = 0
    var delayMillis: Long = 10

    companion object{
        const val BAUD: Int = 115200
        const val SERIAL_PORT: String = "/dev/ttyS0"        // change the value with the serial port used

        const val TERMINATOR_CHAR: Char = '\u0079'

        private var instance: SerialPortController? = null

        @JvmStatic
        fun getInstance(): SerialPortController {
            if(instance == null)
                instance = SerialPortController()
            return instance as SerialPortController
        }
    }

    fun startConnection(uartMessagesCallback: (String, Int) -> Unit):Boolean {
        RootCmd.execRootCmdSilent("chmod 666 $SERIAL_PORT")
        fd = WpiControl.serialOpen(SERIAL_PORT, BAUD)
        if (fd > 0) {
           handler = Handler()
           handler!!.postDelayed(object : Runnable {
               override fun run() {
                   val size: Int = WpiControl.serialDataAvail(fd)
                   if (size > 0) {
                       val b = ByteArray(size)
                       var i = 0
                       while (i < size) {
                           val `val`: Int = WpiControl.serialGetchar(fd)
                           b[i] = `val`.toByte()
                           if (`val` == '\u0000'.code) break
                           i++
                       }
                       val str = String(b, StandardCharsets.UTF_8)
                       uartMessagesCallback(str, size)
                   }
                   handler!!.postDelayed(this, delayMillis)
               }
           }, delayMillis)      //10
            return true
        } else { return false }
    }

    fun stopConnection() {
        if (fd >= 0) {
            WpiControl.serialFlush(fd)
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