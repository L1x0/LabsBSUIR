package by.astakhau.examresults.model.service;

import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;
import java.util.Optional;


public class DataSourceChooser {

    // Тип источника данных
    public enum DataSourceType {
        LOCAL_DB,
        XML_FILE
    }

    // Результат выбора источника данных
    @Setter
    public static class DataSourceChoice {
        private DataSourceType type;
        @Getter private File file; // Если выбран XML_FILE, содержит выбранный файл; иначе null

        public DataSourceChoice(DataSourceType type, File file) {
            this.type = type;
            this.file = file;
        }

        public DataSourceType getType() {
            return type.equals(DataSourceType.XML_FILE) ? DataSourceType.XML_FILE : DataSourceType.LOCAL_DB;
        }

    }


    public static DataSourceChoice chooseDataSource(Stage stage) {
        // Диалог выбора с вариантами "Local DB" и "XML File"
        ChoiceDialog<String> choiceDialog =
                new ChoiceDialog<>("Локальная база данных", List.of("Локальная база данных", "XML Файл"));
        choiceDialog.setTitle("Выбор источника данных");
        choiceDialog.setHeaderText("Выберите источник данных для загрузки (по умолчанию Локальная база данных):");
        choiceDialog.setContentText("Источник:");

        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            String selected = result.get();
            if (selected.equals("Локальная база данных")) {
                // Пользователь выбрал локальную базу данных
                return new DataSourceChoice(DataSourceType.LOCAL_DB, null);
            } else if (selected.equals("XML Файл")) {
                // Пользователь выбрал XML-файл, открываем FileChooser
                File xmlFile = getXmlFile(stage);
                if (xmlFile != null) {
                    return new DataSourceChoice(DataSourceType.XML_FILE, xmlFile);
                }
            }
        }
        // Если пользователь отменил выбор или не выбрал файл, возвращаем null
        return null;
    }

    public static File getXmlFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите XML-файл для загрузки");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Файлы", "*.xml"));
        File xmlFile = fileChooser.showOpenDialog(stage);

        return xmlFile;
    }
}
