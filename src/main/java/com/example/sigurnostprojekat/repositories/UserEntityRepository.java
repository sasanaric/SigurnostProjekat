package com.example.sigurnostprojekat.repositories;

import com.example.sigurnostprojekat.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity,Integer> {
    Optional<UserEntity> findById(Integer integer);
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
    List<UserEntity> findAllByIdIsNot(Integer id);
}
