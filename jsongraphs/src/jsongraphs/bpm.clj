(ns jsongraphs.bpm)

;; Businss Process as Graph

;; Entperise Business Automation
;; https://www.activiti.org/userguide/
;; https://flowable.com/products/flowable-core/
;; https://camunda.com/
;; BPMN-DMN-CMMN

#{:bpmn/definitions
  :bpmn/dataObject
  :bpmn/callActivity ;sub-process
  :bpmn/signal
  :bpmn/process
  :bpmn/message
  :bpmn/error
  :bpmn/association
  :bpmn/dataInputAssociation
  :bpmn/dataOutputAssociation
  :bpmn/conditionExpression
  :bpmn/timeDate
  :bpmn/timeDuration
  :bpmn/timeCycle
  ;; gateways
  :bpmn/sequenceFlow
  :bpmn/exclusiveGateway
  :bpmn/parallelGateway
  :bpmn/eventGateway
  :bpmn/inclusiveGateway ; fork-join


  ;; events
  :bpmn/startEvent 
  :bpmn/endEvent ;noneEndEvent, errorEndEvent
  :bpmn/boundaryEvent
  :bpmn/intermediateThrowEvent
  :bpmn/intermediateCatchEvent
  :bpmn/timerEventDefinition
  :bpmn/errorEventDefinition
  :bpmn/terminateEventDefinition
  :bpmn/cancelEventDefinition
  :bpmn/messageEventDefinition
  :bpmn/signalEventDefinition
  :bpmn/compensateEventDefinition


  :bpmn/serviceTask
  :bpmn/userTask
  :bpmn/manualTask
  :bpmn/scriptTask
  :bpmn/businssRuleTask
  :bpmn/receiveTask


  }

;;;;;;;;;;;;;;;;;;;;;;;;
;; Examples
;;;;;;;;;;;;;;;;;;;;;;;

;; None Start Event
{:bpmn/startEvent {}}

;; Timer Start Event
{:bpmn/startEvent {:bpmn/timerEventDefinition {:bpmn/timeCycle ""}}}
;;

;; Message start Event
{:bpmn/definitions
 {:bpmn/message {}
  :bpmn/process #{{:bpmn/startEvent {:bpmn/messageEventDefinition {:bpmn/messageRef "tns:newInvoice"}}}}}}

;; Signal Start Event
;;

{:bpmn/signal {:id "theSignal"}
 :bpmn/process
   #{{:bpmn/startEvent
      {:id "theStart"
       :bpmn/signalEventDefintion
         {:id ""
          :signalRef "theSignal"}}}}}


;; Error End Event
{:bpmn/endEvent {:bpmn/errorEventDefinition {:errorRef ""}}}








;; BPMN Process Example
{
 :graph
 {:id "definitionsID",
  :type: "bpmn20",
  :label: "BPMN MODEL",
  :metadata: {}
  :nodes: #{{:id "financialReportID"
            :type :process
            :name "Monthly financial report reminder process"},
           {:id "theStartID"
            :type :startEvent}
           {:id "flow1ID"
            :type :sequenceFlow
            :name ""}
           {:id "writeReportTaskID"
            :type :userTask
            :metadata {:potentialOwner [{:resourceAssignmentExpression "accountancy"}]
                       :doc "Write Monthly financial report for publication to shareholders"}}
            {:id "verifyReportTaskID"
             :type :userTask
             :metadata {:potentialOwner [{:resourceAssignmentExpression "management"}]
                        :doc "verify monthly report to shareholders"}}
            {:id "theEndID"
             :type :endEvent}

   }

  :edges: [{:id "flow1ID"
            :source "theStartID"
            :target "writeReportTaskID"
            :type :sequenceFlow}
           {:id "flow2ID"
            :source "writeReportTaskID"
            :target "verifyReportTaskID"
            :type :sequenceFlow}
           {:id "flow3ID"
            :source "verifyReportTaskID"
            :target "theEnd"
            :type :sequenceFlow}]
  }
 }
