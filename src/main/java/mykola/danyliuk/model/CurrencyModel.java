package mykola.danyliuk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "currencies")
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyModel {
    @Id
    private String code;
    private String name;
}
