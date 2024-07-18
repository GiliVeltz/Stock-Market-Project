package Domain.Entities.Policies;

import Domain.Entities.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PRODUCT_POLICY")
public class ProductPolicy extends Policy<User> {

}
