package subtopic_mining_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class tools {
	
	
	static Vector<String> read_input(String src,int start,int end) throws IOException{
		
		File file = new File(src);
		FileInputStream is = new FileInputStream(file);
		BufferedReader reader = new BufferedReader( new InputStreamReader(is,"UTF-8"));
		
		String str = "";
		str = reader.readLine();
		
		for(int i=1;i<start;i++){
			str = reader.readLine();
		}
		
		Vector<String> queries = new Vector<String>();
		
		for(int i= 0; i< (end-start+1);i++){
			str = reader.readLine();
			queries.add(str);
		}
		
		return queries;		
	}
	
	
	static void write_result(OutputStreamWriter bfw,LuceneSearch searcher,int num_of_suptopic,int num_of_page,int algorithm,Vector<String> record) throws IOException{
		 	
		List<Map.Entry<String, Double>> tf_idf_sorted_list = null;
		if(algorithm == 1) tf_idf_sorted_list= searcher.return_ranker().tf_idf_sorted_list;
		else if(algorithm == 2) tf_idf_sorted_list= searcher.return_ranker().tf_idf_sorted_list2;

			
		Ranker ranker = searcher.return_ranker();
		 
		 if( tf_idf_sorted_list.size() == 0 || num_of_suptopic == 0) {return;}

		 
		 if(algorithm == 1)ranker.rmOverlap(num_of_suptopic);
		 ranker.rmOverlap2(num_of_suptopic);
		 
		 
		 
		 for(int i=0;i<num_of_suptopic;i++){
			 
			 if( !record.contains(tf_idf_sorted_list.get(i).getKey()) ){
			 
			 if(i<tf_idf_sorted_list.size()){
				 String[] keys = null;
				 int whichKey = 0;
				 for(int k = 0; k < searcher.key.length(); k++) {
					 if(searcher.key.charAt(k) == ' ') { keys = searcher.key.split(" "); whichKey = 1; break; }
				 }
				 if(whichKey == 0) bfw.write(searcher.key + tf_idf_sorted_list.get(i).getKey());
				 else bfw.write(keys[0] + keys[1] + tf_idf_sorted_list.get(i).getKey());
				 bfw.write("\r\n");
				 
				 record.add(tf_idf_sorted_list.get(i).getKey());
				 
			 for(int j=0;j<num_of_page;j++){
				 
				 if(j<ranker.return_links_of_doc_to_term(tf_idf_sorted_list.get(i).getKey()).size()){
					 bfw.write(ranker.return_links_of_doc_to_term(tf_idf_sorted_list.get(i).getKey()).get(j).get("DOCNO"));
					 bfw.write("\r\n");

					 String contents = ranker.return_links_of_doc_to_term(tf_idf_sorted_list.get(i).getKey()).get(j).get("contents");
				 
					 if(whichKey == 0) bfw.write(contents_prune(contents, searcher.key));
					 else bfw.write(contents_prune2(contents, keys));
					 bfw.write("\r\n");
				 }
			 }
			 }
		 }
		 }
	}
	
	static boolean isPunc(char c) {
		char[] punctuation = {'(',')','/','=','%',':','<','>','!','\'','\"','_',';','{','}','+','#','^','|','[',']','$'};
		for(int i = 0; i < punctuation.length; i++) {
			if(c == punctuation[i]) return true;
		}
		return false;
	}
	
	static String contents_prune(String source, String key) {
		String[] strarr = source.split("\n");
		String result = "", output = "";
        int max_len = 0;
        char c;
      
        for(int a = 0; a < strarr.length; a++) {
                 if(strarr[a].indexOf(key) != -1) {
                         if(strarr[a].length() > 150) {
                                 int idx = strarr[a].indexOf(key);
                                 String combine = "";
                                 for(int b = idx; b < strarr[a].length() && b < idx+150; b++) {
                               	  	c = strarr[a].charAt(b);
                               	  	if(!isPunc(c))
                                         combine += strarr[a].charAt(b);
                                 }
                                 combine = combine.replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                      
                                 //System.out.println(result_cnt++ + combine);
                                 result = combine;
                         }
                         else { 
                       	  	//System.out.println(result_cnt++ + strarr[a].trim());
                       	  	result = strarr[a].trim().replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                       	  	String tmp = "";
                       	  	for(int aa = 0; aa < result.length(); aa++) {
                       	  		c = result.charAt(aa);
                       	  		if(!isPunc(c))
                       	  			tmp += c;
                       	  	}
                       	  	result = tmp;
                         }
                         if(result.length() > max_len) {
                       	  	max_len = result.length();
                       	  	output = result;
                         }
                 }
         }
        result = "";
        for(int i = 0; i < output.length(); i++) {
        	if(output.charAt(i) != ' ' && output.charAt(i) != '¡@') 
        		result += output.charAt(i);
        }
        return result;
	}
	
	static String contents_prune2(String source, String[] key) {
		String[] strarr = source.split("\n");
		String result = "", output = "";
        int max_len = 0;
        char c;
      
        for(int a = 0; a < strarr.length; a++) {
                 if(strarr[a].indexOf(key[0]) != -1 && strarr[a].indexOf(key[1]) != -1) {
                         if(strarr[a].length() > 150) {
                                 int idx = Math.min(strarr[a].indexOf(key[0]), strarr[a].indexOf(key[1]));
                                 String combine = "";
                                 for(int b = idx; b < strarr[a].length() && b < idx+150; b++) {
                               	  	c = strarr[a].charAt(b);
                               	  	if(!isPunc(c))
                                         combine += strarr[a].charAt(b);
                                 }
                                 combine = combine.replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                      
                                 //System.out.println(result_cnt++ + combine);
                                 result = combine;
                         }
                         else { 
                       	  	//System.out.println(result_cnt++ + strarr[a].trim());
                       	  	result = strarr[a].trim().replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                       	  	String tmp = "";
                       	  	for(int aa = 0; aa < result.length(); aa++) {
                       	  		c = result.charAt(aa);
                       	  		if(!isPunc(c))
                       	  			tmp += c;
                       	  	}
                       	  	result = tmp;
                         }
                         if(result.length() > max_len) {
                       	  	max_len = result.length();
                       	  	output = result;
                         }
                 }
         }
        result = "";
        for(int i = 0; i < output.length(); i++) {
        	if(output.charAt(i) != ' ' && output.charAt(i) != '¡@') 
        		result += output.charAt(i);
        }
        /******************* Not Found key[0] and key[1] ********************/
        if(result.length() == 0) {
        	String result1 = null, result2 = null;
        	max_len = 0;
        	for(int a = 0; a < strarr.length; a++) {
                if(strarr[a].indexOf(key[0]) != -1) {
                        if(strarr[a].length() > 150) {
                                int idx = strarr[a].indexOf(key[0]);
                                String combine = "";
                                for(int b = idx; b < strarr[a].length() && b < idx+150; b++) {
                              	  	c = strarr[a].charAt(b);
                              	  	if(!isPunc(c))
                                        combine += strarr[a].charAt(b);
                                }
                                combine = combine.replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                     
                                //System.out.println(result_cnt++ + combine);
                                result1 = combine;
                        }
                        else { 
                      	  	//System.out.println(result_cnt++ + strarr[a].trim());
                      	  	result1 = strarr[a].trim().replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                      	  	String tmp = "";
                      	  	for(int aa = 0; aa < result1.length(); aa++) {
                      	  		c = result1.charAt(aa);
                      	  		if(!isPunc(c))
                      	  			tmp += c;
                      	  	}
                      	  	result1 = tmp;
                        }
                        if(result1.length() > max_len) {
                      	  	max_len = result1.length();
                      	  	output = result1;
                        }
                }
        	}
        	result1 = "";
        	for(int i = 0; i < output.length(); i++) {
        		if(output.charAt(i) != ' ' && output.charAt(i) != '¡@') 
        			result1 += output.charAt(i);
       		}
        	/****************************************************************/
        	max_len = 0;
        	for(int a = 0; a < strarr.length; a++) {
                if(strarr[a].indexOf(key[1]) != -1) {
                        if(strarr[a].length() > 150) {
                                int idx = strarr[a].indexOf(key[1]);
                                String combine = "";
                                for(int b = idx; b < strarr[a].length() && b < idx+150; b++) {
                              	  	c = strarr[a].charAt(b);
                              	  	if(!isPunc(c))
                                        combine += strarr[a].charAt(b);
                                }
                                combine = combine.replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                     
                                //System.out.println(result_cnt++ + combine);
                                result2 = combine;
                        }
                        else { 
                      	  	//System.out.println(result_cnt++ + strarr[a].trim());
                      	  	result2 = strarr[a].trim().replaceAll("[a-zA-Z0-9.a-zA-Z0-9]*[,@-]*", "");
                      	  	String tmp = "";
                      	  	for(int aa = 0; aa < result2.length(); aa++) {
                      	  		c = result2.charAt(aa);
                      	  		if(!isPunc(c))
                      	  			tmp += c;
                      	  	}
                      	  	result2 = tmp;
                        }
                        if(result2.length() > max_len) {
                      	  	max_len = result2.length();
                      	  	output = result2;
                        }
                }
        	}
        	result2 = "";
        	for(int i = 0; i < output.length(); i++) {
        		if(output.charAt(i) != ' ' && output.charAt(i) != '¡@') 
        			result2 += output.charAt(i);
       		}
        	
        	result = result1 + " ";
        	for(int i = 0; i < result2.length() && result.length() < 150; i++)
        		result += result2.charAt(i);
        	System.out.println(result1 + " " + result2);
        }
        return result;
	}
	
	
}
