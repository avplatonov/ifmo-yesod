package ru.ifmo.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PreparationDocument {

    private static final Logger logger = LogManager.getLogger(PreparationDocument.class);
    private List<DocObor> docFilesForES = new ArrayList<>();
    private String path;

    public PreparationDocument(String path) {
        this.path = path;
        logger.info("Start scan document to path: " + path);
    }

    public List<DocObor> getDocFilesForES() {
        return docFilesForES;
    }

    public boolean isEmpty(){
        return docFilesForES.isEmpty();
    }

    /**
     *
     */
    public void callAllDocumentsReceiving(){
        try {
            List<File> listFiles = new ArrayList<>(Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList()));
            for (File file : listFiles) {
                documentReceiving(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void documentReceiving(File file){
        List<String> lines = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String body = "";
            boolean flagBody=false;

            while ((line = reader.readLine()) != null) {
                if(!flagBody && !line.contains("BODY:") && !line.isEmpty()){
                    lines.add(line.replaceAll("[\\t+||\\n+|\\r+|\\f+]", " "));
                    continue;
                }
                flagBody=true;
                body+=line + " ";

            }
            lines.add(body.replaceAll("\\t+|\\s+|\\n+|\\r+|\\f+", " "));
        }
        catch (IOException ioExp){
            System.out.println(ioExp.getMessage());
        }
        migrateToDocObrType(lines);

        logger.info("Search count document: " + docFilesForES.size());
    }

    private void migrateToDocObrType(List<String> lines){
        DocObor docObor = new DocObor();
        docObor.setDocId(UUID.randomUUID().toString());
        docObor.setName(lines.get(0).split("Name:", 2)[1]);
        docObor.setCountry(lines.get(1).split("Country:", 2)[1].replaceAll("\\s+",""));
        docObor.setType(lines.get(2).split("Type:", 2)[1].replaceAll("\\s+",""));
        docObor.setOrganization(lines.get(3).split("Organization:", 2)[1].replaceAll("^\\s+",""));
        docObor.setRelationType(lines.get(4).split("Relation type:", 2)[1].replaceAll("^\\s+",""));
        docObor.setTags(splitTags(lines.get(5).split("Tags:", 2)[1]));
        docObor.setTS_1(lines.get(6).split("TS_1:", 2)[1].replaceAll("\\s+",""));
        docObor.setTS_2(lines.get(7).split("TS_2:", 2)[1].replaceAll("\\s+",""));
        docObor.setStatus(lines.get(8).split("Status:", 2)[1].replaceAll("\\s+",""));
        docObor.setBody(lines.get(9).split("BODY:", 2)[1]);

        docFilesForES.add(docObor);
    }

    private List<String> splitTags(String tags){
        return Arrays.asList(tags.replaceAll("^\\s+","").split(","));
    }

}
