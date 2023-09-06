package com.example.sigurnostprojekat.services.implementation;

import com.example.sigurnostprojekat.base.CrudJpaService;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.User;
import com.example.sigurnostprojekat.models.entity.UserEntity;
import com.example.sigurnostprojekat.repositories.UserEntityRepository;
import com.example.sigurnostprojekat.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation extends CrudJpaService<UserEntity,Integer> implements UserService {
    private final ModelMapper modelMapper;
    private final UserEntityRepository repository;
    public UserServiceImplementation(UserEntityRepository userEntityRepository, ModelMapper modelMapper){
        super(userEntityRepository,modelMapper, UserEntity.class);
        this.repository = userEntityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userEntity = repository.findByUsername(username);

        if(userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }

        return modelMapper.map(userEntity, User.class);
    }
    @Override
    public Boolean isUsernameTaken(String username) {
        return repository.existsByUsername(username);
    }
    @Override
    public Integer getCurrentUserId() {
        try {
            User user = getCurrentUser();
            return user.getId();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public List<User> getAllOtherUsers() {
        Integer currentUserId = getCurrentUserId();
        return repository
                .findAllByIdIsNot(currentUserId)
                .stream()
                .map(u -> modelMapper.map(u,User.class))
                .toList();
    }

    @Override
    public String getPrivateKeyById(Integer id) throws NotFoundException {
        return repository
                .findById(id)
                .map(UserEntity::getPrivateKey)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public String getPublicKeyById(Integer id) throws NotFoundException {
        return repository
                .findById(id)
                .map(UserEntity::getPublicKey)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
