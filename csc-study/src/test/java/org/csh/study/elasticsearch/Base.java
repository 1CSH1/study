package org.csh.study.elasticsearch;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author James
 * @date 2018/6/6
 */
public class Base {

    protected TransportClient client = null;

    @Before
    public void init() throws UnknownHostException, InterruptedException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.28.148.161"), 9300));
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("47.106.115.171"), 9300));
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch").build();
        TransportClient client = new PreBuiltTransportClient(settings);
    }

    @After
    public void close() {
        client.close();
    }
}
