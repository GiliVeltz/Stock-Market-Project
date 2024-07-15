package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.Role;

@NoRepositoryBean
public interface InterfaceRoleRepository extends JpaRepository<Role, Integer>{
}
