package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.document.DocumentStatus;
import ru.uni.orgapproval.exception.ApprovalException;
import ru.uni.orgapproval.model.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ApprovalRoute {
    private final Document document;
    private final List<ApprovalStep> steps = new ArrayList<>();
    private final List<ApprovalRecord> history= new ArrayList<>();
    private int currentStepIndex=0;

    public ApprovalRoute(Document document, List<ApprovalRule> rules) {
        this.document = Objects.requireNonNull(document, "Document must not be null");

        Objects.requireNonNull(rules, "rules must not be null");
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("Маршрут должен содержать хотя бы одно правило");
        }

        for (ApprovalRule rule : rules) {
            Employee approver = rule.findApprover(document);
            ApprovalStep step = new ApprovalStep(rule);
            step.setAssignedApprover(approver);
            this.steps.add(step);
        }

        this.document.setStatus(DocumentStatus.UNDER_PROCESSING);
    }

    public void approve(Employee approver, String comment) {
        if (isFinished()) {
            throw new ApprovalException("Согласование по документу завершено");
        }

        ApprovalStep curStep = steps.get(currentStepIndex);

        if(!curStep.getAssignedApprover().equals(approver)) {
            throw new ApprovalException("Сотрудник "+ approver.getFullName() + " не имеет права подписывать текущий шаг." +
                    " Ожидается " + curStep.getAssignedApprover().getFullName());
        }

        curStep.setCompleted(true);
        history.add(new ApprovalRecord(approver, true, LocalDate.now(), comment));

        currentStepIndex++;

        if(currentStepIndex >= steps.size()) {
            document.setStatus(DocumentStatus.APPROVED);
        }

    }

    public void reject(Employee approver, String comment) {
        if(isFinished()){
            throw new ApprovalException("Согласование по документу завершено");
        }

        ApprovalStep curStep = steps.get(currentStepIndex);

        if(!curStep.getAssignedApprover().equals(approver)) {
            throw new ApprovalException("Сотрудник " + approver.getFullName() +
                    " не имеет права отклонять текущий шаг!");
        }

        history.add(new ApprovalRecord(approver, false, LocalDate.now(), comment));
        document.setStatus(DocumentStatus.REJECTED);
    }

    public boolean isFinished() {
        return document.getStatus() == DocumentStatus.APPROVED ||
                document.getStatus() == DocumentStatus.REJECTED;
    }

    public Document getDocument() {
        return document;
    }

    public List<ApprovalStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public List<ApprovalRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
