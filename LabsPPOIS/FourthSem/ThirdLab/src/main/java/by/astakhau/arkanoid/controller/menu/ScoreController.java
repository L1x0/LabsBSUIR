package by.astakhau.arkanoid.controller.menu;

import by.astakhau.arkanoid.Arkanoid;
import by.astakhau.arkanoid.controller.SceneService;
import by.astakhau.arkanoid.controller.SceneUpdater;
import by.astakhau.arkanoid.model.data.score.Player;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ScoreController {
    @FXML
    public TableColumn nameColumn;
    @FXML
    public TableColumn scoreColumn;
    @FXML
    public TableView leaderTable;

    SceneService sceneUpdater = new SceneUpdater();

    @FXML
    public void onGoBack() {
        sceneUpdater.uploadResource("main-menu.fxml");
    }

    public void initialize() {
        leaderTable.setItems(FXCollections.observableArrayList(Arkanoid.getScoreTable().getPlayers()));

        nameColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("score"));
    }
}
