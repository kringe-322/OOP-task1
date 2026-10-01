package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.model.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

public class ApprovalStep {
    private final ApprovalRule rule;
    private Employee assignedApprover;
    private boolean completed;
    private Period deadline;
    private LocalDate activatedAt;

    public ApprovalStep(ApprovalRule rule) {
        this(rule, Period.ofDays(3));
    }


    public ApprovalStep(ApprovalRule rule, Period deadlinePeriod) {
        this.rule = Objects.requireNonNull(rule);
        this.deadline = Objects.requireNonNull(deadlinePeriod);
        this.completed = false;
        this.activatedAt = LocalDate.now();
    }

    public Period getDeadline() {
        return deadline;
    }
    public LocalDate getActivatedAt() {
        return activatedAt;
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
    public void setActivatedAt(LocalDate activatedAt) {
        this.activatedAt = activatedAt;
    }
}
