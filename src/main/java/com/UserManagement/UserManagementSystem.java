package com.UserManagement;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.*;

public class UserManagementSystem {
    private static final String USER_BASE_URL = "http://localhost:8080/users";
    private static final String USER_DETAILS_URL = "http://localhost:8080/user-details";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nUser Management System");
            System.out.println("1. Add User");
            System.out.println("2. View All Users");
            System.out.println("3. View User by ID");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Add User Details");
            System.out.println("7. View User Details");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter user ID: ");
                    String userId = scanner.nextLine();
                    System.out.print("Enter user name: ");
                    String name = scanner.nextLine();
                    System.out.println("Response: " + sendPostUser(userId, name));
                    break;
                case 2:
                    System.out.println("Users: " + sendGetUser(null)); // Fetch all users
                    break;
                case 3:
                    System.out.print("Enter user ID to fetch: ");
                    String idToFetch = scanner.nextLine();
                    System.out.println("User Details: " + sendGetUser(idToFetch)); // Fetch user by ID
                    break;
                case 4:
                    System.out.print("Enter user ID to update: ");
                    String idToUpdate = scanner.nextLine();
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    System.out.println("Response: " + sendPutUser(idToUpdate, newName));
                    break;
                case 5:
                    System.out.print("Enter user ID to delete: ");
                    String idToDelete = scanner.nextLine();
                    System.out.println("Response: " + sendDeleteUser(idToDelete));
                    break;
                case 6:
                    System.out.print("Enter user ID for details: ");
                    String detailUserId = scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter age: ");
                    String age = scanner.nextLine();
                    System.out.println("Response: " + sendPostUserDetails(detailUserId, email, age));
                    break;
                case 7:
                    System.out.print("Enter user ID to fetch details: ");
                    String detailFetchId = scanner.nextLine();
                    System.out.println("User Details: " + sendGetUserDetails(detailFetchId)); // Fetch user details
                    break;
                case 8:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ------------------------- User CRUD Methods ------------------------- //
    private static String sendGetUser(String id) throws IOException {
        String urlString = (id == null) ? USER_BASE_URL : USER_BASE_URL + "?id=" + id;
        return sendRequest("GET", urlString, null);
    }

    private static String sendPostUser(String id, String name) throws IOException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        return sendRequest("POST", USER_BASE_URL, json.toString());
    }

    private static String sendPutUser(String id, String name) throws IOException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        return sendRequest("PUT", USER_BASE_URL, json.toString());
    }

    private static String sendDeleteUser(String id) throws IOException {
        return sendRequest("DELETE", USER_BASE_URL + "?id=" + id, null);
    }

    // ------------------------- User Details Methods ------------------------- //
    private static String sendGetUserDetails(String id) throws IOException {
        return sendRequest("GET", USER_DETAILS_URL + "?id=" + id, null);
    }

    private static String sendPostUserDetails(String id, String email, String age) throws IOException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("email", email);
        json.put("age", age);
        return sendRequest("POST", USER_DETAILS_URL, json.toString());
    }

    // ------------------------- HTTP Request Helper Method ------------------------- //
    private static String sendRequest(String method, String urlString, String jsonBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        if (jsonBody != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes());
                os.flush();
            }
        }

        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                (responseCode < 300) ? conn.getInputStream() : conn.getErrorStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
