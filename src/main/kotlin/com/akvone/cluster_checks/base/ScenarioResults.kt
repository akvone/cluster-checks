package com.akvone.cluster_checks.base

import com.akvone.cluster_checks.core.ResultStatus
import com.akvone.cluster_checks.core.ScenarioResult

sealed class AbstractScenarioResult(
    override val status: ResultStatus,
) : ScenarioResult {
    class SimpleResult(status: ResultStatus) : AbstractScenarioResult(status)

    object SuccessfulResult : AbstractScenarioResult(ResultStatus.OK)
    class ProblemResult(
        val message: String,
    ) : AbstractScenarioResult(ResultStatus.PROBLEM_DETECTED)
}
