package edu.steam.cms.test;

import edu.steam.cms.util.FileUtil;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		
		for( int i = 0; i < 10; i++ ) {
			System.out.println(FileUtil.generateUniqueFileName("png"));
			Thread.sleep(100);
		}
	}

}
