package com.akvone.cluster_checks.base

import com.akvone.cluster_checks.core.ResultStatus
import com.akvone.cluster_checks.core.SFunction
import com.akvone.cluster_checks.core.ScenarioResult
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertEquals


internal class AbstractDiffScenarioTest {

    private val abstractOneStepScenarioMethodName = "handleStepResult"
    private val abstractDiffScenario = AbstractDiffScenarioForTest()

    @Test
    fun `test successful`() {
        test(
            ResultStatus.OK,
            listOf(
                setOf(1, 2),
                setOf(1, 2)
            )
        )
    }

    @Test
    fun `test problem with one request`() {
        test(
            ResultStatus.PROBLEM_DETECTED,
            listOf(
                setOf(1, 2, 3),
                setOf(1, 2)
            )
        )
    }

    private fun test(expectedStatus: ResultStatus, responses: List<Collection<Any>>) {
        val scenarioInput = Any()
        val stepResult = StepResult(
            responses.map { FunctionResult(Any(), Result.success(it)) }
        )
        val scenarioResult = ReflectionTestUtils.invokeMethod<ScenarioResult>(
            abstractDiffScenario, abstractOneStepScenarioMethodName,
            scenarioInput,
            stepResult
        )
        assertEquals(expectedStatus, scenarioResult!!.status)
    }
}

class AbstractDiffScenarioForTest : AbstractDiffScenario<Any, Any, Any>(
    object : SFunction<Any, Collection<Any>> {
        override suspend fun execute(context: Any) = TODO("Not yet implemented")
    },
    object : ContextGenerator<Any, Any> {
        override fun generate(scenarioInput: Any) = TODO("Not yet implemented")
    }
)