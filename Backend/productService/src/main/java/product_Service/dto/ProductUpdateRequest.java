package product_Service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductUpdateRequest {
    @Size(min = 1, message = "Product name cannot be empty")
    private String productName;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0, message = "Product quantity can't be negative")
    private Long productQuantity;

    @Size(min = 1, message = "Category cannot be empty")
    private String category;

    private Map<String, Object> extraAttributes;
}
