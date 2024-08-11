package com.github.jing332.lib_gojni

import tts_server_lib.EdgeApi
import tts_server_lib.Tts_server_lib
import tts_server_lib.VoiceExpressAs
import tts_server_lib.VoiceProperty
import tts_server_lib.VoiceProsody

object TtsGoNative {
    fun getEdgeVoices(): String {
        return String(Tts_server_lib.getEdgeVoices())
    }

    private val mEdgeApi: EdgeApi by lazy { EdgeApi() }

    /**
     * 设置超时
     */
    fun setTimeout(ms: Int) {
        mEdgeApi.timeout = ms
    }

    fun setUseDnsLookup(isEnabled: Boolean) {
        mEdgeApi.useDnsLookup = isEnabled
    }

    /**
     *  获取音频 在Go中生成SSML
     */
    fun getAudio(
        text: String,
        voice: String,
        rate: Int,
        volume: Int,
        pitch: Int,
        format: String,
    ): ByteArray? {
        if (mEdgeApi.timeout <= 0){
            mEdgeApi.timeout = 5000
        }

        return mEdgeApi.getEdgeAudio(
            text,
            format,
            VoiceProperty().apply {
                voiceName = voice
            },
            VoiceProsody().also {
                it.rate = rate.toByte()
                it.volume = volume.toByte()
                it.pitch = pitch.toByte()
            },
        )

    }
}

data class ResultProperty(
    var voiceProperty: VoiceProperty,
    var voiceProsody: VoiceProsody,
    var voiceExpressAs: VoiceExpressAs
)