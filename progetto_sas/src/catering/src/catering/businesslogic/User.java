package catering.businesslogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User {
    public static enum Role {Cuoco, Chef, Organizzatore, Servizio};

    private String name;
    private Set<Role> roles;

    public User(String name) {
        this.name = name;
        this.roles = new HashSet<Role>();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean isChef() {
        return this.roles.contains(Role.Chef);
    }

    public String toString() {
        return this.name;
    }
}
