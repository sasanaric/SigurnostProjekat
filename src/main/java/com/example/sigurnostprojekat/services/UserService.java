package com.example.sigurnostprojekat.services;

import com.example.sigurnostprojekat.base.CrudService;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.User;

import java.util.List;

public interface UserService extends CrudService<Integer> {
    Boolean isUsernameTaken(String username);
    User getCurrentUser();
    Integer getCurrentUserId();
    List<User> getAllOtherUsers();
    String getPrivateKeyById(Integer id) throws NotFoundException;
    String getPublicKeyById(Integer id) throws NotFoundException;
}
