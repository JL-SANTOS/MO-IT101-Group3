# MotorPH Employee Attendance & Payroll Management System

A Java-based console application for managing employee records and attendance, designed to simulate a small-scale HR/payroll system.

## Features

- Add, edit, view, or delete employee records
- Record attendance with clock-in/clock-out times
- View weekly hours worked and salary summaries
- Calculate gross and net salaries with deductions:
  - SSS
  - PhilHealth
  - Pag-IBIG
  - Withholding Tax
- Data stored in CSV files for portability

## Tests/Quality Assurance Screenshots

- Located in Test Folder
- Java Test Files in Test Folder

## Technologies Used

- Java SE
- OpenCSV for CSV parsing
- Java Time API (LocalDate, LocalTime, Duration)

## Project Structure

```
MotorPH/
├── src/
│   └── MotorPH/
│       ├── Main.java
│       ├── MainController.java
│       ├── EmployeeManager.java
│       ├── AttendanceManager.java
│       ├── FileHandler.java
│       ├── Global.java
│       ├── EmployeeClass.java
├── test/
│   ├── AttendanceManagerTest.java
│   ├── EmployeeManagerTest.java
│   ├── IntegrationTest.java
│   ├── EdgeCaseTest.java
│   └── FileHandlerTest.java
├── Employees.csv
├── Attendance.csv
```

## How to Run

1. Clone the repository

2. Open in your Java IDE (e.g., NetBeans, IntelliJ)

3. Make sure `Employees.csv` and `Attendance.csv` exist in the root `MotorPH/` directory.

4. Run `Main.java`  
   The console-based menu will guide your interaction.

## Notes

- Dates must follow `MM/dd/yyyy` format.
- Time inputs use `H:mm` (24-hour) format.
- Deductions are computed only at end-of-month weeks.

## Testing/Quality Assurance

All unit and integration tests are located under the `test/` directory and written using JUnit 4.

### Test Coverage:
- **EmployeeManagerTest**: Add, edit, delete employees, field mapping
- **AttendanceManagerTest**: Attendance logging, monthly summaries, deduction computations
- **FileHandlerTest**: File read/write accuracy for employees
- **IntegrationTest**: End-to-end workflow (employee creation, attendance, salary computation)
- **EdgeCaseTest**: Handles invalid/edge input (negative hours, invalid time format, duplicate logs)

To run tests, ensure JUnit 4 is set up in your IDE and execute the test classes.

## Future Improvements

- GUI Interface (JavaFX or Swing)
- User authentication
- User Access

---

© 2025 MotorPH Java Payroll System
