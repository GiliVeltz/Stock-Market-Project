package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.Product;

@NoRepositoryBean
public interface InterfaceProductRepository extends JpaRepository<Product, Long> {
}
