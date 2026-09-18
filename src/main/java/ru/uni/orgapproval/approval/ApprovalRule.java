package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.model.Employee;

sealed interface ApprovalRule permits DirectManagerRule, DepartmentHeadRule, RoleBasedRule {
    Employee findApprover(Document document);
}
