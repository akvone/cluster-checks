package com.akvone.cluster_checks

import com.akvone.cluster_checks.base.AbstractOneStepScenario
import com.akvone.cluster_checks.base.AbstractScenarioResult.SimpleResult
import com.akvone.cluster_checks.base.ContextGenerator
import com.akvone.cluster_checks.base.StepResult
import com.akvone.cluster_checks.core.Function
import com.akvone.cluster_checks.core.ResultStatus.OK
import com.akvone.cluster_checks.core.ResultStatus.PROBLEM_DETECTED
import com.akvone.cluster_checks.core.ScenarioResult
import com.akvone.cluster_checks.utils.Utils.getLogger
import org.springframework.stereotype.Component

@Component
class InfoFunction : Function<PermissionCheckContext, Boolean> {

    override suspend fun execute(context: PermissionCheckContext): Boolean {
        val body = context.mockRestClient.get(context.host)

        return body.contains(context.permission)
    }
}

data class PermissionCheckContext(
    val mockRestClient: MockRestClient,
    val host: String,
    val permission: String
)

@Component
class PermissionScenario(
    infoFunction: InfoFunction,
    permissionContextGenerator: PermissionContextGenerator
) : AbstractOneStepScenario<PermissionScenarioInput, PermissionCheckContext, Boolean>(
    infoFunction,
    permissionContextGenerator
) {

    private val log = getLogger()

    override fun handleStepResult(
        scenarioInput: PermissionScenarioInput,
        stepResult: StepResult<PermissionCheckContext, Boolean>
    ): ScenarioResult {
        stepResult.functionResults.forEach {
            log.debug("[permission=${scenarioInput.permission},host=${it.context.host},result=${it.result}]")
        }
        val resultStatus = if (stepResult.functionResults.any { it.result.isFailure }) {
            PROBLEM_DETECTED
        } else {
            if (stepResult.functionResults.all { it.result.getOrThrow() }) {
                OK
            } else {
                PROBLEM_DETECTED
            }
        }
        return SimpleResult(resultStatus)
    }

}

data class PermissionScenarioInput(
    val tier: String,
    val permission: String
)

@Component
class PermissionContextGenerator : ContextGenerator<PermissionScenarioInput, PermissionCheckContext> {
    override fun generate(input: PermissionScenarioInput): Collection<PermissionCheckContext> {
        val contexts = (1..10).map {
            "https://${input.tier}.example$it.org"
        }.map { PermissionCheckContext(MockRestClient(), it, input.permission) }

        return contexts
    }

}