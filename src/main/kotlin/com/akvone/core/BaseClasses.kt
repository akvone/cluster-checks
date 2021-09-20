package com.akvone.core

import com.akvone.core.Utils.getLogger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ContextGenerator<ScenarioInput, FunctionContext> {
    fun generate(input: ScenarioInput): Collection<FunctionContext>
}

abstract class BaseOneStepScenario<ScenarioInput, FunctionContext, SuccessfulFunctionResult>(
    private val function: Function<FunctionContext, SuccessfulFunctionResult>,
    private val contextGenerator: ContextGenerator<ScenarioInput, FunctionContext>
) : Scenario<ScenarioInput>, Stateless {

    private val log = getLogger()

    override suspend fun execute(input: ScenarioInput): ScenarioResult {
        log.info("[scenarioInput=$input]")
        val contexts = contextGenerator.generate(input)
        val stepResult: StepResult<FunctionContext, SuccessfulFunctionResult> = Step(function, contexts).execute()
        val scenarioResult: ScenarioResult = handleStepResult(input, stepResult)

        return scenarioResult
    }

    protected abstract fun handleStepResult(
        scenarioInput: ScenarioInput,
        stepResult: StepResult<FunctionContext, SuccessfulFunctionResult>
    ): ScenarioResult

}

class Step<FunctionContext, SuccessfulFunctionResult>(
    private val function: Function<FunctionContext, SuccessfulFunctionResult>,
    private val contexts: Collection<FunctionContext>
) {
    suspend fun execute(): StepResult<FunctionContext, SuccessfulFunctionResult> {
        return coroutineScope { // TODO: Check it
            val functionResults: List<FunctionResult<FunctionContext, SuccessfulFunctionResult>> =
                contexts.map { context ->
                    async {
                        val result = kotlin.runCatching {
                            function.execute(context)
                        }
                        FunctionResult(context, result)
                    }
                }.awaitAll()

            StepResult(functionResults)
        }
    }
}

data class StepResult<FunctionContext, SuccessfulFunctionResult>(
    val functionResults: Collection<FunctionResult<FunctionContext, SuccessfulFunctionResult>>
)

data class FunctionResult<FunctionContext, SuccessfulFunctionResult>(
    val context: FunctionContext,
    val result: Result<SuccessfulFunctionResult>
)

data class SimpleScenarioResult(val status: ResultStatus) : ScenarioResult {
    override fun getResultStatus() = status
}
