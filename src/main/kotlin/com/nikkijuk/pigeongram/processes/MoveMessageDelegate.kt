package com.nikkijuk.pigeongram.processes

import com.nikkijuk.pigeongram.processes.util.runProcess
import mu.KotlinLogging
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import javax.inject.Named

@Named
class MoveMessageDelegate : JavaDelegate {

    private val log = KotlinLogging.logger { }

    override fun execute(execution: DelegateExecution) {

        runProcess(execution)  {
            // gateway needs variable 'archive' to be set - otherwise crashes
            setVariable("archive","yes") // only "no" disables archiving
        }
    }
}