package org.csh.study.nio.chat.myself.dao;

import org.csh.study.nio.chat.myself.model.BaseModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO 基础操作
 */
public class BaseDao<T extends BaseModel> {

    /**
     * 保存数据
     */
    private ConcurrentHashMap<Integer, T> cacheMap = new ConcurrentHashMap<Integer, T>();
    /**
     * 计数器
     */
    private AtomicInteger count = new AtomicInteger(0);

    /**
     * 新增对象
     */
    public T save(T t) {
        t.setId(count.incrementAndGet());
        return this.cacheMap.put(t.getId(), t);
    }

    /**
     * 删除对象
     */
    public T delete(int id) {
        return this.cacheMap.remove(id);
    }

    /**
     * 查询
     */
    public T query(int id) {
        return this.cacheMap.get(id);
    }

    /**
     * 当前总数
     */
    public int totalCount() {
        return this.cacheMap.size();
    }

}
