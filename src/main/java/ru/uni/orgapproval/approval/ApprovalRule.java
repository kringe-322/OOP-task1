package ru.uni.orgapproval.approval;

import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.model.Employee;

sealed public interface ApprovalRule permits DirectManagerRule, DepartmentHeadRule, RoleBasedRule {
    /**
     * Полиморфный метод: определяет сотрудника-согласующего для переданного документа
     * на основе конкретного бизнес-правила.
     *
     * @param document согласовываемый документ
     * @return сотрудник, назначенный согласующим
     * @throws ru.uni.orgapproval.exception.ApprovalException если согласующего найти невозможно
     */
    Employee findApprover(Document document);
}
