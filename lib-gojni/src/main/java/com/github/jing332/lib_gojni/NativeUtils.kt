package com.github.jing332.lib_gojni

import tts_server_lib.Tts_server_lib

object NativeUtils {
    fun uploadLog(log: String): String {
        return Tts_server_lib.uploadLog(log)
    }

    fun getOutboundIP(): String {
        return Tts_server_lib.getOutboundIP()
    }
}