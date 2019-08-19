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

package ru.ifmo.yesod.common.morphology;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import ru.ifmo.yesod.common.model.Document;

public class LuceneDocumentNormalizer implements DocumentNormalizer {
    private RussianAnalyzer analyzer = new RussianAnalyzer();

    public Document normalizeFields(Document doc) {
        return new Document(
            normalize(doc.getName()),
            doc.getCountry(),
            doc.getTypeName(),
            doc.getRelatedOrganization(),
            doc.getRelationType(),
            Arrays.stream(doc.getTags()).map(this::normalize).toArray(String[]::new),
            doc.getStatus(),
            doc.getTs(),
            normalize(doc.getBody()));
    }

    private String normalize(String s) {
        try {
            StringBuilder builder = new StringBuilder();
            TokenStream tokenStream = analyzer.tokenStream(null, s);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
                builder.append(attribute.toString()).append(" ");
            }
            tokenStream.close();
            return builder.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
