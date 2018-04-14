package org.csh.study.nio.chat.myself.controller;

import org.csh.study.nio.chat.myself.model.User;
import org.csh.study.nio.chat.myself.service.MessageService;
import org.csh.study.nio.chat.myself.service.UserService;

/**
 * 服务端控制类
 */
public class ServerController {

    private UserService userService = new UserService();
    private MessageService messageService = new MessageService();

    /**
     * 服务端接收新的连接
     */
    public void acceptNewConnection(String name, int age, int gender) {
        this.userService.addUser(name, age, gender);
    }

    /**
     * 添加用户
     * @param name      名称
     * @param age       年龄
     * @param gender    性别
     */
    public void addUser(String name, int age, int gender) {
        this.userService.addUser(name,age, gender);
    }

    /**
     * 判断是否存在用户
     * @param name      用户名
     * @return
     */
    public boolean isExists(String name) {
        return this.userService.isExists(name);
    }

}
