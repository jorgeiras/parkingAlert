package com.example.parkingalert;

/**
 * Clase encargada de encapsular y manejar la informacion de un usuario
 */
public class UserInfo {

    private  String name;
    private String email;
    private int score;

    public UserInfo() {
    }

    public UserInfo(String name, String email, int score) {
        this.name = name;
        this.email = email;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
