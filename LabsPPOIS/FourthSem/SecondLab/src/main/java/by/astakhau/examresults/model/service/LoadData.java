package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class LoadData {
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

}
