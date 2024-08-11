package com.github.jing332.lib_gojni

import tts_server_lib.ScriptCodeSyncServerCallback

class CodeSyncServer {
    private val server by lazy { tts_server_lib.ScriptSyncServer() }

    fun init(
        onLog: (level: Int, msg: String) -> Unit,
        onAction: (name: String, body: ByteArray) -> Unit,
        onPull: () -> String,
        onPush: (code: String) -> Unit

    ) {
        server.init(object : ScriptCodeSyncServerCallback {
            override fun log(level: Int, msg: String?) {
                onLog(level, msg ?: "")
            }

            override fun action(name: String?, body: ByteArray?) {
                onAction(name ?: "", body ?: byteArrayOf())
            }

            override fun pull(): String {
                return onPull()
            }

            override fun push(code: String?) {
                onPush(code ?: "")
            }

        })
    }

    fun start(timeout: Long = 5000) {
        server.start(timeout)
    }

    fun shutdown() {
        server.close()
    }
}