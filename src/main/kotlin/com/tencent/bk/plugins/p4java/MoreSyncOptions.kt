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

package com.tencent.bk.plugins.p4java

import com.perforce.p4java.option.client.SyncOptions
import com.perforce.p4java.server.IServer

data class MoreSyncOptions(
    /**
     * 强制更新 -f
     * */
    val forceUpdate: Boolean = false,

    /**
     * 预览 -n
     * */
    val noUpdate: Boolean = false,
    /**
     * 跳过客户端 -k
     * */
    val clientBypass: Boolean = false,
    /**
     * 跳过服务端 -p
     * */
    val serverBypass: Boolean = false,
    /**
     * 安静模式 -q
     * */
    val quiet: Boolean = false,
    /**
     * 安全检查 -s
     * */
    val safetyCheck: Boolean = false,
    /**
     * 同步最大数量 -m
     * */
    val max: Int = 0
) : SyncOptions(
    forceUpdate, noUpdate, clientBypass,
    serverBypass, safetyCheck
) {
    companion object {
        const val OPTIONS_SPECS = "b:f b:n b:k b:p b:q b:s i:m"
    }

    init {
        super.quiet = quiet
    }

    override fun processOptions(server: IServer?): MutableList<String> {
        return processFields(
            OPTIONS_SPECS, this.forceUpdate,
            this.noUpdate,
            this.clientBypass,
            this.serverBypass,
            this.quiet,
            this.safetyCheck, max
        )
    }
}
