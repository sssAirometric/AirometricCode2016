package com.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	private String fileNameToSearch;
	private List<String> result = new ArrayList<String>();
	
	public String getFileNameToSearch() {
		return fileNameToSearch;
	}
 
	public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}
 
	public List<String> getResult() {
	  return result;
	}
	/**
	 * This method Searches the file name in the mentioned directory
	 * @param directory
	 * @param fileNameToSearch
	 * @return
	 */
	public boolean searchDirectory(File directory, String fileNameToSearch) {
		boolean fileExists = false;
		setFileNameToSearch(fileNameToSearch);
		if (directory.isDirectory()) {
			fileExists =  search(directory);
		} else {
		    System.out.println(directory.getAbsoluteFile() + " is not a directory!");
		}
		return fileExists;
	  }
	 /**
	  * Search the file and returns true if found else flase
	  * @param file
	  * @return
	  */
	  private boolean search(File file) {
		boolean fileExists = false;
		if (file.isDirectory()) {
		  System.out.println("Searching directory ... " + file.getAbsoluteFile());
		    if (file.canRead()) {
				for (File temp : file.listFiles()) {
				    if (temp.isDirectory()) {
				    	search(temp);
				    } else {
						if (getFileNameToSearch().equals(temp.getName().toLowerCase())) {			
						    result.add(temp.getAbsoluteFile().toString());
						    fileExists = true;
					    }
				    }
			    }
	 
		    }else {
		    	System.out.println(file.getAbsoluteFile() + "Permission Denied");
		    }
	      }
		return fileExists;
	  }
}
