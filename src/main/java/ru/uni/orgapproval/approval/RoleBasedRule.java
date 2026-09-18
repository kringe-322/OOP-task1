package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.exception.ApprovalException;
import ru.uni.orgapproval.model.Department;
import ru.uni.orgapproval.model.Employee;
import ru.uni.orgapproval.model.Role;

import java.util.Objects;

public final class RoleBasedRule implements ApprovalRule {

    private final Role targetRole;

    public RoleBasedRule(Role targetRole) {
        this.targetRole = Objects.requireNonNull(targetRole, "Роль не может быть null");
    }

    public Role getTargetRole() {
        return targetRole;
    }

    @Override
    public Employee findApprover(Document document) {
        Department dept = document.getAuthor().getDepartment();
        if (dept == null) {
            throw new ApprovalException(String.format(
                    "У автора документа '%s' не указано подразделение", document.getAuthor().getFullName()));        }

        Department root = dept;
        while (root.getParent() != null) {
            root = root.getParent();
        }

        for(Employee e: root.getAllSubordinates()) {
            if(e.getRole() == targetRole) {
                return e;
            }
        }

        throw new ApprovalException("В компании не найден сотрудник с ролью: " +targetRole);
    }
}
