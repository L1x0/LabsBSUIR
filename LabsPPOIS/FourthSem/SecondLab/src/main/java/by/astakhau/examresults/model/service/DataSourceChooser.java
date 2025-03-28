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

    public enum DataSourceType {
        LOCAL_DB,
        XML_FILE
    }

    @Setter
    public static class DataSourceChoice {
        private DataSourceType type;
        @Getter private File file;

        public DataSourceChoice(DataSourceType type, File file) {
            this.type = type;
            this.file = file;
        }

        public DataSourceType getType() {
            return type.equals(DataSourceType.XML_FILE) ? DataSourceType.XML_FILE : DataSourceType.LOCAL_DB;
        }

    }


    public static DataSourceChoice chooseDataSource(Stage stage) {
        ChoiceDialog<String> choiceDialog =
                new ChoiceDialog<>("Локальная база данных", List.of("Локальная база данных", "XML Файл"));
        choiceDialog.setTitle("Выбор источника данных");
        choiceDialog.setHeaderText("Выберите источник данных для загрузки (по умолчанию Локальная база данных):");
        choiceDialog.setContentText("Источник:");

        Optional<String> result = choiceDialog.showAndWait();
        if (result.isPresent()) {
            String selected = result.get();
            if (selected.equals("Локальная база данных")) {
                return new DataSourceChoice(DataSourceType.LOCAL_DB, null);
            } else if (selected.equals("XML Файл")) {
                File xmlFile = getXmlFile(stage);
                if (xmlFile != null) {
                    return new DataSourceChoice(DataSourceType.XML_FILE, xmlFile);
                }
            }
        }
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
