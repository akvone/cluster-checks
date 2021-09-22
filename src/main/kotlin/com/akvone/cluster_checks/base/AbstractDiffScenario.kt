package com.akvone.cluster_checks.base

import com.akvone.cluster_checks.core.SFunction
import com.akvone.cluster_checks.core.ScenarioResult
import com.akvone.cluster_checks.utils.Utils.getLogger

abstract class AbstractDiffScenario<ScenarioInput, Context, T>(
    function: SFunction<Context, Collection<T>>,
    ContextGenerator: ContextGenerator<ScenarioInput, Context>
) : AbstractOneStepScenario<ScenarioInput, Context, Collection<T>>(
    function,
    ContextGenerator
) {

    private val log = getLogger()

    override fun handleStepResult(
        scenarioInput: ScenarioInput,
        stepResult: StepResult<Context, Collection<T>>
    ): ScenarioResult {
        val successfulResults = stepResult.functionResults
            .filter { it.result.isSuccess }
            .map { SuccessfulResult(it.context, it.result.getOrThrow()) }

        val baseResult = successfulResults.maxByOrNull { it.result.size }

        log.info("Comparing others with base result provided by ${baseResult?.context}")

        val differentResults = successfulResults.filterNot { it.result == baseResult?.result }

        if (differentResults.isEmpty()) {
            if (successfulResults.size != stepResult.functionResults.size) {
                val problemMessage = "Results are the same, but some function results have execution errors"
                return AbstractScenarioResult.ProblemResult(problemMessage)
            } else {
                return AbstractScenarioResult.SuccessfulResult
            }
        } else {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Different result found.").also { it.appendLine() }
            stringBuilder.append("Base result: ${baseResult?.result}").also { it.appendLine() }
            differentResults.map {
                stringBuilder.append("Different result provided by ${it.context}: ${it.result}").also { it.appendLine() }
            }
            val problemMessage = stringBuilder.toString()
            return AbstractScenarioResult.ProblemResult(problemMessage)
        }

    }

}

private data class SuccessfulResult<C, T>(
    val context: C,
    val result: T,
)