package MotorPH;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * JUnit Test Suite for AttendanceManager class.
 * 
 * Tests the ability to:
 * - Record attendance
 * - Aggregate weekly/monthly hours
 * - Calculate standard government deductions
 * 
 * Author: Lyan
 */
public class AttendanceManagerTest {

    private AttendanceManager attendanceManager;
    private EmployeeManager employeeManager;
    private Employee testEmployee;

    @Before
    public void setup() {
        attendanceManager = new AttendanceManager();
        employeeManager = new EmployeeManager();

        // Add dummy employee with ID 1
        employeeManager.recordEmployee(new String[]{
            "Ana", "Reyes", "02/14/1991", "Cebu", "09181234567",
            "SSS456", "PH456", "TIN456", "PAG456", "Married", "Engineer", "HEAD",
            "30000", "1500", "1000", "1000"
        }, 1);

        testEmployee = employeeManager.getEmployeeById(1);
    }

    /**
     * Tests that hours are properly recorded into the correct week and year.
     */
    @Test
    public void testRecordAttendance() {
        LocalDate date = LocalDate.of(2024, 4, 5); // Week 14
        attendanceManager.recordAttendance(1, date, 8.0);

        Map<Integer, Map<Integer, Map<Integer, Double>>> data = attendanceManager.getAllAttendance();
        assertTrue(data.containsKey(1));
        assertTrue(data.get(1).containsKey(2024));
        assertTrue(data.get(1).get(2024).containsKey(14));

        assertEquals(8.0, data.get(1).get(2024).get(14), 0.01);
    }

    /**
     * Tests monthly aggregation of hours based on weekly data.
     */
    @Test
    public void testMonthlyHours() {
        attendanceManager.recordAttendance(1, LocalDate.of(2024, 3, 4), 8.0);
        attendanceManager.recordAttendance(1, LocalDate.of(2024, 3, 5), 4.0);
        attendanceManager.recordAttendance(1, LocalDate.of(2024, 3, 10), 6.0);

        double total = attendanceManager.getMonthlyHours(1, 3, 2024);
        assertEquals(18.0, total, 0.01);
    }

    /**
     * Tests the SSS contribution based on salary range.
     */
    @Test
    public void testSSSContribution() {
        double sss = AttendanceManager.calculateSSSContribution(30000);
        assertEquals(1125.00, sss, 0.01); // Max cap reached
    }

    /**
     * Tests PhilHealth computation with max contribution limit.
     */
    @Test
    public void testPhilHealthContribution() {
        double philHealth = AttendanceManager.calculatePhilHealthContribution(100000);
        assertEquals(1800.00, philHealth, 0.01);
    }

    /**
     * Tests Pag-IBIG deduction with capped value.
     */
    @Test
    public void testPagIbigContribution() {
        double pagIbig = AttendanceManager.calculatePagIbigContribution(10000);
        assertEquals(100.00, pagIbig, 0.01);
    }

    /**
     * Tests combined deductions and withholding tax logic.
     */
    @Test
    public void testTotalDeductions() {
        double total = AttendanceManager.calculateTotalDeductions(testEmployee);
        assertTrue(total > 0);
    }
}
