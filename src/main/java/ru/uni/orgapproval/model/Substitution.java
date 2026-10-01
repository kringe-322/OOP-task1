package ru.uni.orgapproval.model;

import java.time.LocalDate;
import java.util.Objects;

public record Substitution(
        Employee substitute,
        LocalDate startDate,
        LocalDate endDate
) {
    public Substitution{
        Objects.requireNonNull(substitute, "Заместитель не может быть null");
        Objects.requireNonNull(startDate, "Начальная дата не может быть null");
        Objects.requireNonNull(endDate, "Конечная дата не может быть null");

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты окончания!");
        }
    }

    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate)&&!date.isAfter(endDate);
    }
}
