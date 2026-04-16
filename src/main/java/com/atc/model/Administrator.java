package com.atc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrators")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Administrator extends User {

    @Override
    public void login() {
        System.out.println("Admin " + getName() + " logged in.");
    }

    @Override
    public void logout() {
        System.out.println("Admin " + getName() + " logged out.");
    }

    public void manageUsers() {
        System.out.println("Managing users...");
    }

    public void generateReports() {
        System.out.println("Generating reports...");
    }
}


