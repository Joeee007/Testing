package UserManagement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Pipe.SourceChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiBody;
import utils.ApiParam;
import utils.ApiParamGroup;
import utils.ResponseParam;
import utils.ResponseParams;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private static final List<String> users = new ArrayList<>();

    @Override
	@ResponseParams({
        @ResponseParam(responseCode = "200", description = "Successful response", examples = {"[{\"id\":1, \"name\":\"John Doe\"}]"}),
        @ResponseParam(responseCode = "404", description = "No users found")
    })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        

        PrintWriter out = response.getWriter();
        out.print(new JSONArray(users).toString());  // Convert List to JSON Array
        out.flush();
        out.close();
    }

    @Override
	@ApiBody(name = "user", type = "JSON", schema = "UserSchema")
    @ResponseParam(responseCode = "201", description = "User created successfully")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println("Header Name: " + headerName);

            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                System.out.println("Header Value: " + headerValue);
          }
       }   
    	
        // Read JSON body from request
    	BufferedReader reader = request.getReader();
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }

        System.out.println("Received JSON: " + jsonString.toString());

        if (jsonString.toString().trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty request body");
            return;
        }

        // Parse JSON manually
        try {
            JSONObject json = new JSONObject(jsonString.toString()); // Convert String to JSON
            String user = json.getString("name").trim(); // Extract "name" field
            
            if (user.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User name cannot be empty");
                return;
            }

            System.out.println("Extracted User: " + user);

            users.add(user); // Add user to list
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"message\": \"User created successfully!\"}");
            out.flush();
            out.close();

        } catch (JSONException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        }

        System.out.println("Updated User List: " + users);
    }
    


    @Override
	@ApiParamGroup(
    		value={@ApiParam(name = "id", type = "Integer"),@ApiParam(name = "name", type = "String")}
     )

    @ResponseParam(responseCode = "200", description = "User updated successfully")
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        if (id < users.size()) {
            users.set(id, name);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"message\": \"User updated successfully!\"}");
            out.flush();
        } 
        else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }

    @Override
	@ApiParam(name = "id", type = "Integer")
    @ResponseParam(responseCode = "200", description = "User deleted successfully")
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        if (id < users.size()) {
            users.remove(id);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"message\": \"User deleted successfully!\"}");
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }
}
