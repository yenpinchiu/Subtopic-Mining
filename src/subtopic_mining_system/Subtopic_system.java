package subtopic_mining_system;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;
import org.apache.lucene.queryParser.ParseException;


public class Subtopic_system {
	
	
	public static void main(String args[]) throws ParseException, IOException{
		 
		
		//java -jar Subtopic_system.jar -index -input -output -start -end  
		
		 String index = args[0];
		 String input = args[1];
		 String output = args[2];
		 int start = Integer.valueOf(args[3]);
		 int end = Integer.valueOf(args[4]);
		 int hits = Integer.valueOf(args[5]);
		 
		 //String index = "R://index";
		 //String input = "R://test.in.txt";
		 //String output = "R://test.out.txt";
		 //int start = 1;
		 //int end = 1;
		 //int hits = 10000;
		 
		 LuceneSearch searcher = new LuceneSearch(index);	 
		 Vector<String> queries = tools.read_input(input,start,end);
		 FileOutputStream fw = new FileOutputStream(output);
		 	  	 
		 OutputStreamWriter bfw = new OutputStreamWriter(fw, "UTF8");
		 
		 Vector<String> record = new Vector<String>();
		 
		 for(int i =start;i<(queries.size()+start);i++){
			 
			 
			 bfw.write(""+i);bfw.write("\r\n");
			 bfw.write(""+10);bfw.write("\r\n");
			 
			 searcher.search(queries.get(i-start),hits,"title",true,2,1);
			 tools.write_result(bfw,searcher,15, 1 ,1,record);
			 tools.write_result(bfw,searcher,15, 1 ,2,record);
			 searcher.refresh();
			 
			 searcher.search(queries.get(i-start),hits,"contents",true,2,1);
			 tools.write_result(bfw,searcher,15, 1 ,1,record);
			 tools.write_result(bfw,searcher,15, 1 ,2,record);
			 searcher.refresh();
			 
			 searcher.search(queries.get(i-start),hits,"title",true,6,2);
			 tools.write_result(bfw,searcher,15, 1 ,2,record);
			 searcher.refresh();
	 
			 //searcher.search(queries.get(i-start),hits,"contents",true,6,2);
			 //tools.write_result(bfw,searcher,15, 1 ,2,record);
			 //searcher.refresh();

			 
			 searcher.search(queries.get(i-start),hits,"title",true,11,2);
			 tools.write_result(bfw,searcher,15, 1 ,2,record);
			 searcher.refresh();
			 
			 //searcher.search(queries.get(i-start),hits,"contents",true,11,2);
			 //tools.write_result(bfw,searcher,15, 1 ,2,record);
			 //searcher.refresh();
			 
			 record.clear();
		 }
		 
		 bfw.flush();
		 fw.close();

	  }
	
	
}
