package jp.co.soliton.keymanager.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileUtils {

	public static boolean saveFileInfo(File file, String content) {
		try{
			FileWriter fwriter = new FileWriter(file, false);// true to append // false to overwrite.
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write(content);
			bwriter.close();
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
