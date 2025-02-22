package UserManagement;


public class UserSchema {
    private int id;
    private String name;
    private String email;

    public UserSchema() {
    }

    public UserSchema(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserSchema { " +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                " }";
    }
}

