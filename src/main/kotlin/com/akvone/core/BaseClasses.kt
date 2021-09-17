package com.akvone.core

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

open class BaseContextHolder<C>(
    private val contexts: Collection<C>
) : ContextHolder<C> {
    override fun get(): Collection<C> = contexts
}

abstract class BaseOneStepScenario<TC, TR, SI, SR>(
    private val task: Task<TC, TR>,
) : Scenario<SI, SR> {

    override suspend fun execute(scenarioInput: SI): SR {
        val contextHolder: ContextHolder<TC> = getContextHolder(scenarioInput)
        val stepResult: StepResult<TC, TR> = Step(task, contextHolder).execute()
        val scenarioResult: SR = handleStepResult(stepResult)

        return scenarioResult
    }


    protected abstract fun getContextHolder(scenarioInput: SI): ContextHolder<TC>

    protected abstract fun handleStepResult(sr: StepResult<TC, TR>): SR

}

class Step<C, R>(
    private val task: Task<C, R>,
    private val contextHolder: ContextHolder<C>
) {
    suspend fun execute(): StepResult<C, R> {
        return coroutineScope { // TODO: Check it
            val taskResults: List<TaskResult<C, R>> = contextHolder.get().map { context ->
                async {
                    val taskResult = task.execute(context)
                    TaskResult(context, taskResult)
                }
            }.awaitAll()

            StepResult(taskResults)
        }
    }
}

data class StepResult<C, R>(
    val taskResults: List<TaskResult<C, R>>
)
