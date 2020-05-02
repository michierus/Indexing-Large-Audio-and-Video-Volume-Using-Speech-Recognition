package mestrado.projeto.core.entities;

import java.util.ArrayList;

public class Word {

	public Word(){
		
	}
	
	public Word(String word){
		this.word = word;	
	}
	
	/*
	 public Word(String word, String begin, String end){
		this.word = word;
		this.begin = begin;
		this.end = end;
	}
	 */
	public Word(String word, ArrayList<String> occurrences){
		this.word = word;
		this.occurrences = occurrences;		
		this.occurrenceQuantity = occurrences.size();
	}
	
	private String word;
	private ArrayList<String> occurrences = new ArrayList<String>();
	private int occurrenceQuantity;
	
	//private String begin;
	//private String end;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	public ArrayList<String> getOccurrences() {
		return occurrences;
	}
	public void setOccurrences(ArrayList<String> occurrences) {
		this.occurrences = occurrences;
		this.occurrenceQuantity = occurrences.size();
	}
	public void addOccurrence(String occurrence){
		this.occurrences.add(occurrence);
		this.occurrenceQuantity = this.occurrenceQuantity + 1; 
	}
	
	public int getOccurrenceQuantity(){
		return this.occurrenceQuantity;
	}
	
	/*
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	*/
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		result = prime * result;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;		
		return true;
	}
	
}
