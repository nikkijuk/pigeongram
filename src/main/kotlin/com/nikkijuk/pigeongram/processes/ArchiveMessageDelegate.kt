package com.nikkijuk.pigeongram.processes

import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class ArchiveMessageDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution?) {
        log.info { "archive - ${execution?.processInstanceId} : ${execution?.currentActivityId}\"" }
    }
}