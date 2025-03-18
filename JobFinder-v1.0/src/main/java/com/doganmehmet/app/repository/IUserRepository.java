package com.doganmehmet.app.repository;

import com.doganmehmet.app.entity.User;
import com.doganmehmet.app.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByRole(Role role);

    Optional<User> findByEmail(String email);
}
