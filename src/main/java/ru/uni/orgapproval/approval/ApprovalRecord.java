package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.model.Employee;

import java.time.LocalDate;
import java.util.Objects;

public record ApprovalRecord(
        Employee approver,
        boolean approved,
        LocalDate decisionDate,
        String comment) {

    public ApprovalRecord {
        Objects.requireNonNull(approver, "Согласующий не может быть null");
        Objects.requireNonNull(decisionDate, "Дата решения не может быть null");
        Objects.requireNonNull(comment, "Комментарий не может быть пустым");
    }
}
