package ru.uni.orgapproval.model;

import java.util.*;

public class Department {
    private final String name;
    private Employee head;
    private final List<Employee> employees;
    private Department parent;
    private final List<Department> children;

    /**
     * Создает новое подразделение с указанным именем и родительским отделом.
     *
     * @param name название подразделения (не null)
     * @param parent родительское подразделение (может быть null для корня компании)
     */
    public Department(String name, Department parent){
        this.name = Objects.requireNonNull(name, "name не может быть null");
        this.parent = parent;
        this.employees = new ArrayList<>();
        this.children = new ArrayList<>();
        this.head = null;

        if (parent != null) {
            parent.children.add(this);
        }
    }

    public String getName() {
        return name;
    }
    public Employee getHead() {
        return head;
    }
    public List<Employee> getEmployees() {
        return Collections.unmodifiableList(employees);
    }
    public Department getParent() {
        return parent;
    }
    public List<Department> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void setHead(Employee head) {
        if (head != null && !employees.contains(head)){
            employees.add(head);
        }
        this.head = head;
    }
    public void setParent(Department parent) {
        if (parent == this) {
            throw new IllegalArgumentException("Отдел не может быть родителем самого себя!");
        }

        this.parent = parent;
    }

    /**
     * Добавляет сотрудника в подразделение и устанавливает двустороннюю связь.
     *
     * @param employee добавляемый сотрудник (не null)
     */
    public void addEmployee(Employee employee){
        Objects.requireNonNull(employee, "employee не может быть null");
        employees.add(employee);
        employee.setDepartment(this);
    }
    public void addSubDepartment(Department subDepatment){
        Objects.requireNonNull(subDepatment, "subDepartment не может быть null");
        children.add(subDepatment);
        subDepatment.parent = this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Department that = (Department) obj;
        return Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "Department{"+
                "name=" +'\''+name+ '\'' +
                ", head=" +'\''+(head!=null ? head.getFullName() : "Не назначен") + '\''+
                ", employees=" + employees.size() +
                ", children=" + children.size() +
                '}';
    }

    /**
     * Выполняет поиск руководителя на указанное количество уровней вверх по дереву.
     *
     * @param levelsUp количество уровней иерархии для подъема (0 - текущий отдел)
     * @return Optional с найденным руководителем либо Optional.empty(), если уровень выше корня
     * @throws IllegalArgumentException если передан отрицательный уровень
     */
    public Optional<Employee> findManagerAbove(int levelsUp){
        if (levelsUp < 0) {
            throw new IllegalArgumentException("Уровень не может быть отрицательным: " + levelsUp);
        }

        Department cur = this;

        for (int i = 0; i < levelsUp; i++) {
            cur = cur.getParent();

            if (cur == null) {
                return Optional.empty();
            }
        }

        return Optional.ofNullable(cur.getHead());
    }

    /**
     * Рекурсивно собирает всех сотрудников текущего подразделения и всех его дочерних отделов вглубь.
     *
     * @return неизменяемый список всех сотрудников в поддереве
     */
    public List<Employee> getAllSubordinates() {
        List<Employee> subordinates = new ArrayList<>();
            subordinates.addAll(employees);
            this.getChildren().forEach(c -> subordinates.addAll
                    (c.getAllSubordinates()));
            return  Collections.unmodifiableList(subordinates);
    }
}
