package com.akvone.cluster_checks.core

import com.akvone.cluster_checks.Stateless

interface Function<Context, Result> : Stateless {
    /**
     * Do not block execution inside this method
     */
    suspend fun execute(context: Context): Result
}

interface Scenario<Input> {
    suspend fun execute(input: Input): ScenarioResult
}

interface ScenarioResult {
    val status: ResultStatus
}

enum class ResultStatus {
    OK, PROBLEM_DETECTED
}