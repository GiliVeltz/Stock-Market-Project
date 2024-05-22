package Domain;
import java.util.HashSet;
import java.util.Set;

import Exceptions.*;

/** 
 * This class represents the role of a user in a specific shop.
 */
//TODO: ADD LOGS
//TODO: ADD TESTS
//TODO: MODIFY MODEL CLASS IF NEEDED.
public class Role {
    /** 
     * The username that appointed this member in the shop.
     * @Constraint Must be an owner in the shop.
     * @Special_Case If the role is the founder of the shop, this field is null.
     */
    private final String _appointedBy;
    
    /**
     *  The store id that this role is connected to.
     */ 
    private final int _storeId;

    /**
     * The username of the user in the system.
     */
    private final String _username;

    /**
     * The permissions of this role in the shop.
     * @Constraint has to be at least one permission.
     */
    private Set<Permission> _permissions;

    /**
     * The appointments of this user in a specific shop.
     */
    private Set<String> _appointments;

    /**
     * Basic constructor with permission set.
     * @param username the username in the system that the role belongs to.
     * @param storeId the shop id that the role belongs to.
     * @param appointedBy the username that appointed this role.
     * @param permissions the permission given to this role.
     * @throws RoleException 
     */
    public Role(String username, int storeId, String appointedBy, Set<Permission> permissions) throws RoleException{
        if(username == null){
            throw new RoleException("Can't create a role with null username.");
        }
        if(permissions == null){
            throw new RoleException("Can't create a role with null permissions.");
        }

        if(permissions.isEmpty()){
            throw new RoleException("Can't create a role without permissions.");
        }

        if(permissions.contains(Permission.FOUNDER) && permissions.size() > 1){
            throw new RoleException("Founder doesn't need other permissions.");
        }

        if(permissions.contains(Permission.FOUNDER) && appointedBy != null){
            throw new RoleException("Founder is not appointed by anyone.");
        }

        if(permissions.contains(Permission.OWNER) && permissions.size() > 1){
            throw new RoleException("Owner doesn't need other permissions.");
        }
        
        _username = username;
        _storeId = storeId;
        _appointedBy = appointedBy;
        _appointments = new HashSet<String>();
        _permissions = permissions;
    }

    public boolean isFounder(){
        return _permissions.contains(Permission.FOUNDER) && _appointedBy == null;
    }

    public boolean isOwner(){
        return _permissions.contains(Permission.OWNER);
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
        return _permissions.contains(p);
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
        permissions.retainAll(_permissions);
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
        return !permissions.retainAll(_permissions);
    }

    /**
     * Add permissions to this role.
     * @param username the user that is giving the new permissions.
     * @param permission the permissions to add.
     * @implNote If the role already has this permission its ok.
     * @Constraint username must be the one that appointed this role.
     * @throws RoleException 
     */
    public void addPermissions(String username, Set<Permission> permissions) throws RoleException {
        if(isFounder() || isOwner()){
            throw new RoleException("Username is a founder of owner. No need to manage permissions.");
        }
        if(!username.equals(_appointedBy)){
            throw new RoleException("Only the role that appointed this user can change permission.");
        }
        if(permissions.contains(Permission.OWNER) || permissions.contains(Permission.FOUNDER)){
            throw new RoleException("Cannot add owner or founder permissions.");
        }
        _permissions.addAll(permissions);
    }


    /**
     * Remove a permission from this role.
     *
     * @param permission the permissions to remove.
     * @implNote if role doesn't have permission its ok.
     * @throws RoleException 
     */
    public void deletePermissions(String username, Set<Permission> permissions) throws RoleException {
        if(isFounder() || isOwner()){
            throw new RoleException("Username is a founder of owner. No need to manage permissions.");
        }
        if(!username.equals(_appointedBy)){
            throw new RoleException("Only the role that appointed this user can change permission.");
        }
        _permissions.removeAll(permissions);
    }


    public void addAppointment(String username) throws RoleException{
        //TODO: Instead of exception maybe just return false?
        if(_appointments.contains(username)){
            throw new RoleException("Username "+username+" is already appointed.");
        }
        if(username.equals(_username)){
            throw new RoleException("Username "+username+" cannot appoint itself.");
        }
        if(username.equals(_appointedBy)){
            throw new RoleException("Username "+username+" cannot appoint the user that appointed him.");
        }
        _appointments.add(username);
    }

    public void deleteAppointment(String username) throws RoleException{
        if(!_appointments.contains(username)){
            throw new RoleException("Username "+username+" is not appointed.");
        }
        if(username.equals(_username)){
            throw new RoleException("Username "+username+" cannot delete himself from his appointments.");
        }
        if(username.equals(_appointedBy)){
            throw new RoleException("Username "+username+" cannot delete appointment of the user that appointed him.");
        }
        _appointments.remove(username);
    }
    
    // GETTERS

    /**
     * Gets the username that appointed this member in the shop.
     * @return the appointed username.
     */
    public String getAppointedBy() {
        return _appointedBy;
    }

    /**
     * Gets the store id that this role is connected to.
     * @return the store id.
     */
    public int getStoreId() {
        return _storeId;
    }

    /**
     * Gets the username of the user in the system.
     * @return the username.
     */
    public String getUsername() {
        return _username;
    }

    /**
     * Gets the permissions of this role in the shop.
     * @return the list of permissions.
     */
    public Set<Permission> getPermissions() {
        return _permissions;
    }

    /**
     * Gets the permissions of this role in the shop.
     * @return the list of permissions.
     */
    public Set<String> getAppointments() {
        return _appointments;
    }

}
