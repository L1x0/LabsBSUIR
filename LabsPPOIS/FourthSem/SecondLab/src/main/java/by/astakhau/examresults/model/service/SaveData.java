package by.astakhau.examresults.model.service;


import by.astakhau.examresults.model.entity.Student;
import javafx.collections.ObservableList;

import java.io.File;

public class SaveData {
    public static void saveFile(File file, ObservableList<Student> students) throws Exception {
        DomStudentWriter writer = new DomStudentWriter();
        writer.writeStudentsToSourceFile(students, file);
    }
}
