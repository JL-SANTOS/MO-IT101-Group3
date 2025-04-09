
package MotorPH;

/**
 *
 * @author santo
 */

import java.util.ArrayList;
import java.util.List;

class EmployeeManager {
    private List<Employee> employees = new ArrayList<>();
    
    public void loadEmployees(String filepath) {
        employees = FileHandler.loadEmployees(filepath);
    }
    
    public List<Employee> getAllEmployees() {
        return employees;
    }
    
    public void viewEmployee(int employeeId) {   
        Employee employee = getEmployeeById(employeeId);
        System.out.println(employee);
    }    
    
    public int getNextEmployeeId() {
        return employees.isEmpty() ? 1 : employees.get(employees.size() - 1).getEmployeeId() + 1;
    }


        
    public void recordEmployee(String[] inputs, int newEmployeeId) {
        
        double basicSalary = Double.parseDouble(inputs[12]);
            if (basicSalary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
                    
        Employee newEmployee = new Employee(
            newEmployeeId,                    // employeeId
            inputs[1],                        // lastName
            inputs[0],                        // firstName
            inputs[2],                        // birthday
            inputs[3],                        // address
            inputs[4],                        // phoneNumber
            inputs[5],                        // sssNumber
            inputs[6],                        // philHealthNumber
            inputs[7],                        // tinNumber
            inputs[8],                        // pagIbigNumber
            inputs[9],                        // status
            inputs[10],                       // position
            inputs[11],                       // immediateSupervisor
            Double.parseDouble(inputs[12]),   // basicSalary
            Double.parseDouble(inputs[13]),   // riceSubsidy
            Double.parseDouble(inputs[14]),   // phoneAllowance
            Double.parseDouble(inputs[15]),   // clothingAllowance
            Double.parseDouble(inputs[12])/2, // grossSemiMonthlyRate (derived from basicSalary)
            Double.parseDouble(inputs[12])/160 // hourlyRate (derived from basicSalary)
        );


                    
        employees.add(newEmployee);
    }

    public Employee getEmployeeById(int employeeId) {
        return employees.stream()
                .filter(emp -> emp.getEmployeeId() == employeeId)
                .findFirst()
                .orElse(null);
    }

    public void updateEmployee(int employeeId, Employee updatedEmployee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeId() == employeeId) {
                employees.set(i, updatedEmployee);
                return;
            }
        }
    }
    
    public void updateEmployeeField(int employeeId, int fieldIndex, Object newValue) {
        Employee employee = getEmployeeById(employeeId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        // Update the appropriate field
        switch (fieldIndex) {
            case 1: employee.setFirstName((String) newValue); break;
            case 2: employee.setLastName((String) newValue); break;
            case 3: employee.setBirthday((String) newValue); break;
            case 4: employee.setAddress((String) newValue); break;
            case 5: employee.setPhoneNumber((String) newValue); break;
            case 6: employee.setSssNumber((String) newValue); break;
            case 7: employee.setPhilHealthNumber((String) newValue); break;
            case 8: employee.setTinNumber((String) newValue); break;
            case 9: employee.setPagIbigNumber((String) newValue); break;
            case 10: employee.setStatus((String) newValue); break;
            case 11: employee.setPosition((String) newValue); break;
            case 12: employee.setImmediateSupervisor((String) newValue); break;
            case 13: // Basic Salary
                double basicSalary = (Double) newValue;
                employee.setBasicSalary(basicSalary);
                // Recalculate derived values
                double newSemiMonthly = basicSalary / 2;
                double newHourlyRate = newSemiMonthly / 80; // Assuming 80 hours per semi-monthly period
                employee.setSemiMonthly(newSemiMonthly);
                employee.setHourlyRate(newHourlyRate);
                break;
            case 14: employee.setRiceSubsidy((Double) newValue); break;
            case 15: employee.setPhoneAllowance((Double) newValue); break;
            case 16: employee.setClothingAllowance((Double) newValue); break;
        }

        // Update the employee in the list
        updateEmployee(employeeId, employee);
    }

    public String[] getEditableFieldNames() {
        // Create a filtered list of editable field names (excluding Employee ID, Semi-Monthly Rate, and Hourly Rate)
        String[] editableFields = new String[Global.FIELD_NAMES.length - 3];
        int index = 0;

        for (int i = 0; i < Global.FIELD_NAMES.length; i++) {
            if (i != 0 && i != 17 && i != 18) { // Skip Employee ID (0), Semi-Monthly (17), and Hourly Rate (18)
                editableFields[index++] = Global.FIELD_NAMES[i];
            }
        }

        return editableFields;
    }

    public int getFieldIndexFromEditableIndex(int editableIndex) {
        // Maps the editable field index back to the original FIELD_NAMES index
        int count = 0;
        for (int i = 0; i < Global.FIELD_NAMES.length; i++) {
            if (i != 0 && i != 17 && i != 18) {
                count++;
                if (count == editableIndex) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean isNumericField(int fieldIndex) {
        // Determines if a field requires numeric input
        return fieldIndex >= 13 && fieldIndex <= 16;
    }
    
    public void deleteEmployee(int employeeId) {
        employees.removeIf(emp -> emp.getEmployeeId() == employeeId);
    }
}


