
package MotorPH;

/**
 *
 * @author santo
 */

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.time.format.DateTimeFormatter;
import java.time.Duration;


class FileHandler {
    public static List<Employee> loadEmployees(String filepath) {
        List<Employee> employees = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
            String[] data;
            boolean isFirstLine = true;

            while ((data = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header row
                    continue;
                }

                try {
                    Employee employee = new Employee(
                            Integer.parseInt(data[0]), data[2], data[1], data[3], data[4],
                            data[5], data[6], data[7], data[8], data[9], data[10],
                            data[11], data[12], Double.parseDouble(data[13]), Double.parseDouble(data[14]),
                            Double.parseDouble(data[15]), Double.parseDouble(data[16]),
                            Double.parseDouble(data[17]), Double.parseDouble(data[18])
                    );
                    employees.add(employee);
                } catch (NumberFormatException e) {
                    Logger.getLogger(FileHandler.class.getName()).log(Level.WARNING, 
                        "Skipping invalid row due to incorrect number format: " + String.join(", ", data), e);
                }
            }
        } catch (IOException | CsvValidationException e) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Error loading employees", e);
        }

        return employees;
    }

    public static void saveEmployees(String filepath, List<Employee> employees) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filepath))) {
            writer.writeNext(Global.FIELD_NAMES);
            for (Employee emp : employees) {
                String[] data = {
                    String.valueOf(emp.getEmployeeId()), emp.getFirstName(), emp.getLastName(), emp.getBirthday(),
                    emp.getAddress(), emp.getPhoneNumber(), emp.getSssNumber(), emp.getPhilHealthNumber(),
                    emp.getTinNumber(), emp.getPagIbigNumber(), emp.getStatus(), emp.getPosition(),
                    emp.getImmediateSupervisor(), String.valueOf(emp.getBasicSalary()),
                    String.valueOf(emp.getRiceSubsidy()), String.valueOf(emp.getPhoneAllowance()),
                    String.valueOf(emp.getClothingAllowance()), String.valueOf(emp.getSemiMonthly()),
                    String.valueOf(emp.getHourlyRate())
                };
                writer.writeNext(data);
            }
            System.out.println("Employees saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving employees: " + e.getMessage());
        }
    }
    
    public static Map<String, Object> loadAttendance(String filepath) {
        Map<Integer, Map<Integer, Map<Integer, Double>>> attendanceData = new HashMap<>();
        List<String[]> attendanceLogs = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
            String[] data;
            boolean isFirstLine = true;
            while ((data = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    int employeeId = Integer.parseInt(data[0].trim());
                    String dateString = data[3].trim();
                    String clockInStr = data[4].trim();
                    String clockOutStr = data[5].trim();

                    LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("M/d/yyyy"));
                    LocalTime clockIn = LocalTime.parse(clockInStr, DateTimeFormatter.ofPattern("H:mm"));
                    LocalTime clockOut = LocalTime.parse(clockOutStr, DateTimeFormatter.ofPattern("H:mm"));

                    if (clockOut.isBefore(clockIn)) {
                        System.out.println("Clock-out before clock-in for employee " + employeeId + " on " + date);
                        continue;
                    }

                    double hoursWorked = Duration.between(clockIn, clockOut).toMinutes() / 60.0;

                    int year = date.getYear();
                    int weekNumber = date.get(WeekFields.ISO.weekOfYear());

                    // Weekly aggregation
                    attendanceData.putIfAbsent(employeeId, new HashMap<>());
                    attendanceData.get(employeeId).putIfAbsent(year, new HashMap<>());
                    attendanceData.get(employeeId).get(year).merge(weekNumber, hoursWorked, Double::sum);

                    // Save full raw log
                    attendanceLogs.add(new String[] {
                        String.valueOf(employeeId),
                        String.valueOf(year),
                        String.valueOf(weekNumber),
                        date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        clockInStr,
                        clockOutStr,
                        String.format("%.2f", hoursWorked)
                    });
                } catch (Exception e) {
                    Logger.getLogger(FileHandler.class.getName()).log(Level.WARNING, "Skipping row: " + Arrays.toString(data), e);
                }
            }
        } catch (IOException | CsvValidationException e) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, "Error loading attendance", e);
        }

        // Return both using a map
        Map<String, Object> result = new HashMap<>();
        result.put("data", attendanceData);
        result.put("logs", attendanceLogs);
        return result;
    }
    
    public static void saveAttendance(String filepath, List<String[]> attendanceLogs) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filepath))) {
            writer.writeNext(new String[]{"Employee ID", "Year", "Week", "Date", "Clock In", "Clock Out", "Hours Worked"});
            for (String[] log : attendanceLogs) {
                writer.writeNext(log);
            }
            System.out.println("Attendance saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving attendance: " + e.getMessage());
        }
    }



    
}

