
package com.example.loginapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.loginapp.entity.LoginActivity;

@Repository
public interface LoginActivityRepository
        extends JpaRepository<LoginActivity, Long> {

    // Find login activities of a particular user,
    // showing the latest activity first.
    List<LoginActivity> findByEmailOrderByActivityTimeDesc(
            String email);

}
