
package com.nikkijuk.pigeongram.process

import org.assertj.core.api.Assertions
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.processInstanceQuery
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension
import org.camunda.bpm.extension.mockito.DelegateExpressions.autoMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

// general testing concept described
// https://camunda.com/best-practices/testing-process-definitions/

// assertions doc
// https://github.com/camunda/camunda-bpm-assert/blob/master/docs/User_Guide_BPMN.md

// mocking
// https://github.com/camunda-community-hub/camunda-bpm-mockito

@ExtendWith(ProcessEngineExtension::class)
class SendDraftProcessTestCaseAutomocking {

  @BeforeEach
  fun setup() {
    // automocking allows running process without delegate classes
    autoMock("send_draft.bpmn");
  }

  @AfterEach
  fun teardown() {
    Mocks.reset()
  }

  @Test
  @Deployment(resources = ["send_draft.bpmn"])
  fun shouldExecuteProcess() {

    // Given we create a new process instance
    val processInstance: ProcessInstance = runtimeService().createProcessInstanceByKey(
      "SendDraftProcess").
      setVariables(
        withVariables(
        "archive", "yes",
        "draftId", "123"
        )
      )
      .startBeforeActivity("ArchiveMessageActivity") // start with last step
      .execute()

    // started with right definition?
    assertThat(processInstance).hasProcessDefinitionKey("SendDraftProcess");

    // just see that variables are there
    assertThat(processInstance).hasVariables ("archive", "draftId")

    // active and only instance running
    assertThat(processInstance).isActive
    Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1)

   // service task is called job, so here we need to see that job exists and then execute it
   assertThat(job("ArchiveMessageActivity", processInstance)).isNotNull
   execute(job("ArchiveMessageActivity", processInstance))

   // finished
   assertThat(processInstance).isEnded
  }
}