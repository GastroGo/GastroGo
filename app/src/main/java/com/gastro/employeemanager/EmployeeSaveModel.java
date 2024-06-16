package com.gastro.employeemanager;

public class EmployeeSaveModel {

    static EmployeeSaveModel instance;

    EmployeeItem employee = new EmployeeItem();

    private EmployeeSaveModel(){}

    public static EmployeeSaveModel getInstance(){
        if (instance == null){
            instance = new EmployeeSaveModel();
        }
        return instance;
    }

    public EmployeeItem getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeItem employee) {
        this.employee = employee;
    }

}
