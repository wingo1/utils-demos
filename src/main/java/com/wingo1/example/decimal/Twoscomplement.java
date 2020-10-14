package com.wingo1.example.decimal;

public class Twoscomplement {

	public static void main(String[] args) {
		String str = "10000000";
		// 2种方法结果完全一样
		System.out.println(binaryToInt(str));
		Integer result = simpleComplementToTrue(str);
		System.out.println(Integer.toBinaryString(result.byteValue() & 0xFF));

	}

	/**
	 * 这是根据口诀来的 chentong 从string表示的补码求真值
	 * 
	 * @param str 01字符串表示的补码
	 * @return int
	 */
	public static int binaryToInt(String str) {

		if (str == null) {
			return 0;
		}
		// 符号位为0表示正数
		if (str.substring(0, 1).equals("0")) {
			return Integer.parseInt(str, 2);
		}

		// 如符号位为1则为负数，补码求真值，按位求反+1
		byte[] b = new byte[str.length()];
		StringBuffer newstr = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) Integer.parseInt(str.substring(i, i + 1));
			if (b[i] == 1) {
				newstr.append("0");
			} else if (b[i] == 0) {
				newstr.append("1");
			}
		}
		int newI = Integer.parseInt(newstr.toString(), 2);

		return -1 * (newI + 1);
	}

	/**
	 * 根据补码的意义来的
	 * 
	 * @param source
	 * @return
	 */
	public static Integer simpleComplementToTrue(String source) {
		if (source.startsWith("0")) {
			return Integer.parseInt(source, 2);
		}
		int result = (int) (Integer.parseInt(source, 2) - Math.pow(2, source.length()));
		return result;
	}
}
