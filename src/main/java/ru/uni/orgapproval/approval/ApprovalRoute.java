package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.document.DocumentStatus;
import ru.uni.orgapproval.exception.ApprovalException;
import ru.uni.orgapproval.model.Department;
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

    /**
     * Формирует маршрут согласования для документа на основе переданного списка правил.
     *
     * @param document согласуемый документ
     * @param rules упорядоченный список правил выбора согласующих
     * @throws IllegalArgumentException если список правил пуст
     */
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

    /**
     * Согласует текущий шаг маршрута от имени указанного сотрудника.
     *
     * @param approver сотрудник, согласующий шаг
     * @param comment комментарий к согласованию
     * @throws ApprovalException если согласование завершено или сотрудник не имеет права подписи
     */
    public void approve(Employee approver, String comment) {
        approve(approver, LocalDate.now(), comment);
    }

    public void approve(Employee approver, LocalDate date, String comment) {
        if (isFinished()) {
            throw new ApprovalException("Согласование по документу завершено");
        }

        ApprovalStep curStep = steps.get(currentStepIndex);

        Employee actualExpectedApprover = curStep.getAssignedApprover().getActualApprover(date);

        if (!actualExpectedApprover.equals(approver)) {
            throw new ApprovalException("Сотрудник " + approver.getFullName() +
                    " не имеет права подписывать шаг. Ожидается: " + actualExpectedApprover.getFullName());
        }

        curStep.setCompleted(true);
        history.add(new ApprovalRecord(approver, true, date, comment));

        currentStepIndex++;

        if (currentStepIndex < steps.size()) {
            steps.get(currentStepIndex).setActivatedAt(date);
        }
        if (currentStepIndex >= steps.size()) {
            document.setStatus(DocumentStatus.APPROVED);
        }
    }
    /**
     * Отклоняет документ на текущем шаге.
     *
     * @param approver сотрудник, отклоняющий шаг
     * @param comment причина отклонения
     * @throws ApprovalException если согласование завершено или сотрудник не имеет права подписи
     */
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

    public boolean escalateIfOverdue(LocalDate currentDate) {
        if(isFinished()){
            return false;
        }

        ApprovalStep curStep = steps.get(currentStepIndex);

        LocalDate deadline = curStep.getActivatedAt().plus(curStep.getDeadline());

        if (currentDate.isAfter(deadline)) {
            Employee slowApprover = curStep.getAssignedApprover();
            Department dept = slowApprover.getDepartment();

            if (dept == null) {
                throw new ApprovalException("Невозможно эскалировать: у согласующего нет отдела");
            }

            // Ищем начальника на 1 уровень выше того, кто просрочил дедлайн
            Employee boss = dept.findManagerAbove(1)
                    .orElseThrow(() -> new ApprovalException("Невозможно эскалировать: вышестоящего руководителя нет"));

            // Переназначаем шаг на начальника!
            curStep.setAssignedApprover(boss);
            // Сбрасываем таймер для нового согласующего
            curStep.setActivatedAt(currentDate);

            // Фиксируем факт эскалации в истории!
            history.add(new ApprovalRecord(
                    slowApprover,
                    false,
                    currentDate,
                    "ПРОСРОЧЕНО (дедлайн " + deadline + "). Шаг эскалирован на: " + boss.getFullName()
            ));

            return true;
        }

        return false;
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
