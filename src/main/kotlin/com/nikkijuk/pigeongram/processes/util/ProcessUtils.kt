package com.nikkijuk.pigeongram.processes.util

import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.impl.context.Context
import javax.inject.Named

private val DEFAULT_ERROR_CODE = "PROCESS_ERROR_LAST_RETRY"
private val DEFAULT_ERROR_MESSAGE = "Process failed after last retry"

private val log = KotlinLogging.logger { }

/*
* Simple around function which could also contain some logging, exception handling, etc.
 */
fun runProcess (ctx: DelegateExecution, action: DelegateExecution.() -> Unit) {
    try {
        ctx.action()
        log.info { "executed: ${ctx.processInstanceId} : ${ctx.currentActivityId}" }
    } catch (e: Exception) {
        log.error (e) { "failed to execute: ${ctx.processInstanceId} : ${ctx.currentActivityId}" }
        throw e
    }
}

fun DelegateExecution.logVariables () {
    variables.entries.iterator().forEach {
        log.info { "${it.key} = ${it.value}" }
    }
}

// please see source of code for discussion / context:
// https://forum.camunda.org/t/retry-task-for-error-handling/12476
fun ifLastRetryThrowBpmnError(errorCode: String = DEFAULT_ERROR_CODE, message: String = DEFAULT_ERROR_MESSAGE) {
    log.info { "Checking if it's last retry" }
    if (isLastRetry()) {
        throw BpmnError(errorCode, message)
    }
}

fun isLastRetry(): Boolean {
    val jobExecutorContext = Context.getJobExecutorContext()
    if (jobExecutorContext?.currentJob != null) {
        val noOfRetries: Int = jobExecutorContext.currentJob.getRetries()
        val activityId = jobExecutorContext.currentJob.getActivityId()

        log.info("$activityId has ${noOfRetries - 1} retries remaining")

        return noOfRetries <= 1
    }
    return false
}

/**
 * Simple delegate which can be used to set up processes without functionality
 */
@Named
class NoOpDelegate : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        runProcess(execution)  {
            // do nothing
        }
    }
}