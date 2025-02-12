package by.astakhau.bonuslab.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.LinkedList;

@Data
@Table("trucks")
public class Truck {
    @Id
    Long id;
    private String color;
    private String model;
    private String engine;
    @Column("max_speed")
    private int maxSpeed;
    private int weight;
    @Column("max_weight")
    private int maxWeight;
    @Column("number_of_axes")
    private int numberOfAxes;

    public Truck(Long id, String color, String model, String engine,
                 int maxSpeed, int weight, int maxWeight, int numberOfAxes) {
        this.id = id;
        this.color = color;
        this.model = model;
        this.engine = engine;
        this.maxSpeed = maxSpeed;
        this.weight = weight;
        this.maxWeight = maxWeight;
        this.numberOfAxes = numberOfAxes;
    }

}
