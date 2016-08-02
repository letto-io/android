package br.com.sienaidea.oddin.retrofitModel;

/**
 * Created by Siena Idea on 27/07/2016.
 */
public class User {
    public static final String EMAIL = "email" ;

    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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
}
