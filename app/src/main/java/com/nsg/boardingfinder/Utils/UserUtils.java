package com.nsg.boardingfinder.Utils;

import com.nsg.boardingfinder.model.User;

import java.util.List;

public class UserUtils {

    public User createUser (List<User> users)  {

        User user = new User();

        for (User currentUser : users) {

            user.setId(currentUser.getId());
            user.setName(currentUser.getName());
            user.setAddress(currentUser.getAddress());
            user.setPhone(currentUser.getPhone());
            user.setEmail(currentUser.getEmail());
            user.setOccupation(currentUser.getOccupation());
            user.setPassword(currentUser.getPassword());
        }

        return user;
    }
}
