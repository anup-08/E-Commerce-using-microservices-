package product_Service.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import product_Service.dto.ProductRequest;
import product_Service.dto.ProductResponse;
import product_Service.dto.ProductUpdateRequest;
import product_Service.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class Controller {

    private final ProductService service;

    @PostMapping("/add-product")
    public ResponseEntity<ProductResponse> addProduct(@AuthenticationPrincipal Jwt jwt,
                                                      @Valid @RequestBody ProductRequest request  ){
        String userId = jwt.getSubject();
        String userName = jwt.getClaimAsString("preferred_username");

        return new ResponseEntity<>(service.addProduct(request , userId , userName) ,HttpStatus.CREATED);

    }

    @PutMapping("/update-product/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal Jwt jwt ,
                                                         @PathVariable String productId ,
                                                         @Valid @RequestBody ProductUpdateRequest updateRequest){
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.updateProduct(updateRequest , productId ,userId));
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<Page<ProductResponse>> getAllProductDetails(Pageable pageable){
        return ResponseEntity.ok(service.getAllProductDetails(pageable));
    }

    @GetMapping("/get-products")
    public ResponseEntity<List<ProductResponse>> getProductDetails(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.getUserProductList(userId));
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductResponse>> getALLProductDetails(@RequestBody List<String> productIds){
        return ResponseEntity.ok(service.getAllProductById(productIds));
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId){
        return ResponseEntity.ok(service.getProductById(productId));
    }


    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal Jwt jwt ,
                                              @PathVariable String productId){
        String userId = jwt.getSubject();
        service.deleteProduct(productId,userId);
        return ResponseEntity.noContent().build();
    }

}
