package com.wingo1.example.io;

import java.io.IOException;
import java.util.Scanner;

/**
 * 用scanner来读取
 * 
 * @author cdatc-wingo1
 *
 */
public class ReadKeyboard {

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.println("输入");
		while (true) {
			String nextLine = scanner.nextLine();
			System.out.println("输入的是：" + nextLine);
			if ("end".equals(nextLine)) {
				break;
			}
			// scanner.close();千万不要关闭System.in，不可能再打开的
		}
	}
}
