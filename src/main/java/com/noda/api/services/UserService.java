package com.noda.api.services;

import com.noda.api.models.User;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<User> users = new ArrayList<>(List.of(
            new User(1L, "Chagas", "gabriel@gmail.com")
    ));

    public List<User> findAllUsers() {
        return users;
    }

    public void addUser (User user) {
        users.add(user);}
}
