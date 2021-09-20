package com.akvone.core

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ContextGenerator<ScenarioInput, TaskContext> {
    fun generate(input: ScenarioInput): Collection<TaskContext>
}

data class TaskResult<TaskContext, TaskResult>(
    val context: TaskContext,
    val taskResult: TaskResult
)

abstract class BaseOneStepScenario<SI, TC, TR>(
    private val function: Function<TC, TR>,
    private val contextGenerator: ContextGenerator<SI, TC>
) : Scenario<SI>, Stateless {

    override suspend fun execute(scenarioInput: SI): ScenarioResult {
        val contexts = contextGenerator.generate(scenarioInput)
        val stepResult: StepResult<TC, TR> = Step(function, contexts).execute()
        val scenarioResult: ScenarioResult = handleStepResult(scenarioInput, stepResult)

        return scenarioResult
    }

    protected abstract fun handleStepResult(scenarioInput: SI, stepResult: StepResult<TC, TR>): ScenarioResult

}

class Step<C, R>(
    private val function: Function<C, R>,
    private val contexts: Collection<C>
) {
    suspend fun execute(): StepResult<C, R> {
        return coroutineScope { // TODO: Check it
            val taskResults: List<TaskResult<C, R>> = contexts.map { context ->
                async {
                    val taskResult = function.execute(context)
                    TaskResult(context, taskResult)
                }
            }.awaitAll()

            StepResult(taskResults)
        }
    }
}

data class StepResult<C, R>(
    val taskResults: Collection<TaskResult<C, R>>
)

data class SimpleScenarioResult(val status: ResultStatus) : ScenarioResult {
    override fun getResultStatus() = status
}
