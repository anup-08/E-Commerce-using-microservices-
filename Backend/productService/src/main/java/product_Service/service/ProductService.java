package product_Service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import product_Service.dto.ProductRequest;
import product_Service.dto.ProductResponse;
import product_Service.dto.ProductUpdateRequest;
import product_Service.exception.ProductNotFound;
import product_Service.exception.UserNotFound;
import product_Service.model.Product;
import product_Service.repo.ProductRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo repo;

    @PreAuthorize("hasRole('CUSTOMER')")
    public ProductResponse addProduct(ProductRequest request , String userId , String userName){
        if(userId == null) throw new UserNotFound("Please login to continue..!");

        Product product = Product.builder()
                .productName(request.getProductName())
                .productQuantity(request.getProductQuantity())
                .sku(request.getSku())
                .category(request.getCategory())
                .createdBy(userId)
                .price(request.getPrice())
                .extraAttributes(request.getExtraAttributes())
                .description(request.getDescription())
                .createdByUsername(userName)
                .build();

        return convertToProductResponse(repo.save(product));

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public ProductResponse updateProduct(ProductUpdateRequest updateRequest , String productId , String userId){
        Product product = repo.findById(productId).orElseThrow(()-> new ProductNotFound("Product doesn't exists..!"));

        if( ! product.getCreatedBy().equals(userId)) throw new AccessDeniedException("You don't have permission to update..!");

        if (updateRequest.getProductName() != null) product.setProductName(updateRequest.getProductName());
        if (updateRequest.getDescription() != null) product.setDescription(updateRequest.getDescription());
        if (updateRequest.getPrice() != null) product.setPrice(updateRequest.getPrice());
        if (updateRequest.getProductQuantity() != null) product.setProductQuantity(updateRequest.getProductQuantity());
        if (updateRequest.getCategory() != null) product.setCategory(updateRequest.getCategory());
        if (updateRequest.getExtraAttributes() != null) product.setExtraAttributes(updateRequest.getExtraAttributes());
        if(updateRequest.getExtraAttributes() != null) product.setExtraAttributes(updateRequest.getExtraAttributes());

        return convertToProductResponse(repo.save(product));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public void deleteProduct(String productId , String userId){
        Product product = repo.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product doesn't exist..!"));

        if( !product.getCreatedBy().equals(userId)) throw new AccessDeniedException("You don't have permission to update..!");
        repo.deleteById(productId);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ProductResponse> getUserProductList(String userId){
        List<Product> productList = repo.findByCreatedBy(userId);

        if(productList.isEmpty()){
            List<ProductResponse> responses = null;
        }

        return productList.stream().map(product -> convertToProductResponse(product)).toList();

    }

    public Page<ProductResponse> getAllProductDetails(Pageable pageable){
        Page<Product> products = repo.findAll(pageable);
        return products.map(this::convertToProductResponse); // Use a cleaner method reference
    }

    public ProductResponse getProductById(String productId) {
        Product product = repo.findById(productId)
                .orElseThrow(() -> new ProductNotFound("Product doesn't exist..!"));
        return convertToProductResponse(product);
    }

    private ProductResponse convertToProductResponse(Product product){
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .productQuantity(product.getProductQuantity())
                .sku(product.getSku())
                .price(product.getPrice())
                .createdBy(product.getCreatedBy())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .extraAttributes(product.getExtraAttributes())
                .category(product.getCategory())
                .createdByUsername(product.getCreatedByUsername())
                .build();
    }
}
