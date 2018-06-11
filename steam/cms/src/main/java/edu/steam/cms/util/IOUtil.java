package edu.steam.cms.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	/**
	 * input stream의 bytes를 output stream으로 복사함
	 * input stream을 reset하지않음. 
	 * TODO reset 추가해야 하나????
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void copy(InputStream input, OutputStream output) throws IOException {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(input);
			out = new BufferedOutputStream(output);
			byte[] buffer = new byte[1024];
			for(int len = 0 ; 
					(len = in.read(buffer)) > 0; 
					out.write(buffer, 0, len));
			out.flush();
			
		}catch(IOException e) {
			throw e;
		}finally {
			in.close();
			out.close();
		}
	}
}
