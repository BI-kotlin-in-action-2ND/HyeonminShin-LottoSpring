package com.hm.hyeonminshinlottospring.global.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Dispatchers.IO 대신에 virtual thread로 작업할 수 있게 해주는 Dispatchers 확장 함수.
 */
val Dispatchers.LOOM: CoroutineDispatcher
    get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
