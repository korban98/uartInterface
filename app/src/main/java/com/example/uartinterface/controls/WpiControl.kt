package com.example.uartinterface.controls

class WpiControl {
    companion object {
        init {
            System.loadLibrary("WiringOP")
        }

        @JvmStatic
        external fun wiringPiSetup(): Int

        @JvmStatic
        external fun pinMode(pin: Int, mode: Int)

        @JvmStatic
        external fun pullUpDnControl(pin: Int, pud: Int)

        @JvmStatic
        external fun digitalRead(pin: Int): Int

        @JvmStatic
        external fun digitalWrite(pin: Int, value: Int)

        @JvmStatic
        external fun wiringPiSPIGetFd(channel: Int): Int

        @JvmStatic
        external fun wiringPiSPIDataRW(channel: Int, data: ByteArray, len: Int): Int

        @JvmStatic
        external fun wiringPiSPISetupMode(channel: Int, port: Int, speed: Int, mode: Int): Int

        @JvmStatic
        external fun wiringPiSPISetup(channel: Int, speed: Int): Int

        @JvmStatic
        external fun serialOpen(dev: String, baud: Int): Int

        @JvmStatic
        external fun serialClose(fd: Int)

        @JvmStatic
        external fun serialFlush(fd: Int)

        @JvmStatic
        external fun serialPutchar(fd: Int, c: Byte)

        @JvmStatic
        external fun serialPuts(fd: Int, s: String)

        @JvmStatic
        external fun serialDataAvail(fd: Int): Int

        @JvmStatic
        external fun serialGetchar(fd: Int): Int

        @JvmStatic
        external fun wiringPiI2CRead(fd: Int): Int

        @JvmStatic
        external fun wiringPiI2CReadReg8(fd: Int, reg: Int): Int

        @JvmStatic
        external fun wiringPiI2CReadReg16(fd: Int, reg: Int): Int

        @JvmStatic
        external fun wiringPiI2CWrite(fd: Int, data: Int): Int

        @JvmStatic
        external fun wiringPiI2CWriteReg8(fd: Int, reg: Int, value: Int): Int

        @JvmStatic
        external fun wiringPiI2CWriteReg16(fd: Int, reg: Int, value: Int): Int

        @JvmStatic
        external fun wiringPiI2CSetup(devId: Int): Int

        @JvmStatic
        external fun wiringPiI2CSetupInterface(device: String, devId: Int): Int

        @JvmStatic
        external fun getGpioInfo(arr: IntArray, strArr: Array<CharSequence>): Int

    }
}