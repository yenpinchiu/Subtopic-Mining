package subtopic_mining_system;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneIndex {
	
	
	private IndexWriter writer = null ;
	static List<String> stopwords = null;
	
	public LuceneIndex(){
		
	    try{
	    	
	    	Directory index = FSDirectory.open(new File("index2"));	
	    	
	        Analyzer analyzer = new IKAnalyzer();      
	        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
	       
	        writer = new IndexWriter(index, config);
	        
	        /* load stopwords.txt */
		 	 File f = new File("dict/stopwords.txt");
		 	 FileInputStream fr = new FileInputStream(f);
			 BufferedReader bfr = new BufferedReader( new InputStreamReader(fr,"UTF-8"));
			 String stoptmp = null;
			 stopwords = new ArrayList<String>();
				 
			 while((stoptmp = bfr.readLine()) != null){
				 stopwords.add(stoptmp);
			 }
			 fr.close();
	    }
	    catch(Exception e){}
	  }
	
	
	public String reFormat(String content) {
        content = content.replaceAll("(?is)\\s\\s", " ");
        content = content.replaceAll("(?is)</?br>", "\n");
        content = content.replaceAll("(?is)</?p>", "\n");
        content = content.replaceAll("(?is)&nbsp;", "");
        content = content.replaceAll("(?is)</?[a-z][a-z0-9]*[^<>]*>", "");
        content = content.replaceAll("<!–/?.*–>", "");
        content = content.replaceAll("　", "");
        content = content.replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
        content = content.trim();
        return content;
	}
	
	private Document getDocument(File f)throws IOException{
		
		Dictionary.loadExtendStopWords(stopwords);
		
	    Document doc = new Document();

	    FileInputStream is = new FileInputStream(f);
	    BufferedReader reader = new BufferedReader( new InputStreamReader(is,"UTF-8"));
	    
	    String str = "", writestr = "";
	    String docno;
	    int flag = 0;
	    String title = "";
	    int title_start = 0; 
	    int title_flag = 0;
	    
	    while((str = reader.readLine()) != null) {

	    	if(str.indexOf("<DOCNO>") != -1) {
	            docno = str;
	            docno = docno.replaceAll("<DOCNO>", "");
	            docno = docno.replaceAll("</DOCNO>", "");
	            Field field = new Field("DOCNO",docno, Field.Store.YES,Field.Index.ANALYZED);
	            doc.add(field);
	    	}
	    	
	    	
	    	if(title_flag == 1) {
                for(int i = 0; i < str.length(); i++) {
                        if(i+1 < str.length() && i+2 < str.length()) {
                                if(str.charAt(i) == '<' && str.charAt(i+1) == '/' && (str.charAt(i+2) == 't' || str.charAt(i+2) == 'T')) {
                                        title_flag = 2;
                                        break;
                                }
                        }
                        title += str.charAt(i);
                }
	    	}

	    	if((title_start = str.indexOf("<title>")) != -1 || (title_start = str.indexOf("<TITLE>")) != -1) {
                if(title_flag != 2) {
                        title_flag = 1;
                        for(int i = title_start+7; i < str.length(); i++) {
                            if(i+1 < str.length() && i+2 < str.length()) {
                                    if(str.charAt(i) == '<' && str.charAt(i+1) == '/' && (str.charAt(i+2) == 't' || str.charAt(i+2) == 'T')) {
                                            title_flag = 2;
                                            break;
                                    }
                            }
                            title += str.charAt(i);
                        }
                }
	    	}
	    	
	    	
	    	if (str.indexOf("<SCRIPT") != -1) { flag = 1; }
            else if (str.indexOf("<script") != -1) { flag = 1; }
            if (str.indexOf("</script>") != -1) { flag = 0; }
            else if (str.indexOf("</SCRIPT>") != -1) { flag = 0; }
            if (str.indexOf("<STYLE") != -1) { flag = 1; }
            else if (str.indexOf("<style") != -1) { flag = 1; }
            if (str.indexOf("</style>") != -1) { flag = 0; }
            else if (str.indexOf("</STYLE>") != -1) { flag = 0; }
            if (str.indexOf("<A") != -1) { flag = 1; }
            else if (str.indexOf("<a") != -1) { flag = 1; }
            if (str.indexOf("</a>") != -1) { flag = 0; }
            else if (str.indexOf("</A>") != -1) { flag = 0; }
            if (flag == 0){writestr = writestr + reFormat(str) + "\n";}
                    
	    }

	    writestr = reFormat(writestr);
	    title = reFormat(title);
	    
	    if(title_flag == 0) { title = "Title Not Found"; }
        Field tfield = new Field("title", title, Field.Store.YES,Field.Index.ANALYZED);
        doc.add(tfield);
	    
	    Field field = new Field("contents",writestr, Field.Store.YES,Field.Index.ANALYZED);
	    doc.add(field);
	    
	    doc.add(new Field("path", f.getPath(),Field.Store.YES,Field.Index.ANALYZED));
	    return doc;

	  }

	  
	  public void writeToIndex(String src)throws IOException{

		File folder = new File(src);
	    if(folder.isDirectory())
	    {
	      String[] files = folder.list();
	      for(int i = 0 ; i <files.length ; i++)
	      {
	        File file = new File(folder,files[i]);
	      if(file.getName().indexOf(".new.utf8.txt") != -1){
	        	Document doc = getDocument(file);
	        	System.out.println("正在建立索引 : "+file+" ");
	        	writer.addDocument(doc);
	        }
	      }
	    }
	  }

	  
	  public void close()throws IOException{
	    writer.close();
	  }
	  
	  public static void main(String args[])throws IOException
	  {

	    LuceneIndex indexer = new LuceneIndex();
	    Date start = new Date();
	    indexer.writeToIndex("../b97064/data");
	    Date end = new Date();
	    System.out.println("建立索引用時"+(end.getTime()-start.getTime())+"毫杪");
	    indexer.close();
	  }
}
