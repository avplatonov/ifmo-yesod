package ru.ifmo.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;


import java.io.IOException;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

public class ElasticSearchYesod {
    private static final Logger logger = LogManager.getLogger(ElasticSearchYesod.class);

    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";

    private static RestHighLevelClient restHighLevelClient;
    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String INDEX = "doc";
    private static final String TYPE = "docobor";

    private static synchronized RestHighLevelClient makeConnection() {

        if(restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT_ONE, SCHEME),
                            new HttpHost(HOST, PORT_TWO, SCHEME)));
        }

        return restHighLevelClient;
    }

    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

    private static DocObor insertDocObor(DocObor docObor){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", docObor.getName());
        dataMap.put("country", docObor.getCountry());
        dataMap.put("type", docObor.getType());
        dataMap.put("organization", docObor.getOrganization());
        dataMap.put("relationType", docObor.getRelationType());
        dataMap.put("tags", docObor.getTags());
        dataMap.put("ts_1", docObor.getTS_1());
        dataMap.put("ts_2", docObor.getTS_2());
        dataMap.put("status", docObor.getStatus());
        dataMap.put("body", docObor.getBody());

        IndexRequest indexRequest = new IndexRequest(INDEX)
                .source(dataMap);
        indexRequest.id(docObor.getDocId());
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        return docObor;
    }

    private static DocObor getDocOborById(String id){

        GetRequest getPersonRequest = new GetRequest(INDEX, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getPersonRequest, RequestOptions.DEFAULT);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        return getResponse != null ?
                objectMapper.convertValue(getResponse.getSourceAsMap(), DocObor.class) : null;
    }


    public static void main(String[] args) throws IOException {

        PreparationDocument preparationDocument =
                new PreparationDocument("Y:\\Workspace\\elasticsearch\\dataset");
        preparationDocument.callAllDocumentReceiving();
        //List<DocObor> docFilesForES = preparationDocument.getDocFilesForES();

        //logger.info("Running high level client for ES");
        System.out.println("Running high level client for ES");
        makeConnection();
        //logger.info("Start produced successfully");
        System.out.println("Start produced successfully");

        //logger.info("Inserting a new doc with...");
        System.out.println("Inserting a new doc with...");
        DocObor docObor = insertDocObor(preparationDocument.getDocFilesForES().get(0));
        //logger.info("docObor inserted --> " + docObor);
        System.out.println("docObor inserted --> " + docObor);

        //logger.info("Getting docObor...");
        System.out.println("Getting docObor...");
        DocObor docOborFromDB = getDocOborById(docObor.getDocId());
        //logger.info("Search was successful. Get document: " + docOborFromDB);
        System.out.println("Search was successful. Received document: "
                + docOborFromDB.getDocId() + " " + docOborFromDB.getName());

        //logger.info("Closing connection...");
        System.out.println("Closing connection...");
        closeConnection();
        //logger.info("Successfully. Shutdown.");
        System.out.println("Successfully. Shutdown.");
    }
}
