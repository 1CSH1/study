package org.csh.study.elasticsearch.demo01;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearchDemo01 {

    private TransportClient client = null;

    @Before
    public void init() throws UnknownHostException, InterruptedException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.94.30.14"), 9300));
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch").build();
        TransportClient client = new PreBuiltTransportClient(settings);
    }

    @Test
    public void indexAPIString() throws IOException {
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";

        IndexResponse response = client.prepareIndex("twitter", "tweet")
                .setSource(json, XContentType.JSON)
                .get();
        printResponse(response);
    }

    @Test
    public void indexAPIObject() throws IOException {
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
                .setSource(jsonBuilder().startObject()
                        .field("user", "james")
                        .field("postDate", new Date())
                        .field("message", "hello elasticsearch")
                        .endObject()
                )
                .get();
        printResponse(response);
    }

    private void printResponse(DocWriteResponse response) {
        System.out.println("id: " + response.getId() + "\n" +
                " type:  " + response.getType() + "\n" +
                " index: " + response.getIndex() + "\n" +
                " version: " + response.getVersion() + "\n" +
                " status: " + response.status());
    }

    @Test
    public void getAPI() {
        GetResponse response = client.prepareGet("twitter", "tweet", "1").get();
        // or client.prepareGet("twitter", "tweet", "1").setOperationThreaded(false).get();
    }

    /**
     * 删除
     */
    @Test
    public void deleteAPI() {
        DeleteResponse response = client.prepareDelete("twitter", "tweet", "1").get();
        printResponse(response);
    }

    /**
     * 通过查询语句删除
     */
    @Test
    public void deleteByQueryAPI01() {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("gender", "male"))
                .source("persons")
                .get();
        long deleted = response.getDeleted();
    }

    /**
     * 通过查询语句删除（异步）
     */
    @Test
    public void deleteByQueryAPI02() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("gender", "male"))
                .source("persons")
                .execute(new ActionListener<BulkByScrollResponse>() {
                    @Override
                    public void onResponse(BulkByScrollResponse response) {
                        long deleted = response.getDeleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 更新
     */
    @Test
    public void updateAPI() throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("twitter")
                .type("tweet")
                .id("1")
                .doc(jsonBuilder()
                        .startObject()
                            .field("gender", "male")
                        .endObject());
        client.update(updateRequest).get();
    }


    @Test
    public void updateByPrepareUpdate() throws IOException {
        client.prepareUpdate("twitter", "tweet", "1")
//                .setScript(new Script("ctx._source.gender = \"male\"", ScriptService.ScriptType.INLINE, null, null))
                .get();

        // or
        client.prepareUpdate("ttl", "doc", "1")
                .setDoc(jsonBuilder()
                        .startObject()
                            .field("gender", "male")
                        .endObject())
                .get();
    }

    @After
    public void close() {
        client.close();
    }

}
