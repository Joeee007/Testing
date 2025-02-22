package UserManagement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class UserManagementSystem {
    private static final String BASE_URL = "http://localhost:8080/users";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nUser Management System");
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter user name: ");
                    String name = scanner.nextLine();
                    System.out.println("Response: " + sendPost(name));
                    break;
                case 2:
                    System.out.println("Users: " + sendGet());
                    break;
                case 3:
                    System.out.print("Enter user ID to update: ");
                    int idToUpdate = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    System.out.println("Response: " + sendPut(idToUpdate, newName));
                    break;
                case 4:
                    System.out.print("Enter user ID to delete: ");
                    int idToDelete = scanner.nextInt();
                    System.out.println("Response: " + sendDelete(idToDelete));
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static String sendGet() throws IOException {
        return sendRequest("GET", null, -1);
    }

    private static String sendPost(String name) throws IOException {
        return sendRequest("POST", name, -1);
    }

    private static String sendPut(int id, String name) throws IOException {
        return sendRequest("PUT", name, id);
    }

    private static String sendDelete(int id) throws IOException {
        return sendRequest("DELETE", null, id);
    }

    private static String sendRequest(String method, String name, int id) throws IOException {
        String urlString = BASE_URL + ((id >= 0) ? "?id=" + id : "");
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if ("POST".equals(method) || "PUT".equals(method)) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(("name=" + name).getBytes());
            os.flush();
            os.close();
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

