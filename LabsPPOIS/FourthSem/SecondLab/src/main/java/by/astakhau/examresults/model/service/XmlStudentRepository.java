package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.entity.Exam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class XmlStudentRepository {
    ObservableList<Student> students;
    DataSourceChooser.DataSourceChoice dataSource;

    public XmlStudentRepository(DataSourceChooser.DataSourceChoice dataSourceChoice) throws Exception {
        students = DataService.loadStudents(dataSourceChoice);
        this.dataSource = dataSourceChoice;
    }

    public void addStudent(Student student) throws Exception {
        DomStudentWriter domStudentWriter = new DomStudentWriter();
        students.add(student);
        domStudentWriter.writeStudentsToSourceFile(students.stream().toList(), dataSource.getFile());
    }

    public void addAllStudents(ObservableList<Student> newStudents) throws Exception {
        DomStudentWriter domStudentWriter = new DomStudentWriter();
        newStudents.addAll(students);
        students = newStudents;
        domStudentWriter.writeStudentsToSourceFile(newStudents.stream().toList(), dataSource.getFile());
    }

    public ObservableList<Student> getAllStudents() {
        SaxStudentReader saxStudentReader = new SaxStudentReader();
        try {
            ObservableList<Student> foundStudents =
                    FXCollections.observableArrayList(saxStudentReader.readStudents(dataSource.getFile()));
            students.addAll(foundStudents);
        } catch (Exception e) {
            System.out.println("не найден файл для чтения");
            return students;
        }
        return students;
    }

    public ObservableList<Student> deleteStudent(Long id) {
        ObservableList<Student> removed = students.filtered(s -> s.getId().equals(id));
        students.removeAll(removed);
        return removed;
    }

    public ObservableList<Student> findByAverageScoreAndSubject(
            int lower,
            int upper,
            String subject
    ) {
        return getAllStudents().filtered(student -> {
            double avg = student.getExams().stream()
                    .filter(e -> e.getSubjectName().equals(subject))
                    .mapToInt(Exam::getScore)
                    .average()
                    .orElse(0.0);
            return avg >= lower && avg <= upper;
        });
    }

    public ObservableList<Student> findByStudentsGroup(
            ObservableList<Student> source,
            String group
    ) {
        return source.filtered(s -> s.getStudentsGroup().equals(group));
    }

    public ObservableList<Student> findByScoreAndSubject(
            int lower,
            int upper,
            String subject
    ) {
        return getAllStudents().filtered(student ->
                student.getExams().stream()
                        .anyMatch(e -> e.getSubjectName().equals(subject) &&
                                e.getScore() >= lower &&
                                e.getScore() <= upper)
        );
    }

    public ObservableList<String> findAllGroups() {
        return getAllStudents().stream()
                .map(Student::getStudentsGroup)
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<String> findSubjectsByGroup(
            String group
    ) {
        return getAllStudents().stream()
                .filter(s -> s.getStudentsGroup().equals(group))
                .flatMap(s -> s.getExams().stream())
                .map(Exam::getSubjectName)
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public int deleteByAverageScoreAndSubject(
            int lower,
            int upper,
            String subject,
            DataSourceChooser.DataSourceChoice dataSourceChoice
    ) throws Exception {
        ObservableList<Student> toRemove = findByAverageScoreAndSubject(lower, upper, subject);
        int sizeToRemove = toRemove.size();

        students.removeAll(toRemove);
        FileSave.saveFile(dataSourceChoice.getFile(), students);

        return sizeToRemove;
    }

    public int deleteByGroup(
            String group,
            DataSourceChooser.DataSourceChoice dataSourceChoice
    ) throws Exception {
        ObservableList<Student> toRemove = findByStudentsGroup(getAllStudents(), group);
        int sizeToRemove = toRemove.size();

        students.removeAll(toRemove);
        FileSave.saveFile(dataSourceChoice.getFile(), students);

        return sizeToRemove;
    }

    public int deleteByScoreAndSubject(
            int lower,
            int upper,
            String subject,
            DataSourceChooser.DataSourceChoice dataSourceChoice
    ) throws Exception {
        ObservableList<Student> toRemove = findByScoreAndSubject(lower, upper, subject);
        int sizeToRemove = toRemove.size();

        students.removeAll(toRemove);
        FileSave.saveFile(dataSourceChoice.getFile(), students);

        return sizeToRemove;
    }


}
