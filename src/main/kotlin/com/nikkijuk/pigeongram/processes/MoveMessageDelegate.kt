package com.nikkijuk.pigeongram.processes

import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class MoveMessageDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution?) {

        // gateway needs variable 'x' to be set - otherwise crashes
        execution?.setVariable("x","y")

        log.info { "move" }
    }
}