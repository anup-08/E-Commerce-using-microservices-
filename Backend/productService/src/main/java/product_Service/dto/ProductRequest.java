package product_Service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name can't be blank")
    private String productName;

    @NotBlank(message = "SKU can't be blank")
    private String sku;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Product quantity can't be negative")
    private Long productQuantity;

    @NotBlank(message = "Please mention the category")
    private String category;

    private Map<String, Object> extraAttributes;
}
