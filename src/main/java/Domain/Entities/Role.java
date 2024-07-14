package Domain.Entities;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.Entities.enums.Permission;
import Exceptions.*;
import jakarta.persistence.*;

/** 
 * This class represents the role of a user in a specific shop.
 */
@Entity
public class Role {
    /** 
     * The username that appointed this member in the shop.
     * @Constraint Must be an owner in the shop.
     * @Special_Case If the role is the founder of the shop, this field is null.
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer roleId;

    @Column(name = "appointedBy")
    private final String appointedBy;
    
    @Transient
    private final Integer shopId; // The store id that this role is connected to.

    @Column(name = "username")
    private final String username; // The username of the user in the system.

    /**
     * The permissions of this role in the shop.
     * @Constraint has to be at least one permission.
     */
    @ElementCollection
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    private Set<Permission> permissions;
    
    @ElementCollection
    @CollectionTable(name = "role_appointments", joinColumns = @JoinColumn(name = "role_id"))
    //@Column(name = "appointment")
    private Set<String> appointments; // The appointments of this user in a specific shop.

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;
    
    @Transient
    private static final Logger logger = Logger.getLogger(Role.class.getName());

    // Default constructor for hibernate.
    public Role(){
        username = null;
        shopId = -1;
        appointedBy = null;
        permissions = new HashSet<Permission>();
        appointments = new HashSet<String>();
    }

    /**
     * Basic constructor with permission set.
     * @param username the username in the system that the role belongs to.
     * @param shop the shop that the role belongs to.
     * @param appointedBy the username that appointed this role.
     * @param permissions the permission given to this role.
     * @throws RoleException 
     */
    public Role(String username, Shop shop, String appointedBy, Set<Permission> permissions) throws StockMarketException{
        logger.log(Level.INFO, "Role - constructor: Creating a new role in shop with id "+shop.getShopId()+" for username "+username+". Permissions are: "+permissions+". The role was appointed by: "+appointedBy);
        if(username == null){
            logger.log(Level.SEVERE, "Role - constructor: Error while creating a new role because username is null.");
            throw new RoleException("Can't create a role with null username.");
        }
        if(permissions == null){
            logger.log(Level.SEVERE, "Role - constructor: Error while creating a new role because permissions are null.");
            throw new RoleException("Can't create a role with null permissions.");
        }

        if(permissions.isEmpty()){
            logger.log(Level.SEVERE, "Role - constructor: Error while creating a new role because 0 permissions.");
            throw new RoleException("Can't create a role without permissions.");
        }

        if(permissions.contains(Permission.FOUNDER) && permissions.size() > 1){
            logger.log(Level.SEVERE, "Role - constructor: Error while creating a new role because can't create a role with founder permission with other permissions too.");
            throw new RoleException("Founder doesn't need other permissions.");
        }

        if(permissions.contains(Permission.FOUNDER) && appointedBy != null){
            logger.log(Level.SEVERE, "Role - constructor: Error while creating a new role because founder doesn't have appointer.");
            throw new RoleException("Founder is not appointed by anyone.");
        }

        if(permissions.contains(Permission.OWNER) && permissions.size() > 1){
            logger.log(Level.SEVERE, "Role - constructor: Error while creating a new role because can't create a role with owner permission with other permissions too.");
            throw new RoleException("Owner doesn't need other permissions.");
        }
        
        this.username = username;
        this.shopId = shop.getShopId();
        this.appointedBy = appointedBy;
        appointments = new HashSet<String>();
        this.permissions = permissions;
        this.shop = shop;

        logger.log(Level.INFO, "Role - constructor: Successfuly created a new role in shop with id "+shop.getShopId()+" for username "+username+". Permissions are: "+permissions+". The role was appointed by: "+appointedBy);
    }

    public boolean isFounder(){
        return permissions.contains(Permission.FOUNDER) && appointedBy == null;
    }

    public boolean isOwner(){
        return permissions.contains(Permission.OWNER);
    }

    
    /**
     * Check if this role has a specific permission.
     * @param p permission to check.
     * @return boolean that represents if the role has this permission.
     */
    public boolean hasPermission(Permission p){
        if(p == null){
            return false;
        }
        return permissions.contains(p);
    }

    /**
     * Check if this role has at least one permission of the given set.
     * @param permissions permission set.
     * @return boolean that represents if the role has at least one permission from the set.
     */
    public boolean hasAtLeastOnePermission(Set<Permission> permissions){
        if(permissions == null || permissions.isEmpty()){
            return false;
        }
        permissions.retainAll(permissions);
        return !permissions.isEmpty();
    }

    /**
     * Check if this role has at all permissions of the given set.
     * @param permissions permission set.
     * @return boolean that represents if the role has all permissions from the set.
     */
    public boolean hasAllPermissions(Set<Permission> permissions){
        if(permissions == null || permissions.isEmpty()){
            return false;
        }
        return !permissions.retainAll(permissions);
    }

