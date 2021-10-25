package com.nikkijuk.pigeongram.processes

import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class MoveMessageDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution?) {

        // gateway needs variable 'archive' to be set - otherwise crashes
        execution?.setVariable("archive","yes") // only "no" disables archiving

        log.info { "move - ${execution?.processInstanceId} : ${execution?.currentActivityId}" }
    }
}