package by.astakhau.arkanoid.model.data.score;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Player {
    private String name;
    private int score;

    @Override
    public Player clone() {
        Player clone = new Player();
        clone.setName(name);
        clone.setScore(score);
        return clone;
    }
}
