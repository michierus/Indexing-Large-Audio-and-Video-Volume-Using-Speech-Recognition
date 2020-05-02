package mestrado.projeto.core.entities;

import java.util.ArrayList;
import java.util.Optional;

public class FileProcessed {

	private FileAttributes fileAttributes;
	private ArrayList<Word> words= new ArrayList<Word>();
	
	public FileAttributes getFileAttributes() {
		return fileAttributes;
	}
	public void setFileAttributes(FileAttributes fileAttributes) {
		this.fileAttributes = fileAttributes;
	}
	
	public ArrayList<Word> getWords() {
		return words;
	}
	public void setWords(ArrayList<Word> words) {
		this.words = words;
	}
	public void addWord(Word word){
		this.words.add(word);
	}
	/*public void addWord(String word, String begin, String end){
		//this.addWord(new Word(word, begin, end));		
	}*/
	
	//This version group by word and use only the begin time where the word is said./
	public void addWord(String word, String occurrence){
		
		Word aux = words.stream().filter(p -> p.getWord().equalsIgnoreCase(word)).findAny().orElse(null);
		
		if(aux == null){
			ArrayList<String> occurrences = new ArrayList<String>();
			occurrences.add(occurrence);
			this.addWord(new Word(word, occurrences));
		}else{
			aux.addOccurrence(occurrence);			
		}
	}
	
}
