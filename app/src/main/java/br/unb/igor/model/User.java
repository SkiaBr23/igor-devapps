package br.unb.igor.model;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String userId;
    private String fullName;
    private String name;
    private String email;
    private String password;
    private String profilePictureUrl;
    private String phoneNumber;
    private String gender;
    private String birthDate;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String fullName, String email, String profilePictureUrl) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public User(String userId, String fullName, String email, String profilePictureUrl, String gender, String birthDate) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public User(String userId, String fullName, String name, String email, String password, String profilePictureUrl, String phoneNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
        this.phoneNumber = phoneNumber;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
