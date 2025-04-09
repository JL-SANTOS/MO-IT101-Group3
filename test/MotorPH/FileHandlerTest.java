package MotorPH;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

public class FileHandlerTest {

    private static final String TEST_EMPLOYEE_FILE = "test_employees.csv";

    @Before
    public void setUp() throws IOException {
        // Write a sample CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_EMPLOYEE_FILE))) {
            writer.println(String.join(",", Global.FIELD_NAMES));
            writer.println("1,John,Doe,01/01/1990,123 Street,09171234567,SSS123,PH123,TIN123,PAGIBIG123,Single,Developer,Jane Smith,20000,1500,1000,1000,10000,125");
        }
    }

    @Test
    public void testLoadEmployees() {
        List<Employee> employees = FileHandler.loadEmployees(TEST_EMPLOYEE_FILE);
        assertEquals(1, employees.size());

        Employee emp = employees.get(0);
        assertEquals(1, emp.getEmployeeId());
        assertEquals("John", emp.getFirstName());
        assertEquals("Doe", emp.getLastName());
        assertEquals(20000.0, emp.getBasicSalary(), 0.01);
    }

    @Test
    public void testSaveEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(
            2, "Smith", "Anna", "02/02/1992", "456 Avenue", "09181234567",
            "SSS456", "PH456", "TIN456", "PAGIBIG456", "Married", "Designer",
            "John Doe", 25000, 1500, 800, 900, 12500, 156.25
        ));

        FileHandler.saveEmployees(TEST_EMPLOYEE_FILE, employees);
        List<Employee> loaded = FileHandler.loadEmployees(TEST_EMPLOYEE_FILE);

        assertEquals(1, loaded.size());
        assertEquals("Anna", loaded.get(0).getFirstName());
        assertEquals(25000, loaded.get(0).getBasicSalary(), 0.01);
    }

    @After
    public void tearDown() {
        new File(TEST_EMPLOYEE_FILE).delete(); // Clean up test file
    }
}
