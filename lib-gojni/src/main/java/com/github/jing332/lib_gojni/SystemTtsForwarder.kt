package com.github.jing332.lib_gojni

class SystemTtsForwarder {
    private val server by lazy {
        tts_server_lib.SysTtsForwarder()
    }

    fun init(
        onCancelAudio: (engine: String) -> Unit,
        onGetAudio: (engine: String, voice: String, text: String, rate: Int, pitch: Int) -> String,
        onGetEngines: () -> String,
        onGetVoices: (engine: String) -> String,
        onLog: (level: Int, msg: String) -> Unit

    ) {
        server.initCallback(object : tts_server_lib.SysTtsForwarderCallback {
            override fun cancelAudio(engine: String?) {
                onCancelAudio(engine ?: "")
            }

            override fun getAudio(
                engine: String?,
                voice: String?,
                text: String?,
                rate: Int,
                pitch: Int
            ): String {
                return onGetAudio(engine ?: "", voice ?: "", text ?: "", rate, pitch)
            }

            override fun getEngines(): String {
                return onGetEngines()
            }

            override fun getVoices(engine: String?): String {
                return onGetVoices(engine ?: "")
            }

            override fun log(level: Int, msg: String?) {
                onLog(level, msg ?: "")
            }

        })
    }

    fun start(port: Long){
        server.start(port)
    }

    fun shutdown(){
        server.close()
    }
}