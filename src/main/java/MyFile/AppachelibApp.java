package MyFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;


public class AppachelibApp {

	public static void main(String[] args) {
		//test: read file
		// folder: "target/classes/bar.txt"   => test run app on Eclipse as Java app
		URL url = AppachelibApp.class.getResource("/bar.txt");

		
		//trường hợp đọc từ file *.jar sẽ khác: xem GetJavaResourcePath();
		
	}
	
	public  byte[] readFile2Bytes() throws IOException {
		
		InputStream in = getClass().getResourceAsStream("/file/test.txt"); //  target/classes/file/test.txt
		return IOUtils.toByteArray(in);
	}

}
