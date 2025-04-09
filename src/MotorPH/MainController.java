
package MotorPH;

/**
 *
 * @author santo
 */

import java.util.Scanner;
import java.util.InputMismatchException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;





class MainController {
    private final EmployeeManager employeeManager;
    private final AttendanceManager attendanceManager;
    private final Scanner scanner;

    public MainController(EmployeeManager employeeManager, AttendanceManager attendanceManager) {
        this.employeeManager = employeeManager;
        this.attendanceManager = attendanceManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
                
        while (true) {
            System.out.println("\nEmployee Management System");
            System.out.println("1. View Employees");
            System.out.println("2. Add Employee");
            System.out.println("3. Edit Employee");
            System.out.println("4. Delete Employee");
            System.out.println("5. View Weekly Hours");
            System.out.println("6. Show Weekly Gross Salary");
            System.out.println("7. Show Weekly Net Salary");
            System.out.println("8. Record Attendance");
            System.out.println("9. View Monthly Summary");
            System.out.println("10. Save and Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewEmployees();
                case 2 -> addEmployee();
                case 3 -> editEmployee();
                case 4 -> deleteEmployee();
                case 5 -> viewWeeklyHours();
                case 6 -> calculateWeeklySalary();
                case 7 -> calculateWeeklyNetSalary();
                case 8 -> recordAttendance();
                case 9 -> viewMonthlySummary();
                case 10 -> {
                    FileHandler.saveEmployees(Global.EMPLOYEE_FILE, employeeManager.getAllEmployees());
                    attendanceManager.saveAttendance(Global.ATTENDANCE_FILE);
                    System.out.println("Data saved. Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void viewEmployees() {
        
        System.out.println("\n1. View All Employees");
        System.out.println("2. View Employee by ID");        
        System.out.print("Choose an option: ");    
        int choice = scanner.nextInt();       
        scanner.nextLine(); // Consume newline 
        System.out.print("\n");
        
        switch (choice) {
            case 1 -> {
                for (Employee employee : employeeManager.getAllEmployees()) {
                    System.out.println(employee);
                    System.out.println("______________________________________________\n");
                }
            }
            
            case 2 -> {
                System.out.print("Enter Employee ID to view: ");
                int employeeId = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                System.out.print("\n");
                employeeManager.viewEmployee(employeeId);
                System.out.print("\n");
            }
        } 
        System.out.print("Press any key to continue...");    
        scanner.nextLine();          
    }
    
    private void addEmployee() {
        
        int newEmployeeId = employeeManager.getNextEmployeeId();
        
        System.out.println("\nCreating Employee #" + newEmployeeId);
        
        String[] editableFields = employeeManager.getEditableFieldNames();         
        
        String [] inputs = new String[editableFields.length];
        
      
        for (int i = 0; i < editableFields.length; i++) {
            String fieldName = editableFields[i];
            String input;

            while (true) {
                System.out.print("\nEnter " + fieldName + ": ");
                input = scanner.nextLine();

                if (fieldName.equalsIgnoreCase("Birthday")) {
                    try {
                        LocalDate.parse(input, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid birthday format. Please use MM/dd/yyyy.");
                    }
                } else if (i >= 12) { // numeric fields from Basic Salary onwards
                    try {
                        Double.parseDouble(input);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric input. Please enter a valid number.");
                    }
                } else {
                    if (input.trim().isEmpty()) {
                        System.out.println("This field cannot be empty.");
                    } else {
                        break;
                    }
                }
            }

            inputs[i] = input;
        }

        try {
            employeeManager.recordEmployee(inputs, newEmployeeId);
            System.out.println("Employee added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Unexpected input error. Please double check values.");
        }

        System.out.print("Press any key to continue...");    
        scanner.nextLine();          
    }
    

    private void editEmployee() {
        try {
            System.out.print("\nEnter Employee ID to edit: ");
            int employeeId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Employee employee = employeeManager.getEmployeeById(employeeId);
            if (employee == null) {
                System.out.println("Employee not found.");
                return;
            }

            System.out.println("\nEmployee found: " + employee.getFirstName() + " " + employee.getLastName());
            System.out.println("Select a field to edit:");

            // Get editable field names
            String[] editableFields = employeeManager.getEditableFieldNames();

            // Display menu
            for (int i = 0; i < editableFields.length; i++) {
                System.out.println((i + 1) + ". " + editableFields[i]);
            }
            System.out.println((editableFields.length + 1) + ". Cancel");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Check for cancel or invalid choice
            if (choice == editableFields.length + 1) {
                System.out.println("Edit cancelled.");
                return;
            }

            if (choice < 1 || choice > editableFields.length) {
                System.out.println("Invalid choice. Please try again.");
                return;
            }

            // Get the actual field index
            int fieldIndex = employeeManager.getFieldIndexFromEditableIndex(choice);
            String fieldName = editableFields[choice - 1];

            System.out.println("Editing " + fieldName);

            // Get and validate input based on field type
            if (employeeManager.isNumericField(fieldIndex)) {
                System.out.print("Enter new value: ");
                double newValue = scanner.nextDouble();
                scanner.nextLine(); // Consume newline

                employeeManager.updateEmployeeField(employeeId, fieldIndex, newValue);

                if (fieldIndex == 13) { // Basic Salary
                    System.out.println("Semi-Monthly Rate and Hourly Rate updated accordingly.");
                }
            } else {
                System.out.print("Enter new value: ");
                String newValue = scanner.nextLine();

                employeeManager.updateEmployeeField(employeeId, fieldIndex, newValue);
            }

            System.out.println(fieldName + " updated successfully!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid value.");
            scanner.nextLine(); // Clear input buffer
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        System.out.print("Press any key to continue...");    
        scanner.nextLine();   
    }

    private void deleteEmployee() {
        System.out.print("Enter Employee ID to delete: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine();
        
        Employee emp = employeeManager.getEmployeeById(employeeId);
            if (emp == null) {
                System.out.println("Employee not found.");
                System.out.print("Press any key to continue...");
                scanner.nextLine();
                return;
            }
        employeeManager.deleteEmployee(employeeId);
        System.out.println("Employee deleted successfully!");
        
        System.out.print("Press any key to continue...");
        scanner.nextLine();
    }

    private void viewWeeklyHours() {
        System.out.print("Enter Employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine();

        Employee emp = employeeManager.getEmployeeById(employeeId);
        if (emp == null) {
            System.out.println("Employee not found.");
            System.out.print("Press any key to continue...");
            scanner.nextLine();
            return;
        }

        System.out.print("Enter year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Employee: " + emp.getFirstName() + " " + emp.getLastName());
        attendanceManager.getTotalHoursWorkedForYear(employeeId, year);

        System.out.print("Press any key to continue...");
        scanner.nextLine();
    }


    private void calculateWeeklySalary() {
        System.out.print("Enter Employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine();

        Employee employee = employeeManager.getEmployeeById(employeeId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Enter year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        attendanceManager.calculateGrossSalary(employeeId, employeeManager, year);
        System.out.print("Press any key to continue...");
        scanner.nextLine();
    }

    
    private void calculateWeeklyNetSalary() {
        System.out.print("Enter Employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine();

        Employee employee = employeeManager.getEmployeeById(employeeId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Enter year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        attendanceManager.calculateNetSalary(employeeId, employeeManager, year);
        System.out.print("Press any key to continue...");
        scanner.nextLine();
    }
        

    
    private void recordAttendance() {
        try {
            System.out.print("Enter Employee ID: ");
            int employeeId = Integer.parseInt(scanner.nextLine());

            Employee employee = employeeManager.getEmployeeById(employeeId);
            if (employee == null) {
                System.out.println("Employee not found.");
                return;
            }

            System.out.print("Enter date (MM/dd/yyyy): ");
            String dateInput = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

            System.out.print("Enter clock-in time (H:mm): ");
            String clockInInput = scanner.nextLine();
            LocalTime clockIn = LocalTime.parse(clockInInput, DateTimeFormatter.ofPattern("H:mm"));

            System.out.print("Enter clock-out time (H:mm): ");
            String clockOutInput = scanner.nextLine();
            LocalTime clockOut = LocalTime.parse(clockOutInput, DateTimeFormatter.ofPattern("H:mm"));

            if (clockOut.isBefore(clockIn)) {
                System.out.println("Clock-out cannot be before clock-in.");
                return;
            }

            double hoursWorked = (double) Duration.between(clockIn, clockOut).toMinutes() / 60;
            if (hoursWorked <= 0) {
                System.out.println("Invalid duration. Worked hours must be greater than 0.");
                return;
            }

            attendanceManager.recordAttendance(employeeId, date, hoursWorked);
            System.out.println("Attendance recorded successfully.");

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please follow MM/dd/yyyy and H:mm formats.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numeric values.");
        } catch (Exception e) {
            System.out.println("Error recording attendance: " + e.getMessage());
        }
        System.out.print("Press any key to continue...");
        scanner.nextLine();
    }
    
    private void viewMonthlySummary() {
        System.out.print("Enter Employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine();

        Employee employee = employeeManager.getEmployeeById(employeeId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();

        System.out.print("Enter year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        double totalHours = attendanceManager.getMonthlyHours(employeeId, month, year);
        double grossPay = totalHours * employee.getHourlyRate();

        // End-of-month deduction assumed
        double totalDeductions = AttendanceManager.calculateTotalDeductions(employee);
        double netPay = grossPay - totalDeductions;

        System.out.println("\n------ Monthly Summary ------");
        System.out.println("Employee: " + employee.getFirstName() + " " + employee.getLastName());
        System.out.println("Month: " + Month.of(month) + " " + year);
        System.out.printf("Total Hours: %.2f hrs%n", totalHours);
        System.out.printf("Gross Pay: Php %.2f%n", grossPay);
        System.out.printf("Net Pay (after deductions): Php %.2f%n", netPay);
        System.out.println("-----------------------------");

        System.out.print("Press any key to continue...");
        scanner.nextLine();
    }


    
}

