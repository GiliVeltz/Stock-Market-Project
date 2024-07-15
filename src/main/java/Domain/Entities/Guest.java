package Domain.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;

@Entity
@Table(name = "guest_ids")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guest_id", nullable = false, unique = true)
    private String guestId;

    @Column(name = "created_at", nullable = false)
    //@Temporal(TemporalType.DATE)
    private LocalDateTime createdAt;

    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "shopping_cart_id")
    @Transient
    private ShoppingCart shoppingCart;
    
    public Guest() {}

    public Guest(String guestId) {
        this.guestId = guestId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Object getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
