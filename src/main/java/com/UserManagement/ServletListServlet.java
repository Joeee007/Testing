package com.UserManagement;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import utils.ServletScanner;

@WebServlet("/jsonCon")
public class ServletListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServletScanner scanner = new ServletScanner();
        JSONArray apis = scanner.scanServlets();
        System.out.println(apis);
        response.getWriter().write(apis.toString());
    }
    public static void main(String args[]) {
    	ServletScanner scanner = new ServletScanner();
        JSONArray apis = scanner.scanServlets();
        System.out.println(apis);
    }


}
