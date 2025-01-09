package mykola.danyliuk.repository;

import mykola.danyliuk.model.RateModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<RateModel, String> {
}
