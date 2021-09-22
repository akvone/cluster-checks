package com.akvone.cluster_checks.core

import com.akvone.cluster_checks.Stateless

interface SFunction<Context, Output> : Stateless {
    /**
     * Do not block execution inside this method
     */
    suspend fun execute(context: Context): Output
}

interface Scenario<Input> {
    suspend fun execute(scenarioInput: Input): ScenarioResult
}

interface ScenarioResult {
    val status: ResultStatus
}

enum class ResultStatus {
    OK, PROBLEM_DETECTED
}