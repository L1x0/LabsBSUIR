package by.astakhau.examresults.view;

import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.DataSourceChooser;
import by.astakhau.examresults.model.service.LoadData;
import by.astakhau.examresults.model.service.StudentRepository;
import by.astakhau.examresults.model.service.XmlStudentRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManipulationsDialog {
    public static void findDialog(Stage stage, DataSourceChooser.DataSourceChoice dataSourceChoice) throws Exception {
        ObservableList<Student> students = LoadData.loadStudents(dataSourceChoice);


        Pagination pagination = new Pagination();
        ChoiceBox<Integer> pageSize = new ChoiceBox<>();
        Label countOfRecords = new Label();
        CustomTable table = null;
        List<String> groups;
        Pair<Integer, Integer> range;
        Optional<String> subject;

        switch (setupDialog("Выберите критерий поиска")) {
            case "По группе":
                Optional<String> group = choiceGroupDialog(dataSourceChoice).describeConstable();

                if (group.isPresent()) {
                    ObservableList<Student> foundStudents = students.filtered(s -> {
                        return s.getStudentsGroup().equals(group.get());
                    });
                    table = new CustomTable(
                            foundStudents,
                            LoadData.loadExamCount(foundStudents),
                            dataSourceChoice,
                            pagination,
                            pageSize,
                            countOfRecords
                            );
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Не получены данные");
                    alert.showAndWait();

                    return;
                }
                break;

            case "По среднему баллу и предмету":
                groups = LoadData.getAllGroups(dataSourceChoice);
                subject =
                        choiceSubjectDialog(groups, LoadData.getAllSubjects(dataSourceChoice, groups.get(0)), dataSourceChoice)
                                .getValue()
                                .describeConstable();
                range = getRange();

                if (subject.isPresent() && range.getKey() < range.getValue()) {
                    ObservableList<Student> foundStudents
                            = dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                            ? new XmlStudentRepository(dataSourceChoice)
                            .findByAverageScoreAndSubject(range.getKey(), range.getValue(), subject.get())
                            : new StudentRepository()
                            .findByAverageScoreAndSubject(range.getKey(), range.getValue(), subject.get());
                    table = new CustomTable(
                            foundStudents,
                            LoadData.loadExamCount(foundStudents),
                            dataSourceChoice,
                            pagination,
                            pageSize,
                            countOfRecords);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Не получены данные");
                    alert.showAndWait();

                    return;
                }
                break;
            case "По баллу и предмету":
                groups = LoadData.getAllGroups(dataSourceChoice);
                subject =
                        choiceSubjectDialog(groups, LoadData.getAllSubjects(dataSourceChoice, groups.get(0)), dataSourceChoice)
                                .getValue()
                                .describeConstable();
                range = getRange();

                if (subject.isPresent() && range.getKey() < range.getValue()) {
                    ObservableList<Student> foundStudents
                            = dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)
                            ? new XmlStudentRepository(dataSourceChoice)
                            .findByScoreAndSubject(range.getKey(), range.getValue(), subject.get())
                            : new StudentRepository()
                            .findByScoreAndSubject(range.getKey(), range.getValue(), subject.get());
                    table = new CustomTable(
                            foundStudents,
                            LoadData.loadExamCount(foundStudents),
                            dataSourceChoice,
                            pagination,
                            pageSize,
                            countOfRecords);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Не получены данные");
                    alert.showAndWait();

                    return;
                }
        }

        table.createTable();
        VBox vbox = new VBox(pagination);
        Scene scene = new Scene(vbox, 800, 400);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private static String setupDialog(String message) {
        List<String> choices = new ArrayList<>();
        choices.add("По группе");
        choices.add("По среднему баллу и предмету");
        choices.add("По баллу и предмету");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("По группе", choices);
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();

        return result.orElse("");
    }

    private static String choiceGroupDialog(DataSourceChooser.DataSourceChoice dataSourceChoice) throws Exception {
        List<String> choices;

        choices = LoadData.getAllGroups(dataSourceChoice);

        ChoiceDialog<String> dialog = new ChoiceDialog<>("№ группы", choices);
        dialog.setContentText("Выберете группу");
        Optional<String> result = dialog.showAndWait();

        return result.orElse("");
    }

    private static Pair<String, String> choiceSubjectDialog(
            List<String> groups,
            List<String> subjects,
            DataSourceChooser.DataSourceChoice dataSourceChoice) {
        // Создаем ComboBox для групп
        ComboBox<String> groupCombo = new ComboBox<>();
        groupCombo.setItems(FXCollections.observableArrayList(groups));
        groupCombo.getSelectionModel().select(groups.get(0));

        // Создаем ComboBox для предметов
        ComboBox<String> subjectCombo = new ComboBox<>();
        subjectCombo.setItems(FXCollections.observableArrayList(subjects));

        // Обновляем предметы при изменении группы
        groupCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newGroup) -> {
            List<String> updatedSubjects = new ArrayList<>();

            if (dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
                try {
                    updatedSubjects = new XmlStudentRepository(dataSourceChoice).findSubjectsByGroup(newGroup);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                updatedSubjects = new StudentRepository().findSubjectsByGroup(newGroup);
            }
            subjectCombo.setItems(FXCollections.observableArrayList(updatedSubjects));
        });

        // Настраиваем layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Группа:"), 0, 0);
        grid.add(groupCombo, 1, 0);
        grid.add(new Label("Предмет:"), 0, 1);
        grid.add(subjectCombo, 1, 1);

        // Создаем диалог
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Выбор группы и предмета");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Преобразуем результат
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Pair<>(groupCombo.getSelectionModel().getSelectedItem(), subjectCombo.getSelectionModel().getSelectedItem());
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static Pair<Integer, Integer> getRange() {
        Slider fromSlider = createSlider();
        Slider toSlider = createSlider();

        fromSlider.setValue(0);
        toSlider.setValue(10);

        // Лейблы для отображения текущих значений
        Label fromValue = new Label("0");
        Label toValue = new Label("10");

        // Обработчики изменений с валидацией границ
        fromSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int currentFrom = (int) newVal.doubleValue();
            int currentTo = (int) toSlider.getValue();

            // Если "от" >= "до", ограничиваем "до" минимально возможным (currentFrom + 1)
            if (currentFrom > currentTo) {
                int newTo = Math.min(currentFrom + 1, 10); // Не больше 10
                toSlider.setValue(newTo);
            }

            fromValue.setText(String.valueOf(currentFrom));
        });

        toSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int currentTo = (int) newVal.doubleValue();
            int currentFrom = (int) fromSlider.getValue();

            // Если "до" <= "от", ограничиваем "от" максимально возможным (currentTo - 1)
            if (currentTo < currentFrom) {
                int newFrom = Math.max(currentTo - 1, 0); // Не меньше 0
                fromSlider.setValue(newFrom);
            }

            toValue.setText(String.valueOf(currentTo));
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("От:"), 0, 0);
        grid.add(fromSlider, 1, 0);
        grid.add(fromValue, 2, 0);

        grid.add(new Label("До:"), 0, 1);
        grid.add(toSlider, 1, 1);
        grid.add(toValue, 2, 1);

        // Создаем диалоговое окно
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Выбор диапазона");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Преобразуем результат
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Pair<>(
                        (int) fromSlider.getValue(),
                        (int) toSlider.getValue()
                );
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static Slider createSlider() {
        Slider slider = new Slider(0, 10, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        return slider;
    }

    public static void deleteDialog(DataSourceChooser.DataSourceChoice dataSourceChoice) throws Exception {
        List<String> groups;
        Optional<String> subject;
        Pair<Integer, Integer> range;
        Alert alert;

        switch (setupDialog("Выберите критерий удаления")) {
            case "По группе":

                Optional<String> group = choiceGroupDialog(dataSourceChoice).describeConstable();

                if (group.isPresent()) {
                    alert = getDeleteByGroupAlert(dataSourceChoice, group.get());
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Не получены данные");
                    alert.showAndWait();

                    return;
                }

                alert.showAndWait();
                break;
            case "По среднему баллу и предмету":
                groups = LoadData.getAllGroups(dataSourceChoice);
                subject =
                        choiceSubjectDialog(groups, LoadData.getAllSubjects(dataSourceChoice, groups.get(0)), dataSourceChoice)
                                .getValue()
                                .describeConstable();
                range = getRange();

                if (subject.isPresent() && range.getKey() <= range.getValue()) {
                    alert = getDeleteByAveregeAndSubjectAlert(dataSourceChoice, range, subject.get());

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Не получены данные");
                    alert.showAndWait();

                    return;
                }
                alert.showAndWait();

                break;
            case "По баллу и предмету":
                groups = LoadData.getAllGroups(dataSourceChoice);
                subject =
                        choiceSubjectDialog(groups, LoadData.getAllSubjects(dataSourceChoice, groups.get(0)), dataSourceChoice)
                                .getValue()
                                .describeConstable();
                range = getRange();

                if (subject.isPresent() && range.getKey() <= range.getValue()) {
                    alert = getDeleteByScoreAndSubjectAlert(dataSourceChoice, range, subject.get());

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Не получены данные");
                    alert.showAndWait();

                    return;
                }
                alert.showAndWait();

                break;
        }
    }

    private static Alert getDeleteByGroupAlert(
            DataSourceChooser.DataSourceChoice dataSourceChoice,
            String group
    ) throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);

        if (dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
            alert.setContentText("Количество удалённых записей: " +
                    new XmlStudentRepository(dataSourceChoice).deleteByGroup(group, dataSourceChoice));
        } else {
            alert.setContentText("Количество удалённых записей: " +
                    new StudentRepository().deleteByGroup(group));
        }

        return alert;
    }

    private static Alert getDeleteByAveregeAndSubjectAlert(
            DataSourceChooser.DataSourceChoice dataSourceChoice,
            Pair<Integer, Integer> range,
            String subject
    ) throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);

        if (dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
            alert.setContentText("Количество удалённых записей: " +
                    new XmlStudentRepository(dataSourceChoice)
                            .deleteByAverageScoreAndSubject(range.getKey(), range.getValue(), subject, dataSourceChoice));
        } else {
            alert.setContentText("Количество удалённых записей: " +
                    new StudentRepository().deleteByAverageScoreAndSubject(range.getKey(), range.getValue(), subject));
        }

        return alert;
    }

    private static Alert getDeleteByScoreAndSubjectAlert(
            DataSourceChooser.DataSourceChoice dataSourceChoice,
            Pair<Integer, Integer> range,
            String subject
    ) throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);

        if (dataSourceChoice.getType().equals(DataSourceChooser.DataSourceType.XML_FILE)) {
            alert.setContentText("Количество удалённых записей: " +
                    new XmlStudentRepository(dataSourceChoice)
                            .deleteByScoreAndSubject(range.getKey(), range.getValue(), subject, dataSourceChoice));
        } else {
            alert.setContentText("Количество удалённых записей: " +
                    new StudentRepository().deleteByScoreAndSubject(range.getKey(), range.getValue(), subject));
        }

        return alert;
    }
}
