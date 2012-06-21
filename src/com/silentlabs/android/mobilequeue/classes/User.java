
package com.silentlabs.android.mobilequeue.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private static final long serialVersionUID = -7400212104052646714L;

    private String userId;
    private String firstName;
    private String lastName;
    private String nickName;
    private boolean instantWatch;
    private final ArrayList<String> preferredFormats;

    public User() {
        preferredFormats = new ArrayList<String>();
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public boolean getInstantWatch() {
        return instantWatch;
    }

    public ArrayList<String> getPreferredFormats() {
        return preferredFormats;
    }

    public void setUserId(String _userid) {
        userId = _userid;
    }

    public void setFirstName(String _firstname) {
        firstName = _firstname;
    }

    public void setLastName(String _lastname) {
        lastName = _lastname;
    }

    public void setNickName(String _nickname) {
        nickName = _nickname;
    }

    public void setInstantWatch(boolean _instantwatch) {
        instantWatch = _instantwatch;
    }

    public void addPreferredFormat(String _preferredformat) {
        preferredFormats.add(_preferredformat);
    }
}
