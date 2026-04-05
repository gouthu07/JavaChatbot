package com.buntu.chatbot;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import com.google.gson.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    // Groq API settings
    private static final String API_KEY = System.getenv("GROQ_API_KEY");
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    // Oracle DB settings
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "system";
    private static final String DB_PASS = "Oracle123";

    public static void main(String[] args) throws Exception {

        // Check API key
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.out.println("ERROR: GROQ_API_KEY not set!");
            return;
        }

        // Connect to Oracle
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        System.out.println("✅ Database connected!");

        OkHttpClient client = new OkHttpClient();
        Scanner scanner = new Scanner(System.in);

        // Generate a simple session ID using timestamp
        String sessionId = "session_" + System.currentTimeMillis();

        System.out.println("=================================");
        System.out.println("  Welcome to Buntu's Chatbot!");
        System.out.println("  Session: " + sessionId);
        System.out.println("  Type 'exit' to quit");
        System.out.println("  Type 'history' to see past chats");
        System.out.println("=================================\n");

        while (true) {

            // Get user input
            System.out.print("You: ");
            String userMessage = scanner.nextLine();

            // Exit condition
            if (userMessage.equalsIgnoreCase("exit")) {
                System.out.println("Bye Buntu! See you next time!");
                break;
            }

            // Show chat history
            if (userMessage.equalsIgnoreCase("history")) {
                showHistory(conn);
                continue;
            }

            // Save user message to DB
            saveMessage(conn, sessionId, "user", userMessage);

            // Build JSON request
            String requestBody = "{"
                    + "\"model\": \"llama-3.1-8b-instant\","
                    + "\"messages\": ["
                    + "  {\"role\": \"user\", \"content\": \"" + userMessage + "\"}"
                    + "]"
                    + "}";

            // Send to Groq API
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Parse response
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            if (jsonResponse.has("error")) {
                String errorMsg = jsonResponse.getAsJsonObject("error").get("message").getAsString();
                System.out.println("Error: " + errorMsg);
            } else {
                String botReply = jsonResponse
                        .getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();

                // Print bot reply
                System.out.println("Bot: " + botReply);

                // Save bot reply to DB
                saveMessage(conn, sessionId, "assistant", botReply);
            }

            System.out.println();
        }

        conn.close();
        scanner.close();
    }

    // Save message to Oracle
    static void saveMessage(Connection conn, String sessionId, String role, String message) throws SQLException {
        String sql = "INSERT INTO chat_history (id, session_id, role, message) "
                + "VALUES (chat_seq.NEXTVAL, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, sessionId);
        ps.setString(2, role);
        ps.setString(3, message);
        ps.executeUpdate();
        ps.close();
    }

    // Show last 10 messages from DB
    static void showHistory(Connection conn) throws SQLException {
        System.out.println("\n--- Last 10 Messages ---");
        String sql = "SELECT role, message, created_at FROM "
                + "(SELECT role, message, created_at FROM chat_history ORDER BY created_at DESC) "
                + "WHERE ROWNUM <= 10 ORDER BY created_at ASC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String role = rs.getString("role");
            String message = rs.getString("message");
            String time = rs.getTimestamp("created_at").toString();
            System.out.println("[" + time + "] " + role.toUpperCase() + ": " + message);
        }
        System.out.println("------------------------\n");
        rs.close();
        ps.close();
    }
}