package Domain.Entities.Rules;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

//@MappedSuperclass
@Entity
@Table(name = "[rule]")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractRule<T> implements Rule<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
