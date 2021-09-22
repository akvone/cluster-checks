package com.akvone.cluster_checks.base

import com.akvone.cluster_checks.core.SFunction
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface ContextGenerator<ScenarioInput, Context> {
    fun generate(scenarioInput: ScenarioInput): Collection<Context>
}

class Step<Context, Output>(
    private val function: SFunction<Context, Output>,
    private val contexts: Collection<Context>
) {
    suspend fun execute(): StepResult<Context, Output> {
        return coroutineScope { // TODO: Check it
            val functionResults: List<FunctionResult<Context, Output>> =
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

data class StepResult<Context, Output>(
    val functionResults: Collection<FunctionResult<Context, Output>>
)

data class FunctionResult<Context, Output>(
    val context: Context,
    val result: Result<Output>
)
