package Dtos;

import java.util.Set;

import enums.Permission;

public class ShopManagerDto {
    String username;
    String role;
    Set<Permission> permissions;

    public ShopManagerDto() {
        this.username = null;
        this.role = null;
        this.permissions = null;
    }

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