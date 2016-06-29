package com.airsoft.goodwin.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    public static final int DUTY_RED = 0;
    public static final int DUTY_YELLO = 1;
    public static final int DUTY_GREEN = 2;

    public String id = "-";
    public String lastname = "-";
    public String firstname = "-";
    public String middlename = "-";
    public String rank = "-";
    public String email = "-";
    public String photoPath = "data/20020aaee71f99d98ec5ddf2c80fa8d8";
    public String personalNumber = "-";
    public String nickname = "-";
    public String birth = "-";
    public String nation = "-";
    public String grad = "-";
    public String unit = "-";
    public String position = "-";
    public String battery = "-";
    public String telnum = "-";
    public String dateregistration = "-";
    public List<String> capabilities = new ArrayList<>();
    public int dutyColor = DUTY_GREEN;
    public int dutyPosition = -1;

    public UserInfo() {}

    public UserInfo(String lastname, String firstname, String middlename, String rank, String email, String photoPath) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.middlename = middlename;
        this.rank = rank;
        this.email = email;
        this.photoPath = photoPath;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", lastname, firstname, middlename);
    }

    public boolean can(String permission) {
        for(String capability : capabilities) {
            if (capability.equals(permission)) {
                return true;
            }
        }
        return false;
    }
}
