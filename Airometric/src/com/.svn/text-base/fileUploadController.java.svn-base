package com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.primefaces.model.UploadedFile;

import com.to.HealthIndexGenerator;


public class fileUploadController 
{
	private UploadedFile file;  
	private static Locale currentLocale = new Locale("es");
	public static ResourceBundle resourceBundle = ResourceBundle.getBundle("Resource",currentLocale);
	  
    public UploadedFile getFile() {  
        return file;  
    }  
  
    public void setFile(UploadedFile file) {  
        this.file = file;  
    } 
    
	public void upload() {  
		 FacesContext context = FacesContext.getCurrentInstance();
		 System.out.println("in file---"+file);
        if(file != null) {  
        	String fileName = file.getFileName();
        	System.out.println("inside fileUploadController fileName : "+fileName);
        	String folder=HealthIndexGenerator.resourceBundle.getString("DOWNLOAD_FOLDER");
        	String path=folder+"\\uploadReport\\"+fileName;
        	//String basePath = "C:" + File.separator + "uploads" + File.separator;
        	System.out.println("inside fileUploadController : "+path);
             File outputFilePath = new File(path);
             
             // Copy uploaded file to destination path
             InputStream inputStream = null;
             OutputStream outputStream = null;
             try {
                 inputStream = file.getInputstream();
                 outputStream = new FileOutputStream(outputFilePath);
      
                 int read = 0;
                 final byte[] bytes = new byte[1024];
                 while ((read = inputStream.read(bytes)) != -1) {
                     outputStream.write(bytes, 0, read);
                 }
                 context.getExternalContext().getSessionMap().put("analysisMsg","File '"+fileName+"' Uploaded Successfully");
             } catch (IOException e) {
                 e.printStackTrace();
                 
             }
        }  
    }  
}
 