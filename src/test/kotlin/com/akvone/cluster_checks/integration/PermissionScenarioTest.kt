package com.akvone.cluster_checks.integration

import com.akvone.cluster_checks.utils.Utils.getLogger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PermissionScenarioTest(
    val permissionScenario: PermissionScenario
) {

    val log = getLogger()

    @Test
    fun test() {
        runBlocking {
            launch {
                val scenarioResult = permissionScenario.execute(PermissionScenarioInput("prod", "admin"))
                log.info(scenarioResult.toString())
            }
            launch {
                val scenarioResult = permissionScenario.execute(PermissionScenarioInput("uat", "non-existing"))
                log.info(scenarioResult.toString())
            }
        }
    }
}

