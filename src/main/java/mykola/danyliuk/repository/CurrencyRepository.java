package mykola.danyliuk.repository;

import mykola.danyliuk.model.CurrencyModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<CurrencyModel, String> {
}
