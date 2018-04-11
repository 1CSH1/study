package org.csh.study.nio.chat.myself.controller;

import org.csh.study.nio.chat.myself.service.MessageService;
import org.csh.study.nio.chat.myself.service.UserService;

public class ClientController {

    private UserService userService = new UserService();
    private MessageService messageService = new MessageService();

}
