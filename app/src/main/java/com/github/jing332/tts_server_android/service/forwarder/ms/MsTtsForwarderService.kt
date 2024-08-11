package com.github.jing332.tts_server_android.service.forwarder.ms

import com.github.jing332.tts_server_android.R
import com.github.jing332.lib_gojni.MsTtsForwarder
import com.github.jing332.tts_server_android.conf.MsTtsForwarderConfig
import com.github.jing332.tts_server_android.service.forwarder.AbsForwarderService

class MsTtsForwarderService(
    override val port: Int = MsTtsForwarderConfig.port.value,
    override val isWakeLockEnabled: Boolean = MsTtsForwarderConfig.isWakeLockEnabled.value
) : AbsForwarderService(
    name = "MsTtsForwarderService",
    id = 1233,
    actionLog = ACTION_ON_LOG,
    actionStarting = ACTION_ON_STARTING,
    actionClosed = ACTION_ON_CLOSED,
    notificationChanId = "server_status",
    notificationChanTitle = R.string.forwarder_ms,
    notificationTitle = R.string.forwarder_ms,
    notificationIcon = R.drawable.ic_microsoft
) {
    companion object {
        const val ACTION_ON_STARTING = "ACTION_ON_STARTED"
        const val ACTION_ON_CLOSED = "ACTION_ON_CLOSED"
        const val ACTION_ON_LOG = "ACTION_ON_LOG"

        val isRunning: Boolean
            get() = instance?.isRunning ?: false

        var instance: MsTtsForwarderService? = null
    }

    private val mServer by lazy { MsTtsForwarder() }

    @Deprecated("Deprecated in Java")
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    @Deprecated("Deprecated in Java")
    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun initServer() {
        mServer.init { level, msg ->
            sendLog(level, msg)
        }
    }

    override fun startServer() {
        mServer.start(port.toLong(), true, MsTtsForwarderConfig.token.value)
    }

    override fun closeServer() {
        mServer.shutdown()
    }
}