package soat.project.fastfoodsoat.adapter.outbound.jpa.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.ColumnTransformer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "public_id", nullable = false, columnDefinition = "uuid")
    private UUID publicId;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "status")
    @ColumnTransformer(write="?::order_status")
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<OrderProductJpaEntity> orderProducts;

    @OneToOne(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private PaymentJpaEntity payment;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public OrderJpaEntity() {}

    public OrderJpaEntity(Integer id, UUID publicId, BigDecimal value, Integer orderNumber, String status, List<OrderProductJpaEntity> orderProducts, PaymentJpaEntity payment, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.publicId = publicId;
        this.value = value;
        this.orderNumber = orderNumber;
        this.status = status;
        this.orderProducts = orderProducts;
        this.payment = payment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<OrderProductJpaEntity> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProductJpaEntity> orderProducts) {
        if (orderProducts == null) return;

        this.orderProducts = orderProducts;
    }

    public PaymentJpaEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentJpaEntity paymentJpa) {
        if (paymentJpa == null) return;

        this.payment = paymentJpa;
    }
}
