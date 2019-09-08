package ru.ifmo.elasticsearch;

import org.elasticsearch.client.RequestOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class ElasticSearchYesodTest {
    private static ElasticSearchYesod elasticSearchYesod;
    private static PreparationDocument preparationDocuments;
    private DocObor docObor0;
    private DocObor docObor1;
    private static String path = "Y:\\\\Workspace\\\\elasticsearch\\\\dataset";

    @BeforeClass
    public static void preparation(){
        elasticSearchYesod = new ElasticSearchYesod();
        preparationDocuments = elasticSearchYesod.prepareDocuments(path);

        try {
            Assert.assertTrue(elasticSearchYesod.makeConnection().ping(RequestOptions.DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void preparationDocumentsTest(){
        Assert.assertEquals(424, preparationDocuments.getDocFilesForES().size());
    }

    @Test
    public void addAllDocumentsToESTest(){
        Assert.assertTrue(elasticSearchYesod.addAllDocumentsToES());
    }

    @Test
    public void getDocOborByIdTest(){
        docObor1 = preparationDocuments.getDocFilesForES().get(0);
        Assert.assertEquals(" СОГЛАШЕНИЕ между Правительством Российской Федерации  и Правительством Федеративной Республики Бразилии о научно-техническом сотрудничестве",
                elasticSearchYesod.getDocOborById(preparationDocuments.getDocFilesForES().get(0).getDocId()).getName());
    }

    @Test
    public void getDocOborByTextTest() throws IOException {
        Assert.assertEquals(10,
                elasticSearchYesod.searchDocsOborByTextFromBody("Бразилии").size());
    }

    @Test
    public void updateDocOborByIdTest(){
        docObor1 = preparationDocuments.getDocFilesForES().get(1);
        elasticSearchYesod.updateDocOborById(preparationDocuments.getDocFilesForES().get(0).getDocId(), preparationDocuments.getDocFilesForES().get(1));
        Assert.assertEquals(docObor1.getName(),elasticSearchYesod.getDocOborById(preparationDocuments.getDocFilesForES().get(0).getDocId()).getName());
    }

    @Test (expected = NullPointerException.class)
    public void deleteDocOborIdTest(){
        elasticSearchYesod.deleteDocOborId(preparationDocuments.getDocFilesForES().get(0).getDocId());
        Assert.assertNull(elasticSearchYesod.getDocOborById(preparationDocuments.getDocFilesForES().get(0).getDocId()));
    }

    @AfterClass
    public static void closeConnectionTest() throws IOException {
        elasticSearchYesod.closeConnection();
    }

}