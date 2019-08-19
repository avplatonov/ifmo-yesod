/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ifmo.yesod.common.model;

import java.util.Arrays;
import java.util.Date;

public class Document {
    private final String name;
    private final String country;
    private final String typeName;
    private final String relatedOrganization;
    private final String relationType;
    private final String[] tags;
    private final String status;
    private final Date ts;
    private final String body;

    public Document(String name, String country, String typeName, String relatedOrganization, String relationType,
        String[] tags, String status, Date ts, String body) {
        this.name = name;
        this.country = country;
        this.typeName = typeName;
        this.relatedOrganization = relatedOrganization;
        this.relationType = relationType;
        this.tags = tags;
        this.status = status;
        this.ts = ts;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getRelatedOrganization() {
        return relatedOrganization;
    }

    public String getRelationType() {
        return relationType;
    }

    public String[] getTags() {
        return tags;
    }

    public String getStatus() {
        return status;
    }

    public Date getTs() {
        return ts;
    }

    public String getBody() {
        return body;
    }

    @Override public String toString() {
        return "Document{" +
            "name='" + name + '\'' +
            ", country='" + country + '\'' +
            ", typeName='" + typeName + '\'' +
            ", relatedOrganization='" + relatedOrganization + '\'' +
            ", relationType='" + relationType + '\'' +
            ", tags=" + Arrays.toString(tags) +
            ", status='" + status + '\'' +
            ", ts=" + ts +
            ", body='" + body + '\'' +
            '}';
    }
}
