package org.example.Repositories;

import org.example.Models.Student;
import org.example.Models.Group;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLRepository implements Repository {
    private final List<Group> groups = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();

    private int maxGroupsId = 0;
    private int maxStudentsId = 0;

    public XMLRepository(String path) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(path));
            doc.getDocumentElement().normalize();

            NodeList elements = doc.getElementsByTagName(Group.GROUP);
            for (int i = 0; i < elements.getLength(); ++i) {
                Element element = (Element) elements.item(i);

                Group group = new Group();
                parseGroup(element, group);

                maxGroupsId = Math.max(maxGroupsId, group.getId());
                groups.add(group);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countGroups() {
        return groups.size();
    }

    @Override
    public int countStudents() {
        return students.size();
    }

    @Override
    public void insertGroup(Group value) {
        maxGroupsId++;
        value.setId(maxGroupsId);
        groups.add(value);

        for (Student student : value.getStudents()) {
            maxStudentsId++;
            student.setId(maxStudentsId);
            students.add(student);
        }
    }

    @Override
    public void insertStudent(int groupId, Student student) {
        Group group = getGroup(groupId);

        if (group != null) {
            maxStudentsId++;
            student.setId(maxStudentsId);

            group.addStudent(student);
            students.add(student);
        }
    }

    @Override
    public void deleteGroup(int id) {
        for (int i = 0; i < groups.size(); ++i) {
            if (groups.get(i).getId() == id) {
                students.removeAll(groups.get(i).getStudents());
                groups.remove(i);
                return;
            }
        }
    }

    @Override
    public void deleteStudent(int id) {
        Student student = getStudent(id);
        if (student != null) {
            for (Group group : groups) {
                if (group.hasStudent(student)) {
                    group.removeStudent(student);
                    return;
                }
            }
        }
    }

    @Override
    public Group getGroup(int id) {
        for (Group item : groups) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public Student getStudent(int id) {
        for (Student item : students) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public List<Group> getGroup() {
        return groups;
    }

    @Override
    public List<Student> getStudents() {
        return students;
    }

    public void save(String file) throws ParserConfigurationException, TransformerException {
        Document doc = createDocumentWithItems();

        Source source = new DOMSource(doc);
        Result result = new StreamResult(new File(file));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(source, result);
    }

    private void parseGroup(Element element, Group group) {
        group.setId(Integer.parseInt(element.getAttribute(Group.ID)));
        group.setName(element.getAttribute(Group.NAME));
        group.setYear(Integer.parseInt(element.getAttribute(Group.YEAR)));

        NodeList children = element.getElementsByTagName(Student.STUDENT);
        for (int i = 0; i < children.getLength(); ++i) {
            Element child = (Element) children.item(i);

            Student student = new Student();
            student.setId(Integer.parseInt(child.getAttribute(Student.ID)));
            student.setName(child.getAttribute(Student.NAME));
            student.setAge(Integer.parseInt(child.getAttribute(Student.AGE)));
            group.addStudent(student);

            maxStudentsId = Math.max(maxStudentsId, student.getId());
            students.add(student);
        }
    }

    private Document createDocumentWithItems() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element groups = doc.createElement(Group.GROUPS);
        for (Group item: this.groups) {
            Element group = doc.createElement(Group.GROUP);

            group.setAttribute(Group.ID, String.valueOf(item.getId()));
            group.setAttribute(Group.NAME, item.getName());
            group.setAttribute(Group.YEAR, String.valueOf(item.getYear()));

            for (Student subitem: item.getStudents()) {
                Element student = doc.createElement(Student.STUDENT);
                student.setAttribute(Student.ID, String.valueOf(subitem.getId()));
                student.setAttribute(Student.NAME, subitem.getName());
                student.setAttribute(Student.AGE, String.valueOf(subitem.getAge()));
                group.appendChild(student);
            }

            groups.appendChild(group);
        }

        doc.appendChild(groups);

        return doc;
    }
}
