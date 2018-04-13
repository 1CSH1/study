package org.csh.study.nio.chat.myself.service;

import org.csh.study.nio.chat.myself.dao.UserDao;
import org.csh.study.nio.chat.myself.model.User;

/**
 * 用户业务处理类
 */
public class UserService {

    private UserDao userDao = new UserDao();

    /**
     * 添加用户
     * @param name      名字
     * @param age       年龄
     * @param gender    性别
     */
    public void addUser(String name, int age, int gender) {
        User user = new User(name, age, gender);
        this.userDao.save(user);
    }

}
