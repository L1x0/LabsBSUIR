package by.astakhau.examresults.util;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.DataSourceChooser;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import lombok.Setter;

import java.util.List;

@Setter
public class CustomTable {
    private static int ROWS_PER_PAGE = 20;
    ObservableList<Student> students;
    int maxExams;
    DataSourceChooser.DataSourceChoice dataSourceType;
    Pagination pagination;
    ChoiceBox<Integer> pageSize;
    Label countOfRecords;

    public CustomTable(
            ObservableList<Student> students,
            int maxExams,
            DataSourceChooser.DataSourceChoice dataSourceType,
            Pagination pagination,
            ChoiceBox<Integer> pageSize,
            Label label) {
        this.students = students;
        this.maxExams = maxExams;
        this.dataSourceType = dataSourceType;
        this.pagination = pagination;
        this.pageSize = pageSize;
        this.countOfRecords = label;
    }

    public void createTable() {
        setupPageSize();
        setupPagination();
        countOfRecords.setText(getCountOfRecords());
    }

    private void setupPageSize() {
        pageSize.setItems(FXCollections.observableArrayList(20,25,30));
        pageSize.setValue(ROWS_PER_PAGE);

        pageSize.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    ROWS_PER_PAGE = newVal;
                    setupPagination();
                }
        );

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

    public void goToLast() {
        pagination.setCurrentPageIndex(pagination.getPageCount() - 1);
    }

    public void goToFirst() {
        pagination.setCurrentPageIndex(0);
    }

    public String getCountOfRecords() {
        StringBuilder builder = new StringBuilder();

        builder.append("Записей в таблице: ");
        builder.append(students.size());

        return builder.toString();
    }
}
