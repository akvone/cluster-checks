package com.akvone.core

interface Function<TaskContext, TaskResult> : Stateless {
    /**
     * Do not block execution inside this method
     */
    suspend fun execute(context: TaskContext): TaskResult
}

interface Scenario<ScenarioInput> {
    suspend fun execute(scenarioInput: ScenarioInput): ScenarioResult
}

interface ScenarioResult {
    fun getResultStatus(): ResultStatus
}

enum class ResultStatus {
    OK, PROBLEM_DETECTED
}