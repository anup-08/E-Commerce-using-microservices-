package product_Service.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import product_Service.model.Product;

import java.util.List;

@Repository
public interface ProductRepo extends MongoRepository<Product , String> {
    List<Product> findByCreatedBy(String userId);
}
