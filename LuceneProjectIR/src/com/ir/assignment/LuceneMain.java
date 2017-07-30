/*
 * This program has been written as part of solution for Information Retrieval course 
 * programming assignment 1. Code has been developed by group of following 4 person
 * Rai Jitendra
 * Shivam Maurya
 * Gaurav Sharma
 * Mukul Salhotra
 * This is the main program where it first creates the indexes by running through
 * the directory and all text and html files present in it. After that it creates 
 * index on the content of the file. 
 */
package com.ir.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.commons.io.FileUtils;
import org.tartarus.snowball.ext.PorterStemmer;

public class LuceneMain {
	String indexDir; 
	String dataDir;
	Indexer indexer;
	Searcher searcher;
	
	public LuceneMain(){
		//initialize the data and index folders
		String filePath = new File("").getAbsolutePath();
		dataDir = filePath + "\\data";
		indexDir= filePath +"\\index";
	}
	
	public void SetIndexPath(String Path)
	{
		indexDir=Path+"\\index";
	}
	
	public String getIndexPath()
	{
		return indexDir;
	}
	
	public static void main(String[] args) throws ParseException {
		LuceneMain app;
		try {
	         app = new LuceneMain();
	         boolean indexed=false;
	         String choice="First";
	         InputStreamReader r=new InputStreamReader(System.in); 
	         app.SetIndexPath(args[0]);
	         BufferedReader br=new BufferedReader(r);
	         while(!indexed)
	         {
	         System.out.println("Do you want to refresh Indexes? Yes/No");
	         choice=br.readLine();
	         
	         if(choice.equalsIgnoreCase("Yes"))
	         {
	        
	        System.out.println("Index path:"+app.getIndexPath());
	        //Deletes all files and folders in this directory.
	        //FileUtils.cleanDirectory(new File(app.getIndexPath()));
	        purgeDirectory(new File(app.getIndexPath()));
	        app.createIndex(args[0]);
	        indexed=true;
	         }
	         else if(choice.equalsIgnoreCase("No"))
	         {
	        	 System.out.println("Using Existing Indexes");
	        	 indexed=true;
	         }
	         else
	         {
	        	 System.out.println("Invalid Input. Please provide correct input.");
	         }
	         
	         }
	         choice="First";  
		 	 
	         
	         while(!choice.equalsIgnoreCase("Exit"))
	         {
	        try{
	        	if(choice.equalsIgnoreCase("first"))
	        	{
	         System.out.println("Enter Search Query");
	        	}
	        	else
	        	{
	        		System.out.println("Enter Search Query or press exit");
	        	}
	         choice=br.readLine();
	         if(!choice.equalsIgnoreCase(""))
	         {
	         if(!choice.equalsIgnoreCase("exit"))
	         {
	        	 System.out.println("Searching Documents for query= "+choice);
	        	 app.search(choice+" "+Indexer.StringPorterStemmer(choice)); 
	         }
	         }
	         else
	         {
	        	 System.out.println("Search String empty. Please provide valid search query. ");
	         }
	        }
	        catch(IndexNotFoundException e)
	        {
	        	System.out.println("Index directory empty. Initializing indexing process.");
	        	app.createIndex(args[0]);
	        	app.search(choice+" "+Indexer.StringPorterStemmer(choice));
	        }
	         }
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	}

	private void search(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
	    long startTime = System.currentTimeMillis();
	    TopDocs hits = searcher.search(searchQuery);
	    long endTime = System.currentTimeMillis();
	    int rank=1;
	    System.out.println(hits.totalHits +
	         " documents found. Time :" + (endTime - startTime));
	    for(ScoreDoc scoreDoc : hits.scoreDocs) {
	        Document doc = searcher.getDocument(scoreDoc);
	        if((doc.get(LuceneConstants.FILE_NAME).contains(".html"))||(doc.get(LuceneConstants.FILE_NAME).contains(".htm")))
	        	System.out.println("Rank(Score):"+rank+"("+scoreDoc.score+") File Name(title):"+doc.get(LuceneConstants.FILE_NAME)+"("+doc.get(LuceneConstants.FILE_HTML_TITLE)+") File Path:" + doc.get(LuceneConstants.FILE_PATH)+"   Time Stamp:"+doc.get(LuceneConstants.FILE_TIMESTAMP));
	        else
	        	System.out.println("Rank(Score):"+rank+"("+scoreDoc.score+") File Name:"+doc.get(LuceneConstants.FILE_NAME)+" File Path:" + doc.get(LuceneConstants.FILE_PATH)+"   Time Stamp:"+doc.get(LuceneConstants.FILE_TIMESTAMP));
	        rank++;
	    }
	}
	static void purgeDirectory(File dir) {
		try{
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory()) purgeDirectory(file);
	        file.delete();
	    }
	    }
		catch(NullPointerException e)
		{
			System.out.println("Index directory empty. Continue");
		}
		
	}
	static public String QueryPorterStemmer(String QueryString)
	{
		PorterStemmer stemmer = new PorterStemmer();
		
			StringBuffer stemedLine = new StringBuffer();
			String[] words = QueryString.split(" ");
			for(int i=0; i<words.length;i++)
			{
				stemmer.setCurrent(words[i]);
				stemmer.stem();
		        stemedLine.append(stemmer.getCurrent()+" ");
			}
		return stemedLine.toString();
	}
	private void createIndex(String dataDirPath) throws IOException {
		
		indexer = new Indexer(indexDir);
	    int numIndexed;
	    long startTime = System.currentTimeMillis();	
	    numIndexed = indexer.createIndex(dataDirPath, new FileTypeFilter());
	    long endTime = System.currentTimeMillis();
	    indexer.close();
	    System.out.println(numIndexed+" File indexed, time taken: "
	         +(endTime-startTime)+" ms");
	}
}
