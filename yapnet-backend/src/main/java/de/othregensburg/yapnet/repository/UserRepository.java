package de.othregensburg.yapnet.repository;

import de.othregensburg.yapnet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
    
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    List<User> findByEmailContainingIgnoreCase(String email);
    
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
    
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    
    List<User> findByEnabled(Boolean enabled);
    
    List<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(String username, String email);
    
    List<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndFullNameContainingIgnoreCase(String username, String email, String fullName);
    
    List<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndFullNameContainingIgnoreCaseAndEnabled(String username, String email, String fullName, Boolean enabled);
    
    // @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    // List<User> searchUsers(String searchTerm);
    
    // @Query("SELECT u FROM User u WHERE u.id <> :userId AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    // List<User> searchUsersExcludingSelf(UUID userId, String searchTerm);

    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}
