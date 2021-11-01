package com.nikkijuk.pigeongram.processes.send

import com.nikkijuk.pigeongram.processes.util.ifLastRetryThrowBpmnError
import com.nikkijuk.pigeongram.processes.util.runProcess
import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class SendMessageDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution) {

        runProcess(execution) {

            val draftId = getVariable("draftId")

            // exception here is directed to errror event after 3 retries
            if ((draftId as String).contains("FAIL")) {
                log.error { "failure sending $draftId" }

                // BPM error won't be retried - this is process stuu
                ifLastRetryThrowBpmnError("SENDING_FAILED", "sending  draft failed")

                // Technical errors will be retried
                throw RuntimeException ("sending draft failed")
            }
        }
    }

}