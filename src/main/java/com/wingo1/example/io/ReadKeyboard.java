package com.wingo1.example.io;

import java.util.Scanner;

/**
 * 用scanner来读取
 * 
 * @author cdatc-wingo1
 *
 */
public class ReadKeyboard {

	public static void main(String[] args) {
		System.out.println("输入");
		try (Scanner scanner = new Scanner(System.in);) {
			while (true) {
				String nextLine = scanner.nextLine();
				System.out.println("输入的是：" + nextLine);
				if ("end".equals(nextLine)) {
					break;
				}

			}
		}
	}
}
