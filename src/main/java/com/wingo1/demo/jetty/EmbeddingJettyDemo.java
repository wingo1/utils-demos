package com.wingo1.demo.jetty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.StringUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 嵌入式jetty容器测试
 * 
 * @author cdatc-wingo1
 *
 */
public class EmbeddingJettyDemo extends HttpServlet {
	private final static String DEFAULT_PORT = "80";
	private final static String DEFAULT_PATH = "/api";

	public static void main(String[] args) throws Exception {
		System.out.println("--------------------参数1 服务端端口号;参数2 servlet路径------------------");
		String port;// server port
		String path;// servlet path
		port = args.length != 0 ? args[0] : DEFAULT_PORT;
		path = args.length > 1 ? args[1] : DEFAULT_PATH;
		// 校验一下
		if (!port.matches("\\d{0,5}")) {
			System.out.println("参数1:" + port + " 端口必须为数字！已设置为默认值");
			port = DEFAULT_PORT;
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		// 启动server
		Server server = new Server(Integer.parseInt(port));
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(EmbeddingJettyDemo.class, path);
		server.setHandler(context);
		server.start();
		// server.join();
		System.out.println("jetty embedding server 已启动！访问路径：http://" + Inet4Address.getLocalHost().getHostAddress()
				+ ":" + port + path);
	}

	/**
	 * GET请求，遍历参数，并打印返回
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("进入GET方法...");
		JSONObject responseJSONObject = new JSONObject();
		Map<String, String[]> map = req.getParameterMap();
		if (!map.isEmpty()) {
			responseJSONObject.put("result", "ok");
			JSONObject params = new JSONObject();
			for (Entry<String, String[]> entry : map.entrySet()) {
				params.put(entry.getKey(),
						entry.getValue() != null && entry.getValue().length > 0 ? entry.getValue()[0] : null);
			}
			responseJSONObject.put("inputParam", params);
		} else {
			responseJSONObject.put("result", "no input:无输入参数");
		}
		System.out.println("返回结果为：" + responseJSONObject.toJSONString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().write(responseJSONObject.toString());
	}

	/**
	 * POST 请求 ，遍历参数获取获取请求体，并打印返回
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("进入POST方法...");
		JSONObject responseJSONObject = new JSONObject();
		responseJSONObject.put("result", "ok");
		Map<String, String[]> map = request.getParameterMap();
		if (!map.isEmpty()) {
			JSONObject params = new JSONObject();
			System.out.println("输入参数为key-value参数");
			// key-value
			for (Entry<String, String[]> entry : map.entrySet()) {
				params.put(entry.getKey(),
						entry.getValue() != null && entry.getValue().length > 0 ? entry.getValue()[0] : null);
			}
			responseJSONObject.put("inputParam", params);
		} else {
			// 请求体 比如json
			try {
				BufferedReader streamReader = new BufferedReader(
						new InputStreamReader(request.getInputStream(), "UTF-8"));
				StringBuilder responseStrBuilder = new StringBuilder();
				String inputStr;
				while ((inputStr = streamReader.readLine()) != null)
					responseStrBuilder.append(inputStr);
				Object object = getJsonObj(responseStrBuilder.toString());
				if (object != null && (object instanceof JSONObject || object instanceof JSONArray)) {
					System.out.println("输入参数为json");
					// String result = (String)
					// object.getClass().getMethod("toString").invoke(object);
					responseJSONObject.put("inputParam", object);
				} else {
					System.out.println("输入参数非json");
					responseJSONObject.put("inputParam", responseStrBuilder.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringUtil.isBlank(responseJSONObject.getString("inputParam"))) {
			responseJSONObject.remove("inputParam");
			responseJSONObject.put("result", "no input无输入参数");
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		System.out.println("返回结果为：" + responseJSONObject.toJSONString());
		response.getWriter().write(responseJSONObject.toString());
	}

	private Object getJsonObj(String str) {
		Object object = null;
		try {
			object = JSONObject.parse(str);
		} catch (Exception e) {
			// nothing to do
		}
		return object;
	}
}
