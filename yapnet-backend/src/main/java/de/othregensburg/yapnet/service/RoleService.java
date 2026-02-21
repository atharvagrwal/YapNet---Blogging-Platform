package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.model.Role;
import de.othregensburg.yapnet.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.io.StringWriter;
import java.io.PrintWriter;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public void initRoles() {
        try {
            System.out.println("Initializing roles...");
            
            // Check if USER role exists
            Role userRole = findByRoleName("USER");
            if (userRole == null) {
                System.out.println("Creating USER role");
                userRole = new Role("USER");
                Role savedRole = roleRepository.save(userRole);
                if (savedRole == null) {
                    throw new RuntimeException("Failed to save USER role");
                }
                System.out.println("Saved USER role details:");
                System.out.println("ID: " + savedRole.getId());
                System.out.println("Name: " + savedRole.getName());
                System.out.println("USER role created successfully");
            } else {
                System.out.println("USER role already exists");
            }
            
            // Check if ADMIN role exists
            Role adminRole = findByRoleName("ADMIN");
            if (adminRole == null) {
                System.out.println("Creating ADMIN role");
                adminRole = new Role("ADMIN");
                Role savedAdminRole = roleRepository.save(adminRole);
                if (savedAdminRole == null) {
                    throw new RuntimeException("Failed to save ADMIN role");
                }
                System.out.println("Saved ADMIN role details:");
                System.out.println("ID: " + savedAdminRole.getId());
                System.out.println("Name: " + savedAdminRole.getName());
                System.out.println("ADMIN role created successfully");
            } else {
                System.out.println("ADMIN role already exists");
            }
            
            System.out.println("Role initialization completed successfully");
        } catch (Exception e) {
            System.err.println("Error initializing roles: " + e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.err.println("Stack trace: " + sw.toString());
            throw e;
        }
    }

        public Role findByRoleName(String roleName) {
        try {
            System.out.println("Looking for role: " + roleName);
            
            Optional<Role> optionalRole = roleRepository.findByName(roleName);
            if (!optionalRole.isPresent()) {
                System.err.println("Role not found in database: " + roleName);
                return null;
            }
            
            Role role = optionalRole.get();
            System.out.println("Found role: " + role.getName());
            return role;
        } catch (Exception e) {
            System.err.println("Error finding role: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to find role: " + roleName, e);
        }
    }
}
