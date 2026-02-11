package com.chandan.ecommerce.service;

import com.chandan.ecommerce.modal.Product;
import com.chandan.ecommerce.modal.Seller;
import com.chandan.ecommerce.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public Product createProduct(CreateProductRequest req, Seller seller);
    public void deleteProduct(Long productId);
    public Product updateProduct(Long productId, Product product);
    Product findProductById(Long productId);
    List<Product> searchProducts();
    public Page<Product> getAllProducts(
            String category,
            String brand,
            String colors,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber
    );
    List<Product> getProductBySellerId(Long sellerId);
}
