package com.wingo1.example.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.locationtech.jts.geom.Coordinate;

public class RegexGroupTest {

	public static void main(String[] args) {
		String testStr = "2021-01-24 19:13:50.203162[warn]____code=-1,data=0x7fa8d013e300,{\"commData\":{\"type\":276,\"timeStamp\":{\"tvSec\":1611515630,\"tvUsec\":202793},\"code\":-1,\"callSign\":\"TEST1234\",\"trackId\":690,\"targetAddr\":16776961,\"xAxis\":-2,\"yAxis\":-2507,\"height\":441,\"speed\":32,\"heading\":0.363257676,\"bComm\":{\"bHeight\":true,\"bSpeed\":true,\"bHeading\":true,\"bCallSign\":true,\"bClimbSpeed\":true,\"bTrackId\":true,\"bTargetAddr\":true}},\"mltExtData\":{\"bMlat\":{},\"mlatData\":{}},\"sdpExtData\":{\"qnhModified\":true,\"inReleteRegion\":true,\"qnhCorrectedHeight\":495,\"flushCircle\":1,\"qnhValue\":1020,\"targetAreaType\":2,\"synthesizeWeight\":{\"posW\":1,\"speedW\":1,\"headingW\":1,\"heightW\":1},\"longitude\":103.950531,\"latitude\":30.5571079},\"smrExtData\":{\"SmrData\":{}}}";
		Pattern pattern = Pattern.compile("(\"xAxis\":-?\\d+\\.?\\d*).+(\"yAxis\":-?\\d+\\.?\\d*)");
		Matcher matcher = pattern.matcher(testStr);
		if (matcher.find()) {
			String xStr = matcher.group(1);
			String yStr = matcher.group(2);
			double x = Double.valueOf(xStr.substring(8));
			double y = Double.valueOf(yStr.substring(8));
			System.out.println(new Coordinate(x, y));
		}
	}

}