    /**
     * Modify permissions to this role.
     * @param username the user that is modifying the new permissions.
     * @param permission the new permissions.
     * @implNote If the role already has this permission its ok.
     * @Constraint username must be the one that appointed this role.
     * @throws RoleException 
     */
    public void modifyPermissions(String username, Set<Permission> permissionsModify) throws StockMarketException {
        logger.log(Level.INFO, "Role - addPermissions: "+username+" trying to add permissions "+permissionsModify+" to user "+username+" in the shop with id "+shopId);
        if(isFounder() || isOwner()){
            logger.log(Level.SEVERE, "Role - addPermissions: Error while adding permissions to owner of founder. Can't add permissions for them.");
            throw new RoleException("Username is a founder of owner. No need to manage permissions.");
        }
        if(!username.equals(appointedBy)){
            logger.log(Level.SEVERE, "Role - addPermissions: Error while adding permissions to "+username+" because "+username+" is not his appointer.");
            throw new RoleException("Only the user that appointed this user can modify the permissions.");
        }
        if(permissionsModify.contains(Permission.OWNER) || permissionsModify.contains(Permission.FOUNDER)){
            logger.log(Level.SEVERE, "Role - addPermissions: Error while adding permissions to "+username+" because we can't add founder of owner permissions.");
            throw new RoleException("Cannot add owner or founder permissions.");
        }
        //Delete all permissions and set the new ones.
        permissions.clear();
        permissions.addAll(permissionsModify);
    }


    // /**
    //  * Remove a permission from this role.
    //  * @param username the user that is removing the permissions.
    //  * @param permission the permissions to remove.
    //  * @implNote if role doesn't have permission its ok.
    //  * @throws RoleException 
    //  */
    // public void deletePermissions(String username, Set<Permission> permissionsRoleException {
    //     logger.log(Level.INFO, "Role - deletePermissions: "+username+" trying to delete permissions "+permissions+" to user "+_username+" in the shop with id "+_storeId);
    //     if(isFounder() || isOwner()){
    //         logger.log(Level.SEVERE, "Role - deletePermissions: Error while deleting permissions from owner of founder. Can't delete permissions for them.");
    //         throw new RoleException("Username is a founder of owner. No need to manage permissions.");
    //     }
    //     if(!username.equals(_appointedBy)){
    //         logger.log(Level.SEVERE, "Role - deletePermissions: Error while deleting permissions from "+_username+" because "+username+" is not his appointer.");
    //         throw new RoleException("Only the role that appointed this user can change permission.");
    //     }
    //     _permissions.removeAll(permissions);
    // }


    public void addAppointment(String username_new) throws StockMarketException{
        logger.log(Level.INFO, "Role - addAppointment: "+username+" trying to appoint user "+username_new+" in the shop with id "+shopId);
        if(appointments.contains(username_new)){
            logger.log(Level.SEVERE, "Role - addAppointment: Error while appointing "+username_new+" because he was already appointed by "+username);
            throw new RoleException("Username "+username_new+" is already appointed.");
        }
        if(username.equals(this.username)){
            logger.log(Level.SEVERE, "Role - addAppointment: Error while appointing "+username_new+" because he is trying to appoint himself.");
            throw new RoleException("Username "+username_new+" cannot appoint itself.");
        }
        if(username_new.equals(appointedBy)){
            logger.log(Level.SEVERE, "Role - addAppointment: Error while appointing "+username_new+" because he is trying to appoint his appointer "+appointedBy);
            throw new RoleException("Username "+username_new+" cannot appoint the user that appointed him.");
        }
        appointments.add(username_new);
        logger.log(Level.INFO, "Role - addAppointment: "+username+" successfuly appointed user "+username_new+" in the shop with id "+shopId);
    }

    public void deleteAppointment(String username_toDelete) throws StockMarketException{
        logger.log(Level.INFO, "Role - deleteAppointment: "+username+" trying delete user "+username_toDelete+" that was appointed by him in the shop with id "+shopId);
        if(!appointments.contains(username_toDelete)){
            logger.log(Level.SEVERE, "Role - deleteAppointment: Error while removing appointed "+username_toDelete+" because he is not appointed by "+username);
            throw new RoleException("Username "+username+" is not appointed.");
        }
        if(username.equals(username_toDelete)){
            logger.log(Level.SEVERE, "Role - deleteAppointment: Error while removing appointed "+username_toDelete+" because he trying to remove himself.");
            throw new RoleException("Username "+username_toDelete+" cannot delete himself from his appointments.");
        }
        if(username_toDelete.equals(appointedBy)){
            logger.log(Level.SEVERE, "Role - deleteAppointment: Error while removing appointed "+username_toDelete+" because "+username+" didn't appoint him.");
            throw new RoleException("Username "+username_toDelete+" cannot delete appointment of the user that appointed him.");
        }
        appointments.remove(username_toDelete);
        logger.log(Level.INFO, "Role - deleteAppointment: "+username+" successfuly deleted the user "+username_toDelete+" that was appointed by him in the shop with id "+shopId);
    }
    
    // GETTERS

    /**
     * Gets the username that appointed this member in the shop.
     * @return the appointed username.
     */
    public String getAppointedBy() {
        return appointedBy;
    }

    /**
     * Gets the store id that this role is connected to.
     * @return the store id.
     */
    public int getShopId() {
        return shopId;
    }

    /**
     * Gets the username of the user in the system.
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the permissions of this role in the shop.
     * @return the set of permissions.
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Gets the appointments of this role in the shop.
     * @return the set of appointed people by this role.
     */
    public Set<String> getAppointments() {
        return appointments;
    }

    // for memory repositories
    public void setId(int size) {
        roleId = size;
    }

}
