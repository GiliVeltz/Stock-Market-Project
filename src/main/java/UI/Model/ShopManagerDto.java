package UI.Model;

import java.util.Set;

public class ShopManagerDto {
    private String username;
    private String role;
    private Set<Permission> permissions;

    public ShopManagerDto(String username, String role, Set<Permission> permissions) {
        this.username = username;
        this.role = role;
        this.permissions = permissions;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
