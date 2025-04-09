package MotorPH;

import org.junit.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;

public class IntegrationTest {

    private static final String EMP_FILE = "test_employees.csv";
    private static final String ATT_FILE = "test_attendance.csv";

    private EmployeeManager empManager;
    private AttendanceManager attManager;

    @Before
    public void setup() {
        empManager = new EmployeeManager();
        attManager = new AttendanceManager();
    }

    @Test
    public void testAddEmployeeAndAttendanceFlow() {
        // Step 1: Add employee
        String[] inputs = {
            "Alice", "Johnson", "01/01/1990", "456 Road", "09181234567",
            "SSS001", "PH001", "TIN001", "PAG001", "Single", "Engineer", "Bob Boss",
            "32000", "1500", "1000", "1000"
        };
        int newId = empManager.getNextEmployeeId();
        empManager.recordEmployee(inputs, newId);

        // Step 2: Save employees to file
        FileHandler.saveEmployees(EMP_FILE, empManager.getAllEmployees());

        // Step 3: Simulate 80 hours of attendance for April 8, 2024
        List<String[]> logs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LocalDate day = LocalDate.of(2024, 4, 1).plusDays(i);
            int week = day.get(java.time.temporal.WeekFields.ISO.weekOfYear());

            logs.add(new String[]{
                String.valueOf(newId),
                String.valueOf(day.getYear()),
                String.valueOf(week),
                day.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                "08:00",
                "16:00",
                "8.00"
            });
        }


        FileHandler.saveAttendance(ATT_FILE, logs);

        // Step 4: Reload data from files
        EmployeeManager loadedEmp = new EmployeeManager();
        loadedEmp.loadEmployees(EMP_FILE);

        AttendanceManager loadedAtt = new AttendanceManager();
        loadedAtt.loadAttendance(ATT_FILE);

        // Step 5: Validate employee and salary
        Employee emp = loadedEmp.getEmployeeById(newId);
        assertNotNull(emp);
        assertEquals("Alice", emp.getFirstName());

        double hours = loadedAtt.getMonthlyHours(newId, 4, 2024); // April
        assertEquals(80.0, hours, 0.01);

        double gross = emp.getHourlyRate() * hours;
        double net = gross - AttendanceManager.calculateTotalDeductions(emp);

        assertTrue("Gross salary should be > 0", gross > 0);
        assertTrue("Net salary should be > 0", net > 0);

        System.out.printf("✅ Gross: %.2f | Net: %.2f%n", gross, net);
    }

    @After
    public void cleanup() {
        new File(EMP_FILE).delete();
        new File(ATT_FILE).delete();
    }
}
