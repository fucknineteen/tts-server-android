@file:Suppress("OVERRIDE_DEPRECATION")

package com.github.jing332.tts_server_android.service.forwarder.system

import android.speech.tts.TextToSpeech
import com.github.jing332.lib_gojni.SystemTtsForwarder
import com.github.jing332.tts_server_android.App
import com.github.jing332.tts_server_android.R
import com.github.jing332.tts_server_android.conf.SystemTtsForwarderConfig
import com.github.jing332.tts_server_android.constant.AppConst
import com.github.jing332.tts_server_android.constant.LogLevel
import com.github.jing332.tts_server_android.help.LocalTtsEngineHelper
import com.github.jing332.tts_server_android.model.speech.tts.LocalTTS
import com.github.jing332.tts_server_android.service.forwarder.AbsForwarderService
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString

class SysTtsForwarderService(
    override val port: Int = SystemTtsForwarderConfig.port.value,
    override val isWakeLockEnabled: Boolean = SystemTtsForwarderConfig.isWakeLockEnabled.value
) :
    AbsForwarderService(
        "SysTtsForwarderService",
        id = 1221,
        actionLog = ACTION_ON_LOG,
        actionStarting = ACTION_ON_STARTING,
        actionClosed = ACTION_ON_CLOSED,
        notificationChanId = "systts_forwarder_status",
        notificationChanTitle = R.string.forwarder_systts,
        notificationIcon = R.drawable.ic_baseline_compare_arrows_24,
        notificationTitle = R.string.forwarder_systts,
    ) {
    companion object {
        const val TAG = "SysTtsServerService"
        const val ACTION_ON_CLOSED = "ACTION_ON_CLOSED"
        const val ACTION_ON_STARTING = "ACTION_ON_STARTING"
        const val ACTION_ON_LOG = "ACTION_ON_LOG"

        val isRunning: Boolean
            get() = instance?.isRunning == true

        var instance: SysTtsForwarderService? = null
    }

    private var mServer: SystemTtsForwarder? = null
    private var mLocalTTS: LocalTTS? = null
    private val mLocalTtsHelper by lazy { LocalTtsEngineHelper(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun initServer() {
        mServer = SystemTtsForwarder().apply {
            init(
                onLog = { level, msg ->
                    sendLog(level, msg)
                },
                onGetAudio = { engine, voice, text, rate, pitch ->
                    if (mLocalTTS?.engine != engine) {
                        mLocalTTS?.onDestroy()
                        mLocalTTS = LocalTTS(engine)
                    }

                    mLocalTTS?.let {
                        it.voiceName = voice
                        val file = it.getAudioFile(text, rate, pitch)
                        if (file.exists()) return@init file.absolutePath
                    }
                    throw Exception(getString(R.string.forwarder_sys_fail_audio_file))
                },
                onGetEngines = {
                    val data = getSysTtsEngines().map { EngineInfo(it.name, it.label) }
                    return@init AppConst.jsonBuilder.encodeToString(data)
                },
                onCancelAudio = { engine ->
                    if (mLocalTTS?.engine == engine) {
                        mLocalTTS?.onStop()
                        sendLog(LogLevel.WARN, "Canceled: $engine")
                    }
                },
                onGetVoices = { engine ->
                    return@init runBlocking {
                        val ok = mLocalTtsHelper.setEngine(engine)
                        if (!ok) throw Exception(getString(R.string.systts_engine_init_failed_timeout))

                        val data = mLocalTtsHelper.voices.map {
                            VoiceInfo(
                                it.name,
                                it.locale.toLanguageTag(),
                                it.locale.getDisplayName(it.locale),
                                it.features?.toList()
                            )
                        }

                        return@runBlocking AppConst.jsonBuilder.encodeToString(data)
                    }
                }

            )
        }
    }

    override fun startServer() {
        mServer?.start(port.toLong())
    }

    override fun closeServer() {
        mServer?.let {
            it.shutdown()
            mLocalTTS?.onDestroy()
            mLocalTTS = null
        }
    }

    private fun getSysTtsEngines(): List<TextToSpeech.EngineInfo> {
        val tts = TextToSpeech(App.context, null)
        val engines = tts.engines
        tts.shutdown()
        return engines
    }

}