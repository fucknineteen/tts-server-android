package com.github.jing332.tts_server_android.compose.codeeditor

import android.util.Log
import androidx.lifecycle.ViewModel
import com.drake.net.utils.withMain
import com.github.jing332.lib_gojni.CodeSyncServer
import com.github.jing332.tts_server_android.utils.runOnUI
import kotlinx.coroutines.runBlocking

class CodeEditorViewModel : ViewModel() {
    companion object {
        const val TAG = "CodeEditorViewModel"

        const val SYNC_ACTION_DEBUG = "debug"
    }

    private var server: CodeSyncServer? = null

    // 代码同步服务器
    fun startSyncServer(
        port: Int,
        onPush: (code: String) -> Unit,
        onPull: () -> String,
        onDebug: () -> Unit,
        onAction: (name: String, body: ByteArray?) -> Unit
    ) {
        if (server != null) return
        server = CodeSyncServer()
        server?.init(
            onLog = { level, msg ->
                Log.i(TAG, "$level $msg")
            },
            onAction = { name, body ->
                runOnUI {
                    if (name == SYNC_ACTION_DEBUG) {
                        onDebug.invoke()
                    } else
                        onAction.invoke(name, body)
                }
            },
            onPull = {
                runBlocking {
                    return@runBlocking withMain {
                        return@withMain onPull.invoke()
                    }
                }
            },
            onPush = { code ->
                runOnUI {
                    onPush.invoke(code)
                }
            }
        )

        server?.start(port.toLong())
    }

    private fun closeSyncServer() {
        server?.shutdown()
        server = null
    }

    override fun onCleared() {
        super.onCleared()
        closeSyncServer()
    }
}