package ru.ifmo.yesod.common.morphology;

import java.io.IOException;
import java.util.Date;
import org.junit.Test;
import ru.ifmo.yesod.common.model.Document;

public class LuceneDocumentNormalizerTest {
    @Test
    public void test() throws IOException {
        LuceneDocumentNormalizer normalizer = new LuceneDocumentNormalizer();
        Date ts = new Date();
        Document result = normalizer.normalizeFields(new Document(
            "Какое-то тестовое имя",
            "Пиндостан",
            "Не знаю никаких типов документов",
            "Пацаны и Ко",
            "Рилейшн",
            new String[] {"Тэг Раз", "Тэг Два"},
            "Статус",
            ts,
            "Инна мыла раму долго и упорно пока не потеряла сознание и не упала с 4го этажа"
        ));

        System.out.println(result);
    }
}
