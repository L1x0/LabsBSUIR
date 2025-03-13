package by.astakhau.examresults.model.service;

import by.astakhau.examresults.model.entity.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.File;

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

}
