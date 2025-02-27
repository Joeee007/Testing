package com.UserManagement;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.*;

import utils.ApiBody;
import utils.ApiParam;
import utils.ApiParamGroup;
import utils.ResponseParam;
import utils.ResponseParams;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        // Store users in ServletContext if not already initialized
        if (getServletContext().getAttribute("users") == null) {
            getServletContext().setAttribute("users", new ConcurrentHashMap<String, JSONObject>());
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, JSONObject> getUsers() {
        return (Map<String, JSONObject>) getServletContext().getAttribute("users");
    }

    @Override
    @ApiParam(name = "id", type = "String")
    @ResponseParams({
        @ResponseParam(responseCode = "200", description = "User found"),
        @ResponseParam(responseCode = "404", description = "User not found")
    })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, JSONObject> users = getUsers();
        String id = request.getParameter("id");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (id == null) {
            // Return all users
            JSONArray allUsers = new JSONArray(users.values());
            try (PrintWriter out = response.getWriter()) {
                out.print(allUsers.toString());
            }
            return;
        }

        if (!users.containsKey(id)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "{\"error\": \"User not found\"}");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(users.get(id).toString());
        }
    }

    @Override
    @ApiBody(name = "user", type = "JSON", schema = "User")
    @ResponseParam(responseCode = "201", description = "User added successfully")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, JSONObject> users = getUsers();
        Map<String, JSONObject> userDetails = (Map<String, JSONObject>) getServletContext().getAttribute("userDetails");

        if (userDetails == null) {
            userDetails = new ConcurrentHashMap<>();
            getServletContext().setAttribute("userDetails", userDetails);
        }

        String jsonString = request.getReader().lines().collect(Collectors.joining());

        if (jsonString.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty request body");
            return;
        }

        try {
            JSONObject json = new JSONObject(jsonString);
            String id = json.optString("id", "").trim();
            String name = json.optString("name", "").trim();
            String email=json.optString("email","").trim();
            String age=json.optString("age","").trim();
            if (id.isEmpty() || name.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID and name cannot be empty");
                return;
            }

            users.put(id, json);
            getServletContext().setAttribute("users", users);

            // **🔹 Auto-add userDetails with default values**
            JSONObject userDetail = new JSONObject();
            userDetail.put("id", id);
            userDetail.put("email", email);
            userDetail.put("age", age);

            userDetails.put(id, userDetail);
            getServletContext().setAttribute("userDetails", userDetails); // Update context

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter out = response.getWriter()) {
                out.print("{\"message\": \"User added successfully\", \"userDetails\": " + userDetail.toString() + "}");
            }

        } catch (JSONException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        }

        System.out.println("Updated Users: " + users);
        System.out.println("Updated UserDetails: " + userDetails);
    }


    @Override
    @ApiBody(name = "userUpdate", type = "JSON", schema = "User")
    @ResponseParams({
        @ResponseParam(responseCode = "200", description = "User updated successfully"),
        @ResponseParam(responseCode = "404", description = "User not found")
    })
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, JSONObject> users = getUsers();
        String jsonString = request.getReader().lines().collect(Collectors.joining());

        if (jsonString.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Empty request body\"}");
            return;
        }

        try {
            JSONObject json = new JSONObject(jsonString);
            String id = json.optString("id", "").trim();
            String name = json.optString("name", "").trim();

            if (id.isEmpty() || name.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"ID and name cannot be empty\"}");
                return;
            }

            if (!users.containsKey(id)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "{\"error\": \"User not found\"}");
                return;
            }

            // Update user
            users.put(id, json);
            getServletContext().setAttribute("users", users);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter out = response.getWriter()) {
                out.print("{\"message\": \"User updated successfully\"}");
            }

        } catch (JSONException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Invalid JSON format\"}");
        }
    }

    @Override
    @ApiParam(name = "id", type = "String")
    @ResponseParams({
        @ResponseParam(responseCode = "200", description = "User deleted successfully"),
        @ResponseParam(responseCode = "404", description = "User not found")
    })
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, JSONObject> users = getUsers();
        Map<String, JSONObject> userDetails = getUserDetails(); // Get user details

        String id = request.getParameter("id");

        if (id == null || !users.containsKey(id)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "{\"error\": \"User not found\"}");
            return;
        }

        users.remove(id);
        getServletContext().setAttribute("users", users); // Update users

        userDetails.remove(id);
        getServletContext().setAttribute("userDetails", userDetails); // Update userDetails

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print("{\"message\": \"User deleted successfully\"}");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, JSONObject> getUserDetails() {
        ServletContext context = getServletContext();

        if (context.getAttribute("userDetails") == null) {
            context.setAttribute("userDetails", new ConcurrentHashMap<String, JSONObject>());
        }

        return (Map<String, JSONObject>) context.getAttribute("userDetails");
    }
}
