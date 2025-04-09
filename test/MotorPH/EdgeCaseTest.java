package MotorPH;

import org.junit.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.temporal.WeekFields;


import static org.junit.Assert.*;

public class EdgeCaseTest {

    private static final String EMP_FILE = "test_employees.csv";
    private static final String ATT_FILE = "test_attendance.csv";

    private EmployeeManager empManager;
    private AttendanceManager attManager;

    @Before
    public void setup() {
        empManager = new EmployeeManager();
        attManager = new AttendanceManager();
    }

    private int createTestEmployee(String firstName, int salary) {
        String[] inputs = {
            firstName, "Test", "01/01/1990", "123 Edge Street", "09000000000",
            "SSS123", "PH123", "TIN123", "PAG123", "Single", "Dev", "Boss",
            String.valueOf(salary), "1500", "1000", "1000"
        };
        int id = empManager.getNextEmployeeId();
        empManager.recordEmployee(inputs, id);
        FileHandler.saveEmployees(EMP_FILE, empManager.getAllEmployees());
        return id;
    }

    private void loadState(EmployeeManager emp, AttendanceManager att) {
        emp.loadEmployees(EMP_FILE);
        att.loadAttendance(ATT_FILE);
    }

    @Test
    public void testZeroHoursWorkedResultsInZeroSalary() {
        int id = createTestEmployee("Zero", 30000);

        // Clock-in and clock-out are the same
        List<String[]> logs = new ArrayList<>();
        logs.add(new String[]{
            String.valueOf(id), "2024", "15", "04/15/2024", "09:00", "09:00", "0.00"
        });
        FileHandler.saveAttendance(ATT_FILE, logs);

        loadState(empManager, attManager);
        double hours = attManager.getMonthlyHours(id, 4, 2024);
        assertEquals(0.0, hours, 0.01);
    }

    @Test
    public void testNegativeHoursAreSkipped() {
        int id = createTestEmployee("Backwards", 30000);

        // Clock-out before clock-in
        List<String[]> logs = new ArrayList<>();
        logs.add(new String[]{
            String.valueOf(id), "2024", "15", "04/15/2024", "16:00", "08:00", "0.00"
        });
        FileHandler.saveAttendance(ATT_FILE, logs);

        loadState(empManager, attManager);
        double hours = attManager.getMonthlyHours(id, 4, 2024);
        assertEquals(0.0, hours, 0.01);
    }

    @Test
    public void testInvalidTimeFormatIsHandled() {
        int id = createTestEmployee("InvalidTime", 30000);

        // Invalid clock-in time
        List<String[]> logs = new ArrayList<>();
        logs.add(new String[]{
            String.valueOf(id), "2024", "15", "04/15/2024", "abc", "16:00", "0.00"
        });
        FileHandler.saveAttendance(ATT_FILE, logs);

        loadState(empManager, attManager);
        double hours = attManager.getMonthlyHours(id, 4, 2024);
        assertEquals(0.0, hours, 0.01);
    }

    @Test
    public void testEndOfMonthDeductionApplied() {
        int id = createTestEmployee("EndMonth", 32000);

        // April 28, 2024 is a Sunday, part of the last week of April
        LocalDate date = LocalDate.of(2024, 4, 28);
        int week = date.get(WeekFields.ISO.weekOfYear());

        List<String[]> logs = new ArrayList<>();
        logs.add(new String[]{
            String.valueOf(id),
            String.valueOf(date.getYear()),
            String.valueOf(week),
            date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
            "08:00",
            "16:00",
            "8.00"
        });
        FileHandler.saveAttendance(ATT_FILE, logs);

        loadState(empManager, attManager);

        Employee emp = empManager.getEmployeeById(id);
        assertNotNull(emp);

        // Call net salary calc which prints deductions only at end-of-month
        attManager.calculateNetSalary(id, empManager, 2024);
    }

    @Test
    public void testDuplicateAttendanceEntriesAreMerged() {
        int id = createTestEmployee("Dup", 32000);

        LocalDate date = LocalDate.of(2024, 4, 8);
        int week = date.get(WeekFields.ISO.weekOfYear());

        List<String[]> logs = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            logs.add(new String[]{
                String.valueOf(id),
                String.valueOf(date.getYear()),
                String.valueOf(week),
                date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                "08:00",
                "12:00",
                "4.00"
            });
        }
        FileHandler.saveAttendance(ATT_FILE, logs);

        loadState(empManager, attManager);
        double hours = attManager.getMonthlyHours(id, 4, 2024);

        // Should merge to 8 hours if logic aggregates
        assertEquals(8.0, hours, 0.01);
    }

    @After
    public void cleanup() {
        new File(EMP_FILE).delete();
        new File(ATT_FILE).delete();
    }
}
