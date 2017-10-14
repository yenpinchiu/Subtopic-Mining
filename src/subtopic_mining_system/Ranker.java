package subtopic_mining_system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.lucene.document.Document;

public class Ranker {
	
	
	  private static int docsNum = 0; 
	  private static Map<String, Integer> wordDocs = new HashMap<String, Integer>();
	  private static Vector<Integer> termNumDoc = new Vector<Integer>();
	  private static Vector<Vector<TermInfo>> termVectors = new Vector<Vector<TermInfo>>(); 
	  private static Vector<String> allterm = new Vector<String>();
	  private static Map<String, Double> total_tf_idf = new HashMap<String, Double>();
	  private static Map<String, Double> high_tf_idf = new HashMap<String, Double>();
	  private static Map<String, Vector<Document>> Docs_of_term = new HashMap<String, Vector<Document>>();
	  List<Map.Entry<String, Double>> tf_idf_sorted_list;
	  List<Map.Entry<String, Double>> tf_idf_sorted_list2;
	  
	  public Ranker(){
		  
		  docsNum = 0; 
		  wordDocs = new HashMap<String, Integer>();
		  termNumDoc = new Vector<Integer>();
		  termVectors = new Vector<Vector<TermInfo>>(); 
		  allterm = new Vector<String>();
		  total_tf_idf = new HashMap<String, Double>();
		  high_tf_idf = new HashMap<String, Double>();
		  Docs_of_term = new HashMap<String, Vector<Document>>();
	  }
	  
	  public int save_term( Collection<String> terms, Vector<TermInfo> termArray) {
		  
		   Collection<String> words = terms;
		   Iterator<String> itr = words.iterator();
		   
		   String word = null;
		   TermInfo termInfo = null;
		   
		   int termMount = 0;
		   
		   while (itr.hasNext()) {
			   
			   word = itr.next();
		    
			   if (termArray.contains(word)) {
				   termInfo = termArray.get(termArray.indexOf(word));
				   termInfo.setMountPerDoc(termInfo.getMountPerDoc() + 1);
			   } else {
				   termInfo = new TermInfo();
				   termInfo.setMountPerDoc(1);
				   termInfo.setTermStr(word);
				   termInfo.setRawWeight(0.0);
				   termInfo.setWeight(0.0);
				   termArray.add(termInfo);
			   }
			   termMount ++ ;
		   }
		   return termMount;
		 }
	  
	  
	  	public void count_tf_idf(Collection<String> terms) {
		   Vector<TermInfo> termVector = null;

		    int docIdx = docsNum;
		    termNumDoc.add(0);
		    termVector = new Vector<TermInfo>();
		    termVectors.add(termVector);
		    docsNum++;

		   termVector = termVectors.elementAt(docIdx);
		   int termMount = save_term(terms, termVector);
		   termNumDoc.set(docIdx, termNumDoc.elementAt(docIdx).intValue() + termMount);

		   TermInfo termInfo = null;
		   String termStr = null;
		   Iterator<TermInfo> termInfoItr = termVector.iterator();
		   
		   Vector<String> check = new Vector<String>();
		   
		   
		   while (termInfoItr.hasNext()) {
		    termInfo = termInfoItr.next();
		    termStr = termInfo.getTermStr();
		    if ( !check.contains(termStr) && wordDocs.get(termStr) != null) {
		    	if(termStr.length()>1){
		    		wordDocs.put(termStr, wordDocs.get(termStr).intValue() + 1);
		    		check.add(termStr);
		    	}
		    } else if(!check.contains(termStr) && wordDocs.get(termStr) == null) {
		    	if(termStr.length()>1){
		    		wordDocs.put(termStr, 1);
			    	check.add(termStr);
		    		allterm.add(termStr); 
		    		total_tf_idf.put(termStr, 0.0);
		    	}   
		    }
		    
		    termInfo.setTf(termInfo.getMountPerDoc() / ((double)termNumDoc.elementAt(docIdx).intValue()));
		   }
		   Iterator<Vector<TermInfo>> docItr = termVectors.iterator();

		   double rwPSum = 0.0;
		   while (docItr.hasNext()) {
		    termVector = docItr.next();
		    termInfoItr = termVector.iterator();
		    rwPSum = 0.0;
		    while (termInfoItr.hasNext()) {
		     termInfo = termInfoItr.next();
		     termInfo.setRawWeight(termInfo.getTf() * Math.log(((double)docsNum) / wordDocs.get(termInfo.getTermStr()).intValue()));
		     rwPSum += termInfo.getRawWeight() * termInfo.getRawWeight();
		    }

		    termInfoItr = termVector.iterator();
		    while (termInfoItr.hasNext()) {
		     termInfo = termInfoItr.next();
		     termInfo.setWeight(termInfo.getRawWeight() / Math.sqrt(rwPSum));
		    }
		   }
		   
		  }
	  	
	  	
	  	
