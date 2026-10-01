package ru.uni.orgapproval.demo;

import ru.uni.orgapproval.approval.*;
import ru.uni.orgapproval.document.Document;
import ru.uni.orgapproval.exception.ApprovalException;
import ru.uni.orgapproval.model.Department;
import ru.uni.orgapproval.model.Employee;
import ru.uni.orgapproval.model.Role;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   ДЕМОНСТРАЦИЯ СИСТЕМЫ ОРГСТРУКТУРЫ И СОГЛАСОВАНИЯ");
        System.out.println("==================================================\n");

        // --- 1. СОЗДАНИЕ ДЕРЕВА ПОДРАЗДЕЛЕНИЙ ---
        Department headOffice = new Department("Головной офис", null);

        Department itDept = new Department("IT Департамент", headOffice);
        Department devDept = new Department("Отдел разработки", itDept);
        Department qaDept = new Department("Отдел тестирования", itDept);

        Department accountingDept = new Department("Бухгалтерия", headOffice);
        Department hrDept = new Department("Отдел кадров", headOffice);

        // --- 2. СОЗДАНИЕ СОТРУДНИКОВ (НАПОЛНЯЕМ ОБЪЕКТАМИ) ---
        // Руководство
        Employee ceo = new Employee("E01", "Алексеев А.А. (Гендиректор)", Role.DEVELOPER, headOffice);
        headOffice.setHead(ceo);

        Employee cto = new Employee("E02", "Борисов Б.Б. (Техдиректор)", Role.DEVELOPER, itDept);
        itDept.setHead(cto);

        // Отдел разработки
        Employee leadDev = new Employee("E03", "Волков В.В. (Тимлид Dev)", Role.DEVELOPER, devDept);
        devDept.setHead(leadDev);
        Employee dev1 = new Employee("E04", "Григорьев Г.Г. (Junior Dev)", Role.DEVELOPER, devDept);
        Employee dev2 = new Employee("E05", "Дмитриев Д.Д. (Senior Dev)", Role.DEVELOPER, devDept);
        devDept.addEmployee(dev1);
        devDept.addEmployee(dev2);

        // Отдел тестирования
        Employee leadQa = new Employee("E06", "Елисеев Е.Е. (Лид QA)", Role.TESTER, qaDept);
        qaDept.setHead(leadQa);
        Employee qa1 = new Employee("E07", "Жуков Ж.Ж. (QA Engineer)", Role.TESTER, qaDept);
        qaDept.addEmployee(qa1);

        // Бухгалтерия
        Employee chiefAcc = new Employee("E08", "Зайцева З.З. (Главбух)", Role.ACCOUNTANT, accountingDept);
        accountingDept.setHead(chiefAcc);
        Employee acc1 = new Employee("E09", "Иванова И.И. (Бухгалтер)", Role.ACCOUNTANT, accountingDept);
        accountingDept.addEmployee(acc1);

        // Отдел кадров
        Employee hrHead = new Employee("E10", "Ковалева К.К. (HR Директор)", Role.LAWYER, hrDept);
        hrDept.setHead(hrHead);

        System.out.println(">>> Оргструктура успешно инициализирована!");
        System.out.println(">>> Всего сотрудников в компании: " + headOffice.getAllSubordinates().size());
        System.out.println();

        // =========================================================================
        // АКТ 2. ДЕМОНСТРАЦИЯ РАБОТЫ С ДЕРЕВОМ
        // =========================================================================
        System.out.println("--- ОПЕРАЦИЯ 1: Поиск руководителей по уровням вверх для Junior Dev ---");
        System.out.println("0 уровней вверх (в своем отделе): " +
                devDept.findManagerAbove(0).map(Employee::getFullName).orElse("Нет"));
        System.out.println("1 уровень вверх (начальник IT):     " +
                devDept.findManagerAbove(1).map(Employee::getFullName).orElse("Нет"));
        System.out.println("2 уровня вверх (Гендиректор):       " +
                devDept.findManagerAbove(2).map(Employee::getFullName).orElse("Нет"));
        System.out.println("5 уровней вверх (выход за пределы): " +
                devDept.findManagerAbove(5).map(Employee::getFullName).orElse("Не найден (пусто)"));


        System.out.println("\n--- ОПЕРАЦИЯ 2: Сбор подчинённых вглубь для IT Департамента ---");
        List<Employee> itStaff = itDept.getAllSubordinates();
        System.out.println("Всего людей в ветке IT: " + itStaff.size());
        itStaff.forEach(emp -> System.out.println("  • " + emp.getFullName()));
        System.out.println();

        // =========================================================================
        // АКТ 3. СОГЛАСОВАНИЕ ДОКУМЕНТА (УСПЕШНЫЙ СЦЕНАРИЙ)
        // =========================================================================
        System.out.println("--- ОПЕРАЦИЯ 3: Создание документа и построение маршрута ---");
        Document doc1 = new Document("DOC-001", "Заявка на покупку MacBook Pro", dev1);
        System.out.println("Создан документ: " + doc1.getTitle() + ", статус: " + doc1.getStatus());

        // Назначаем правила согласования
        List<ApprovalRule> rules = List.of(
                new DirectManagerRule(),
                new RoleBasedRule(Role.ACCOUNTANT)
        );

        ApprovalRoute route1 = new ApprovalRoute(doc1, rules);
        System.out.println("Маршрут построен! Статус документа стал: " + doc1.getStatus());
        System.out.println("Шаги согласования:");
        for (int i = 0; i < route1.getSteps().size(); i++) {
            ApprovalStep step = route1.getSteps().get(i);
            System.out.println("  Шаг " + (i + 1) + ": ожидает подписи от -> " + step.getAssignedApprover().getFullName());
        }

        System.out.println("\n--- Процесс подписания шагов ---");
        // Шаг 1: Тимлид согласует
        route1.approve(leadDev, "Одобряю, старый ноут совсем тормозит");
        System.out.println("Шаг 1 подписан Тимлидом. Статус документа: " + doc1.getStatus());

        // Шаг 2: Главбух согласует
        route1.approve(chiefAcc, "Бюджет на технику согласован");
        System.out.println("Шаг 2 подписан Бухгалтером. Статус документа: " + doc1.getStatus());

        System.out.println("\n--- История согласования документа DOC-001 ---");
        for (ApprovalRecord rec : route1.getHistory()) {
            System.out.printf("  [%s] %s | Решение: %s | Комментарий: \"%s\"%n",
                    rec.decisionDate(),
                    rec.approver().getFullName(),
                    rec.approved() ? "СОГЛАСОВАНО" : "ОТКЛОНЕНО",
                    rec.comment());
        }
        System.out.println();

        // =========================================================================
        // АКТ 4. ОБРАБОТКА ОШИБОК (ЧУЖОЙ ШАГ) И ОТКЛОНЕНИЕ ДОКУМЕНТА
        // =========================================================================
        System.out.println("==================================================");
        System.out.println("--- ОПЕРАЦИЯ 4: Проверка безопасности (чужой шаг) ---");

        // Тестировщик создает документ
        Document doc2 = new Document("DOC-002", "Заявка на командировку на DevConf", qa1);
        ApprovalRoute route2 = new ApprovalRoute(doc2, List.of(new DirectManagerRule()));

        System.out.println("Создан документ: " + doc2.getTitle());
        System.out.println("Ожидается подпись от: " + route2.getSteps().get(0).getAssignedApprover().getFullName());

        // ПОПЫТКА СОГЛАСОВАТЬ ЧУЖОЙ ШАГ:
        // Разработчик dev1 пытается подписать за Лида QA!
        System.out.println("\n[!] Разработчик " + dev1.getFullName() + " пытается подписать шаг за начальника...");
        try {
            route2.approve(dev1, "Я тут за начальника подмахну!");
        } catch (ApprovalException e) {
            System.out.println(">>> УСПЕШНЫЙ ПЕРЕХВАТ ОШИБКИ: " + e.getMessage());
            System.out.println(">>> Безопасность сработала! Чужой шаг не подписан.");
        }

        // НАСТОЯЩИЙ НАЧАЛЬНИК ОТКЛОНЯЕТ ДОКУМЕНТ:
        System.out.println("\n--- ОПЕРАЦИЯ 5: Отклонение документа законным согласующим ---");
        route2.reject(leadQa, "Отклонено: бюджет на командировки на этот квартал исчерпан");
        System.out.println("Статус документа DOC-002 после отклонения: " + doc2.getStatus());

        // Печатаем историю отклонения
        System.out.println("\n--- История согласования документа DOC-002 ---");
        for (ApprovalRecord rec : route2.getHistory()) {
            System.out.printf("  [%s] %s | Решение: %s | Причина: \"%s\"%n",
                    rec.decisionDate(),
                    rec.approver().getFullName(),
                    rec.approved() ? "СОГЛАСОВАНО" : "ОТКЛОНЕНО",
                    rec.comment());
        }

        System.out.println("\n==================================================");
        System.out.println("   ДЕМОНСТРАЦИЯ ВСЕХ ОПЕРАЦИЙ УСПЕШНО ЗАВЕРШЕНА!");
        System.out.println("==================================================");
    }
}
