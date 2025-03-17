package by.astakhau.examresults.controller;

import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.DataSourceChooser;
import by.astakhau.examresults.model.service.LoadData;
import by.astakhau.examresults.model.service.StudentRepository;
import by.astakhau.examresults.model.service.XmlStudentRepository;
import by.astakhau.examresults.util.AddStudentDialog;
import by.astakhau.examresults.util.ManipulationsDialog;
import by.astakhau.examresults.util.CustomTable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.stage.Stage;

public class MainController {

    private DataSourceChooser.DataSourceChoice dataSourceType;
    private CustomTable table;

    @FXML
    private Pagination pagination;

    private static final int ROWS_PER_PAGE = 20;
    private ObservableList<Student> students;
    private int maxExams;

    @FXML
    public void initialize() {
        try {
            dataSourceType = DataSourceChooser.chooseDataSource(new Stage());
            students = LoadData.loadStudents(dataSourceType);
            maxExams = LoadData.loadExamCount(students);

            table = new CustomTable(students, maxExams, dataSourceType, pagination);
            table.createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Остальные методы остаются без изменений
    @FXML
    private void handleAddStudent() {
        try {
            Student student = AddStudentDialog.AddStudentDialog();
            students.add(student);

            if (dataSourceType.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
                new XmlStudentRepository(dataSourceType).addAllStudents(students);
            } else {
                new StudentRepository().addStudent(student);
            }

            students = LoadData.loadStudents(dataSourceType);
            maxExams = LoadData.loadExamCount(students);

            table.setStudents(students);
            table.setMaxExams(maxExams);
            table.setDataSourceType(dataSourceType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        table.createTable();
    }

    @FXML
    private void handleDelete() {
        try {
            ManipulationsDialog.deleteDialog(dataSourceType);

            students = LoadData.loadStudents(dataSourceType);
            maxExams = LoadData.loadExamCount(students);

            table.setStudents(students);
            table.setMaxExams(maxExams);
            table.setDataSourceType(dataSourceType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        table.createTable();
    }

    @FXML
    private void handleSearch() throws Exception {
        ManipulationsDialog.findDialog(new Stage(), dataSourceType);
    }

    @FXML
    private void handleLoad() {
        if (dataSourceType.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
            try {
                dataSourceType.setFile(LoadData.loadNewFile());
                students = LoadData.loadStudents(dataSourceType);
                maxExams = LoadData.loadExamCount(students);

                table.setStudents(students);
                table.setMaxExams(maxExams);
                table.setDataSourceType(dataSourceType);
            } catch (Exception e) {
                e.printStackTrace();
            }

            table.createTable();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Техническая особенность");
            alert.setHeaderText(null);
            alert.setContentText("Все изменения базы данных сохраняются автоматически");
            alert.showAndWait();
        }
    }


    @FXML
    private void handleTurnDataSource() {
        initialize();
    }
}