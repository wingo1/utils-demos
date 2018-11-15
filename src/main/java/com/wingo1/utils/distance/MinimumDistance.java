package com.wingo1.utils.distance;

/**
 * 计算2动点最小距离和时间
 * 
 * @author cdatc-wingo1
 *
 */
public class MinimumDistance {

	public static void main(String[] args) throws Exception {

		byte[] p1 = new byte[1024];
		byte[] p2 = new byte[1024];
		System.out.println("输入1: x1,y1,v1,degree1");
		System.in.read(p1);
		String[] p1s = new String(p1).split(",");
		System.out.println("输入2: x2,y2,v2,degree2");
		System.in.read(p2);
		String[] p2s = new String(p2).split(",");
		double x1 = Double.valueOf(p1s[0]);
		double y1 = Double.valueOf(p1s[1]);
		double v1 = Double.valueOf(p1s[2]);
		double degree1 = Double.valueOf(p1s[3]);
		double x2 = Double.valueOf(p2s[0]);
		double y2 = Double.valueOf(p2s[1]);
		double v2 = Double.valueOf(p2s[2]);
		double degree2 = Double.valueOf(p2s[3]);

		double time = getTheTime(x1, x2, y1, y2, v1, v2, degree1, degree2);
		if (!Double.isNaN(time)) {
			System.out.println("minimumTime:" + time);
			double distance = getTheMinimumDestance(x1, x2, y1, y2, v1, v2, degree1, degree2, time);
			System.out.println("minimumDistance:" + distance);
		} else {
			System.out.println("2点相对静止，没有最小距离");
		}

	}

	/**
	 * 到最短距离时的时间
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param v1
	 * @param v2
	 * @param degree1
	 *            为NaN时不适用，所以随便给个值
	 * @param degree2
	 * @return
	 */
	private static double getTheTime(double x1, double x2, double y1, double y2, double v1, double v2, double degree1,
			double degree2) {
		degree1 = Double.isNaN(degree1) ? 1.0 : degree1;// 随便设个值
		degree2 = Double.isNaN(degree2) ? 1.0 : degree2;// 随便设个值
		double Vx1 = Math.cos(degree1) * v1;
		double Vx2 = Math.cos(degree2) * v2;
		double Vy1 = Math.sin(degree1) * v1;
		double Vy2 = Math.sin(degree2) * v2;
		double time = -((2 * (Vx1 - Vx2) * (x1 - x2) + 2 * (Vy1 - Vy2) * (y1 - y2))
				/ (2 * (Math.pow((Vx1 - Vx2), 2) + Math.pow((Vy1 - Vy2), 2))));
		return time;
	}

	/**
	 * 最短距离
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param v1
	 * @param v2
	 * @param degree1
	 * @param degree2
	 * @param t
	 * @return
	 */
	private static double getTheMinimumDestance(double x1, double x2, double y1, double y2, double v1, double v2,
			double degree1, double degree2, double t) {
		degree1 = Double.isNaN(degree1) ? 1.0 : degree1;// 随便设个值
		degree2 = Double.isNaN(degree2) ? 1.0 : degree2;// 随便设个值
		double Vx1 = Math.cos(degree1) * v1;
		double Vx2 = Math.cos(degree2) * v2;
		double Vy1 = Math.sin(degree1) * v1;
		double Vy2 = Math.sin(degree2) * v2;
		double D = Math.pow((x1 + Vx1 * t) - (x2 + Vx2 * t), 2) + Math.pow((y1 + Vy1 * t) - (y2 + Vy2 * t), 2);
		return Math.sqrt(D);

	}
}
