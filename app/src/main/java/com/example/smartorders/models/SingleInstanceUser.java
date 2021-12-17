package com.example.smartorders.models;

/* Domain object for AuthCredential using Singleton Design pattern */
public class SingleInstanceUser {

    //create an object of SingleObject
    private static SingleInstanceUser instance = new SingleInstanceUser();

    //make the constructor private so that this class cannot be
    //instantiated
    private SingleInstanceUser(){}

    //Get the only object available
    public static SingleInstanceUser getInstance(){
        return instance;
    }

    private String uId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public String getuID() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
