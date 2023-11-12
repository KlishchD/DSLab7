package org.example;

import org.example.Models.Student;
import org.example.Models.Group;
import org.example.Repositories.DBRepository;
import org.example.Repositories.Repository;
import org.example.Repositories.XMLRepository;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, SQLException, ClassNotFoundException {
        XMLRepository repository = new XMLRepository("src/main/resources/groups.xml");
        //Repository repository = new DBRepository("jdbc:postgresql://localhost:5432/dev", "postgres", "postgres");

        System.out.println("Groups count is " + repository.countGroups());
        System.out.println("Students count is " + repository.countStudents());
        System.out.println("Groups: " + repository.getGroup());

        System.out.println();

        Group group0 = repository.getGroup(0);
        System.out.println(group0);

        Group group1 = repository.getGroup(1);
        System.out.println(group1);

        System.out.println();

        repository.deleteGroup(1);
        System.out.println("Groups count is " + repository.countGroups());
        System.out.println("Students count is " + repository.countStudents());
        System.out.println("Groups: " + repository.getGroup());

        System.out.println("Student: " + repository.getStudent(1));

        System.out.println("Groups count is " + repository.countGroups());
        System.out.println("Students count is " + repository.countStudents());
        System.out.println("Groups: " + repository.getGroup());

        System.out.println("Student: " + repository.getStudent(10));

        Group group = new Group();
        group.setId(3);
        group.setName("Group A");
        group.setYear(2);
        Student student = new Student();
        student.setName("Student A");
        student.setAge(18);
        group.addStudent(student);
        repository.insertGroup(group);

        //repository.deleteStudent(3);

        repository.save("src/main/resources/group1.xml");
    }
}