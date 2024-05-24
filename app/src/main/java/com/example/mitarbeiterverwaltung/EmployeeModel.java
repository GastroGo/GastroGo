package com.example.mitarbeiterverwaltung;

public class EmployeeModel {

    static EmployeeModel instance;

    User employee = new User();

    private EmployeeModel(){}

    public static EmployeeModel getInstance(){
        if (instance == null){
            instance = new EmployeeModel();
        }
        return instance;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

}
