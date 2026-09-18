package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.exception.ApprovalException;
import ru.uni.orgapproval.model.Department;
import ru.uni.orgapproval.model.Employee;

public final class DepartmentHeadRule implements ApprovalRule{
    @Override
    public Employee findApprover(Document document) {
        Department dept = document.getAuthor().getDepartment();

        if (dept == null || dept.getHead() == null) {
            throw new ApprovalException("У автора нет отдела или в отделе нет руководителя!");
        }
        return dept.getHead();
    }
}
