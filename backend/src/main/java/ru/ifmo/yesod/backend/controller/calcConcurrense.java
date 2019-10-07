package ru.ifmo.yesod.backend.controller;
import ru.ifmo.yesod.common.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;


public class calcConcurrense {

	private static RussianAnalyzer analyzer;

    {
        try {
            analyzer = new RussianAnalyzer();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	private static String normalize(String s) {
		try {
			RussianAnalyzer analyzer = new RussianAnalyzer();
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
	public static double solve(String text, String query) {
		List<String> sentences =  new ArrayList<>(Arrays.asList(text.split("([:\\.] |\\d+[:\\.])",0)));
		double points = 0.0;
		double allWordCounter = 0;
		double firstWordCounter = 0;
		double secondWordCounter = 0;
		double noneWordCounter = 0;
		double sentencesCounter = 0;
		String[] queryWords = (normalize(query)).split(" ");

		sentences.removeIf(x ->(x.length() <= 10));
		sentencesCounter = sentences.size(); 
		Boolean firstWord;
		Boolean secondWord;
		String tmp;
		for(String s : sentences) {		
			tmp = normalize(s);
			firstWord = tmp.contains(queryWords[0]);
			secondWord = tmp.contains(queryWords[1]);
			if (firstWord && secondWord) {
				allWordCounter++;
			}			
			else if (!(firstWord || secondWord)) {
				 noneWordCounter++;
				}
			else if(firstWord) {
				firstWordCounter++;
			}
			else
				secondWordCounter++;
			points = 2 * Math.abs(Math.sqrt((firstWordCounter/sentencesCounter)*(secondWordCounter/sentencesCounter)) - Math.sqrt((allWordCounter/sentencesCounter)*(noneWordCounter/sentencesCounter)));
					
		}
		return points;
	}
		

}
