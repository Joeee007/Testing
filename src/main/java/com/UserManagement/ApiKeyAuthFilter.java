package com.UserManagement;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter({"/users/*", "/user-details/*"}) // Covers both UserServlet & UserDetailServlet
public class ApiKeyAuthFilter implements Filter {
    private static final String API_KEY_HEADER = "API-KEY";
    private static final String VALID_API_KEY = "API-KEY"; // Replace with actual key or validation logic

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String apiKey = httpRequest.getHeader(API_KEY_HEADER);
        String method = httpRequest.getMethod();

        // Allow CORS Preflight requests without API key validation
        if ("OPTIONS".equalsIgnoreCase(method)) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Validate API key
        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            System.err.println("Unauthorized access attempt: " + httpRequest.getRequestURI());
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API Key");
            return;
        }

        // Allow all other requests to proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
