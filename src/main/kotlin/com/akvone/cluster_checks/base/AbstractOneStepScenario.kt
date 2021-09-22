package com.akvone.cluster_checks.base

import com.akvone.cluster_checks.Stateless
import com.akvone.cluster_checks.core.SFunction
import com.akvone.cluster_checks.core.Scenario
import com.akvone.cluster_checks.core.ScenarioResult
import com.akvone.cluster_checks.utils.Utils.getLogger

abstract class AbstractOneStepScenario<ScenarioInput, Context, Output>(
    private val function: SFunction<Context, Output>,
    private val contextGenerator: ContextGenerator<ScenarioInput, Context>
) : Scenario<ScenarioInput>, Stateless {

    private val log = getLogger()

    override suspend fun execute(scenarioInput: ScenarioInput): ScenarioResult {
        log.info("[scenario input=$scenarioInput]")
        val contexts = contextGenerator.generate(scenarioInput)
        val stepResult: StepResult<Context, Output> = Step(function, contexts).execute()
        val scenarioResult: ScenarioResult = handleStepResult(scenarioInput, stepResult)

        return scenarioResult
    }

    protected abstract fun handleStepResult(
        scenarioInput: ScenarioInput,
        stepResult: StepResult<Context, Output>
    ): ScenarioResult

}