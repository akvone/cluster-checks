package com.akvone.core

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ContextGenerator<ScenarioInput, FunctionContext> {
    fun generate(input: ScenarioInput): Collection<FunctionContext>
}

data class FunctionResult<FunctionContext, FunctionResult>(
    val context: FunctionContext,
    val taskResult: FunctionResult
)

abstract class BaseOneStepScenario<ScenarioInput, FunctionContext, FunctionResult>(
    private val function: Function<FunctionContext, FunctionResult>,
    private val contextGenerator: ContextGenerator<ScenarioInput, FunctionContext>
) : Scenario<ScenarioInput>, Stateless {

    override suspend fun execute(scenarioInput: ScenarioInput): ScenarioResult {
        val contexts = contextGenerator.generate(scenarioInput)
        val stepResult: StepResult<FunctionContext, FunctionResult> = Step(function, contexts).execute()
        val scenarioResult: ScenarioResult = handleStepResult(scenarioInput, stepResult)

        return scenarioResult
    }

    protected abstract fun handleStepResult(scenarioInput: ScenarioInput, stepResult: StepResult<FunctionContext, FunctionResult>): ScenarioResult

}

class Step<FunctionContext, FunctionResult>(
    private val function: Function<FunctionContext, FunctionResult>,
    private val contexts: Collection<FunctionContext>
) {
    suspend fun execute(): StepResult<FunctionContext, FunctionResult> {
        return coroutineScope { // TODO: Check it
            val functionResults: List<com.akvone.core.FunctionResult<FunctionContext, FunctionResult>> = contexts.map { context ->
                async {
                    val taskResult = function.execute(context)
                    FunctionResult(context, taskResult)
                }
            }.awaitAll()

            StepResult(functionResults)
        }
    }
}

data class StepResult<FunctionContext, FunctionResult>(
    val functionResults: Collection<com.akvone.core.FunctionResult<FunctionContext, FunctionResult>>
)

data class SimpleScenarioResult(val status: ResultStatus) : ScenarioResult {
    override fun getResultStatus() = status
}
