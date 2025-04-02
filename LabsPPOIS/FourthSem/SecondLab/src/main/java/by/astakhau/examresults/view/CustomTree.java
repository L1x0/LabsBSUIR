package by.astakhau.examresults.view;

import by.astakhau.examresults.model.entity.Exam;
import by.astakhau.examresults.model.entity.Student;
import by.astakhau.examresults.model.service.DataService;
import by.astakhau.examresults.model.service.DataSourceChooser;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CustomTree {
    private TreeView<String> treeView;
    private DataSourceChooser.DataSourceChoice dataSource;

    public CustomTree(TreeView<String> treeView, DataSourceChooser.DataSourceChoice dataSourceChoice) {
        this.dataSource = dataSourceChoice;
        this.treeView = treeView;
        configureTreeView();
    }

    private void configureTreeView() {
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if (getTreeItem() != null && getTreeItem().isLeaf()) {
                        setDisclosureNode(null);
                    }
                }
            }
        });
    }

    public void createTreeView() {
        ObservableList<Student> students = loadStudents();
        TreeItem<String> root = createRootItem();

        if (students != null) {
            students.forEach(student -> {
                TreeItem<String> studentItem = createStudentItem(student);
                root.getChildren().add(studentItem);
            });
        }

        treeView.setRoot(root);
    }

    private TreeItem<String> createRootItem() {
        TreeItem<String> root = new TreeItem<>("Студенты");
        root.setExpanded(true);
        return root;
    }

    private TreeItem<String> createStudentItem(Student student) {
        TreeItem<String> studentItem = new TreeItem<String>(student.getFullName()) {
            @Override
            public boolean isLeaf() {
                return false;
            }
        };

        // Узел группы (лист)
        TreeItem<String> groupItem = new TreeItem<>("Группа: " + student.getStudentsGroup()) {
            @Override
            public boolean isLeaf() {
                return true;
            }
        };

        if (!student.getExams().isEmpty()) {
            TreeItem<String> examsItem = new TreeItem<>("Экзамены") {
                @Override
                public boolean isLeaf() {
                    return false;
                }
            };

            student.getExams().forEach(exam -> {
                examsItem.getChildren().add(createExamItem(exam));
            });
            studentItem.getChildren().addAll(groupItem, examsItem);
        } else {
            studentItem.getChildren().add(groupItem);
        }

        return studentItem;
    }

    private TreeItem<String> createExamItem(Exam exam) {
        TreeItem<String> examItem = new TreeItem<String>(exam.getSubjectName()) {
            @Override
            public boolean isLeaf() {
                return false;
            }
        };

        TreeItem<String> scoreItem = new TreeItem<>("Оценка: " + exam.getScore()) {
            @Override
            public boolean isLeaf() {
                return true;
            }
        };

        examItem.getChildren().add(scoreItem);
        return examItem;
    }

    private ObservableList<Student> loadStudents() {
        try {
            return DataService.loadStudents(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}