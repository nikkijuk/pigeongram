package com.nikkijuk.pigeongram.processes.send

import com.nikkijuk.pigeongram.processes.util.runProcess
import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class ValidateDraftDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution) {

        runProcess(execution) {
            log.info { "validate - ${processInstanceId} : ${currentActivityId}\"" }
        }
    }
}