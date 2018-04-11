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
    public void acceptNewConnection() {

        // 创建用户
        User user = new User();
    }

}
