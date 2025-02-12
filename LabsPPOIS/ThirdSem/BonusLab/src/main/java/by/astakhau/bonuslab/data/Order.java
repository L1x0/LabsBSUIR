package by.astakhau.bonuslab.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("orders")
public class Order {
    @Id
    private long id;
    @Column("client_id")
    private long client_id;
    @Column("product")
    private String product;
    @Column("destination_country")
    private String destination_country;

    public Order(long id, long client_id,
                 String product, String destination_country) {
        this.id = id;
        this.client_id = client_id;
        this.product = product;
        this.destination_country = destination_country;
    }
}