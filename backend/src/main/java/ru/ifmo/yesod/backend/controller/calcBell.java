package ru.ifmo.yesod.backend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import Jama.Matrix;
import opennlp.tools.stemmer.snowball.*;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;
import ru.ifmo.yesod.backend.model.DocumentItem;
public class calcBell {
	
	private static String[] words = null;
	private static RussianAnalyzer analyzer;
	private static HashMap<String, Integer> index = new HashMap<>();
	private static double [] [] hal;
	private static Matrix gsCoef;
	private static Matrix orths1;
	private static Matrix orths2;
	private static double proj_coef1;
	private static double proj_coef2;
	private static SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.RUSSIAN);
	
	
	public calcBell() {
			
	    {
	        try {
	            analyzer = new RussianAnalyzer();
	        }
	        catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }	
	}

	/**
	 * Split text to words. 
	 * Result will saved in static variable "words".
	 * @param t Document text
	 */
	private static void spliter(String t) {
		words = t.toLowerCase().split("[^a-zа-яё]"); 
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
	
	/**
	 * Build document index in static variable "index"
	 * @param text
	 */
	private static void buidIndex(String text) {
		
		int counter = index.size();	
		spliter(text);
		Stream<String> stream = Arrays.stream(words); 
        words = stream.filter(s->s.length()>2).map(s ->(stemmer.stem(s)).toString()).toArray(size -> new String[size]);
		for (String w : words) {
			if (!index.containsKey(w)) {
				index.put(w, counter);
				counter++;
			}
			
		}
	}
	
	/**
	 * Build HAL matrix with using splitted words of text and index.
	 * @param windowSize Size of text window
	 */
	
	private static void buildHalMatrix(int windowSize) {
		
		hal = new double[index.size()][index.size()];
		String word;
		int wordIndexFrom;
		int wordIndexTo;
		String rigthWord;
		int lenWords = words.length;
		for(int i =0; i<words.length; i++) {
			word = words[i];
			wordIndexFrom = index.get(word);
			for(int j = 1; j < windowSize +1; j++) {
				if( i + j < lenWords) {
					rigthWord = words[i + j];
					wordIndexTo = index.get(rigthWord);
					hal[wordIndexFrom][wordIndexTo] = hal[wordIndexFrom][wordIndexTo] + windowSize - j +1;				}
			}
			
		}
	}
	public static double[][] getHal(int window, String text) {
		buidIndex(text);
		buildHalMatrix(window);
		return hal;
	}
	
	
	/**
	 * Calc coefficient for Gram–Schmidt method and save it in static variable
	 * @param a
	 * @param b
	 */		
	private static void gsCofficientCalc(Matrix a, Matrix b) {
		b = new Matrix(b.getRowPackedCopy(),b.getColumnDimension());
		Matrix aT = a.transpose();
		gsCoef = (a.times(b)).arrayRightDivide(a.times(aT));
	}
	
	private static Matrix multiply(Matrix vec) {
		vec = vec.times(gsCoef.get(0, 0));
		return vec;
	}
	private static Matrix proj(Matrix a, Matrix b) {
		gsCofficientCalc(a,b);
		return multiply(b);
	}
	
	private static Matrix gs(Matrix x) {
		List<double[]> y = new ArrayList<double[]>();
		Matrix proj_vec;
		Matrix temp_vec;
		int xRows = x.getRowDimension();
		int xCols = x.getColumnDimension();
		for(int i = 0; i<xRows;i++) {
			temp_vec = x.getMatrix(i, i, 0, xCols-1);
			for (int j = 0; j < y.size(); j++ ) {				
				
				proj_vec = proj(new Matrix(y.get(j),1), temp_vec);
				temp_vec = temp_vec.minus(proj_vec);
			}
			y.add(temp_vec.getColumnPackedCopy());
		}
		return new Matrix(y.toArray(new double[y.size()][]));
	}
	
	/**
	 * Create document vector, that  base on HAL matrix
	 * @return Matrix result - Document vector
	 */
	private static Matrix defineDocumentVector() {
		Matrix result = new Matrix(1,hal[0].length);
		for(int i = 0; i<hal.length;i++) {
			result = result.plus(new Matrix(hal[i],1));
		}
		return result;
	}
	
	/**
	 * The decomposition of word vectors into bases, and save it in static variables
	 *  
	 */
	
	private static void define_orths(Matrix word1, Matrix word2) {
		
		Matrix w1;
		Matrix w2;
		if(word1.getColumnDimension() == 1) {
			w1 = word1.transpose();
		}
		else {
			w1 = word1;
		}
		if(word2.getColumnDimension() == 1) {
			w2 = word2.transpose();
		}
		else {
			w2 = word2;
		}
		Matrix tmp = new Matrix(2,w1.getColumnDimension());		
		tmp.setMatrix(0, 0, 0, w1.getColumnDimension()-1, w1);

		tmp.setMatrix(1, 1, 0, w2.getColumnDimension()-1, w2);						
		orths1 = gs(tmp);
		
		tmp.setMatrix(0, 0, 0, word2.getColumnDimension()-1, word2);
		tmp.setMatrix(1, 1, 0, word1.getColumnDimension()-1, word1);
		orths2 = gs(tmp);
	}

	
	private static double define_proj_coef(Matrix document_vector, Matrix word_vector, Matrix orth_word_vector) {
		double denom = Math.sqrt(Math.pow(document_vector.times(word_vector.transpose()).get(0, 0),2) + Math.pow(document_vector.times(orth_word_vector.transpose()).get(0, 0),2));
		return document_vector.times(word_vector.transpose()).get(0, 0)/denom;
	}
	
	
	/**
	 * calculation of the coordinates of the projection vector onto the specified basis word vector, orth_word vector, and save it in static variable
	 * @param document_vector 
	 * @param word_vector 
	 * @param orth_word_vector 
	 */	
	private static void define_proj_coefs(Matrix document_vector, Matrix word_vector, Matrix orth_word_vector) {
		proj_coef1 = define_proj_coef(document_vector, word_vector,orth_word_vector);
		proj_coef2 = define_proj_coef(document_vector, orth_word_vector,word_vector);
		
	}
	
	/**
	 * Bourne rule for determining the probability of a state
	 * @param operator
	 * @param bra
	 * @param ket
	 * @return
	 */
	private static double bornRule(Matrix operator,Matrix bra, Matrix ket) {
		double ans = bra.times(operator.times(ket)).get(0, 0);
		return ans;
	}
	
	/**
	 * Calculate Bell metric
	 * @param documentVector
	 * @param word1
	 * @param word2
	 * @return
	 */
	
	private static double computeBell(Matrix documentVector, Matrix word1, Matrix word2) {
		
		Matrix w1;
		Matrix w2;
		if(word1.getColumnDimension()==1) {
			w1 = word1.transpose();
		}
		else {
			w1 = word1;
		}
		if(word2.getColumnDimension()==1) {
			w2 = word2.transpose();
		}
		else {
			w2 = word2;
		}
		Matrix wd1 = w1.arrayRightDivide(new Matrix(w1.getRowDimension(),w1.getColumnDimension(),w1.normF()));
		Matrix wd2 = w2.arrayRightDivide(new Matrix(w2.getRowDimension(),w2.getColumnDimension(),w2.normF()));
		documentVector = documentVector.arrayRightDivide(new Matrix(documentVector.getRowDimension(),documentVector.getColumnDimension(),documentVector.normF()));
		
		define_orths(wd1, wd2);		
		
		define_proj_coefs(documentVector, orths1.getMatrix(0, 0, 0, orths1.getColumnDimension()-1), orths1.getMatrix(0, 0, 0, orths1.getColumnDimension()-1));
		
	
		double p = wd1.times(wd2.transpose()).get(0, 0);
		
		Matrix M = new Matrix (new double[][]{{p,Math.sqrt(1-p*p)},{-Math.sqrt(1-p*p),p}});
		Matrix inM = M.inverse();
		Matrix A = new Matrix(new double[][] {{1,0},{0,-1}});
		Matrix Ax = new Matrix(new double[][] {{0,1},{1,0}});
		Matrix B = inM.times(A.times(M));
		Matrix Bx = inM.times(Ax.times(M));
		Matrix Bplus =  (B.plus(Bx)).times(-1/Math.sqrt(2));
		Matrix Bminus =  (B.minus(Bx)).times(1/Math.sqrt(2));
		
		Matrix ket_doc_in_word_1_proj = new Matrix(new double [] {proj_coef1, proj_coef2},2);
		Matrix  bra_doc_in_word_1_proj = new Matrix(new double [] {proj_coef1, proj_coef2},1);
		
		Matrix ABplus = A.times(Bplus);
		Matrix AxBplus = Ax.times(Bplus);
		Matrix ABminus = A.times(Bminus);
		Matrix AxBminus = Ax.times(Bminus);
		
		double res = Math.abs(bornRule(ABplus,bra_doc_in_word_1_proj,ket_doc_in_word_1_proj)+bornRule(AxBplus, bra_doc_in_word_1_proj, ket_doc_in_word_1_proj)) + Math.abs(bornRule(ABminus,bra_doc_in_word_1_proj,ket_doc_in_word_1_proj)-bornRule(AxBminus, bra_doc_in_word_1_proj, ket_doc_in_word_1_proj)) ;
		return res;
	}
	
	public static double solve(String text, String query, int window) {
		String[] splitQuery = query.split(" ");
		String wordProc1 = stemmer.stem(splitQuery[0]).toString();
		String wordProc2 = stemmer.stem(splitQuery[1]).toString();
		buidIndex(text);
		buildHalMatrix(window);
		Matrix documentVector = defineDocumentVector();
		Matrix word1Vector = null;
		Matrix word2Vector = null;
		try {
		
		word1Vector = new Matrix(hal[index.get(wordProc1)],1);}
		catch(Exception e) {
		word1Vector = new Matrix(hal[0].length,1);	
		}
		try {
		word2Vector = new Matrix(hal[index.get(wordProc2)],1);
		}
		catch(Exception e) {
			word2Vector = (new Matrix(1,hal[0].length));	
		}
	

		double ans = computeBell(documentVector,word1Vector, word2Vector);
		return ans;
	}
	
	
	public static void main (String[] args) throws java.lang.Exception
	{
				

	}

	
}
