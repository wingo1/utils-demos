package com.wingo1.demo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileCopy {

	public static void main(String[] args) throws Exception {
		FileChannel in = new FileInputStream(
				"F:\\TDownload\\[我不是药神].Dying.To.Survive.2018.WEB-DL.1080p.H264.AAC-CMCTV.mp4").getChannel();
		FileChannel out = new FileOutputStream("D:/a.mp4").getChannel();
		long size = in.size();
		long total = size;
		long position = 0;
		long start = System.currentTimeMillis();
		// 注意:文件过大transferTo一次可能搞不完，比如WINDOWS上，一次只能复制2GB.
		while (position < size) {
			position += in.transferTo(position, 1000 * 1024 * 1024, out);
		}
		float speed = total / 1024f / 1024f / (System.currentTimeMillis() - start) * 1000;
		System.out.println("speed:" + speed + " MB/s");

	}

}
