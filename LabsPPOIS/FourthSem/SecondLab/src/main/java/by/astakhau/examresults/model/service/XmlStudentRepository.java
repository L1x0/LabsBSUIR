package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.entity.Exam;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class XmlStudentRepository {
    ObservableList<Student> students;

    public XmlStudentRepository(DataSourceChooser.DataSourceChoice dataSourceChoice) throws Exception {
        students = LoadData.loadStudents(dataSourceChoice);
    }

    // Создание (Create)
    public void addStudent(Student student) {
        students.add(student);
    }

    public void addAllStudents(ObservableList<Student> newStudents) {
        students.addAll(newStudents);
    }

    // Чтение (Read)
    public ObservableList<Student> getAllStudents() {
        return students;
    }

    // Обновление (Update)
    public void updateStudent(Student updatedStudent) {
        students.replaceAll(student ->
                student.getId().equals(updatedStudent.getId()) ? updatedStudent : student
        );
    }

    // Удаление (Delete)
    public ObservableList<Student> deleteStudent(Long id) {
        ObservableList<Student> removed = students.filtered(s -> s.getId().equals(id));
        students.removeAll(removed);
        return removed;
    }

    // Поиск по среднему баллу и предмету
    public ObservableList<Student> findByAverageScoreAndSubject(
            ObservableList<Student> source,
            double lower,
            double upper,
            String subject
    ) {
        return source.filtered(student -> {
            double avg = student.getExams().stream()
                    .filter(e -> e.getSubjectName().equals(subject))
                    .mapToInt(Exam::getScore)
                    .average()
                    .orElse(0.0);
            return avg >= lower && avg <= upper;
        });
    }

    // Поиск по номеру группы
    public ObservableList<Student> findByStudentsGroup(
            ObservableList<Student> source,
            String group
    ) {
        return source.filtered(s -> s.getStudentsGroup().equals(group));
    }

    // Поиск по баллу и предмету
    public ObservableList<Student> findByScoreAndSubject(
            ObservableList<Student> source,
            int lower,
            int upper,
            String subject
    ) {
        return source.filtered(student ->
                student.getExams().stream()
                        .anyMatch(e -> e.getSubjectName().equals(subject) &&
                                e.getScore() >= lower &&
                                e.getScore() <= upper)
        );
    }

    // Методы для получения списков
    public ObservableList<String> findAllGroups(ObservableList<Student> source) {
        return source.stream()
                .map(Student::getStudentsGroup)
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<String> findSubjectsByGroup(
            ObservableList<Student> source,
            String group
    ) {
        return source.stream()
                .filter(s -> s.getStudentsGroup().equals(group))
                .flatMap(s -> s.getExams().stream())
                .map(Exam::getSubjectName)
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    // Методы удаления с возвратом удаленных элементов
    public ObservableList<Student> deleteByAverageScoreAndSubject(
            ObservableList<Student> source,
            double lower,
            double upper,
            String subject
    ) {
        ObservableList<Student> toRemove = findByAverageScoreAndSubject(source, lower, upper, subject);
        students.removeAll(toRemove);
        return toRemove;
    }

    public ObservableList<Student> deleteByGroup(
            ObservableList<Student> source,
            String group
    ) {
        ObservableList<Student> toRemove = findByStudentsGroup(source, group);
        students.removeAll(toRemove);
        return toRemove;
    }

    public ObservableList<Student> deleteByScoreAndSubject(
            ObservableList<Student> source,
            int lower,
            int upper,
            String subject
    ) {
        ObservableList<Student> toRemove = findByScoreAndSubject(source, lower, upper, subject);
        students.removeAll(toRemove);
        return toRemove;
    }


}
