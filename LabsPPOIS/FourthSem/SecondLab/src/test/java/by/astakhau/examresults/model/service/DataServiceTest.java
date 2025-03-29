package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import static org.junit.jupiter.api.Assertions.*;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DataServiceTest {
    private DataSourceChooser.DataSourceChoice dataSource;

    @BeforeEach
    public void setUp() {
        dataSource
                = DataSourceChooser.chooseDataSource(DataSourceChooser.DataSourceType.LOCAL_DB);

        Student student = new Student();
        student.setFullName("Артём");
        student.setStudentsGroup("321701");

        List<Exam> exams = new ArrayList<>();

        Exam exam = new Exam();
        exam.setStudent(student);
        exam.setSubjectName("math");
        exam.setScore(10);

        exams.add(exam);

        student.setExams(exams);

        new StudentRepository().addStudent(student);
    }

    @Test
    public void loadDataTest() throws Exception {
        assertNotNull(DataService.loadStudents(dataSource));
    }

    @Test
    public void loadExamCount() throws Exception {
        ObservableList<Student> students = DataService.loadStudents(dataSource);
        int count = students.stream()
                .mapToInt(s -> s.getExams().size())
                .max()
                .orElse(0);

        assertEquals(count, DataService.loadExamCount(students));
    }
}
