package com.spbutu.gia.core.application.dto;

import java.util.UUID;

public class TeacherDto {
    private UUID id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String fullName;
    private String department;
    private String position;
    private String degree;
    private String email;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
