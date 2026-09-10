package ru.uni.orgapproval.model;

import java.util.Objects;

public class Employee {
    private final String id;
    private String fullName;
    private Role role;
    private Department department;

    public Employee(String id, String fullName, Role role,Department department){
        this.id= Objects.requireNonNull(id, "ID не может быть null");
        this.fullName= Objects.requireNonNull(fullName, "fullname не может быть null");
        this.role= Objects.requireNonNull(role, "role не может быть null");
        this.department=department;
    }

    public String getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }
    public Role getRole() {
        return role;
    }
    public Department getDepartment() {
        return department;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        return Objects.equals(id,employee.id);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Employee{"+
                "id=" +'\''+ id+ '\'' +
                ", fullName="+ '\'' +fullName + '\''+
                ", role=" + role +
                ", department=" + (department!= null ? department.getName(): "Без отдела") +
                '}';
    }
}
