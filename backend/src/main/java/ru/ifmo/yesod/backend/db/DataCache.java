package ru.ifmo.yesod.backend.db;

import java.sql.Connection;   
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataCache {
	
	//private static Connection connection = null;	
	private static String connectionHeader = "jdbc:sqlite:";
	private static String connectionString;
	final private static String driverName = "org.sqlite.JDBC";
	
	public DataCache(String baseName) {
        connectionString = connectionHeader + baseName;
		try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't get class. No driver found");
            e.printStackTrace();
            return;
        }	
	}
	
	/**
	 * Function, that connect instance to SQLite base 
	 * 
	 */
	private static Connection openConnect() {
		try {
			Connection connection = DriverManager.getConnection(connectionString);
			return connection;
		} catch (SQLException e) {
            System.out.println("Can't get connection. Incorrect URL");
            e.printStackTrace();
            return null;
        } 
	}
	
	
	/**
	 * Function, that disconnect instance
	 * @param connection - instance for disconnection
	 */
	private static void closeConnect(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Can't close connection");
            e.printStackTrace();
            return;
        }
	}
	
	/**
	 * Function for cache index in base in table with name -"idx"
	 * @param words - array of unique word from text space
	 */
	public void insertIndexData(String[] words) {
		Connection connect = openConnect();
		String sqlQuery = "CREATE TABLE IF NOT EXISTS idx (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " word text NOT NULL\n"    
                + ");";	
		try {
			Statement statement = connect.createStatement();
		    statement.execute(sqlQuery);  
		} catch (SQLException e) 
		{  
		 	System.out.println(e.getMessage());  
		}		
		sqlQuery = "INSERT INTO idx (word) VALUES(?)";
        try{  
        	PreparedStatement pstmt = connect.prepareStatement(sqlQuery);
        	for (int i = 0; i < words.length; i++) {            	  
            	pstmt.setString(1,words[i]);
            	pstmt.executeUpdate();
            	
            }
            
            System.out.println("ok");
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
        closeConnect(connect);        
	}
	
	/**
	 * Function for getting words index from base
	 * @return List of words
	 */
	public List<String> getAllIndexData() {
		Connection connect = openConnect();
		String sqlQuery = "SELECT * FROM idx";
		List<String> idx = new ArrayList<>(); 
		try {
        	Statement stmt  = connect.createStatement();  
        	ResultSet rs    = stmt.executeQuery(sqlQuery);  
        	while (rs.next()) {  
        		idx.add(rs.getString("word"));
        	}  
       } catch (SQLException e) {             
    	   System.out.println(e.getMessage());  
       }
        closeConnect(connect);
        return idx;
	}
	
	/**
	 * Function for cache tf data of all word from text space in table named "tf".
	 * This table has form (n*m), where n - number of words, m - number of docs
	 * @param tf
	 */
	public void insertTfData( List<List<Double>> tf) {
		Connection connect = openConnect();
		String sqlQuery = "CREATE TABLE IF NOT EXISTS tf (\n"  
                + " id integer PRIMARY KEY";  
                //+ " word text NOT NULL";    
               // + ");";
		for(int i = 0; i< tf.get(0).size(); i++) {
			sqlQuery+=",\n "+"n"+i+" real NOT NULL";
		}
		sqlQuery += ");";
		
		try {
			Statement statement = connect.createStatement();
		    statement.execute(sqlQuery);  
		} catch (SQLException e) 
		{  
		 	System.out.println(e.getMessage());  
		}
		System.out.println("Create tf");
		String sqlQueryPart1 = "INSERT INTO tf (n0";
		String sqlQueryPart2 = " VALUES(?";
		for(int i = 1; i<tf.get(0).size(); i++) {
			sqlQueryPart1 +=", " +"n" + i;
			sqlQueryPart2 +=", ?";
		}
		sqlQueryPart1 +=")";
		sqlQueryPart2 +=")";
		
		sqlQuery = sqlQueryPart1 + sqlQueryPart2;
		try{  
        	PreparedStatement pstmt = connect.prepareStatement(sqlQuery);
        	int counter = 0;
        	for (List<Double> i: tf) {            	  
            	counter = 0;
        		for(double j: i) {
            		pstmt.setDouble(counter+1,j);
            		counter++;
            	}
        		pstmt.executeUpdate();
        	}
        	System.out.println("tf table add");	
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
        closeConnect(connect);        
	}
	
	/**
	 * Function for cache idf data of all words from index
	 * @param idf
	 */
	public void insertIdfData(List<Double> idf) {
		Connection connect = openConnect();
		String sqlQuery = "CREATE TABLE IF NOT EXISTS idf (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " idf real NOT NULL\n"    
                + ");";	
		try {
			Statement statement = connect.createStatement();
		    statement.execute(sqlQuery);  
		} catch (SQLException e) 
		{  
		 	System.out.println(e.getMessage());  
		}		
		sqlQuery = "INSERT INTO idf (idf) VALUES(?)";
        try{  
        	PreparedStatement pstmt = connect.prepareStatement(sqlQuery);
        	for (int i = 0; i < idf.size(); i++) {            	  
            	pstmt.setDouble(1,idf.get(i));
            	pstmt.executeUpdate();
            	
            }
            
            System.out.println("IDF added");
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
        closeConnect(connect);     
	}
	
	/**
	 * Function, that save all id of documents in elastic base
	 * @param id
	 */
	public void insertIdDocData(List<String> id) {
		Connection connect = openConnect();
		String sqlQuery = "CREATE TABLE IF NOT EXISTS docsId (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " docId String NOT NULL\n"    
                + ");";	
		try {
			Statement statement = connect.createStatement();
		    statement.execute(sqlQuery);  
		} catch (SQLException e) 
		{  
		 	System.out.println(e.getMessage());  
		}		
		sqlQuery = "INSERT INTO docsId (docId) VALUES(?)";
        try{  
        	PreparedStatement pstmt = connect.prepareStatement(sqlQuery);
        	for (int i = 0; i < id.size(); i++) {            	  
            	pstmt.setString(1,id.get(i));
            	pstmt.executeUpdate();
            	
            }
            
            System.out.println("id Added");
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
        closeConnect(connect);     
	}
	
	
	//this need use in the future for use cache hal matrixes or upgrade this idea 
	public void inserthalData(double[][] hal, int id) {
		Connection connect = openConnect();
		String sqlQuery = "CREATE TABLE IF NOT EXISTS hal"+id + " (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " hal real NOT NULL\n"    
                + ");";	
		try {
			Statement statement = connect.createStatement();
		    statement.execute(sqlQuery);  
		} catch (SQLException e) 
		{  
		 	System.out.println(e.getMessage());  
		}		
		sqlQuery = "INSERT INTO hal"+id+" (hal) VALUES(?)";
        try{  
        	PreparedStatement pstmt = connect.prepareStatement(sqlQuery);
        	pstmt.setDouble(1,hal.length);
        	pstmt.executeUpdate();
        	pstmt.setDouble(1,hal[0].length);
        	pstmt.executeUpdate();
        	for (int i = 0; i < hal.length; i++) {            	  
        		System.out.println("hal String " +i);
        		for(int j= 0; j<hal[0].length;j++) {
            		pstmt.setDouble(1,hal[i][j]);
                	pstmt.executeUpdate();
            	}     		            
            }
            
            System.out.println("hal Added");
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
        closeConnect(connect);     
	}
	
	
	
	/**
	 * Function for getting document id for matching this base and document from elastic base
	 * @param id
	 * @return
	 */
	public int getDocIndById(String id) {
		Connection connect = openConnect();
		String sqlQuery = "SELECT id FROM docsId WHERE docId = '"+id+"'";		
		int docId = 0; 
		try {
        	Statement stmt  = connect.createStatement();  
        	ResultSet rs    = stmt.executeQuery(sqlQuery);  
        	docId = rs.getInt("id") ;
        	      	
       } catch (SQLException e) {             
    	   System.out.println(e.getMessage());  
       }
        closeConnect(connect);
        return docId;
	}
	
	/**
	 * Function for getting word tf value, defined for a specific document
	 * @param wordId
	 * @param doc
	 * @return
	 */
	public double getWordTf(int wordId, int doc) {
		Connection connect = openConnect();
		String sqlQuery = "SELECT id, n"+doc+" FROM tf WHERE id == "+(wordId+1);		 
		double tf = 0.0;
		try {
        	Statement stmt  = connect.createStatement();  
        	ResultSet rs    = stmt.executeQuery(sqlQuery);  
        	tf = rs.getDouble("n"+doc);       	      	
       } catch (SQLException e) {             
    	   System.out.println(e.getMessage());  
       }
        closeConnect(connect);
        return tf;
	}
	/**
	 * function for getting idf data
	 * @return
	 */
	public List<Double> getAllIdfData() {
		Connection connect = openConnect();
		String sqlQuery = "SELECT * FROM idf";
		List<Double> idf = new ArrayList<>(); 
		try {
        	Statement stmt  = connect.createStatement();  
        	ResultSet rs    = stmt.executeQuery(sqlQuery);  
        	while (rs.next()) {  
        		idf.add(rs.getDouble("idf"));
        	}  
       } catch (SQLException e) {             
    	   System.out.println(e.getMessage());  
       }
        closeConnect(connect);
        return idf;
	}
	
	//this function not use, and it have error
	public List<List<Double>> getAllTfData() {
		Connection connect = openConnect();
		String sqlQuery = "SELECT * FROM tf";
		List<List<Double>> tf = new ArrayList<>(); 
		List<Double> tmp = new ArrayList<>();
		try {
        	Statement stmt  = connect.createStatement();  
        	ResultSet rs    = stmt.executeQuery(sqlQuery);          	
        	while (rs.next()) {  
        		rs.toString();
        		tmp.clear();
        		for(int i =0; i<424;i++) {
        			tmp.add(rs.getDouble("n"+i));
        			
        		}
        		tf.add(tmp);
        	}
       } catch (SQLException e) {             
    	   System.out.println(e.getMessage());  
       }
        closeConnect(connect);
        return tf;
	}
	/**
	 * Function for getting array of doc IDs for elastic base
	 * @return
	 */
	public List<String> getAllDocIdData() {
		Connection connect = openConnect();
		String sqlQuery = "SELECT * FROM docsId";
		List<String> docIds = new ArrayList<>(); 
		try {
        	Statement stmt  = connect.createStatement();  
        	ResultSet rs    = stmt.executeQuery(sqlQuery);  
        	while (rs.next()) {  
        		docIds.add(rs.getString("docId"));
        	}  
       } catch (SQLException e) {             
    	   System.out.println(e.getMessage());  
       }
        closeConnect(connect);
        return docIds;
	}
	
	


}
	
