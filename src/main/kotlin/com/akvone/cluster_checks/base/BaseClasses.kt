package com.akvone.cluster_checks.base

import com.akvone.cluster_checks.core.Function
import com.akvone.cluster_checks.core.ResultStatus
import com.akvone.cluster_checks.core.ScenarioResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ContextGenerator<ScenarioInput, FunctionContext> {
    fun generate(input: ScenarioInput): Collection<FunctionContext>
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
