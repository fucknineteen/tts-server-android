package com.github.jing332.lib_gojni

class MsTtsForwarder {
    val server by lazy { tts_server_lib.MsTtsForwarder() }
    fun init(onLog: (level: Int, msg: String) -> Unit) {
        server.init { level, msg -> onLog(level, msg) }
    }

    fun start(port: Long, useDns: Boolean, token: String): Boolean {
        server.useDNS = useDns
        server.token = token
        return server.start(port)
    }

    fun shutdown() = server.shutdown()
}