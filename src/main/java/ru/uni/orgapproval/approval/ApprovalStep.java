package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.model.Employee;

import java.util.Objects;

public class ApprovalStep {
    private final ApprovalRule rule;
    private Employee assignedApprover;
    private boolean completed;

    public ApprovalStep(ApprovalRule rule) {
        this.rule = Objects.requireNonNull(rule);
        this.completed = false;
    }

    public ApprovalRule getRule() {
        return rule;
    }
    public Employee getAssignedApprover() {
        return assignedApprover;
    }

    public void setAssignedApprover(Employee assignedApprover) {
        this.assignedApprover = assignedApprover;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
