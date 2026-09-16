package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.uni.orgapproval.model.Department;
import ru.uni.orgapproval.model.Employee;
import ru.uni.orgapproval.model.Role;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentTest {
    private Department headOffice;
    private Department itDept;
    private Department devDept;

    private Employee ceo;
    private Employee cto;
    private Employee lead;
    private Employee dev;

    @BeforeEach
    void setUp() {

        headOffice = new Department("Головной офис", null);
        itDept = new Department("IT Департамент", headOffice);
        devDept = new Department("Отдел разработки", itDept);


        ceo = new Employee("1", "Иван Гендиректор", Role.DEVELOPER, headOffice);
        cto = new Employee("2", "Петр Техдиректор", Role.DEVELOPER, itDept);
        lead = new Employee("3", "Алексей Тимлид", Role.DEVELOPER, devDept);
        dev = new Employee("4", "Василий Разработчик", Role.DEVELOPER, devDept);


        headOffice.setHead(ceo);
        itDept.setHead(cto);
        devDept.setHead(lead);


        devDept.addEmployee(dev);
    }

    @Test
    void testFindManagerAbove() {

        assertEquals(Optional.of(lead), devDept.findManagerAbove(0));

        assertEquals(Optional.of(cto), devDept.findManagerAbove(1));

        assertEquals(Optional.of(ceo), devDept.findManagerAbove(2));

        assertEquals(Optional.empty(), devDept.findManagerAbove(3));
    }

    @Test
    void testFindManagerAboveNegativeLevelThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            devDept.findManagerAbove(-1);
        });
    }

    @Test
    void testGetAllSubordinates() {
        List<Employee> all = headOffice.getAllSubordinates();

        assertEquals(4, all.size());
        assertTrue(all.contains(ceo));
        assertTrue(all.contains(cto));
        assertTrue(all.contains(lead));
        assertTrue(all.contains(dev));
    }
}
