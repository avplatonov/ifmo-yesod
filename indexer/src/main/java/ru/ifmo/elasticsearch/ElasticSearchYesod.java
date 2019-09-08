package ru.ifmo.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


public class ElasticSearchYesod {
    //private static final Logger logger = LogManager.getLogger(ElasticSearchYesod.class);

    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9300;
    private static final String SCHEME = "http";

    private RestHighLevelClient restHighLevelClient;
    private ObjectMapper objectMapper = new ObjectMapper();
    private PreparationDocument preparationDocuments;
    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

    private static final String INDEX = "doc";

    /**
     * @value TYPE is deprecated and is no longer used
     */
    //private static final String TYPE = "docobor";

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public PreparationDocument getPreparationDocuments() {
        return preparationDocuments;
    }


    /**
     * starting the process of extracting documents from the local storage
     * @param path path to DB the documents
     * @return PreparationDocument list if preparation documents was successful or null
     */
    public PreparationDocument prepareDocuments(String path){
        //logger.info("Running preparation documents for ES...");
        System.out.println("Running preparation documents for ES...");
        preparationDocuments = new PreparationDocument(path);
        preparationDocuments.callAllDocumentsReceiving();
        if(preparationDocuments.isEmpty()){
            //logger.info("Preparation documents is failed");
            System.out.println("Preparation documents is failed");
            return null;
        }
        //logger.info("Preparation documents was successful");
        System.out.println("Preparation documents was successful");
        return preparationDocuments;
    }

    /**
     * starting adding all documents to ES
     */
    public boolean addAllDocumentsToES(){
        //logger.info("Starting adding all documents to ES...");
        System.out.println("Starting adding all documents to ES...");
        for (DocObor docObor : preparationDocuments.getDocFilesForES()) {
            insertDocObor(docObor);
        }
        //logger.info("Adding was successful");
        System.out.println("Adding was successful");
        return true;
    }

    /**
     * creates a connection witch ES using the given HOST, PORTS, SCHEME
     * @return RestHighLevelClient for various statistics
     */
    public synchronized RestHighLevelClient makeConnection() {
        //logger.info("Running high level client for ES");
        System.out.println("Running high level client for ES");
        if(restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT_ONE, SCHEME),
                            new HttpHost(HOST, PORT_TWO, SCHEME)));
        }

        //logger.info("Start produced successfully");
        System.out.println("Start produced successfully");
        return restHighLevelClient;
    }

    /**
     * closed a connection witch ES
     */
    public synchronized void closeConnection() throws IOException {
        //logger.info("Closing connection...");
        System.out.println("Closing connection...");

        restHighLevelClient.close();
        restHighLevelClient = null;

        //logger.info("Successfully. Shutdown ES connection.");
        System.out.println("Successfully. Shutdown ES connection.");
    }

    /**
     * adds a document to the ES using internal mapping
     * @param docObor received document from the database
     * @return docObor received document from the database (from param)
     */
    public DocObor insertDocObor(DocObor docObor){
        //logger.info("Inserting a new doc...");
        System.out.println("Inserting a new doc...");
        Map<String, Object> dataMap = new HashMap<>();

        //todo: might be worth putting fields into a variable
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
       //logger.info("new docObor inserted --> " + docObor);
        System.out.println("new docObor inserted --> " + docObor);
        return docObor;
    }

    /**
     * get a document from the ES by id
     * @param id id received document from the database
     * @return docObor document from the ES (if found) or null
     */
    public DocObor getDocOborById(String id){
        //logger.info("Getting docObor...");
        System.out.println("Getting docObor...");

        GetResponse getResponse = null;
        GetRequest getPersonRequest = null;
        try {
            getPersonRequest = new GetRequest(INDEX, id);
            getResponse = restHighLevelClient.get(getPersonRequest, RequestOptions.DEFAULT);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }catch (NullPointerException ex){
            ex.getMessage();
        }

        DocObor getDocObor = null;
        if(getResponse != null) {
            getDocObor = objectMapper.convertValue(getResponse.getSourceAsMap(), DocObor.class);
            //logger.info("Search was successful. Received document: "
            //        + getDocObor.getDocId() + " " + getDocObor.getName());
            System.out.println("Search was successful. Received document: "
                    + getDocObor.getDocId() + " " + getDocObor.getName());
        }

        return getDocObor;
    }

    /**
     * get a document from the ES by search text
     * @param text search text
     * @return SearchHits hits
     */
    public List<DocObor> searchDocsOborByTextFromBody(String text) throws IOException {
        //todo: ищет ли ES с помощью шинглов... или самому разбивать?...
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery("body", text));
        //searchSourceBuilder.size();
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHits hits = searchResponse.getHits();
        List<DocObor> hitsDocObor = new ArrayList<>();

        for (SearchHit hit : hits) {
            hitsDocObor.add(objectMapper.convertValue(hit.getSourceAsMap(),DocObor.class));
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        boolean succeeded = clearScrollResponse.isSucceeded();
        return hitsDocObor;
    }


    /**
     * update a document from the ES by id
     * @param id id received document from the database
     * @param docObor docObor document that will replace
     * @return docObor updated document from the ES (if successfully) or null
     */
    public DocObor updateDocOborById(String id ,DocObor docObor){
        //logger.info("Updating docObor...");
        System.out.println("Updating docObor...");
        UpdateRequest updateRequest = new UpdateRequest(INDEX, id)
                .fetchSource(true);    // Fetch Object after its update
        try {
            String personJson = objectMapper.writeValueAsString(docObor);
            updateRequest.doc(personJson, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            //logger.info("Updating was successful.");
            System.out.println("Updating was successful.");
            return objectMapper.convertValue(updateResponse.getGetResult().sourceAsMap(), DocObor.class);
        }catch (JsonProcessingException e){
            e.getMessage();
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        //logger.info("Updating is failed.");
        System.out.println("Updating is failed.");
        return null;
    }

    /**
     * delete a document from the ES by id
     * @param id id received document from the database
     */
    public void deleteDocOborId(String id) {
        //logger.info("Deleting docObor...");
        System.out.println("Deleting docObor...");
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, id);
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (java.io.IOException e){
            //logger.info("Deleting is failed.");
            System.out.println("Deleting is failed.");
            e.getLocalizedMessage();
        }
        //logger.info("Deleting was successful.");
        System.out.println("Deleting was successful.");
    }
}
