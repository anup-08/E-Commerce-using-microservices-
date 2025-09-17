package product_Service.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ProductResponse> addProduct( @RequestHeader("user-id") String userId ,
                                                      @RequestHeader("user-username") String userName ,
                                                      @Valid @RequestBody ProductRequest request  ){

        return new ResponseEntity<>(service.addProduct(request , userId , userName) ,HttpStatus.CREATED);

    }

    @PutMapping("/update-product/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String productId ,
                                                         @RequestHeader("user-id") String userId,
                                                         @Valid @RequestBody ProductUpdateRequest updateRequest){
        return ResponseEntity.ok(service.updateProduct(updateRequest , productId ,userId));
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<List<ProductResponse>> getAllProductDetails(){
        return ResponseEntity.ok(service.getAllProductDetails());
    }

    @GetMapping("/get-products")
    public ResponseEntity<List<ProductResponse>> getProductDetails(@RequestHeader("user-id") String userId){
        return ResponseEntity.ok(service.getUserProductList(userId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId){
        return ResponseEntity.ok(service.getProductById(productId));
    }


    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader("user-id") String userId ,
                                              @PathVariable String productId){
        service.deleteProduct(productId,userId);
        return ResponseEntity.noContent().build();
    }

}
