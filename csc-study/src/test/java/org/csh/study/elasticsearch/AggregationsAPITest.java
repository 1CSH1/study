package org.csh.study.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBounds;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanks;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.junit.Test;

/**
 * @author James
 * @date 2018/6/6
 */
public class AggregationsAPITest extends Base {

    @Test
    public void structuringAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.terms("by_country")
                        .field("country")
                        .subAggregation(AggregationBuilders.dateHistogram("by_year")
                                .field("dateOfBirth")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                                .subAggregation(AggregationBuilders.avg("avg_children"))
                                .field("children")
                        )
                ).execute()
                .actionGet();

    }

    @Test
    public void minAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.min("age").field("age")
                )
                .execute()
                .actionGet();
        Min age = response.getAggregations().get("age");
        double value = age.getValue();
    }

    @Test
    public void maxAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.max("age").field("age")
                )
                .execute()
                .actionGet();
        Max age = response.getAggregations().get("age");
        double value = age.getValue();
    }

    @Test
    public void sumAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.sum("age").field("age")
                )
                .execute()
                .actionGet();
        Sum age = response.getAggregations().get("age");
        double value = age.getValue();
    }

    @Test
    public void avgAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.avg("age").field("age")
                )
                .execute()
                .actionGet();
        Avg age = response.getAggregations().get("age");
        double value = age.getValue();
    }

    @Test
    public void statsAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.stats("age").field("age")
                )
                .execute()
                .actionGet();
        Stats stats = response.getAggregations().get("age");
        stats.getAvg();
        stats.getMax();
    }

    @Test
    public void extendedStatsAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.extendedStats("age").field("age")
                )
                .execute()
                .actionGet();
        ExtendedStats stats = response.getAggregations().get("age");
        stats.getAvg();
        stats.getMax();
        stats.getStdDeviation();
        stats.getSumOfSquares();
    }

    @Test
    public void valueCountAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.count("age").field("age")
                )
                .execute()
                .actionGet();
        ValueCount valueCount = response.getAggregations().get("age");
        long value = valueCount.getValue();
    }

    @Test
    public void percentilesAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.percentiles("age").field("age")
                                .percentiles(1.0, 5.0, 10.0, 20.0, 30.0, 75.0, 95.0, 99.0)
                )
                .execute()
                .actionGet();
        Percentiles percentiles = response.getAggregations().get("age");
        for (Percentile percentile : percentiles) {
            double percent = percentile.getPercent();
            double value = percentile.getValue();
        }
    }

    @Test
    public void percentileRanksAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.percentileRanks("age").field("age")
                        .values(1.23, 2.34, 3.45)
                )
                .execute()
                .actionGet();
        PercentileRanks percentileRanks = response.getAggregations().get("age");
        for (Percentile percentile : percentileRanks) {
            double percent = percentile.getPercent();
            double value = percentile.getValue();
        }
    }

    @Test
    public void cardinalityAggregations() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.cardinality("age").field("age")
                )
                .execute()
                .actionGet();
        Cardinality age = response.getAggregations().get("age");
        long value = age.getValue();
    }

    @Test
    public void geoBoundsAggregation() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.geoBounds("agg").field("address.location")
                        .wrapLongitude(true)
                )
                .execute()
                .actionGet();

        GeoBounds agg = response.getAggregations().get("agg");
        GeoPoint bootomRight = agg.bottomRight();
        GeoPoint topLeft = agg.topLeft();
    }

    @Test
    public void topHitsAggregation() {
        SearchResponse response = client.prepareSearch("twitter")
                .addAggregation(
                        AggregationBuilders.terms("agg").field("gender")
                        .subAggregation(AggregationBuilders.topHits("top"))
                )
                .execute()
                .actionGet();

        Terms agg = response.getAggregations().get("agg");
        for (Terms.Bucket entry : agg.getBuckets()) {
            String key = (String) entry.getKey();
            long docCount = entry.getDocCount();

            TopHits topHits = entry.getAggregations().get("top");
            for (SearchHit hit : topHits.getHits().getHits()) {
                hit.getId();
                hit.getSourceAsString();
            }
        }
    }

}
