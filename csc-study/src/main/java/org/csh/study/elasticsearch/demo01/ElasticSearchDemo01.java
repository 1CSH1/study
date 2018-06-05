package org.csh.study.elasticsearch.demo01;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearchDemo01 {

    private TransportClient client = null;

    @Before
    public void init() throws UnknownHostException, InterruptedException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.28.148.161"), 9300));
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
                .setScript(new Script(ScriptType.INLINE,"ctx._source.gender = \"male\"", null, null))
                .get();

        // or
        client.prepareUpdate("ttl", "doc", "1")
                .setDoc(jsonBuilder()
                        .startObject()
                            .field("gender", "male")
                        .endObject())
                .get();
    }

    /**
     * 插入或更新
     */
    @Test
    public void updateByUpsert() throws IOException, ExecutionException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest("index", "type", "1")
                .source(jsonBuilder()
                            .startObject()
                                .field("name", "James")
                                .field("gender", "male")
                            .endObject());
        UpdateRequest updateRequest = new UpdateRequest("index", "type", "1")
                .doc(jsonBuilder()
                        .startObject()
                            .field("gender", "female")
                        .endObject())
                .upsert(indexRequest);
        client.update(updateRequest).get();
    }

    https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.6/java-docs-update-by-query.html

    /**
     * 查询多个对象
     */
    @Test
    public void multiGetAPI() {
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("twitter", "tweet", "1")
                .add("twitter", "tweet", "2", "3", "4")
                .add("another", "type", "foo")
                .get();
        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse getResponse = itemResponse.getResponse();
            if (null != getResponse && getResponse.isExists()) {
                String json = getResponse.getSourceAsString();
                System.out.println(json);
            }
        }
    }

    /**
     * 可以在一个请求中添加或者删除多个文档
     */
    @Test
    public void bulkAPI() throws IOException {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        bulkRequestBuilder.add(client.prepareIndex("twitter", "tweet", "2")
                                        .setSource(jsonBuilder()
                                                        .startObject()
                                                            .field("user", "Lebron")
                                                            .field("postDate", new Date())
                                                            .field("message", "哈哈哈")
                                                        .endObject()
                                        )
                                );
        bulkRequestBuilder.add(client.prepareIndex("twitter", "tweet", "3")
                                        .setSource(jsonBuilder()
                                                        .startObject()
                                                            .field("user", "Love")
                                                            .field("postDate", new Date())
                                                            .field("message", "乐福")
                                                        .endObject()
                                        )
                                );

        BulkResponse bulkResponse = bulkRequestBuilder.get();
        if (bulkResponse.hasFailures()){
            System.out.println("has some failures");
        }
    }

    /**
     * 设置一个预先的阈值，当到达那个阈值就会自动执行 bulk 请求
     */
    @Test
    public void bulkProcessor() throws IOException, InterruptedException {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                System.out.println("beforeBulk");
                System.out.println(executionId);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                System.out.println("afterBulk");
                System.out.println(executionId);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                failure.printStackTrace();
            }
        }).setBulkActions(10000) //.达到10000个则执行
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)) // 每5MB数据则刷新
                .setFlushInterval(TimeValue.timeValueSeconds(5)) // 每5秒执行一次，不管有多少数据
                .setConcurrentRequests(1) // 并发线程
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)) //
          .build();

        bulkProcessor.add(new IndexRequest("twitter", "tweet", "4")
                .source(jsonBuilder( )
                            .startObject()
                                .field("user", "Love")
                                .field("postDate", new Date())
                                .field("message", "乐福")
                            .endObject()));
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "4"));

        bulkProcessor.awaitClose(10, TimeUnit.MINUTES);

    }

    @After
    public void close() {
        client.close();
    }

}
