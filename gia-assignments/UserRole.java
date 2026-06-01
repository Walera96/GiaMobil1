package com.spbutu.gia.auth.domain.enums;

public enum UserRole {
    SYSTEM_ADMIN, UNIVERSITY_ADMIN, DEAN, DEAN_SECRETARY,
    DEPARTMENT_HEAD, DEPARTMENT_SECRETARY, SUPERVISOR,
    GEK_SECRETARY, GEK_CHAIRMAN, GEK_MEMBER,
    METHODIST, STUDENT;

    // helper методы для определения портала
    public boolean isAdminPortal() { 
        return this == SYSTEM_ADMIN || this == UNIVERSITY_ADMIN; 
    }
    
    public boolean isDeaneryPortal() { 
        return this == DEAN || this == DEAN_SECRETARY; 
    }
    
    public boolean isGekPortal() { 
        return this == GEK_SECRETARY || this == GEK_CHAIRMAN || this == GEK_MEMBER; 
    }
    
    public boolean isDepartmentPortal() { 
        return this == DEPARTMENT_HEAD || this == DEPARTMENT_SECRETARY || this == SUPERVISOR; 
    }
    
    public boolean isTeacherPortal() { 
        return this == SUPERVISOR || this == DEPARTMENT_HEAD; 
    }
    
    public boolean isStudentPortal() { 
        return this == STUDENT; 
    }
    
    public String getPortal() { 
        return switch (this) {
            case SYSTEM_ADMIN, UNIVERSITY_ADMIN -> "ADMIN_PORTAL";
            case DEAN, DEAN_SECRETARY -> "DEANERY_PORTAL";
            case GEK_SECRETARY, GEK_CHAIRMAN, GEK_MEMBER -> "GEK_PORTAL";
            case DEPARTMENT_HEAD, DEPARTMENT_SECRETARY -> "DEPARTMENT_PORTAL";
            case SUPERVISOR -> "TEACHER_PORTAL";
            case STUDENT -> "STUDENT_PORTAL";
            case METHODIST -> "METHODIST_PORTAL";
        };
    }
}
