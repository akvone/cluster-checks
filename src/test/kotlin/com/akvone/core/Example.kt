package com.akvone.core

import com.akvone.core.Utils.getLogger
import kotlinx.coroutines.delay
import org.springframework.stereotype.Component

@Component
class InfoTask : Task<PermissionCheckContext, Boolean> {

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

class MockRestClient {

    val log = getLogger()

    suspend fun get(url: String): List<String> {
        log.debug("Get to $url")
        delay(1000)
        return listOf("admin", "reviewer")
    }
}

class PermissionContextHolder(contexts: Collection<PermissionCheckContext>) :
    BaseContextHolder<PermissionCheckContext>(contexts)

@Component
class PermissionScenario(
    private val contextHolderService: ContextHolderService,
    task: InfoTask
) : BaseOneStepScenario<PermissionCheckContext, Boolean, PermissionScenarioInput, Unit>(task) {

    val log = getLogger()

    override fun handleStepResult(sr: StepResult<PermissionCheckContext, Boolean>) {
        sr.taskResults.forEach {
            log.info("${it.context.host}: ${it.taskResult}")
        }
    }

    override fun getContextHolder(scenarioInput: PermissionScenarioInput): ContextHolder<PermissionCheckContext> {
        return contextHolderService.getPermissionContextHolder(scenarioInput)
    }
}

data class PermissionScenarioInput(
    val tier: String,
    val permission: String
)

@Component
class ContextHolderService {

    fun getPermissionContextHolder(permissionScenarioInput: PermissionScenarioInput): PermissionContextHolder {
        val contexts = (1..10).map {
            "https://${permissionScenarioInput.tier}.example$it.org"
        }
            .map { PermissionCheckContext(MockRestClient(), it, permissionScenarioInput.permission) }

        return PermissionContextHolder(contexts)
    }
}