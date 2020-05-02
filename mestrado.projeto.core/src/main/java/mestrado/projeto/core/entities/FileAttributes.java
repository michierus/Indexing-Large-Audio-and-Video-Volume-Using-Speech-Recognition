package mestrado.projeto.core.entities;

public class FileAttributes {
	
	private String fileName;
	private String filePath;
	private String creationTime;
	private Long size;
	private String owner;
	private String processedTime;
	private String UUID;
		
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	
	public Long getSize() {
		return size;
	}	
	public void setSize(long fileSize) {
		this.size = fileSize;
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String fileOwner) {
		this.owner = fileOwner;
	}
	
	public String getProcessedTime() {
		return processedTime;
	}
	public void setProcessedTime(String processedTime) {
		this.processedTime = processedTime;
	}
	
	public String getUUID(){
		return this.UUID;
	}
	public void setUUID(String uuid){
		this.UUID = uuid;
	}

}
