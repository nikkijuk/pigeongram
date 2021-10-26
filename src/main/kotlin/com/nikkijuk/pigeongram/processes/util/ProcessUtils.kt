package com.nikkijuk.pigeongram.processes.util

import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

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