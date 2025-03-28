package by.astakhau.examresults.view;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddStudentDialog {
    private ObservableList<Student> students;

    public static Student getStudentDialog() {
        ObservableList<Student> students = FXCollections.observableArrayList();
        AtomicBoolean anyProblem = new AtomicBoolean(true);

        Dialog<Pair<Pair<String, String>, Integer>> firstDialog = new Dialog<>();
        firstDialog.setTitle("Добавление студента");
        firstDialog.setHeaderText("Введите основные данные");

        TextField nameField = new TextField();
        TextField groupField = new TextField();
        Spinner<Integer> examsSpinner = new Spinner<>(1, 10, 1);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("ФИО:"), nameField);
        grid.addRow(1, new Label("Группа:"), groupField);
        grid.addRow(2, new Label("Количество экзаменов:"), examsSpinner);

        firstDialog.getDialogPane().setContent(grid);
        firstDialog.getDialogPane().getButtonTypes().addAll(ButtonType.NEXT, ButtonType.CANCEL);


        firstDialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.NEXT) {
                anyProblem.set(validateStudentsInputs(nameField.getText().trim(), groupField.getText().trim()));

                return new Pair<>(
                        new Pair<>(nameField.getText().trim(), groupField.getText().trim()),
                        examsSpinner.getValue()
                );
            } else if (buttonType == ButtonType.CANCEL) {
                anyProblem.set(false);
                return null;
            }
            return null;
        });


        Optional<Pair<Pair<String, String>, Integer>> firstResult = Optional.empty();

        while (anyProblem.get()) {
            firstResult = firstDialog.showAndWait();
        }
        anyProblem.set(true);

        firstResult.ifPresent(result -> {
            Pair<String, String> nameGroup = result.getKey();
            int examsCount = result.getValue();

            if (nameGroup.getKey().isEmpty() || nameGroup.getValue().isEmpty()) {
                showErrorAlert("Все поля должны быть заполнены!");
                return;
            }

            // Второй диалог: экзамены
            Dialog<List<Exam>> secondDialog = new Dialog<>();
            secondDialog.setTitle("Добавление экзаменов");
            secondDialog.setHeaderText("Введите данные об экзаменах");

            VBox vbox = new VBox(10);
            ScrollPane scrollPane = new ScrollPane(vbox);
            scrollPane.setFitToWidth(true);

            List<TextField> subjectFields = new ArrayList<>();
            List<Spinner<Integer>> scoreSpinners = new ArrayList<>();


            for (int i = 0; i < examsCount; i++) {
                TextField subjectField = new TextField();
                Spinner<Integer> scoreSpinner = new Spinner<>(0, 10, 5);

                HBox hbox = new HBox(10);
                hbox.getChildren().addAll(
                        new Label("Экзамен " + (i + 1) + ":"),
                        subjectField,
                        new Label("Оценка:"),
                        scoreSpinner
                );

                subjectFields.add(subjectField);
                scoreSpinners.add(scoreSpinner);
                vbox.getChildren().add(hbox);
            }

            secondDialog.getDialogPane().setContent(scrollPane);
            secondDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


            secondDialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    List<Exam> exams = new ArrayList<>();
                    for (int i = 0; i < examsCount; i++) {
                        String subject = subjectFields.get(i).getText().trim();
                        anyProblem.set(validateExamInputs(subject));

                        int score = scoreSpinners.get(i).getValue();

                        if (subject.isEmpty()) {
                            showErrorAlert("Название предмета не может быть пустым!");
                        }

                        exams.add(new Exam(subject, score, null));
                    }
                    return exams;
                } else if (buttonType == ButtonType.CANCEL) {
                    anyProblem.set(false);
                    return null;
                }
                return null;
            });

            Optional<List<Exam>> secondResult = Optional.empty();

            while (anyProblem.get()) {
                secondResult = secondDialog.showAndWait();
            }

            secondResult.ifPresent(exams -> {
                Student student = new Student();
                student.setFullName(nameGroup.getKey());
                student.setStudentsGroup(nameGroup.getValue());

                for (Exam exam : exams) {
                    exam.setStudent(student);
                }
                student.setExams(exams);

                if (students.stream().anyMatch(s -> s.getFullName().equals(student.getFullName()))) {
                    showErrorAlert("Студент с таким ФИО уже существует!");
                    return;
                }

                students.add(student);
            });
        });
        if (students.isEmpty())
            return null;

        return students.get(0);
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static boolean validateStudentsInputs(String name, String group) {
        if (Validator.validateFullName(name) && Validator.validateGroupNumber(group)) {
            return false;
        } else {
            showStudentsValidatorErrorAlert(name, group);
            return true;
        }
    }

    private static void showStudentsValidatorErrorAlert(String name, String group) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        StringBuilder warningMessage = new StringBuilder();

        warningMessage.append("Проверьте введённые данные\n");
        if (!Validator.validateSubjectName(name)) {
            warningMessage.append("Имя может состоять только из русских и латинских букв\n");
        }

        if (!Validator.validateGroupNumber(group)) {
            warningMessage.append("Группа может состоять только из 6 цифр");
        }

        alert.setHeaderText(null);
        alert.setContentText(warningMessage.toString());
        alert.showAndWait();
    }

    private static boolean validateExamInputs(String subjectName) {
        if (Validator.validateSubjectName(subjectName)) {
            return false;
        } else {
            showExamsValidatorErrorAlert();
            return true;
        }
    }

    private static void showExamsValidatorErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(null);
        alert.setContentText("Проверьте названия экзаменов, они могут состоять только из букв");
        alert.showAndWait();
    }
}