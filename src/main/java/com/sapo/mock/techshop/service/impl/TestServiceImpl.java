package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.service.TestService;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final RestHighLevelClient client;
    @Override
    public void test(String serviceName) {
        System.out.println("chay test " + serviceName);
    }

    @Override
    public void getIndex(String keyword) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("test_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MatchQueryBuilder matchQuery = new MatchQueryBuilder("listCv.content", "*" + keyword + "*");

        // Tạo một NestedQueryBuilder để thực hiện truy vấn trong nested field
        NestedQueryBuilder nestedQuery = new NestedQueryBuilder("listCv", matchQuery, ScoreMode.None);

        boolQueryBuilder.must(nestedQuery);
        sourceBuilder.query(boolQueryBuilder);
        request.source(sourceBuilder);
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            // Xử lý kết quả
            System.out.println(hit.getSourceAsString());
        }
    }
}
