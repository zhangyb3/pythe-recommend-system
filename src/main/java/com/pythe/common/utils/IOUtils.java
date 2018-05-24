package com.pythe.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IOUtils {
	public static List<String> readFileByLines(String fileName) {
		List<String> list = new ArrayList<String>();
		File file = new File(fileName);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(read);

			String line;
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}

			read.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}


	public static void writeFile(String filePathAndName, String fileContent) {
		//FileWriter writer = null;
		BufferedWriter writer = null;     
		
		try {
			File f = new File(filePathAndName);
			if (!f.exists()) {
				f.createNewFile();
			}
//			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
//			BufferedWriter writer = new BufferedWriter(write);
			
			// PrintWriter writer = new PrintWriter(new BufferedWriter(new
			// FileWriter(filePathAndName)));
			// PrintWriter writer = new PrintWriter(new
			// FileWriter(filePathAndName));
			
//			writer = new FileWriter(f, true);     
//            writer.write(fileContent);  
	
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathAndName, true), "UTF-8"));     
            writer.write(fileContent);     
            
//			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			System.out.println("写文件内容操作出错");
			e.printStackTrace();
		}
	}
	
	
    /**
     * 打开一个目录，获取全部的文件名
     * @param path
     * @return
     */
    public static List<File> open(String path)
    {
        List<File> fileList = new LinkedList<File>();
        File folder = new File(path);
        handleFolder(folder, fileList);
        return fileList;
    }

    private static void handleFolder(File folder, List<File> fileList)
    {
        File[] fileArray = folder.listFiles();
        if (fileArray != null)
        {
            for (File file : fileArray)
            {
                if (file.isFile())
                {
                    fileList.add(file);
                }
                else
                {
                    handleFolder(file, fileList);
                }
            }
        }
    }
	
}
