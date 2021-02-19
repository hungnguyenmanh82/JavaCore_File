package hung.com.filetest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class TraditionalWayApp {

	public static void main(String[] args) {
		//test: read file
		// folder: "target/classes/bar.txt"   => test run app on Eclipse as Java app
		URL url = TraditionalWayApp.class.getResource("/bar.txt");
		System.out.println("url="+url.getPath());
		readFileLine(url.getPath());

		//read file
		// folder: "target/classes/bar.txt"   => test run app on Eclipse as Java app
		readFileUtf8(url.getPath());

		System.out.println();
		System.out.println();
		//write file1:
		url = TraditionalWayApp.class.getResource("/"); // see folder "$Project/target/classes/output.txt"
		System.out.println("url="+url.getPath() + "/output.txt");
		writeByte2File(url.getPath()+"/output.txt", new String("\r\ntest write to file1").getBytes());
		
		//write file2:  // see folder "$Project/target/classes/output.txt"
		writeCharacter2FileUtf8(url.getPath()+"/output.txt", "\r\ntest write to writeCharacter2FileUtf8");

		//write file3:  // see folder "$Project/target/classes/output.txt"
		writeCharacter2FileUtf8_Buffer(url.getPath()+"/output.txt", "\r\ntest write to writeCharacter2FileUtf8_Buffer");

		//trường hợp đọc từ file *.jar sẽ khác: xem GetJavaResourcePath();

	}

	/**
	 * lúc compile sẽ gộp "main/resources/" và "main/java/" vào 1 folder chung
	 App81_https_Server.class.getResource("/") = root = main/resources/ = main/java/
	 App81_https_Server.class.getResource("/abc") = main/resource/abc  = main/java/abc  
	 //
	 App81_https_Server.class.getResource(".") = root/pakage_name/     => package_name của class này
	 App81_https_Server.class.getResource("abc") = root/pakage_name/abc
	 App81_https_Server.class.getResource("abc").getPath()
	 //
	   App81_https_Server.class.getResource("..") = parent folder of root/pakage_name/
	   App81_https_Server.class.getResource("../..") = parent of parent of root/pakage_name/  
	  //===========================
	  + Run or Debug mode trên Eclipse lấy ./ = project folder 

	  + run thực tế:  ./ = folder run "java -jar *.jar"
	 //========= 
	 File("loginTest.json"):   file ở ./ folder    (tùy run thực tế hay trên eclipse)
	 File("./abc/test.json"):   
	 File("/abc"): root folder on linux (not window)
	 */

	public static void GetJavaResourcePath(){
		//hàm này tính từ Class hiện tại làm vị trí tương đối nếu ko có “/” ở đầu path
		URL url1 = TraditionalWayApp.class.getResource("bar.txt"); 
		System.out.println(url1.getPath());

		// tính từ root nếu có “/” ở đầu path
		URL url2 = TraditionalWayApp.class.getResource("/");
		System.out.println(url2.getPath());

		URL url3 = TraditionalWayApp.class.getResource("/bar.txt");  
		System.out.println(url3.getPath());
		//===================
		// trường hợp files đc đóng gói cùng file *.jar:
		// đối với *.jar => WEB-INF/lib/
		InputStream in = TraditionalWayApp.class.getResourceAsStream("/file.txt"); 
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

	}

	public static byte[] readBytes_fromFile() throws Exception {
		// inputStream sẽ read bytes
		return  TraditionalWayApp.class.getResourceAsStream("/bar.txt").readAllBytes();
	}

	public static byte[] readBytes_FileInputStream() throws Exception {

		// inputStream sẽ read bytes
		FileInputStream fileInputStream = new FileInputStream("/nonces.json");
		byte[] bytes = fileInputStream.readAllBytes();

		fileInputStream.close();  // phải close lại sau khi read
		return  bytes;
	}

	public static byte[] readBytes_FileInputStream2()  {
		
		// try() statement là ở Java 8
		// try() sẽ tự động close FileInputStream sau khi hoàn thành
		try(FileInputStream is = new FileInputStream("/nonces.json");){
			byte[] bytes = is.readAllBytes();
			return  bytes;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 * + Cách này ko dùng buffer sẽ ko tốn RAM, nhưng performance sẽ ko tốt.
	 * + Cách này ko có chuyển đổi UTF8.
	 * + Dữ liệu ghi vào file là kiểu byte
	 * */
	public static boolean writeByte2File(String path, byte[] data) {
		File file = new File(path);
		if(file.exists())
			file.delete();
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);  //dùng OutputStream để write byte
			fos.write(data); //ghi kiểu byte array
			fos.flush();
			fos.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}




	/**
	 * OutputStreamWriter: chuyển đổi UTF8
	 * Ko có buffer. nếu có buffer thì phải có mục config size của buffer
	 */
	public static void writeCharacter2FileUtf8(String fileName,String content){
		File file = new File(fileName);
		try {
			FileOutputStream fs = new FileOutputStream(file,true); // true to append,  false to overwrite (default)
			//chuyển đổi character (1 character = 2byte) về UTF8
			OutputStreamWriter ow = new OutputStreamWriter(fs,"UTF-8");
			try {
				ow.write(content);//ghi vào buffer, chưa ghi vào file
				ow.flush();  //ghi phần còn lại của buffer vào file
			} catch (Exception e) {
				e.printStackTrace();//nếu file bị close trc lúc đang ghi vào file
			}
			ow.close() ; //nên dùng hàm này để close
			fs.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * BufferedWriter: ghi theo 1 character UTF16 = 2bytes xuống file.
	 * Kiểu String trên Java và android là UTF16 => mỗi character là 2bytes.
	 */
	public static void writeCharacter2FileUtf8_Buffer(String fileName,String content){
		File file = new File(fileName);
		BufferedWriter writerBuff;
		FileOutputStream fs;

		try {
			fs = new FileOutputStream(file,true); // true to append,  false to overwrite (default)
			// false to overwrite (default)

			OutputStreamWriter ow = new OutputStreamWriter(fs,"UTF-8");
			//lúc ghi xuống file thì chuyển thành UTF8
			writerBuff = new BufferedWriter(ow,4096); //buffer for char 4096 char buffer
			writerBuff.write(content);//ghi vào buffer, buffer chỉ đc ghi vào file khi buffer đầy

			//hoặc dùng lệnh flush:ghi buffer vao file và xóa buffer hiện tại
			writerBuff.flush();//chuyển từ buffer vào file, xóa buffer (ko cần đợi buffer đầy)
			writerBuff.close() ; //nên dùng hàm này để close
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();//nếu file bị close trc lúc đang ghi vào file
		}

	}

	/**
	 * đọc file định dạng UTF8 => chuyển về UTF16 trên java.
	 * String trên java là UTF16 tất cả các ký tự đều 2byte
	 */
	public static String readFileUtf8( String fileName){
		String st;

		System.out.println("=========================readFileUtf8(String filename)");

		try{
			File file = new File(fileName);
			if( !(file.exists() && file.isFile())){
				System.out.println("File is not exist: "+fileName);
				return null;//file ko ton tai
			}

			FileInputStream inputStream= new FileInputStream(file); //sequence bytes of file no buffer

			//nếu file chứa nội dụng định dạng UTF8 thi dùng inputStreamReader
			//convert UTF8 to character (1 character = 2bytes trên android và java)
			InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8"); 
			BufferedReader buffReader = new BufferedReader(reader,4096); //hỗ trợ buffer tăng performance

			//Read text from file
			StringBuilder stBuilder = new StringBuilder(); //stringBuilder hỗ trợ append() tốt hơn String
			char[] buf = new char[1024]; //1 char = 2byte
			int n;	    
			while (true) {
				n = buffReader.read(buf);  //performance tốt hơn Readline()
				//n = 0 là trường hợp đặc biệt (file chưa kết thức, socket chưa đóng, logcat chưa terminate)
				//n <0 file đã kết thúc, socket và Logcat đã bị đóng lại	
				if(n < 0){
					buffReader.close();
					break;//stop vòng lặp
				}else{
					stBuilder.append(buf,0,n);
				}  
			}

			System.out.print(stBuilder.toString());

			return stBuilder.toString();
		}catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * đọc file định dạng UTF8 => chuyển về UTF16 trên java.
	 * String trên java là UTF16 tất cả các ký tự đều 2byte
	 */
	public static String readFileUtf8_2(String filename, String charSetName) throws IOException  {

		//convert UTF8 to character (1 character = 2bytes trên android và java)
		BufferedReader buffReader =  new BufferedReader(new InputStreamReader(	new FileInputStream(filename),"UTF-8"),4096);

		//Read text from file
		StringBuilder stBuilder = new StringBuilder(); //stringBuilder hỗ trợ append() tốt hơn String
		char[] buf = new char[1024]; //1 char = 2byte
		int n;	    
		while (true) {
			n = buffReader.read(buf);  //performance tốt hơn Readline()
			//n = 0 là trường hợp đặc biệt (file chưa kết thức, socket chưa đóng, logcat chưa terminate)
			//n <0 file đã kết thúc, socket và Logcat đã bị đóng lại	
			if(n < 0){
				buffReader.close();
				break;//stop vòng lặp
			}else{
				stBuilder.append(buf,0,n);
			}  
		}

		return stBuilder.toString();

	}

	/**
	 * cái này dùng thư viện NIO để đọc file ra => asynchronous
		import java.nio.file.Files;
		import java.nio.file.Paths;
		import java.nio.file.Path;
	 */
	public static byte[] readFile2Bytes_nio(String filename) throws IOException {

		Path path = Paths.get(filename);
		byte[] data = Files.readAllBytes(path);  // vẫn là synchronous vì ko có callback

		return data;

	}

	/**
	 * cái này dùng thư viện NIO để đọc file ra => asynchronous
		import java.nio.file.Files;
		import java.nio.file.Paths;
		import java.nio.file.Path;
	 */
	public static void writeBytes2File_nio(String filePath, byte[] data) throws IOException{

		Path path = Paths.get(filePath);
		Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

	}

	/**
	 * BufferedReader: đọc theo kiểu Character = 2 bytes ra.
	 */
	public static String readFileLine(String fileName){
		//Get the text file
		File file = new File(fileName);

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
			//FileReader sẽ convert mặc đinh ASCCI tới 2byte Unicode
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("=========================readFileLine(String filename)");
		System.out.print(text.toString());

		return text.toString();
	}
}
