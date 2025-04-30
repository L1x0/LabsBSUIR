package by.astakhau.arkanoid.model.data.score;

import by.astakhau.arkanoid.Arkanoid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public class ScoreTable {
    @JsonIgnore
    private ScoreTableManager manager;
    private List<Player> players;

    public ScoreTable(List<Player> players) {
        this.players = players;
    }

    public ScoreTable() {
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        if (player.getName().isBlank())
            player.setName(generateName());

        Optional<Player> existing = players.stream()
                .filter(p -> p.getName().equals(player.getName()))
                .findFirst();

        if (existing.isPresent()) {
            if (existing.get().getScore() < player.getScore()) {
                players.removeIf(p -> p.getName().equals(player.getName()));
                players.add(player);
            }
        } else {
            players.add(player);
        }

        players.sort(Comparator.comparingInt(Player::getScore).reversed());
        saveChanges();
    }

    private String generateName() {
        Arkanoid.getPlayer().setName("Unknown_Player" + new Random().nextInt(900) + 100);
        return Arkanoid.getPlayer().getName();
    }

    private void saveChanges() {
        manager = new ScoreTableManager();
        manager.writeScoreTable(this);
    }
}
