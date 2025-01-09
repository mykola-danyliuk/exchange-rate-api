package mykola.danyliuk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "usd_rates")
@AllArgsConstructor
@NoArgsConstructor
public class RateModel {
    @Id
    private String target;
    private BigDecimal rate;
    private Long timestamp;
}
