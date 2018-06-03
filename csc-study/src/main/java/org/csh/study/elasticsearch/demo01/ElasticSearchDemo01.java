package org.csh.study.elasticsearch.demo01;

import com.alibaba.fastjson.JSON;
import org.csh.study.elasticsearch.model.User;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.CaretListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ElasticSearchDemo01 {

    private Client client = null;

    @Before
    public void init() throws UnknownHostException, InterruptedException {
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .put("client.transport.sniff", true)
                .build();

        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("47.106.115.171"), 9300));
    }

    @Test
    public void createIndex() {
        User user = new User(1, "James", 33, 1, "13588888888", "james@gmail.com", "America");
        String userJson = JSON.toJSONString(user);
        IndexResponse indexResponse = client.prepareIndex("user", "common")
                .setId("1")
                .setSource(userJson, XContentType.JSON)
                .execute()
                .actionGet();
        System.out.println("response.version() " + indexResponse.getVersion());
    }


    @After
    public void close() {
        client.close();
    }

}
