package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.exception.ApprovalException;
import ru.uni.orgapproval.model.Department;
import ru.uni.orgapproval.model.Employee;


public final class DirectManagerRule implements ApprovalRule {

    @Override
    public Employee findApprover(Document document) {
        Employee author = document.getAuthor();
        Department dept = author.getDepartment();

        if (dept == null) {
            throw new ApprovalException("У автора нету отдела"+ author.getFullName());
        }

        boolean isHead = dept.getHead() != null && dept.getHead().equals(author);
        int level = isHead ? 1 : 0;

        return  dept.findManagerAbove(level).orElseThrow(()-> new ApprovalException
                ("Не удалось найти непосредтсвенного руководителя для:" + author.getFullName()));
    }
}
