package org.csh.study.nio.chat.myself.dao;

import org.csh.study.nio.chat.myself.model.User;

public class UserDao extends BaseDao<User> {

    /**
     * 判断是否存在用户
     * @param name      用户名
     * @return
     */
    public boolean isExists(String name) {
        for (int id: cacheMap.keySet()) {
            if (cacheMap.get(id).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
