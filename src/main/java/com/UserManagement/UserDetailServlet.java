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
import utils.ResponseParam;
import utils.ResponseParams;

@WebServlet("/user-details")
public class UserDetailServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        if (getServletContext().getAttribute("userDetails") == null) {
            getServletContext().setAttribute("userDetails", new ConcurrentHashMap<String, JSONObject>());
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, JSONObject> getUsers() {
        ServletContext context = getServletContext();

        if (context.getAttribute("users") == null) {
            context.setAttribute("users", new ConcurrentHashMap<String, JSONObject>());
        }

        return (Map<String, JSONObject>) context.getAttribute("users");
    }

    @SuppressWarnings("unchecked")
    protected Map<String, JSONObject> getUserDetails() {
        ServletContext context = getServletContext();

        if (context.getAttribute("userDetails") == null) {
            context.setAttribute("userDetails", new ConcurrentHashMap<String, JSONObject>());
        }

        return (Map<String, JSONObject>) context.getAttribute("userDetails");
    }


    @Override
    @ApiParam(name = "id", type = "String")
    @ResponseParams({
        @ResponseParam(responseCode = "200", description = "User details found"),
        @ResponseParam(responseCode = "404", description = "User or details not found")
    })
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, JSONObject> users = getUsers();
        Map<String, JSONObject> userDetails = getUserDetails();

        String id = request.getParameter("id");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (id == null) {
            JSONArray allDetails = new JSONArray(userDetails.values());
            try (PrintWriter out = response.getWriter()) {
                out.print(allDetails.toString());
            }
            return;
        }

        if (!userDetails.containsKey(id)) {
            if (!users.containsKey(id)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "{\"error\": \"User not found\"}");
                return;
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "{\"error\": \"User details not found\"}");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(userDetails.get(id).toString());
        }

        System.out.println("Fetched user details for ID: " + id);
    }

    @Override
    @ApiBody(name = "userDetail", type = "JSON", schema = "User")
    @ResponseParam(responseCode = "201", description = "User details added successfully")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, JSONObject> users = getUsers();
        Map<String, JSONObject> userDetails = getUserDetails();

        String jsonString = request.getReader().lines().collect(Collectors.joining());

        if (jsonString.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Empty request body\"}");
            return;
        }

        try {
            JSONObject json = new JSONObject(jsonString);
            String id = json.optString("id", "").trim();
            String email = json.optString("email", "").trim();
            String age = json.optString("age", "").trim();

            if (id.isEmpty() || email.isEmpty() || age.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"ID, email, and age cannot be empty\"}");
                return;
            }

            if (!users.containsKey(id)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"User does not exist\"}");
                return;
            }

            userDetails.put(id, json);
            getServletContext().setAttribute("userDetails", userDetails);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter out = response.getWriter()) {
                out.print("{\"message\": \"User details added successfully\"}");
            }

        } catch (JSONException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"Invalid JSON format\"}");
        }

        System.out.println("Updated user details in ServletContext: " + userDetails);
    }
}