	  	public void link_doc_to_term(String term , Document doc){
	  		
	  		 if (Docs_of_term.get(term) == null) {
	  			Vector<Document> docs = new Vector<Document>();
	  			Docs_of_term.put(term,docs);  			 
	  		 }
	  		
	  		 if(!Docs_of_term.get(term).contains(doc)){
	  			 Docs_of_term.get(term).add(doc);
	  		 }
	  	}
	  	
	  	public Vector<Document> return_links_of_doc_to_term(String term){
	  				
	  		return Docs_of_term.get(term);
	  	}
	  	
	  	public  void  sorted(){
	  		
			  for(int i=0;i<termVectors.size();i++){
				  for(int j=0;j<termVectors.get(i).size();j++){			  
					  total_tf_idf.put( termVectors.get(i).get(j).getTermStr() , total_tf_idf.get(termVectors.get(i).get(j).getTermStr()) + termVectors.get(i).get(j).getWeight() );
				  }
			  }
 
			  List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(total_tf_idf.entrySet());
			  Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>()
					  {  
					      public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
					      {	
					    	  int flag;
					    	  if(o2.getValue() - o1.getValue()>0)flag = 1;
					    	  else if(o2.getValue() - o1.getValue()<0)flag = -1;
					    	  else flag =0;
					    	
					          return flag;
					      }
					  });
			  tf_idf_sorted_list = list_Data;
	  	}
	  	
	  	public  void  sorted2(int threshold){
	  		
	  		for(int i=0;i<termVectors.size();i++){
				  for(int j=0;j<termVectors.get(i).size();j++){		  
					  if( high_tf_idf.get(termVectors.get(i).get(j).getTermStr()) == null || termVectors.get(i).get(j).getWeight() > high_tf_idf.get(termVectors.get(i).get(j).getTermStr()) ){
						  
						  if( wordDocs.get(termVectors.get(i).get(j).getTermStr()) >= threshold )high_tf_idf.put( termVectors.get(i).get(j).getTermStr() ,termVectors.get(i).get(j).getWeight() );
						  else {high_tf_idf.put( termVectors.get(i).get(j).getTermStr(),0.0 );}
					  }
				  }
			  }
	  		
	  		List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(high_tf_idf.entrySet());
			  Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>()
					  {  
					      public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
					      {	
					    	  int flag;
					    	  if(o2.getValue() - o1.getValue()>0)flag = 1;
					    	  else if(o2.getValue() - o1.getValue()<0)flag = -1;
					    	  else flag =0;
					    	
					          return flag;
					      }
					  });
			  
			  tf_idf_sorted_list2 = list_Data;
		 }
	  	
	  	public void rmOverlap(int numOfSubtopic) {
	  		List<String> docno = new ArrayList<String>();
	  		String docnostr = null;
	  		boolean thesame = false;
	  		int run = numOfSubtopic;
	  		for(int i = 0; i < run; i++) {
	  			//System.out.println(tf_idf_sorted_list.get(i).getKey() + "  " + i + "  " + Docs_of_term.get(tf_idf_sorted_list.get(i).getKey()).size());
	  			docnostr = Docs_of_term.get(tf_idf_sorted_list.get(i).getKey()).get(0).get("DOCNO");
	  			//System.out.println(docnostr);
	  			thesame = false;
	  			for(int j = 0; j < docno.size(); j++) {
	  				if(docno.get(j).equals(docnostr)) { thesame = true; break; }
	  			}
	  			if(thesame) {
	  				tf_idf_sorted_list.remove(i);
	  				i--; //list.remove will cause size decrease immediately
	  			}
	  			else {
	  				docno.add(docnostr);
	  			}
	  			if(i == run-1 && docno.size() < numOfSubtopic) { run++; }
	  			if(run >= tf_idf_sorted_list.size()) break;
	  		}
	  	}
	  	
		public void rmOverlap2(int numOfSubtopic) {
	  		List<String> docno2 = new ArrayList<String>();
	  		String docnostr2 = null;
	  		boolean thesame2 = false;
	  		int run2 = numOfSubtopic;
	  		for(int i = 0; i < run2; i++) {
	  			//System.out.println(tf_idf_sorted_list.get(i).getKey() + "  " + i + "  " + Docs_of_term.get(tf_idf_sorted_list.get(i).getKey()).size());
	  			docnostr2 = Docs_of_term.get(tf_idf_sorted_list2.get(i).getKey()).get(0).get("DOCNO");
	  			//System.out.println(docnostr);
	  			thesame2 = false;
	  			for(int j = 0; j < docno2.size(); j++) {
	  				if(docno2.get(j).equals(docnostr2)) { thesame2 = true; break; }
	  			}
	  			if(thesame2) {
	  				tf_idf_sorted_list2.remove(i);
	  				i--; //list.remove will cause size decrease immediately
	  			}
	  			else {
	  				docno2.add(docnostr2);
	  			}
	  			if(i == run2-1 && docno2.size() < numOfSubtopic) { run2++; }
	  			if(run2 >= tf_idf_sorted_list2.size()) break;
	  		}
	  	}
	  	
}
