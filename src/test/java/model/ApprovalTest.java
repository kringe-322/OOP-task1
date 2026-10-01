package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.uni.orgapproval.approval.ApprovalRoute;
import ru.uni.orgapproval.approval.ApprovalRule;
import ru.uni.orgapproval.approval.DirectManagerRule;
import ru.uni.orgapproval.approval.RoleBasedRule;
import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.document.DocumentStatus;
import ru.uni.orgapproval.exception.ApprovalException;
import ru.uni.orgapproval.model.Department;
import ru.uni.orgapproval.model.Employee;
import ru.uni.orgapproval.model.Role;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApprovalTest {

    private Department headOffice;
    private Department itDept;
    private Department devDept;
    private Department accDept;

    private Employee ceo;
    private Employee cto;
    private Employee lead;
    private Employee junior;
    private Employee accountant;

    @BeforeEach
    void setUp() {
        headOffice = new Department("Головной офис", null);
        itDept = new Department("IT", headOffice);
        devDept = new Department("Dev", itDept);
        accDept = new Department("Бухгалтерия", headOffice);

        ceo = new Employee("1", "Гендир", Role.DEVELOPER, headOffice);
        headOffice.setHead(ceo);

        cto = new Employee("2", "Техдир", Role.DEVELOPER, itDept);
        itDept.setHead(cto);

        lead = new Employee("3", "Тимлид", Role.DEVELOPER, devDept);
        devDept.setHead(lead);

        junior = new Employee("4", "Джун", Role.DEVELOPER, devDept);
        devDept.addEmployee(junior);

        accountant = new Employee("5", "Бухгалтер", Role.ACCOUNTANT, accDept);
        accDept.setHead(accountant);
    }

    // ТЕСТ 4: Прямой начальник для обычного сотрудника (Джуна) — это Тимлид
    @Test
    void testDirectManagerForRegularEmployee() {
        Document doc = new Document("D-1", "Заявка", junior);
        ApprovalRule rule = new DirectManagerRule();

        Employee approver = rule.findApprover(doc);
        assertEquals(lead, approver);
    }

    // ТЕСТ 5: Прямой начальник для Тимлида — это начальник отдела выше (Техдир!)
    @Test
    void testDirectManagerForDepartmentHeadEscalates() {
        Document doc = new Document("D-2", "Заявка тимлида", lead);
        ApprovalRule rule = new DirectManagerRule();

        Employee approver = rule.findApprover(doc);
        assertEquals(cto, approver);
    }

    // ТЕСТ 6: Поиск по роли находит бухгалтера в компании
    @Test
    void testRoleBasedRuleFindsAccountant() {
        Document doc = new Document("D-3", "Счет", junior);
        ApprovalRule rule = new RoleBasedRule(Role.ACCOUNTANT);

        Employee approver = rule.findApprover(doc);
        assertEquals(accountant, approver);
    }

    // ТЕСТ 7: Поиск несуществующей роли выбрасывает ApprovalException
    @Test
    void testRoleBasedRuleNotFoundThrowsException() {
        Document doc = new Document("D-4", "Договор", junior);
        // В нашей компании нет юристов
        ApprovalRule rule = new RoleBasedRule(Role.LAWYER);

        assertThrows(ApprovalException.class, () -> rule.findApprover(doc));
    }
    // ТЕСТ 8: Успешный прогон документа по цепочке шагов (статус становится APPROVED)
    @Test
    void testApprovalRouteHappyPath() {
        Document doc = new Document("D-5", "Покупка монитора", junior);
        ApprovalRoute route = new ApprovalRoute(doc, List.of(
                new DirectManagerRule(),
                new RoleBasedRule(Role.ACCOUNTANT)
        ));

        assertEquals(DocumentStatus.UNDER_PROCESSING, doc.getStatus());

        // Шаг 1: Тимлид одобряет
        route.approve(lead, "Ок");
        assertEquals(DocumentStatus.UNDER_PROCESSING, doc.getStatus());

        // Шаг 2: Бухгалтер одобряет (финал)
        route.approve(accountant, "Оплачено");
        assertEquals(DocumentStatus.APPROVED, doc.getStatus());
        assertEquals(2, route.getHistory().size());
    }

    // ТЕСТ 9: Попытка согласовать чужой шаг выбрасывает исключение ApprovalException
    @Test
    void testApproveForeignStepThrowsException() {
        Document doc = new Document("D-6", "Отгул", junior);
        ApprovalRoute route = new ApprovalRoute(doc, List.of(new DirectManagerRule()));

        // Джуниор пытается сам себе подписать шаг за Тимлида!
        assertThrows(ApprovalException.class, () -> {
            route.approve(junior, "Сам себе подпишу");
        });
    }

    // ТЕСТ 10: Отклонение документа переводит статус в REJECTED
    @Test
    void testRejectDocument() {
        Document doc = new Document("D-7", "Премия", junior);
        ApprovalRoute route = new ApprovalRoute(doc, List.of(new DirectManagerRule()));

        route.reject(lead, "Отказано, мало работал");

        assertEquals(DocumentStatus.REJECTED, doc.getStatus());
        assertTrue(route.isFinished());
        assertFalse(route.getHistory().get(0).approved());
    }

    // ТЕСТ 11: Попытка подписать уже закрытый документ выбрасывает ошибку
    @Test
    void testApproveAfterFinishedThrowsException() {
        Document doc = new Document("D-8", "Заявка", junior);
        ApprovalRoute route = new ApprovalRoute(doc, List.of(new DirectManagerRule()));

        route.reject(lead, "Отклонено");

        // Пытаемся подписать отклоненный документ
        assertThrows(ApprovalException.class, () -> {
            route.approve(lead, "Передумал");
        });
    }

    // ТЕСТ 12: Проверка контракта equals и hashCode (ОБЯЗАТЕЛЬНО ПО ТЗ)
    @Test
    void testEmployeeEqualsAndHashCodeContract() {
        Employee emp1 = new Employee("100", "Иван", Role.DEVELOPER, devDept);
        Employee emp2 = new Employee("100", "Иван ДРУГОЙ", Role.TESTER, itDept);
        Employee emp3 = new Employee("200", "Иван", Role.DEVELOPER, devDept);

        // 1. Рефлексивность: объект равен сам себе
        assertEquals(emp1, emp1);

        // 2. Одинаковый id => объекты равны (даже если имена и отделы разные!)
        assertEquals(emp1, emp2);
        assertEquals(emp2, emp1);

        // 3. Одинаковый id => хэш-коды ОБЯЗАНЫ совпадать
        assertEquals(emp1.hashCode(), emp2.hashCode());

        // 4. Разные id => объекты НЕ равны
        assertNotEquals(emp1, emp3);

        // 5. Безопасность при сравнении с null и другим типом
        assertNotEquals(null, emp1);
        assertNotEquals("Строка", emp1);
    }
}