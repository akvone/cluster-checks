package com.akvone.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Utils {

    fun Any.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)
}