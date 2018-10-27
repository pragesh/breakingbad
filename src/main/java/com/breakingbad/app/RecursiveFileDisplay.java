package com.breakingbad.app;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


public class RecursiveFileDisplay {
	
	public static void display(String uri,List<String> dirList) throws SVNException {
		File currentDir = new File(uri);
		displayDirectoryContents(currentDir,dirList);
	}

	public static void displayDirectoryContents(File dir,List<String> dirList) throws SVNException {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					boolean isSvnDirectory = SVNWCUtil.isVersionedDirectory(file);
					System.out.println("directory:" + file.getCanonicalPath());
					if(isSvnDirectory){
					dirList.add(file.getName());
					}
				} else {
					System.out.println("file:" + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
