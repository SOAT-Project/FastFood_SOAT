package soat.project.fastfoodsoat.adapter.outbound.jpa.mapper;

import soat.project.fastfoodsoat.adapter.outbound.jpa.entity.ProductJpaEntity;
import soat.project.fastfoodsoat.domain.product.Product;
import soat.project.fastfoodsoat.domain.product.ProductId;
import soat.project.fastfoodsoat.domain.product.productCategory.ProductCategoryId;

public final class ProductMapper {

    public ProductMapper() {
    }

    public static ProductJpaEntity fromDomain(final Product product){
        final Integer id = product.getId() != null ? product.getId().getValue() : null;
        return new ProductJpaEntity(
                id,
                product.getName(),
                product.getDescription(),
                product.getValue(),
                product.getImageURL(),
                product.getProductCategoryId(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getDeletedAt()
        );
    }

    public static Product toDomain(ProductJpaEntity productJpa) {
        return Product.with(
                ProductId.of(productJpa.getId()),
                productJpa.getName(),
                productJpa.getDescription(),
                productJpa.getValue(),
                productJpa.getImageURL(),
                ProductCategoryId.of(productJpa.getProductCategoryId()),
                productJpa.getCreatedAt(),
                productJpa.getUpdatedAt(),
                productJpa.getDeletedAt()
        );
    }
}
