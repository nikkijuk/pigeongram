package com.nikkijuk.pigeongram.processes.util

import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution

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
