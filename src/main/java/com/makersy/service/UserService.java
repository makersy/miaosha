package com.makersy.service;

import com.makersy.dao.UserDao;
import com.makersy.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by makersy on 2019
 */

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getById(int userId) {
        return userDao.getById(userId);
    }

    @Transactional
    public boolean tx() {
        User u1 = new User();
        u1.setId(3);
        u1.setName("John");
        userDao.insert(u1);

        User u2 = new User();
        u1.setId(1);
        u1.setName("John");
        userDao.insert(u1);

        return true;
    }
}
