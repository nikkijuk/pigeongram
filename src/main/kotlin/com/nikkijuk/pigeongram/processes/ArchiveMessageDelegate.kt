package com.nikkijuk.pigeongram.processes

import com.nikkijuk.pigeongram.processes.util.ifLastRetryThrowBpmnError
import com.nikkijuk.pigeongram.processes.util.runProcess
import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class ArchiveMessageDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution) {

        runProcess(execution) {

            val draftId = getVariable("draftId")

            // exception here is directed to errror event after last retry
            if ((draftId as String).contains("FAIL_ARCHIVE")) {
                log.error { "failure archiving $draftId" }

                // BPM error won't be retried - this is process stuu
                ifLastRetryThrowBpmnError("ARCHIVE_FAILED", "archiving  draft failed")

                // Technical errors will be retried
                throw RuntimeException ("arhiving draft failed")
            }
        }
    }
}