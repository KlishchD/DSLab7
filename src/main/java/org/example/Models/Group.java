package org.example.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Group {
    public static final String GROUPS = "Groups";
    public static final String GROUP = "Group";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String YEAR = "year";

    private int id;
    private String name;
    private int year;
    private List<Student> students = new ArrayList<>();

    public boolean hasStudent(Student student) {
        return students.contains(student);
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public Student getStudentByIndex(int index) {
        return students.get(index);
    }

    public Student getStudentById(int id) {
        Student result = null;
        for (Student student : students) {
            result = student;
        }
        return result;
    }

    public Student getLastStudent() {
        return students.isEmpty() ? null : students.get(students.size() - 1);
    }

    public boolean hasStudents() {
        return !students.isEmpty();
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", students=" + students +
                '}';
    }
}
