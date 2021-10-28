
package com.nikkijuk.pigeongram.process

import org.assertj.core.api.Assertions
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

// source of example:
// https://github.com/camunda-community-hub/camunda-bpm-junit5/tree/master/examples/camunda-bpm-assert

// NOTE: this example is not enough to run processes which use @Named beans injected by spring boot

@ExtendWith(ProcessEngineExtension::class)
class SimpleProcessTestCase {

  @Test
  @Deployment(resources = ["simple_process.bpmn"])
  fun shouldExecuteProcess() {
    val processName = "simpleProcess"

    val processParams = mapOf(
      Pair("param1", "a"),
      Pair("param2", "b")
    )

    // Given we create a new process instance
    val processInstance = BpmnAwareTests.runtimeService().startProcessInstanceByKey(
      processName, processParams)

    // Then it should be active
    BpmnAwareTests.assertThat(processInstance).isActive

    // And it should be the only instance
    Assertions.assertThat(BpmnAwareTests.processInstanceQuery().count()).isEqualTo(1)

    // And there should exist just a single task within that process instance
    BpmnAwareTests.assertThat(BpmnAwareTests.task(processInstance)).isNotNull

    // When we complete that task
    BpmnAwareTests.complete(BpmnAwareTests.task(processInstance))

    // Then the process instance should be ended
    BpmnAwareTests.assertThat(processInstance).isEnded
  }
}