package com.koreait.board.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utils {
	public static void forwardTemp(String title, String openPage
			, String innerPage, HttpServletRequest request
			, HttpServletResponse response) throws ServletException, IOException {				
		request.setAttribute("page", String.format("/WEB-INF/view/%s.jsp"
				, innerPage));		
		forward(title, openPage, request, response);
	}
	
	public static void forward(String title, String openPage
			, HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		request.setAttribute("title", title);
		String jsp = String.format("/WEB-INF/view/%s.jsp", openPage);
		request.getRequestDispatcher(jsp).forward(request, response);
	}
	
	
	public static int getIntParam(HttpServletRequest request, String key) {
		return getIntParam(request, key, 0);
	}
	
	public static int getIntParam(HttpServletRequest request, String key, int defVal) {
		String param = request.getParameter(key);
		return parseStrToInt(param, defVal);
	}
	
	public static int parseStrToInt(String val) {
		return parseStrToInt(val, 0);
	}
	
	public static int parseStrToInt(String val, int defVal) {
		try {
			return Integer.parseInt(val);
		} catch(Exception e) {}
		return defVal;
	}
}
