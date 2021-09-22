package com.akvone.cluster_checks

import com.akvone.cluster_checks.utils.Utils.getLogger
import kotlinx.coroutines.delay

class MockRestClient {

    val log = getLogger()

    suspend fun get(url: String): List<String> {
        log.debug("Get to $url")
        delay(1000)
        return listOf("admin", "reviewer")
    }
}