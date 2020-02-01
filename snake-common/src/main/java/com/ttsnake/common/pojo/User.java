package com.ttsnake.common.pojo;

import javafx.beans.property.DoubleProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String salt;
    private Double exp;
    private Double level;
    private Double level_exp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Double getExp() {
        return exp;
    }

    public void setExp(Double exp) {
        this.exp = exp;
    }

    public Double getLevel() {
        Double exp = getExp();
        if (exp == null) {
            return null;
        }

        if (exp == 0) {
            return 0d;
        }

        return Math.floor(Math.log(exp));
    }

    public void setLevel(Double level) {
        this.level = level;
    }

    public Double getLevel_exp() {
        Double exp = getExp();
        if (exp == null) {
            return null;
        }

        if (exp == 0) {
            return 0d;
        }

        Double level = getLevel();
        double pow = Math.pow(Math.E, level);
        return getExp() - pow;
    }

    public void setLevel_exp(Double level_exp) {
        this.level_exp = level_exp;
    }

    public Double expContain() {
        double front = Math.pow(Math.E, getLevel());
        double after = Math.pow(Math.E, getLevel() + 1);
        return after - front;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", exp=" + exp +
                ", level=" + level +
                ", level_exp=" + level_exp +
                '}';
    }
}
