package mestrado.projeto.test.code;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mestrado.projeto.core.entities.FileAttributes;
import mestrado.projeto.core.entities.Word;
import mestrado.projeto.helpers.FileAttributesHelper;
import mestrado.projeto.helpers.JsonHelper;
import mestrado.projeto.helpers.elasticsearch.ElasticSearchHelper;

/**
 * Hello world!
 *
 */
public class App 
{

	static List<String> stopwords;
	
	public static void loadStopwords() throws IOException {
	    stopwords = Files.readAllLines(Paths.get("src/main/resources/english_stopwords.txt"));
	}
	
	static List<Word> stopwords2 = new ArrayList<Word>();
	public static void loadStopwords2() throws IOException {
	    List<String> list = Files.readAllLines(Paths.get("src/main/resources/english_stopwords.txt"));
	    
	    for (String string : list) {
	    	stopwords2.add(new Word(string));
		}
	}
	
    public static void main( String[] args ) throws Exception
    {
    	//TESTI}NG SEN DATA TO ELASTICSEARCH
    	ElasticSearchHelper helper = new ElasticSearchHelper(true); 
    	
    	String jsonString = "{" +
    	        "\"user\":\"kimchy\"," +
    	        "\"postDate\":\"2013-01-30\"," +
    	        "\"message\":\"trying out Elasticsearch\"" +
    	        "}";
    	
    	helper.put("java-index", jsonString, 1);
    	
    	
    	//TESTING STOP WORDS
        /*System.out.println( "Hello World!" );
        
        List<Word> original2 = new ArrayList<Word>();
        original2.add(new Word("i"));
        original2.add(new Word("am"));
        original2.add(new Word("fat"));
        original2.add(new Word("me"));
        original2.add(new Word("guy"));
        
        loadStopwords2();
        
        original2.removeAll(stopwords2);
        
        System.out.println("New result: " + original2.size());
        for (Word word : original2) {
			System.out.println("Word: " + word.getWord());
		}*/
        
    }
    
    public static void testFileAttributes() throws IOException{
    	
    	String filePath = "C:\\Google Drive\\Mestrado - Ciência da Computação\\ProjetoOficial\\indir\\test.wav";
    	File file = new File(filePath);
    	FileAttributes fa = FileAttributesHelper.getFileAttributes(file);
    	
    	System.out.println(JsonHelper.convertToJson(fa));
    }
    
    public static void testJson(){
    	FileAttributes f = new FileAttributes(); 
    	
    	f.setFileName("file name test");
    	f.setFilePath("C://");
    	f.setCreationTime("Creation time");
    	f.setOwner("michel");
    	f.setSize(500);
    	
    	String json = JsonHelper.convertToJson(f);
    	
    	System.out.println(json);
    	
    	FileAttributes f2 = JsonHelper.convertFromJson(FileAttributes.class, json);
    	System.out.println("Owner: " + f2.getOwner());
    }
    
    public static void getFileAttributes() throws IOException{
    	
    	//To get attributes from video and audio files
    	//https://github.com/javacreed/how-to-retrieve-the-video-properties
    	
    	String filePath = "C:\\Google Drive\\Mestrado - Ciência da Computação\\ProjetoOficial\\indir\\";
    	
    	String fileName = "";
    	String fileAbsolutePath = "";
    	String fileCreationTime = "";
    	Long fileSize = null;
    	String fileOwner = "";
    	
    	File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	
		    	fileName = file.getName();
		    	fileAbsolutePath = file.getAbsolutePath();
		    	fileSize = file.length();
		    	
		    	//Read file info
		        Path path = Paths.get(file.getAbsolutePath());
		        
		        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);		        
		        fileCreationTime = attrs.creationTime().toString();		        
		        fileOwner = Files.getOwner(path).getName();
		        
		    }
		}

    	
    }
}
