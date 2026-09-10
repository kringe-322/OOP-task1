package ru.uni.orgapproval.demo;

import ru.uni.orgapproval.model.Department;
import ru.uni.orgapproval.model.Employee;
import ru.uni.orgapproval.model.Role;


public class Main {
    static void main() {
        Department IT = new Department("IT",null);
        Department dev = new Department("Разработка", null);
        Department accounting = new Department("Бухгалтерия", null);

        Employee ivan = new Employee("E1", "Иван Иванов", Role.DEVELOPER, dev);

        dev.addEmployee(ivan);
        dev.setHead(ivan);

        System.out.println(dev.getEmployees());

        IT.addSubDepartment(dev);
        dev.addSubDepartment(accounting);
        System.out.println(IT.getChildren());
    }
}

