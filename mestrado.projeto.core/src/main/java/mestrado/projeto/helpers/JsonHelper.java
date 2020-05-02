package mestrado.projeto.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mestrado.projeto.core.entities.FileAttributes;

public class JsonHelper {

	static Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
	
	public static String convertToJson(Object fileDetails){			
		return gson.toJson(fileDetails);
	}
	
	public static <T> T convertFromJson(Class<T> classType, String json){		
		return gson.fromJson(json, classType);
	}
	
	
}
