package com.akvone.core

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
    fun getResultStatus(): ResultStatus
}

enum class ResultStatus {
    OK, PROBLEM_DETECTED
}