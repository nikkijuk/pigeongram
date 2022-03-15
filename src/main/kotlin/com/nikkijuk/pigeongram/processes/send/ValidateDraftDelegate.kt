package com.nikkijuk.pigeongram.processes.send

import com.nikkijuk.pigeongram.processes.util.logVariables
import com.nikkijuk.pigeongram.processes.util.runProcess
import mu.KotlinLogging
import org.apache.http.util.Asserts
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class ValidateDraftDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution) {

        runProcess(execution) {
            logVariables()

            // exception here prevents starting process
            val archive = getVariable("archive")
            Asserts.notNull(archive, "archive")

            // exception here prevents starting process
            val draftId = getVariable("draftId")
            Asserts.notNull(draftId, "draftId")

            // exception here is directed to errror event
            if ((draftId as String).length < 2) {
                throw BpmnError ("INVALID_DRAFT_ID", "draftId is invalid")
            }

            log.info {  "validated draft '$draftId' with archival set to '$archive'"  }
        }
    }
}