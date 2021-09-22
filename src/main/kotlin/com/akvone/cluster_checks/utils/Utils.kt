package com.akvone.cluster_checks.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Utils {

    fun Any.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)
}