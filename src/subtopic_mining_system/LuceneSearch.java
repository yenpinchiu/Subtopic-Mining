package subtopic_mining_system;


import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class LuceneSearch {
	
	 IndexSearcher searcher = null;
	 Query query = null;
	 IndexReader reader = null;
	 Analyzer analyzer = new IKAnalyzer();
	 
	 static List<String> stopwords = null;
	 
	 static Ranker ranker;

	 String key;
	 
	 public LuceneSearch(String src){
		    try{
		      
		      Directory index = FSDirectory.open(new File(src));
		      reader = IndexReader.open(index);
		      searcher = new  IndexSearcher(reader);
		      
		      ranker = new Ranker();
		        
		    }catch(Exception e){}

		  }
	 
	 public final ScoreDoc[] searcher(String keyword,int hit,String search_field) throws ParseException, IOException{
		 
		 key = keyword;
		 
		 int hitsPerPage = hit; 
		 TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true); 
		 
		 
		 System.out.println("正在檢索關鍵字 : "+keyword);
		 
		 BooleanClause.Occur[] flags = {BooleanClause.Occur.MUST, BooleanClause.Occur.MUST}; //表示多個條件之間的關係
		 String[] fields = {search_field, search_field};
		 String[] keys = null;
			
		 int whichQuery = 0;
			
		 for(int i = 0; i < keyword.length(); i++) {
				if(keyword.charAt(i) == ' ') {
					keys = keyword.split(" ");
					whichQuery = 1;
					break;
				}
		 }
		 
		 if(whichQuery == 0) query = new QueryParser(Version.LUCENE_36, search_field, analyzer).parse(keyword);
		 else query = MultiFieldQueryParser.parse(Version.LUCENE_36, keys, fields,flags,analyzer);
		 
		 Date start = new Date();
		 
		 searcher.search(query, collector);
		 ScoreDoc[] hits = collector.topDocs().scoreDocs;
		 
	     Date end = new Date();
	     System.out.println("檢索完成，用時"+(end.getTime()-start.getTime())+"毫杪");

	     return hits;
	  }
	 
	 public void printResult(ScoreDoc[] s){
		 
		 if(s.length == 0){
		      System.out.println("對不起！沒有您要找的資料!");
		    }
		 else{
		      for(int i = 0 ; i <s.length;i++)
		      {
		        try{
		        	int docId = s[i].doc;
		        	Document doc = searcher.doc(docId);
		            System.out.println("這是第"+(i+1)+"個檢索到的結果,檔案為 : "+doc.get("path"));

		            if(doc.get("DOCNO") != null) {
		                System.out.println("檔案ID : " + doc.get("DOCNO"));
		            }
		            
		            if(doc.get("title") != null) {
		                System.out.println("title : " + doc.get("title"));
		            }
		              
		        	
		        } catch(Exception e) {e.printStackTrace();}
		     }
		 }
		 System.out.println("---------------------------");
	 }
	 
	 public void save_terms_docs_to_ranker(ScoreDoc[] s,int threshold,int algorithm){
		
		 if(s.length != 0){
		      for(int i = 0 ; i <s.length;i++)
		      {
		        try{
		        	int docId = s[i].doc;
		        	Document doc = searcher.doc(docId);		            
		            if(!doc.get("title").trim().equals("") && doc.get("title") != null) {
		            	CAnalyzer(doc.get("title"),doc);
		            }
	
		        } catch(Exception e) {e.printStackTrace();}
		     }
		 }
		 if(algorithm != 2)ranker.sorted();
		 ranker.sorted2(threshold);
	 }
	 
	 public static void CAnalyzer(String str,Document doc) throws Exception{
		 Dictionary.loadExtendStopWords(stopwords);
		 
         Analyzer analyzer = new IKAnalyzer();
         Reader r = new StringReader(str);
         TokenStream ts = (TokenStream)analyzer.tokenStream("", r);       
         

         Collection<String> doc_terms = new ArrayList<String>();
         
         while (ts.incrementToken()) {    
             
        	 if(ts.getAttribute(CharTermAttribute.class).toString().length()>1){
        		 
        		 doc_terms.add(ts.getAttribute(CharTermAttribute.class).toString());
        		 ranker.link_doc_to_term(ts.getAttribute(CharTermAttribute.class).toString(), doc);
        	 }			 
         }

         ranker.count_tf_idf(doc_terms);
         
   }
	 
	 public void search(String keyword,int hit,String field,boolean print,int threshold,int algorithm) throws ParseException, IOException{
		 
		 ScoreDoc[] result =  searcher(keyword,hit,field);	 
		 if(print)printResult(result);
		 save_terms_docs_to_ranker(result,threshold,algorithm);
	 }
	 
	 public void refresh(){
		 
		 ranker = new Ranker();
	 }
	 
	 public Ranker return_ranker(){
		 
		 return ranker;
	 }
}
