package UserManagement;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/users")
public class ApiKeyAuthFilter implements Filter {
    private static final String API_KEY_HEADER = "API-KEY";
    private static final String VALID_API_KEY = "API-KEY"; // Replace with actual key or use a dynamic check

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

        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API Key");
            return;
        }

        // Allow all HTTP methods
        System.out.println(method+" jskdk ");
        
        
      
            chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
