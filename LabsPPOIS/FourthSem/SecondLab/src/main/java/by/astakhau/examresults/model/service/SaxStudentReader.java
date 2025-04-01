package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaxStudentReader {
    private List<Student> students;
    private Student currentStudent;
    private Exam currentExam;
    private StringBuilder currentValue;

    public List<Student> readStudents(File file) throws Exception {
        if (file.length() == 0) {
            return new ArrayList<>();
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        StudentHandler handler = new StudentHandler();
        saxParser.parse(file, handler);

        return students;
    }

    private class StudentHandler extends DefaultHandler {
        @Override
        public void startDocument() {
            students = new ArrayList<>();
            currentValue = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes) {
            currentValue.setLength(0);
            switch (qName) {
                case "student":
                    currentStudent = new Student();
                    break;
                case "exam":
                    currentExam = new Exam();
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            currentValue.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            switch (qName) {
                case "fullName":
                    currentStudent.setFullName(currentValue.toString().trim());
                    break;
                case "studentsGroup":
                    currentStudent.setStudentsGroup(currentValue.toString().trim());
                    break;
                case "subjectName":
                    if (currentExam != null) {
                        currentExam.setSubjectName(currentValue.toString().trim());
                    }
                    break;
                case "score":
                    if (currentExam != null) {
                        currentExam.setScore(Integer.parseInt(currentValue.toString().trim()));
                    }
                    break;
                case "exam":
                    currentStudent.getExams().add(currentExam);
                    currentExam = null;
                    break;
                case "student":
                    students.add(currentStudent);
                    currentStudent = null;
                    break;
            }
        }
    }
}