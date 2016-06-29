package com.airsoft.goodwin.Staff;

import com.airsoft.goodwin.UserInfo.UserInfo;

/**
 * Created by cherry on 11.02.2016.
 */
public class StaffDepartmentUser {
    private StaffDepartment department;
    private UserInfo departmentUser;

    public StaffDepartmentUser(StaffDepartment department, UserInfo userInfo) {
        this.department = department;
        this.departmentUser = userInfo;
    }

    public void setDepartment(StaffDepartment department) {
        this.department = department;
    }

    public StaffDepartment getDepartment() {
        return department;
    }

    public void setDepartmentUser(UserInfo userInfo) {
        departmentUser = userInfo;
    }

    public UserInfo getDepartmentUser() {
        return departmentUser;
    }
}
