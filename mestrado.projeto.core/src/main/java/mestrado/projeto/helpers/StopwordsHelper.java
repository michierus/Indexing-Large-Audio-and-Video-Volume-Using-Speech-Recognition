package mestrado.projeto.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import mestrado.projeto.core.entities.Word;

public class StopwordsHelper {
	
	protected static ArrayList<Word> stopwords = new ArrayList<Word>();
	
	protected static void loadStopwords() throws IOException {
		if(stopwords != null && stopwords.isEmpty()){
			
			//ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			ClassLoader classloader = StopwordsHelper.class.getClassLoader();			
			try (InputStream inputStream = classloader.getResourceAsStream("english_stopwords.txt");
				    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				
				String line;
		        while ((line = reader.readLine()) != null) {		            
		            stopwords.add(new Word(line));
		        }
			}
				 
				
								
				
		    /*List<String> list = Files.readAllLines(Paths.get("src/main/resources/english_stopwords.txt"));		    
		    
		    for (String string : list) {
		    	stopwords.add(new Word(string));
			}*/
		}
	}
	
	public static ArrayList<Word> removeStopWords(ArrayList<Word> words) throws IOException{
		loadStopwords();
		words.removeAll(stopwords);
		return words;
	}

}
