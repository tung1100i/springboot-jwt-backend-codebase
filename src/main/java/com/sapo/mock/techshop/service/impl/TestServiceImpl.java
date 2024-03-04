package com.sapo.mock.techshop.service.impl;

import com.sapo.mock.techshop.model.Comment;
import com.sapo.mock.techshop.repository.CommentRepository;
import com.sapo.mock.techshop.service.TestService;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final RestHighLevelClient client;

    @Override
    public void test(String serviceName) {
        System.out.println("chay test " + serviceName);
    }

    @Autowired
    private CommentRepository commentRepository;

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

    @Override
    public void pushDocument() throws IOException {
        //get all comment
        List<String> comments = commentRepository.getAllComment();

        BulkRequest bulkRequest = new BulkRequest();
        List<List<String>> contentsOfCv = new ArrayList<>();
        Random rand = new Random();
        int randomNumber = 0;
        for (int i = 0; i < comments.size(); i = randomNumber) {
            System.out.print("from: " + randomNumber);
            int min = randomNumber;
            int max = randomNumber + rand.nextInt(10) + 4;

            int newRandomNumber = rand.nextInt((max - min) + 1) + min;

            System.out.println(" - to: " + newRandomNumber);
            List<String> str = new ArrayList<>();
            for (int j = randomNumber; j < newRandomNumber; j++) {
                str.add(comments.get(j));
            }
            contentsOfCv.add(str);
            if (newRandomNumber > 48800) break;

            randomNumber = newRandomNumber;
        }
        String dateString = "1980-01-01 00:01:01";

        //push comment to elk
        for (List<String> contentArray : contentsOfCv) {

            Map<String, Object> jsonMap = new HashMap<>();
            Map<String, Object> jsonSubMap = new HashMap<>();
            jsonSubMap.put("content", contentArray);
            jsonSubMap.put("fileName", "fine_name_1.pdf");
            jsonSubMap.put("fileId", 123534);
            jsonMap.put("listCv", Collections.singletonList(jsonSubMap));
            jsonMap.put("modifiedTime", this.getDate(dateString));
            // Tạo một yêu cầu đẩy dữ liệu vào Elasticsearch
            bulkRequest.add(new IndexRequest("test_index")
                    .type("_doc")
                    .source(jsonMap, XContentType.JSON));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(dateString, formatter);
            dateString = this.toStringDate(startDateTime.plusMinutes(10));
        }
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        if (response.hasFailures()) {
            System.out.println("false");
            System.out.println(response.buildFailureMessage());

        } else {
            System.out.println("true");
        }
    }

    private String getDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(dateString, formatter);
        return this.toStringDate(startDateTime.plusMinutes(10));
    }

    private String toStringDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }
}
