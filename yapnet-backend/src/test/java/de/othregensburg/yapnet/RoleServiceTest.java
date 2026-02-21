package de.othregensburg.yapnet.service;

import de.othregensburg.yapnet.model.Role;
import de.othregensburg.yapnet.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock private RoleRepository roleRepository;
    @InjectMocks private RoleService roleService;

    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        userRole = new Role("USER");
        adminRole = new Role("ADMIN");
    }

    @Test
    void testInitRolesCreatesRolesIfMissing() {
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(userRole);
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        // Only stub save for ADMIN if actually called
        // when(roleRepository.save(any(Role.class))).thenReturn(adminRole);

        assertDoesNotThrow(() -> roleService.initRoles());
        verify(roleRepository, atLeastOnce()).save(any(Role.class));
    }

    @Test
    void testInitRolesRolesAlreadyExist() {
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

        assertDoesNotThrow(() -> roleService.initRoles());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testFindByRoleNameFound() {
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        Role result = roleService.findByRoleName("USER");
        assertNotNull(result);
        assertEquals("USER", result.getName());
    }

    @Test
    void testFindByRoleNameNotFound() {
        when(roleRepository.findByName("GUEST")).thenReturn(Optional.empty());
        Role result = roleService.findByRoleName("GUEST");
        assertNull(result);
    }

    @Test
    void testFindByRoleNameThrowsException() {
        when(roleRepository.findByName("USER")).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> roleService.findByRoleName("USER"));
    }
} 