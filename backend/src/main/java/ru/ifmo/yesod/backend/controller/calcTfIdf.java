package ru.ifmo.yesod.backend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.*;
import ru.ifmo.yesod.backend.db.*;


import opennlp.tools.stemmer.snowball.*;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;


public class calcTfIdf {

	
	private static SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.RUSSIAN);
	private static List<String> words;
	
	private static HashMap<String, Integer> index = new HashMap<>();
	private static List<List<Integer>> TF;
    private static DataCache cacher = new DataCache("index.db");
	private static double[][] hal;
	public static String bufferText;

	private static String[] splitter(String t) {
		return t.toLowerCase().split("[^a-zа-яё]");
	}

	private static List<String> getNormWords(String text) {

		Stream<String> stream = Arrays.stream(splitter(text));
		List<String> norms;
		norms = stream.filter(s -> s.length() > 1).map(s -> (stemmer.stem(s)).toString()).collect(Collectors.toList());
		return norms;

	}


	/**
	 * Function to remove word duplicates
	 * @param <T>
	 * @param list
	 * @return
	 */
	private static <T> List<T> removeDuplicates(List<T> list) 
    {  
        Set<T> set = new LinkedHashSet<>();  
        set.addAll(list);   
        list.clear();   
        list.addAll(set); 
        return list; 
    } 
	
	
	public static double solve(String query, String id, String text, List<Double> idf, List<String> docIds, List<String> index, DataCache cach) {
		String[] queryWords = query.toLowerCase().split(" ");	
		queryWords[0] = (String) stemmer.stem(queryWords[0]);
		queryWords[1] = (String) stemmer.stem(queryWords[1]);		
		int docId = docIds.indexOf(id);
		String arrayOfwords[] = text.toLowerCase().split("[^a-zа-яё]");
		
		List<String> listOfStemmingWords = new ArrayList<>();
		List<Integer> checkpoints1 = new ArrayList<>();
		List<Integer> checkpoints2 = new ArrayList<>();
		
		for(int i =0; i<arrayOfwords.length;i++) {
			String tmp = stemmer.stem(arrayOfwords[i]).toString();
			if (tmp.equals(queryWords[0])) {
				checkpoints1.add(i);
			}
			if (tmp.equals(queryWords[1])) {
				checkpoints2.add(i);
			}
			listOfStemmingWords.add(tmp);
		}

		List<String> surroundWords1 = new ArrayList<>();
		List<String> surroundWords2 = new ArrayList<>();
		
		for(int i :checkpoints1) {
			if(i==0) {
				surroundWords1.add(listOfStemmingWords.get(i+1));
			}
			else if(i==listOfStemmingWords.size()-1) {
				surroundWords1.add(listOfStemmingWords.get(i-1));
			}
			else {
			surroundWords1.add(listOfStemmingWords.get(i-1));
			surroundWords1.add(listOfStemmingWords.get(i+1));
			}
		}
		for(int i :checkpoints2) {
			if(i==0) {
				surroundWords2.add(listOfStemmingWords.get(i+1));
			}
			else if(i==listOfStemmingWords.size()-1) {
				surroundWords2.add(listOfStemmingWords.get(i-1));
			}
			else {
			surroundWords2.add(listOfStemmingWords.get(i-1));
			surroundWords2.add(listOfStemmingWords.get(i+1));
			}
		}
		List<Double> tfIdfVec1 = new ArrayList<>();
		List<Double> tfIdfVec2 = new ArrayList<>();
		surroundWords1 = removeDuplicates(surroundWords1);
		surroundWords2 = removeDuplicates(surroundWords2);
		

		for(String w :surroundWords1) {
			if(index.indexOf(w)>=0) {
			tfIdfVec1.add(cach.getWordTf(index.indexOf(w), docId)*idf.get(index.indexOf(w)));}
		}
		for(String w :surroundWords2) {
			if(index.indexOf(w)>=0) {
				tfIdfVec2.add(cach.getWordTf(index.indexOf(w), docId)*idf.get(index.indexOf(w)));}
		}

		int tfIdfVec1size = tfIdfVec1.size();
		int tfIdfVec2size = tfIdfVec2.size();
		
		if(tfIdfVec1size < tfIdfVec2size) {
			for(int i = 0; i<tfIdfVec2size-tfIdfVec1size;i++) {
				tfIdfVec1.add(0.0);
			}
		}
		if(tfIdfVec2size < tfIdfVec1size) {
				for(int i = 0; i<tfIdfVec1size-tfIdfVec2size;i++) {
					tfIdfVec2.add(0.0);
				}
		}


		double vecComposition= 0;
		double vec1sq= 0;
		double vec2sq= 0;
		for (int j =0; j< tfIdfVec1.size();j++) {
			vecComposition+=tfIdfVec1.get(j)*tfIdfVec2.get(j);
			vec1sq+= tfIdfVec1.get(j) * tfIdfVec1.get(j);
			vec2sq+=tfIdfVec2.get(j) * tfIdfVec2.get(j);
		}

		
		return (vecComposition/(Math.sqrt(vec1sq)*Math.sqrt(vec2sq)));
		
	}

		public static void main(String[] args) throws InterruptedException,IOException {
		
		}
	}


