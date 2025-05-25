package soat.project.fastfoodsoat.adapter.outbound.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.PaymentJpaEntity;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentJpaEntity, Integer>  {

    @Query("SELECT p FROM PaymentJpaEntity p JOIN p.order o WHERE p.externalReference = :externalReference AND p.deletedAt IS NULL AND o.deletedAt IS NULL")
    Optional<PaymentJpaEntity> findByExternalReference(String externalReference);
}
