package com.akvone.core

interface Task<Context, Result> {
    /**
     * Do not block execution inside this method
     */
    suspend fun execute(context: Context): Result
}

interface ContextHolder<Context> {

    fun get(): Collection<Context>
}

data class TaskResult<Context, Result>(
    val context: Context,
    val taskResult: Result
)

interface Scenario<Input, Result> {
    suspend fun execute(scenarioInput: Input): Result
}