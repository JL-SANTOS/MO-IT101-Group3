package MotorPH;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit Test Suite for EmployeeManager
 * Covers:
 * - Adding valid/invalid employees
 * - Editing salary (with derived field recalculation)
 * - Deleting employees
 * - Editable field mapping
 */
public class EmployeeManagerTest {

    private EmployeeManager manager;

    @Before
    public void setup() {
        manager = new EmployeeManager();
    }

    @Test
    public void testAddValidEmployee() {
        String[] inputs = new String[]{
                "Lia", "Lopez", "01/01/1995", "Pasig", "09991234567",
                "SSS123", "PH123", "TIN123", "PAG123", "Single", "HR", "BOSS",
                "30000", "1500", "1000", "1000"
        };
        manager.recordEmployee(inputs, 1);
        List<Employee> all = manager.getAllEmployees();
        assertEquals(1, all.size());
        assertEquals("Lia", all.get(0).getFirstName());
    }


    @Test(expected = IllegalArgumentException.class) 
    public void testAddInvalidEmployeeNegativeSalary() {
        String[] inputs = new String[]{
            "Leo", "Reyes", "01/01/1990", "QC", "09991112222",
            "SSS124", "PH124", "TIN124", "PAG124", "Married", "Sales", "HEAD",
            "-5000", "1500", "1000", "1000"
        };
        manager.recordEmployee(inputs, 2);
}


    @Test
    public void testEditBasicSalary() {
        String[] inputs = new String[]{
                "Marco", "Santos", "01/01/1988", "Makati", "09175556666",
                "SSS789", "PH789", "TIN789", "PAG789", "Single", "Clerk", "SUP",
                "16000", "1500", "1000", "1000"
        };
        manager.recordEmployee(inputs, 3);
        manager.updateEmployeeField(3, 13, 20000.0);

        Employee emp = manager.getEmployeeById(3);
        assertEquals(20000.0, emp.getBasicSalary(), 0.01);
        assertEquals(10000.0, emp.getSemiMonthly(), 0.01);
        assertEquals(125.0, emp.getHourlyRate(), 0.01);
    }

    @Test
    public void testDeleteEmployee() {
        String[] inputs = new String[]{
                "Rina", "Cruz", "02/02/1992", "Manila", "09178889999",
                "SSS888", "PH888", "TIN888", "PAG888", "Single", "IT", "CEO",
                "25000", "1500", "1000", "1000"
        };
        manager.recordEmployee(inputs, 4);
        manager.deleteEmployee(4);
        assertNull(manager.getEmployeeById(4));
    }

    @Test
    public void testEditableFieldMapping() {
        String[] editable = manager.getEditableFieldNames();
        int fieldIndex = manager.getFieldIndexFromEditableIndex(1); // First editable field = First Name
        assertEquals("First Name", editable[0]);
        assertEquals(1, fieldIndex);
    }
}
