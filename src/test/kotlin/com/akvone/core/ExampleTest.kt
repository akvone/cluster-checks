package com.akvone.core

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ExampleTest(
    val permissionScenario: PermissionScenario
) {

    @Test
    fun test() {
        runBlocking {
            launch {
                permissionScenario.execute(PermissionScenarioInput("prod", "admin"))
            }
            launch {
                permissionScenario.execute(PermissionScenarioInput("uat", "non-existing"))
            }
        }
    }
}

