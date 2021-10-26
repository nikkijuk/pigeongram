package com.nikkijuk.pigeongram.processes.util

import org.camunda.bpm.engine.delegate.DelegateExecution

/*
* Simple around function which could also contain some logging, exception handling, etc.
 */
fun runProcess (ctx: DelegateExecution, action: DelegateExecution.() -> Unit) {
    ctx.action()
}
