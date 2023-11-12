package org.example.Repositories;

import org.example.Models.Student;
import org.example.Models.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBRepository implements Repository {
    private Connection connection;

    public DBRepository(String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(url, user, password);

        Statement statement = connection.createStatement();

        String groupsTable = "CREATE TABLE IF NOT EXISTS groups (id SERIAL PRIMARY KEY, name varchar(30), year int)";
        String studentsTable = "CREATE TABLE IF NOT EXISTS students (id SERIAL PRIMARY KEY, group_id int, name varchar(30), age int, CONSTRAINT fk_group FOREIGN KEY(group_id) REFERENCES groups(id))";

        statement.execute(groupsTable);
        statement.execute(studentsTable);
    }

    @Override
    public int countGroups() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM groups";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int countStudents() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM students";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void insertGroup(Group group) {
        try {
            Statement statement = connection.createStatement();
            String groupQuery = "INSERT INTO groups(name, year) VALUES('" + group.getName() + "'," + group.getYear() + ")";
            statement.execute(groupQuery);

            String findQuery = "SELECT * FROM groups WHERE name='" + group.getName() + "' AND year=" + group.getYear();
            ResultSet resultSet = statement.executeQuery(findQuery);
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                for (Student student : group.getStudents()) {
                    String studentQuery = "INSERT INTO students(group_id, name, age) VALUES(" + id + ",'" + student.getName() + "'," + student.getAge() + ")";
                    statement.execute(studentQuery);
                }
            }
        } catch (SQLException e) {
            System.out.println("Cannot add group as it already exists");
        }
    }

    @Override
    public void insertStudent(int groupId, Student student) {
        try {
            Statement statement = connection.createStatement();
            String studentsQuery = "INSERT INTO students(group_id, name, age) VALUES(" + groupId + ",'" + student.getName() + "'," + student.getAge() + ")";
            statement.execute(studentsQuery);
        } catch (SQLException e) {
            System.out.println("Cannot add student as it already exists");
        }
    }

    @Override
    public void deleteGroup(int id) {
        try {
            Statement statement = connection.createStatement();
            String studentsQuery = "DELETE FROM students WHERE group_id = " + id;
            statement.execute(studentsQuery);
            String groupQuery = "DELETE FROM groups WHERE id = " + id;
            statement.execute(groupQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteStudent(int id) {
        try {
            Statement statement = connection.createStatement();
            String studentQuery = "DELETE FROM students WHERE id = " + id;
            statement.execute(studentQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Group getGroup(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM groups WHERE id = " + id + ") JOIN (SELECT id as student_id, group_id as id, name as student_name, age as student_age FROM students) USING (id)";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Group group = new Group();
                group.setId(resultSet.getInt("id"));
                group.setName(resultSet.getString("name"));
                group.setYear(resultSet.getInt("year"));

               do {
                    Student student = new Student();
                    student.setId(resultSet.getInt("student_id"));
                    student.setName(resultSet.getString("student_name"));
                    student.setAge(resultSet.getInt("student_age"));
                    group.addStudent(student);
               } while (resultSet.next());

               return group;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Student getStudent(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM students WHERE id=" + id;

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt("id"));
                student.setName(resultSet.getString("name"));
                student.setAge(resultSet.getInt("age"));
                return student;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Group> getGroup() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM groups) LEFT JOIN (SELECT id as student_id, group_id as id, name as student_name, age as student_age FROM students) USING (id)";

            List<Group> groups = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Group group = null;

                do {
                    if (group == null || resultSet.getInt("id") != group.getId()) {
                        if (group != null) {
                            groups.add(group);
                        }

                        group = new Group();
                        group.setId(resultSet.getInt("id"));
                        group.setName(resultSet.getString("name"));
                        group.setYear(resultSet.getInt("year"));
                    }

                    Student student = new Student();
                    student.setId(resultSet.getInt("student_id"));
                    if (resultSet.wasNull()) {
                        continue;
                    }
                    student.setName(resultSet.getString("student_name"));
                    student.setAge(resultSet.getInt("student_age"));
                    group.addStudent(student);
                } while (resultSet.next());

                groups.add(group);
            }

            return groups;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> getStudents() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM students";

            List<Student> students = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt("id"));
                student.setName(resultSet.getString("name"));
                student.setAge(resultSet.getInt("age"));
                students.add(student);
            }

            return students;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
