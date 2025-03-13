package by.astakhau.examresults.controller;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.DataSourceChooser;
import by.astakhau.examresults.model.service.LoadData;
import by.astakhau.examresults.model.service.SaveData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class MainController {

    private DataSourceChooser.DataSourceChoice dataSourceType;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupPagination();
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) students.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
    }

    private TableView<Student> createPage(int pageIndex) {
        TableView<Student> tableView = new TableView<>();
        createColumns(tableView);
        setPageData(tableView, pageIndex);
        return tableView;
    }

    private void createColumns(TableView<Student> tableView) {
        // Основные колонки
        TableColumn<Student, String> fioCol = new TableColumn<>("ФИО студента");
        fioCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName()));

        TableColumn<Student, String> groupCol = new TableColumn<>("Группа");
        groupCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudentsGroup()));

        // Общая колонка для экзаменов
        TableColumn<Student, Void> examsCol = new TableColumn<>("Экзамены");

        // Создаем подколонки для каждого экзамена
        for (int i = 0; i < maxExams; i++) {
            TableColumn<Student, Void> examNumberCol = new TableColumn<>(String.valueOf(i + 1));

            TableColumn<Student, String> subjectCol = new TableColumn<>("Наименование");
            TableColumn<Student, String> scoreCol = new TableColumn<>("Балл");

            final int examIndex = i;
            subjectCol.setCellValueFactory(cellData -> {
                List<Exam> exams = cellData.getValue().getExams();
                return exams.size() > examIndex
                        ? new SimpleStringProperty(exams.get(examIndex).getSubjectName())
                        : new SimpleStringProperty("");
            });

            scoreCol.setCellValueFactory(cellData -> {
                List<Exam> exams = cellData.getValue().getExams();
                return exams.size() > examIndex
                        ? new SimpleStringProperty(String.valueOf(exams.get(examIndex).getScore()))
                        : new SimpleStringProperty("");
            });

            examNumberCol.getColumns().addAll(subjectCol, scoreCol);
            examsCol.getColumns().add(examNumberCol);
        }

        tableView.getColumns().addAll(fioCol, groupCol, examsCol);
    }

    private void setPageData(TableView<Student> tableView, int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, students.size());
        tableView.setItems(FXCollections.observableArrayList(students.subList(fromIndex, toIndex)));
    }

    // Остальные методы остаются без изменений
    @FXML
    private void handleAdd() { /* ... */ }

    @FXML
    private void handleEdit() { /* ... */ }

    @FXML
    private void handleDelete() { /* ... */ }

    @FXML
    private void handleSearch() { /* ... */ }

    @FXML
    private void handleLoad() {
        if (dataSourceType.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
            try {
                dataSourceType.setFile(LoadData.loadNewFile());
                students = LoadData.loadStudents(dataSourceType);
                maxExams = LoadData.loadExamCount(students);
            } catch (Exception e) {
                e.printStackTrace();
            }

            setupPagination();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Техническая особенность");
            alert.setHeaderText(null);
            alert.setContentText("Все изменения базы данных сохраняются автоматически");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSave() {
        if (dataSourceType.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
            try {
                SaveData.saveFile(dataSourceType.getFile(), students);
            } catch (Exception e) {
                e.printStackTrace();
            }
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