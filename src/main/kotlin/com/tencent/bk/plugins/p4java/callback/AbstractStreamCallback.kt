/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.bk.plugins.p4java.callback

import com.perforce.p4java.core.file.FileSpecOpStatus
import com.perforce.p4java.impl.mapbased.server.cmd.ResultListBuilder
import com.perforce.p4java.server.IServer
import com.perforce.p4java.server.callback.IStreamingCallback
import org.slf4j.LoggerFactory
import java.lang.StringBuilder

abstract class AbstractStreamCallback(private val server: IServer) : IStreamingCallback {
    private val logger = LoggerFactory.getLogger(AbstractStreamCallback::class.java)

    open fun buildMessage(resultMap: Map<String, Any>): String {
        val depotFile = resultMap["depotFile"]
        val rev = resultMap["rev"]
        val clientFile = resultMap["clientFile"]
        val action = resultMap["action"]
        return "$depotFile#$rev - $action as $clientFile"
    }

    override fun startResults(key: Int): Boolean {
        return true
    }

    override fun endResults(key: Int): Boolean {
        return true
    }

    override fun handleResult(resultMap: MutableMap<String, Any>, key: Int): Boolean {
        val fileSpec = ResultListBuilder.handleFileReturn(resultMap, server)
        val statusMessage = fileSpec.statusMessage
        when (fileSpec.opStatus) {
            FileSpecOpStatus.VALID -> {
                val msg = buildMessage(resultMap)
                success()
                log(msg, LOG_LEVEL_INFO)
            }
            FileSpecOpStatus.INFO -> log(statusMessage, LOG_LEVEL_INFO)
            FileSpecOpStatus.ERROR,
            FileSpecOpStatus.CLIENT_ERROR -> log(statusMessage, LOG_LEVEL_ERROR)
            else -> log(mapToString(resultMap), LOG_LEVEL_WARN)
        }

        return true
    }

    open fun success() {}

    protected fun mapToString(resultMap: Map<String, Any>): String {
        val builder = StringBuilder()
        resultMap.forEach {
            builder.append("${it.key}=${it.value} ")
        }
        return builder.toString()
    }

    fun prefix(): String {
        return "Task ${taskName()}:"
    }

    open fun normalMessages(): List<String> = emptyList()

    private fun log(msg: String, level: Int) {
        val prefix = prefix()
        when (level) {
            LOG_LEVEL_INFO -> logger.info("$prefix $msg")
            LOG_LEVEL_ERROR -> {
                normalMessages().forEach {
                    if (msg.contains(it)) {
                        logger.info("$prefix $msg")
                        return
                    }
                }
                logger.error("$prefix $msg")
            }
        }
    }

    abstract fun taskName(): String

    companion object {
        const val LOG_LEVEL_INFO = 10
        const val LOG_LEVEL_WARN = 20
        const val LOG_LEVEL_ERROR = 30
    }
}
