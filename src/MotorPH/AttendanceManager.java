
package MotorPH;

/**
 *
 * @author santo
 */

import java.time.*;
import java.util.*;
import java.time.temporal.WeekFields;
import java.time.format.DateTimeFormatter;

class AttendanceManager {
    private List<String[]> attendanceLogs = new ArrayList<>();
    private Map<Integer, Map<Integer, Map<Integer, Double>>> attendanceData = new HashMap<>();


public void loadAttendance(String filepath) {
    Map<String, Object> result = FileHandler.loadAttendance(filepath);
    attendanceData = (Map<Integer, Map<Integer, Map<Integer, Double>>>) result.get("data");
    attendanceLogs = (List<String[]>) result.get("logs");
}

    //FOR WHAT
    //public void recordAttendance(int employeeId, LocalDate date, double hoursWorked) {
    //    int weekNumber = date.get(java.time.temporal.WeekFields.ISO.weekOfYear());
    //    attendanceData.putIfAbsent(employeeId, new HashMap<>());
    //    attendanceData.get(employeeId).merge(weekNumber, hoursWorked, Double::sum);
    //} 
    //REVIEW

    
    public void calculateGrossSalary(int employeeId, EmployeeManager employeeManager, int year) {
        if (!attendanceData.containsKey(employeeId)) {
            System.out.println("No attendance data found for Employee ID: " + employeeId);
            return;
        }

        Map<Integer, Map<Integer, Double>> yearlyData = attendanceData.get(employeeId);
        if (!yearlyData.containsKey(year)) {
            System.out.println("No attendance records for year " + year);
            return;
        }

        Employee employee = employeeManager.getEmployeeById(employeeId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        double hourlyRate = employee.getHourlyRate();

        System.out.println("Year: " + year);
        Map<Integer, Double> weeks = yearlyData.get(year);
        for (int week : weeks.keySet()) {
            double hoursWorked = weeks.get(week);
            double grossSalary = hoursWorked * hourlyRate;
            System.out.printf("  Week %3d: %.2f hours -> Php %.2f%n", week, hoursWorked, grossSalary);
        }
    }

 
    public void calculateNetSalary(int employeeId, EmployeeManager employeeManager, int year) {
        if (!attendanceData.containsKey(employeeId)) {
            System.out.println("No attendance data found for Employee ID: " + employeeId);
            return;
        }

        Map<Integer, Map<Integer, Double>> yearlyData = attendanceData.get(employeeId);
        if (!yearlyData.containsKey(year)) {
            System.out.println("No attendance records for year " + year);
            return;
        }

        Employee employee = employeeManager.getEmployeeById(employeeId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        double hourlyRate = employee.getHourlyRate();

        // Calculate deductions
        double sss = calculateSSSContribution(employee.getBasicSalary());
        double philHealth = calculatePhilHealthContribution(employee.getBasicSalary());
        double pagIbig = calculatePagIbigContribution(employee.getBasicSalary());
        double tax = calculateWithholdingTax(employee.getBasicSalary(), sss + philHealth + pagIbig);
        double totalDeductions = sss + philHealth + pagIbig + tax;

        System.out.println("Year: " + year);
        Map<Integer, Double> weeks = yearlyData.get(year);

        for (int week : weeks.keySet()) {
            double hoursWorked = weeks.get(week);
            double grossSalary = hoursWorked * hourlyRate;

            boolean isEndOfMonthWeek = isEndOfMonthWeek(week, year);
            double netSalary = isEndOfMonthWeek ? grossSalary - totalDeductions : grossSalary;

            System.out.printf("  Week %3d: %.2f hours -> Gross: Php %.2f", week, hoursWorked, grossSalary);
            if (isEndOfMonthWeek) {
                System.out.println(" (End of Month)");
                System.out.printf("    Deductions: SSS=%.2f, PhilHealth=%.2f, Pag-IBIG=%.2f, Tax=%.2f => Total=%.2f%n",
                                  sss, philHealth, pagIbig, tax, totalDeductions);
                System.out.printf("    Net Salary: Php %.2f%n", netSalary);
            } else {
                System.out.printf(" -> Net: Php %.2f%n", netSalary);
            }
        }
    }

    
    public static double calculateSSSContribution(double basicSalary) {
        double[][] sssTable = {
            {0, 3249.99, 135.00}, {3250, 3749.99, 157.50}, {3750, 4249.99, 180.00},
            {4250, 4749.99, 202.50}, {4750, 5249.99, 225.00}, {5250, 5749.99, 247.50},
            {5750, 6249.99, 270.00}, {6250, 6749.99, 292.50}, {6750, 7249.99, 315.00},
            {7250, 7749.99, 337.50}, {7750, 8249.99, 360.00}, {8250, 8749.99, 382.50},
            {8750, 9249.99, 405.00}, {9250, 9749.99, 427.50}, {9750, 10249.99, 450.00},
            {10250, 10749.99, 472.50}, {10750, 11249.99, 495.00}, {11250, 11749.99, 517.50},
            {11750, 12249.99, 540.00}, {12250, 12749.99, 562.50}, {12750, 13249.99, 585.00},
            {13250, 13749.99, 607.50}, {13750, 14249.99, 630.00}, {14250, 14749.99, 652.50},
            {14750, 15249.99, 675.00}, {15250, 15749.99, 697.50}, {15750, 16249.99, 720.00},
            {16250, 16749.99, 742.50}, {16750, 17249.99, 765.00}, {17250, 17749.99, 787.50},
            {17750, 18249.99, 810.00}, {18250, 18749.99, 832.50}, {18750, 19249.99, 855.00},
            {19250, 19749.99, 877.50}, {19750, 20249.99, 900.00}, {20250, 20749.99, 922.50},
            {20750, 21249.99, 945.00}, {21250, 21749.99, 967.50}, {21750, 22249.99, 990.00},
            {22250, 22749.99, 1012.50}, {22750, 23249.99, 1035.00}, {23250, 23749.99, 1057.50},
            {23750, 24249.99, 1080.00}, {24250, 24749.99, 1102.50}, {24750, Double.MAX_VALUE, 1125.00}
        };

        for (double[] range : sssTable) {
            if (basicSalary >= range[0] && basicSalary <= range[1]) {
                return range[2];
            }
        }
        return 0.0;
    }

    public static double calculatePhilHealthContribution(double basicSalary) {
        double premiumRate = 0.03; // 3% of the basic salary
        double monthlyPremium = basicSalary * premiumRate;
        double maxContribution = 1800;
        double minContribution = 300;

        if (monthlyPremium > maxContribution) {
            monthlyPremium = maxContribution;
        } else if (monthlyPremium < minContribution) {
            monthlyPremium = minContribution;
        }

        return monthlyPremium;
    }

    public static double calculatePagIbigContribution(double basicSalary) {
        double employeeRate = (basicSalary <= 1500) ? 0.01 : 0.02; // 1% if ≤1500, otherwise 2%
        return Math.min(basicSalary * employeeRate, 100); // Cap at 100
    }

    public static double calculateWithholdingTax(double basicSalary, double totalDeductions) {
        double taxableIncome = basicSalary - totalDeductions;

        if (taxableIncome <= 20832) return 0.0;
        if (taxableIncome <= 33333) return (taxableIncome - 20833) * 0.20;
        if (taxableIncome <= 66667) return (taxableIncome - 33333) * 0.25;
        if (taxableIncome <= 166667) return (taxableIncome - 66667) * 0.30;
        if (taxableIncome <= 666667) return (taxableIncome - 166667) * 0.32;
        return (taxableIncome - 666667) * 0.35;
    }

    public static double calculateTotalDeductions(Employee employee) {
        double sss = calculateSSSContribution(employee.getBasicSalary());
        double philHealth = calculatePhilHealthContribution(employee.getBasicSalary());
        double pagIbig = calculatePagIbigContribution(employee.getBasicSalary());
        double tax = calculateWithholdingTax(employee.getBasicSalary(), sss + philHealth + pagIbig);

        return sss + philHealth + pagIbig + tax;
    }    
    
    private boolean isEndOfMonthWeek(int weekNumber, int year) {
        // Get the last day of the week
        LocalDate weekStart = LocalDate.ofYearDay(year, 1)
                .with(WeekFields.ISO.weekOfYear(), weekNumber)
                .with(DayOfWeek.MONDAY);

        LocalDate weekEnd = weekStart.plusDays(6); // Sunday

        // Check if the week contains the last day of a month
        LocalDate lastDayOfMonth = weekEnd.withDayOfMonth(weekEnd.lengthOfMonth());

        // If the last day of the month is in this week, it's an end-of-month week
        return (weekEnd.getDayOfMonth() == lastDayOfMonth.getDayOfMonth() || 
                (weekStart.getMonthValue() != weekEnd.getMonthValue()));
    }
    
    public Map<Integer, Map<Integer, Map<Integer, Double>>> getAllAttendance() {
        return attendanceData;
    }
    
    public void recordAttendance(int employeeId, LocalDate date, double hoursWorked) {
        int year = date.getYear();
        int weekNumber = date.get(WeekFields.ISO.weekOfYear());

        attendanceData.putIfAbsent(employeeId, new HashMap<>());
        attendanceData.get(employeeId).putIfAbsent(year, new HashMap<>());
        attendanceData.get(employeeId).get(year).merge(weekNumber, hoursWorked, Double::sum);

        // Store full log
        attendanceLogs.add(new String[]{
            String.valueOf(employeeId),
            String.valueOf(year),
            String.valueOf(weekNumber),
            date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
            LocalTime.ofSecondOfDay((long)(hoursWorked * 3600)).toString(), // Rough clockIn
            LocalTime.ofSecondOfDay((long)(hoursWorked * 3600)).plusMinutes(60).toString(), // Dummy clockOut
            String.format("%.2f", hoursWorked)
        });
    }

    
    public void saveAttendance(String filepath) {
        FileHandler.saveAttendance(filepath, attendanceLogs);
    }

    
    public void getTotalHoursWorkedForYear(int employeeId, int year) {
        if (!attendanceData.containsKey(employeeId)) {
            System.out.println("No attendance data found.");
            return;
        }

        Map<Integer, Map<Integer, Double>> yearly = attendanceData.get(employeeId);
        if (!yearly.containsKey(year)) {
            System.out.println("No data for year " + year);
            return;
        }

        System.out.println("Weekly Hours for Year " + year + ":");
        Map<Integer, Double> weeklyData = yearly.get(year);
        for (int week : weeklyData.keySet()) {
            System.out.printf("  Week %3d: %.2f hours%n", week, weeklyData.get(week));
        }
    }
    
    public double getMonthlyHours(int employeeId, int month, int year) {
        if (!attendanceData.containsKey(employeeId)) return 0;

        Map<Integer, Map<Integer, Double>> yearly = attendanceData.get(employeeId);
        if (!yearly.containsKey(year)) return 0;

        double totalHours = 0;

        // Convert week numbers to day ranges and filter by month
        for (int week : yearly.get(year).keySet()) {
            LocalDate weekStart = LocalDate.ofYearDay(year, 1)
                .with(WeekFields.ISO.weekOfYear(), week)
                .with(DayOfWeek.MONDAY);

            if (weekStart.getMonthValue() == month || weekStart.plusDays(6).getMonthValue() == month) {
                totalHours += yearly.get(year).get(week);
            }
        }

        return totalHours;
    }
    
}



