package com.example.uartinterface.utilities

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

object RootCmd {
    private const val TAG = "RootCmd"
    //  private var mHaveRoot = false

    /**
     * 判断机器Android是否已经root，即是否获取root权限
     */
    /*  fun haveRoot(): Boolean {
          if (!mHaveRoot) {
              val ret = execRootCmdSilent("echo test") // 通过执行测试命令来检测
              if (ret != -1) {
                  Log.i(TAG, "have root!")
                  mHaveRoot = true
              } else {
                  Log.i(TAG, "not root!")
              }
          } else {
              Log.i(TAG, "mHaveRoot = true, have root!")
          }
          return mHaveRoot
      }
  */
    /**
     * 执行命令并且输出结果
     */
    fun execRootCmd(cmd: String): Array<String?>? {
        var result: Array<String?>? = null
        var dos: DataOutputStream? = null
        var dis: DataInputStream? = null
        try {
            val p = Runtime.getRuntime().exec("su") // 经过Root处理的android系统即有su命令
            dos = DataOutputStream(p.outputStream)
            dis = DataInputStream(p.inputStream)
            Log.i(TAG, cmd)
            dos.writeBytes(
                """
                    $cmd
                    
                    """.trimIndent()
            )
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            var line: String?
            val list = ArrayList<String?>()
            while (dis.readLine().also { line = it } != null) {
                Log.d("result", line!!)
                list.add(line)
            }
            if (list.size > 0) {
                result = arrayOfNulls(list.size)
                for (i in list.indices) result[i] = list[i]
            }
            p.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (dis != null) {
                try {
                    dis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    /**
     * 执行命令但不关注结果输出
     */
    fun execRootCmdSilent(cmd: String): Int {
        var result = -1
        var dos: DataOutputStream? = null
        try {
            val p = Runtime.getRuntime().exec("su")
            dos = DataOutputStream(p.outputStream)
            Log.i(TAG, cmd)
            dos.writeBytes(
                """
                    $cmd
                    
                    """.trimIndent()
            )
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            p.waitFor()
            result = p.exitValue()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}