package MyFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class MyFile {

	public static void main(String[] args) {

	}

	/*
	 * + Cách này ko dùng buffer sẽ ko tốn RAM, nhưng performance sẽ ko tốt.
	 * + Cách này ko có chuyển đổi UTF8.
	 * + Dữ liệu ghi vào file là kiểu byte
	 * */
	public static boolean writeByte2File(String path, String in) {
		File file = new File(path);
		if(file.exists())
			file.delete();
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(in.getBytes()); //ghi kiểu byte array
			fos.flush();
			fos.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * cách này sẽ chuyển đổi dữ liệu từ UTF16 sang UTF8 trc khi ghi xuống file.
	 * OutputStreamWriter: chuyển đổi UTF8
	 */
	private void writeUtf8File(String fileName,String content){
		File file = new File(fileName);
		try {
			FileOutputStream fs = new FileOutputStream(file,false); // false to overwrite (default)
			OutputStreamWriter ow = new OutputStreamWriter(fs,"UTF-8");//byte to charset convert 8k char buffer
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
	private void writeCharacter2FileUtf8(String fileName,String content){
		File file = new File("test.txt");
		BufferedWriter writer;
		FileOutputStream fs;

		try {
			fs = new FileOutputStream(file,false); // true to append
			// false to overwrite (default)

			OutputStreamWriter ow = new OutputStreamWriter(fs,"UTF-8");//byte to charset convert 8k char buffer
			//lúc ghi xuống file thì chuyển thành UTF8
			writer = new BufferedWriter(ow,4096); //buffer for char 4096 char buffer
			writer.write("content");//ghi vào buffer, buffer chỉ đc ghi vào file khi buffer đầy

			//hoặc dùng lệnh flush:ghi buffer vao file và xóa buffer hiện tại
			writer.flush();//chuyển từ buffer vào file, xóa buffer (ko cần đợi buffer đầy)
			writer.close() ; //nên dùng hàm này để close
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();//nếu file bị close trc lúc đang ghi vào file
		}

	}

	/**
	 * BufferedReader: đọc ra theo 1 character UTF16 = 2bytes xuống file.
	 * Kiểu String trên Java và android là UTF16 => mỗi character là 2bytes.
	 */
	public static String readFileUtf8( String fileName){
		String st;

		try{
			File file = new File(fileName);
			if( !(file.exists() && file.isFile())){
				System.out.println("File is not exist: "+fileName);
				return null;//file ko ton tai
			}

			FileInputStream inputStream= new FileInputStream(file); //sequence byte of file no buffer

			//nếu file chứa nội dụng định dạng UTF8 thi dùng inputStreamReader
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

			return stBuilder.toString();
		}catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String readFileUtf8_2(String filename, String charSetName) throws IOException  {
	
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
	 * BufferedReader: đọc theo kiểu Character = 2 bytes ra.
	 */
	public static void readFileLine(String fileName){
		//Get the text file
		File file = new File(fileName);

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
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
	}
}
