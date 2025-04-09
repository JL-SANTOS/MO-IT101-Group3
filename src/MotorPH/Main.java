
package MotorPH;

/**
 *
 * @author santo
 */


public class Main {
    public static void main(String[] args) {
        EmployeeManager employeeManager = new EmployeeManager();
        AttendanceManager attendanceManager = new AttendanceManager();

        // Load employees from file
        employeeManager.loadEmployees(Global.EMPLOYEE_FILE);
        attendanceManager.loadAttendance(Global.ATTENDANCE_FILE);

        // Create and start the controller
        MainController controller = new MainController(employeeManager, attendanceManager);
        controller.start();
    }
}
