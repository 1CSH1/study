package org.csh.study.elasticsearch;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

/**
 * @author James
 * @date 2018/6/6
 */
public class SearchAPITest extends Base{

    /**
     * 查询
     */
    @Test
    public void searchAPI() {
        SearchResponse response = client.prepareSearch("twitter")
                .setTypes("tweet")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("user", "james"))
                .setFrom(0)
                .setSize(10)
                .setExplain(true)
                .get();
    }

    @Test
    public void scrollsSearch() {
        SearchResponse scrollResp = client.prepareSearch("twitter")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(QueryBuilders.termQuery("tweet", "james"))
                .setSize(100)
                .get();

        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                System.out.println(hit.docId());
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
        } while (scrollResp.getHits().getHits().length != 0);
    }

    @Test
    public void multiSearchAPI() {
        SearchRequestBuilder srb1 = client
                .prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
        SearchRequestBuilder srb2 = client
                .prepareSearch().setQuery(QueryBuilders.matchQuery("name", "kimchy")).setSize(1);

        MultiSearchResponse sr = client.prepareMultiSearch()
                .add(srb1)
                .add(srb2)
                .get();

        // You will get all individual responses from MultiSearchResponse#getResponses()
        long nbHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            nbHits += response.getHits().getTotalHits();
        }
    }

    @Test
    public void aggregationsSearchAPI() {
        SearchResponse sr = client.prepareSearch()
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(
                        AggregationBuilders.terms("agg1").field("field")
                )
                .addAggregation(
                        AggregationBuilders.dateHistogram("agg2")
                                .field("birth")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                )
                .get();

// Get your facet results
        Terms agg1 = sr.getAggregations().get("agg1");
        Histogram agg2 = sr.getAggregations().get("agg2");
    }

    @Test
    public void terminateAfterSearchAPI() {
        SearchResponse sr = client.prepareSearch("")
                .setTerminateAfter(1000)
                .get();

        if (sr.isTerminatedEarly()) {
            // We finished early
        }
    }

}
