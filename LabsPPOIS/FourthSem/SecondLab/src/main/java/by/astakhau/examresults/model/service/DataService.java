package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class DataService {
    public static ObservableList<Student> loadStudents(DataSourceChooser.DataSourceChoice dataSourceType) throws Exception {

        if (dataSourceType.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
            File xmlFile = dataSourceType.getFile();
            SaxStudentReader reader = new SaxStudentReader();
            return FXCollections.observableArrayList(reader.readStudents(xmlFile));
        } else {
            StudentRepository repo = new StudentRepository();
            return FXCollections.observableArrayList(repo.getAllStudents());
        }
    }

    public static int loadExamCount(ObservableList<Student> students) throws Exception {
         return students.stream()
                .mapToInt(s -> s.getExams().size())
                .max()
                .orElse(0);
    }

    public static File loadNewFile() {
        return DataSourceChooser.getXmlFile(new Stage());
    }

    public static List<String> getAllGroups(DataSourceChooser.DataSourceChoice dataSourceChoice) throws Exception {
        return dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                ? new XmlStudentRepository(dataSourceChoice).findAllGroups()
                : new StudentRepository().findAllGroups();
    }

    public static List<String> getAllSubjects(DataSourceChooser.DataSourceChoice dataSourceChoice, String group) throws Exception {
        return dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                ? new XmlStudentRepository(dataSourceChoice).findSubjectsByGroup(group)
                : new StudentRepository().findSubjectsByGroup(group);
    }

    public static ObservableList<Student> getStudentsByAverageAndSubject(
            DataSourceChooser.DataSourceChoice dataSourceChoice,
            Pair<Integer, Integer> range,
            Optional<String> subject) throws Exception {

        return dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                ? new XmlStudentRepository(dataSourceChoice)
                .findByAverageScoreAndSubject(range.getKey(), range.getValue(), subject.get())

                : new StudentRepository()
                .findByAverageScoreAndSubject(range.getKey(), range.getValue(), subject.get());
    }

    public static ObservableList<Student> getStudentsByScoreAndSubject(
            DataSourceChooser.DataSourceChoice dataSourceChoice,
            Pair<Integer, Integer> range,
            Optional<String> subject) throws Exception {

        return dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                ? new XmlStudentRepository(dataSourceChoice)
                .findByScoreAndSubject(range.getKey(), range.getValue(), subject.get())

                : new StudentRepository()
                .findByScoreAndSubject(range.getKey(), range.getValue(), subject.get());
    }

    public static int deleteByGroup(DataSourceChooser.DataSourceChoice dataSourceChoice, String group) throws Exception {
        return dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                ? new XmlStudentRepository(dataSourceChoice).deleteByGroup(group, dataSourceChoice)
                : new StudentRepository().deleteByGroup(group);
    }

    public static int deleteByAverageAndSubject(DataSourceChooser.DataSourceChoice dataSourceChoice,
                                                Pair<Integer, Integer> range,
                                                String subject) throws Exception {

        return dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                ? new XmlStudentRepository(dataSourceChoice)
                .deleteByAverageScoreAndSubject(range.getKey(), range.getValue(), subject, dataSourceChoice)

                : new StudentRepository()
                .deleteByAverageScoreAndSubject(range.getKey(), range.getValue(), subject);
    }

    public static int deleteByScoreAndSubject(DataSourceChooser.DataSourceChoice dataSourceChoice,
                                                Pair<Integer, Integer> range,
                                                String subject) throws Exception {

        return dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                ? new XmlStudentRepository(dataSourceChoice)
                .deleteByScoreAndSubject(range.getKey(), range.getValue(), subject, dataSourceChoice)

                : new StudentRepository()
                .deleteByScoreAndSubject(range.getKey(), range.getValue(), subject);
    }
}
