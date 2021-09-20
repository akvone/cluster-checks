package com.akvone.core

import com.akvone.core.Utils.getLogger
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
) : BaseOneStepScenario<PermissionScenarioInput, PermissionCheckContext, Boolean>(
    infoFunction,
    permissionContextGenerator
) {

    val log = getLogger()

    override fun handleStepResult(sr: StepResult<PermissionCheckContext, Boolean>): ScenarioResult {
        sr.taskResults.forEach {
            log.debug("${it.context.host}: ${it.taskResult}")
        }
        val resultStatus = if (sr.taskResults.all { it.taskResult }) ResultStatus.OK else ResultStatus.PROBLEM_DETECTED

        return SimpleScenarioResult(resultStatus)
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