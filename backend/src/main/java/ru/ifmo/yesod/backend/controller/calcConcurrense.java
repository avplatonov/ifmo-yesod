package ru.ifmo.yesod.backend.controller;
import ru.ifmo.yesod.common.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import opennlp.tools.stemmer.snowball.*;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;



public class calcConcurrense {


	private static SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.RUSSIAN);

	/**
	 * The function of calculating the probability of assigning a sentence to a state of relevance (Part of environment words in a sentence).
	 * @param text
	 * @param env
	 * @return
	 */
	public static double env_prob(List<String> text, List<String> env) {
		int count =0;
		for(String s: text)
			for(String w: env) {
				if(w.equals(s)){
					count++;
				}
			}
		
		return (double)count/text.size();
	}
	/**
	 * Function calculate Concurrence metric. t - special parameter.
	 * @param text
	 * @param query
	 * @param t
	 * @return
	 */
	public static double solve(String text, String query, double t) {
		List<String> sentences =  new ArrayList<>(Arrays.asList(text.split("([:\\.] |\\d+[:\\.])",0)));
		double points = 0.0;
		//счетчик для состояния "00" (слова запроса нерелевантны)
	    double noncount = 0;
	    //счетчик для состояния "10" (1-е слово запроса релевантно)
	    double fcount = 0;
	    //счетчик для состояния "01" (2-е слово запроса релевантно)
	    double scount = 0;
	    //счетчик для состояния "11" (слова запроса релевантны)
	    double dcount = 0;
		String[] queryWords = query.split(" ");
		String wordProc1 = stemmer.stem(queryWords[0]).toString();
		String wordProc2 = stemmer.stem(queryWords[1]).toString();
		sentences.removeIf(x ->(x.length() <= 10));
		int sentencesCounter = sentences.size(); 
		List<List<String>> normSentences =  new ArrayList<>();
		String[] tmp = null;
		//получение наормальных форм слов в каждом предложении
		for(String s:sentences) {
			tmp = null;		
			tmp = s.toLowerCase().split("[^a-zа-яё]");
			for (int i = 0; i<tmp.length; i++) {
				tmp[i] = stemmer.stem(tmp[i]).toString();
			}
			
			normSentences.add(Arrays.asList(tmp));
		}
		List<String> env1 = new ArrayList<>();
		List<String> env2 = new ArrayList<>();
		
		//построение окружения для первого слова запроса
		for(List<String> s: normSentences) {
			if(s.contains(wordProc1)) {
				
				for(String w: s) {
					if(!env1.contains(w)) {
						env1.add(w);
					}
				}
			}
		}
		//построение окружения для второго слова запроса
		for(List<String> s: normSentences) {
			if(s.contains(wordProc2)) {			
				for(String w: s) {					
					if(!env2.contains(w)) {
						env2.add(w);
					}
				}
			}
		}
	
		//подсчет счетчиков состояний
		for (List<String> s: normSentences) {
			if(s.contains( wordProc1) || s.contains( wordProc2)) {
				if(s.contains( wordProc1) && !s.contains( wordProc2)) {
					fcount++;			
				}
				if(!s.contains( wordProc1) && s.contains( wordProc2)) {
					scount++;			
				}
				if(s.contains( wordProc1) && s.contains( wordProc2)) {
					dcount++;			
				}
			}
			else {
				double p_1 = env_prob(s,env1);
				double p_2 = env_prob(s,env2);
				if(p_1<t && p_2<t) {
					noncount++;
				}
				if(p_1>=t && p_2<t) {
					fcount++;
				}
				if(p_1 < t && p_2 >= t) {
					scount++;
				}
				if(p_1 >= t && p_2 >= t) {
					 dcount++;
				}
			}
		}
		points = 2 * Math.abs(Math.sqrt((fcount/sentencesCounter)*(scount/sentencesCounter)) - Math.sqrt((dcount/sentencesCounter)*(noncount/sentencesCounter)));
					
		
		return points;
	}
	
	private static List<Integer> 	idx_copies(List<String> s, String w) {
		List<Integer> idxs = new ArrayList<>();
		for (int i = 0; i<s.size(); i++) {
			if (w == s.get(i)) {
				idxs.add(i);
			}
		}
		return idxs;
		
	}
	
	private static List<Double>  w_weight(List<String> e, List<List<String>> s, String w){
		List<Double> dist = new ArrayList<>();
		List<Integer> freq = new ArrayList<>();
		List<Integer> tmp_dist = new ArrayList<>();
		int tmp_freq = 0;
		List<Integer> idx_env = new ArrayList<>();
		for (String ew: e) {
			for(List<String> sent: s) {
				if((sent.indexOf(w) != -1) && (sent.indexOf(ew) != -1)) {
					idx_env = idx_copies(sent,ew);
					for(int i: idx_env) {
						tmp_dist.add(Math.abs(sent.indexOf(w) - i));
						tmp_freq +=1;
					}
				}
			}
			
			dist.add((double)tmp_dist.stream().mapToInt(Integer::intValue).sum()/tmp_dist.size());
			freq.add(tmp_freq);	
			tmp_freq = 0;
			tmp_dist.clear();
		}
		List<Double> weight = new ArrayList<>();
		double max = 0 ;
		for(int i = 0; i< dist.size(); i++) {
			double tmp = freq.get(i)/dist.get(i);
			weight.add(tmp);
			if(Math.abs(tmp) > max) {
				max = Math.abs(tmp);
			}
		}
		if (weight.size() > 0) {
			if(max > 0) {
				for(int i = 0; i< weight.size(); i++) {
					weight.set(i, weight.get(i)/max);
				}	
			}
			else {
				weight.clear();
				weight.add(0.0);
			}		
		}
		else {
			weight.clear();
			weight.add(0.0);
		}				
		return weight;
	}
	
	
	public static double solve1(String text, String query, double t) {
		List<String> sentences =  new ArrayList<>(Arrays.asList(text.split("([:\\.] |\\d+[:\\.])",0)));
		double points = 0.0;
		//счетчик для состояния "00" (слова запроса нерелевантны)
	    double noncount = 0;
	    //счетчик для состояния "10" (1-е слово запроса релевантно)
	    double fcount = 0;
	    //счетчик для состояния "01" (2-е слово запроса релевантно)
	    double scount = 0;
	    //счетчик для состояния "11" (слова запроса релевантны)
	    double dcount = 0;
		String[] queryWords = query.split(" ");
		String wordProc1 = stemmer.stem(queryWords[0]).toString();
		String wordProc2 = stemmer.stem(queryWords[1]).toString();
		sentences.removeIf(x ->(x.length() <= 10));
		int sentencesCounter = sentences.size(); 
		List<List<String>> normSentences =  new ArrayList<>();
		String[] tmp = null;
		//получение наормальных форм слов в каждом предложении
		for(String s:sentences) {
			tmp = null;		
			tmp = s.toLowerCase().split("[^a-zа-яё]");
			for (int i = 0; i<tmp.length; i++) {
				tmp[i] = stemmer.stem(tmp[i]).toString();
			}
			
			normSentences.add(Arrays.asList(tmp));
		}
		List<String> env1 = new ArrayList<>();
		List<String> env2 = new ArrayList<>();
		
		//построение окружения для первого слова запроса
		for(List<String> s: normSentences) {
			if(s.contains(wordProc1)) {
				
				for(String w: s) {
					if(!env1.contains(w)) {
						env1.add(w);
					}
				}
			}
		}
		//построение окружения для второго слова запроса
		for(List<String> s: normSentences) {
			if(s.contains(wordProc2)) {			
				for(String w: s) {					
					if(!env2.contains(w)) {
						env2.add(w);
					}
				}
			}
		}
	
		List<Double> weight_w1 = new ArrayList<>();
		List<Double> weight_w2 = new ArrayList<>();
		
		weight_w1 = w_weight(env1, normSentences, wordProc1);
		weight_w2 = w_weight(env2, normSentences, wordProc2);
		
		//подсчет счетчиков состояний
		for (List<String> s: normSentences) {
			if(s.contains( wordProc1) || s.contains( wordProc2)) {
				if(s.contains( wordProc1) && !s.contains( wordProc2)) {
					fcount++;			
				}
				if(!s.contains( wordProc1) && s.contains( wordProc2)) {
					scount++;			
				}
				if(s.contains( wordProc1) && s.contains( wordProc2)) {
					dcount++;			
				}
			}
			else {
				//double p_1 = env_prob(s,env1);
				List<Integer> f = Collections.nCopies(env1.size(), 0);
				double p_1 = 0;
				double sum_w = weight_w1.stream().mapToDouble(Double::doubleValue).sum(); 
				if(sum_w != 0) {
					for(String w: s) {
						for(String e: env1) {
							if(w == e) {
								f.set(env1.indexOf(e), 1);
								
							}
						}
					}
					List<Double> weight_env1 = new ArrayList<>();
					for(int i =0; i< f.size();i++) {
						weight_env1.add(weight_w1.get(i)*f.get(i));
					}
					p_1 = weight_env1.stream().mapToDouble(Double::doubleValue).sum()/sum_w;
				}
				else
					p_1=0;
				
				double p_2 = 0;
				f = Collections.nCopies(env2.size(), 0);
				sum_w = weight_w2.stream().mapToDouble(Double::doubleValue).sum(); 
				if(sum_w != 0) {
					for(String w: s) {
						for(String e: env2) {
							if(w == e) {
								f.set(env2.indexOf(e), 1);
								
							}
						}
					}
					List<Double> weight_env2 = new ArrayList<>();
					for(int i =0; i< f.size();i++) {
						weight_env2.add(weight_w2.get(i)*f.get(i));
					}
					p_1 = weight_env2.stream().mapToDouble(Double::doubleValue).sum()/sum_w;
				}
				else
					p_2=0;												
				//double p_2 = env_prob(s,env2);
				if(p_1<t && p_2<t) {
					noncount++;
				}
				if(p_1>=t && p_2<t) {
					fcount++;
				}
				if(p_1 < t && p_2 >= t) {
					scount++;
				}
				if(p_1 >= t && p_2 >= t) {
					 dcount++;
				}
			}
		}
		points = 2 * Math.abs(Math.sqrt((fcount/sentencesCounter)*(scount/sentencesCounter)) - Math.sqrt((dcount/sentencesCounter)*(noncount/sentencesCounter)));
					
		
		return points;
		
		
	}
	
	
		

}
