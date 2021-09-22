package com.akvone.cluster_checks.base

import com.akvone.cluster_checks.Stateless
import com.akvone.cluster_checks.core.Function
import com.akvone.cluster_checks.core.Scenario
import com.akvone.cluster_checks.core.ScenarioResult
import com.akvone.cluster_checks.utils.Utils.getLogger

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