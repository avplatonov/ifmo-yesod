package ru.ifmo.elasticsearch;

import java.util.ArrayList;
import java.util.List;

public class DocObor {

    private String docId;
    private String name;
    private String country;
    private String type;
    private String organization;
    private String relationType;
    private List<String> tags;
    private String TS_1;
    private String TS_2;
    private String status;
    private String body;

    public DocObor() {
    }

    public DocObor(String name, String country, String type, String organization, String relationType, ArrayList<String> tags, String TS_1, String TS_2, String status, String body) {
        this.name = name;
        this.country = country;
        this.type = type;
        this.organization = organization;
        this.relationType = relationType;
        this.tags = tags;
        this.TS_1 = TS_1;
        this.TS_2 = TS_2;
        this.status = status;
        this.body = body;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setTS_1(String TS_1) {
        this.TS_1 = TS_1;
    }

    public void setTS_2(String TS_2) {
        this.TS_2 = TS_2;
    }

    public void setStatus(String status) {
        status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDocId() {
        return docId;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getType() {
        return type;
    }

    public String getOrganization() {
        return organization;
    }

    public String getRelationType() {
        return relationType;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTS_1() {
        return TS_1;
    }

    public String getTS_2() {
        return TS_2;
    }

    public String getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}
