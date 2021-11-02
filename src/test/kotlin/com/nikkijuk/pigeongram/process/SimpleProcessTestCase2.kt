
package com.nikkijuk.pigeongram.process

import org.assertj.core.api.Assertions
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.processInstanceQuery
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


// source of example:
// https://github.com/camunda-community-hub/camunda-bpm-junit5/tree/master/examples/camunda-bpm-assert

// NOTE: this example is not enough to run processes which use @Named beans injected by spring boot

@ExtendWith(ProcessEngineExtension::class)
class SimpleProcessTestCase2 {

  @Test
  @Deployment(resources = ["simple_process.bpmn"])
  fun shouldExecuteProcess() {
    val processName = "simpleProcess"

    val processParams = mapOf(
      Pair("param1", "a"),
      Pair("param2", "b")
    )

    // Given we create a new process instance
    val processInstance = runtimeService().startProcessInstanceByKey(
      processName, processParams)

    // Then it should be active
    assertThat(processInstance).isActive

    // And it should be the only instance
    Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1)

    // And there should exist just a single task within that process instance
    assertThat(task(processInstance)).isNotNull

    // When we complete that task
    complete(task(processInstance))

    // Then the process instance should be ended
    assertThat(processInstance).isEnded
  }
}