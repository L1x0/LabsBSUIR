package by.astakhau.arkanoid.model.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.util.Pair;
import lombok.Setter;

@Setter
public class Brick {
    int health;
    @JsonProperty("xPos")
    int xPos;
    @JsonProperty("yPos")
    int yPos;

    public Pair<Integer, Integer> getPosition() {
        return new Pair<>(xPos, yPos);
    }
}
