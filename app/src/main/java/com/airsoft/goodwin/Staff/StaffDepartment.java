package com.airsoft.goodwin.Staff;

public class StaffDepartment {
    private long id;
    private String departmentName;
    private String departmentDescription;

    public StaffDepartment(long id, String name, String description) {
        this.id = id;
        this.departmentName = name;
        this.departmentDescription = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDepartmentName(String name) {
        this.departmentName = name;
    }

    public void setDepartmentDescription(String description) {
        this.departmentDescription = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return departmentName;
    }

    public String getDescription() {
        return departmentDescription;
    }
}
