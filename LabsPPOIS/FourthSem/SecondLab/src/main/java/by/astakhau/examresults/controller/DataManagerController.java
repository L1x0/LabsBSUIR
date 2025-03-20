package by.astakhau.examresults.controller;

import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.DataSourceChooser;
import by.astakhau.examresults.model.service.LoadData;
import by.astakhau.examresults.model.service.StudentRepository;
import by.astakhau.examresults.model.service.XmlStudentRepository;
import by.astakhau.examresults.view.AddStudentDialog;
import by.astakhau.examresults.view.CustomTree;
import by.astakhau.examresults.view.ManipulationsDialog;
import by.astakhau.examresults.view.CustomTable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DataManagerController {

    private DataSourceChooser.DataSourceChoice dataSourceType;
    private CustomTable table;

    @FXML
    private Pagination pagination;
    @FXML
    private VBox tableView;
    @FXML
    TreeView<String> treeView;
    @FXML
    StackPane contentPane;
    @FXML
    ToggleButton viewChangeToggle;
    @FXML
    private ChoiceBox<Integer> pageSize;
    private ObservableList<Student> students;
    private int maxExams;
    @FXML
    private Label countOfRecords;

    @FXML
    public void initialize() {
        try {
            viewChangeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    tableView.setVisible(false);
                    treeView.setVisible(true);

                    CustomTree tree = new CustomTree(treeView, dataSourceType);
                    tree.createTreeView();
                } else {
                    tableView.setVisible(true);
                    treeView.setVisible(false);

                    table.setDataSourceType(dataSourceType);
                }
            });

            viewChangeToggle.setSelected(false);

            dataSourceType = DataSourceChooser.chooseDataSource(new Stage());
            students = LoadData.loadStudents(dataSourceType);
            maxExams = LoadData.loadExamCount(students);

            table = new CustomTable(students, maxExams, dataSourceType, pagination, pageSize, countOfRecords);
            table.createTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Остальные методы остаются без изменений
    @FXML
    private void handleAddStudent() {
        try {
            Student student = AddStudentDialog.getStudentDialog();
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
    private void handleSearch() {
        try {
            ManipulationsDialog.findDialog(new Stage(), dataSourceType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToLast() {
        table.goToLast();
    }

    @FXML
    private void handleGoToFirst() {
        table.goToFirst();
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