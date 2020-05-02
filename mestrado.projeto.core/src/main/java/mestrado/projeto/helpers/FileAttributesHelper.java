package mestrado.projeto.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;

import mestrado.projeto.core.entities.FileAttributes;

public class FileAttributesHelper {

	public static FileAttributes getFileAttributes(File file) throws IOException{
		
		FileAttributes f = new FileAttributes();
		
		f.setFileName(file.getName());
		f.setFilePath(file.getAbsolutePath());
		f.setSize(file.length());
		
		//Read file info
        Path path = Paths.get(file.getAbsolutePath());
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        f.setCreationTime(attrs.creationTime().toString());                                        
        f.setOwner(Files.getOwner(path).getName());
        f.setUUID(java.util.UUID.randomUUID().toString());
        
		return f;
	}
}
