package com.UserManagement;

import java.io.Serializable;
import org.json.JSONObject;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String age;

    // Constructor
    public User(String id, String name, String email, String age) {
        this.id = id;
        this.name = name;
        this.email = (email != null && !email.isEmpty()) ? email : "not_set@example.com";
        this.age = (age != null && !age.isEmpty()) ? age : "unknown";
    }

    // Default constructor
    public User() {}

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAge() { return age; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { 
        this.email = (email != null && !email.isEmpty()) ? email : "not_set@example.com";
    }
    public void setAge(String age) { 
        this.age = (age != null && !age.isEmpty()) ? age : "unknown"; 
    }

    // Convert User to JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("name", this.name);
        json.put("email", this.email);
        json.put("age", this.age);
        return json;
    }

    // Convert JSON to User object
    public static User fromJson(JSONObject json) {
        return new User(
            json.optString("id", "").trim(),
            json.optString("name", "").trim(),
            json.optString("email", "not_set@example.com").trim(),
            json.optString("age", "unknown").trim()
        );
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
