package Dtos;

import java.util.Set;

import enums.Permission;

public class ShopManagerDto {
    private String username;
    private String role;
    private Set<Permission> permissions;

    public ShopManagerDto(String username, String role, Set<Permission> permissions) {
        this.username = username;
        this.role = role;
        this.permissions = permissions;
    }
}