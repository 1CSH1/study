package org.csh.study.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.junit.Test;


/**
 * @author James
 * @date 2018/6/8
 */
public class BucketAggregationsAPITest extends Base {

    @Test
    public void globalAggregation() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.global("agg")
                        .subAggregation(
                                AggregationBuilders.terms("genders").field("gender")
                        )
                )
                .execute()
                .actionGet();

        Global agg = response.getAggregations().get("agg");
        long docCount = agg.getDocCount();
    }

    @Test
    public void filterAggregation() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.filter("agg", QueryBuilders.termQuery("gender", "male"))
                )
                .execute()
                .actionGet();

        Filter agg = response.getAggregations().get("agg");
        long docCount = agg.getDocCount();
    }
}
