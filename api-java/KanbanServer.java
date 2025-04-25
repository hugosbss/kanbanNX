package com.mycompany.kanbanapi;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class KanbanServer {

    public static void main(String[] args) throws Exception {
        int port = 8000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor rodando na porta " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleRequest(socket)).start();
        }
    }

    private static void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {

            String line = in.readLine();
            if (line == null) return;

            String[] requestLine = line.split(" ");
            String method = requestLine[0];
            String path = requestLine[1];

            while (!(line = in.readLine()).isEmpty()); // headers

            // Leitura do corpo (se houver)
            StringBuilder body = new StringBuilder();
            while (in.ready()) body.append((char) in.read());

            // OPTIONS (CORS)
            if (method.equals("OPTIONS")) {
                writeCorsPreflight(out);
                return;
            }

            // GET /tasks
            if (method.equals("GET") && path.equals("/tasks")) {
                List<Map<String, String>> tasks = getTasks();
                writeJsonResponse(out, 200, toJson(tasks));
                return;
            }

            // POST /tasks
            if (method.equals("POST") && path.equals("/tasks")) {
                createTask(body.toString());
                writePlainResponse(out, 201, "Tarefa criada");
                return;
            }

            // PUT /tasks/{id}
            if (method.equals("PUT") && path.startsWith("/tasks/")) {
                int id = Integer.parseInt(path.substring("/tasks/".length()));
                updateTask(id, body.toString());
                writePlainResponse(out, 200, "Tarefa atualizada");
                return;
            }

            // DELETE /tasks/{id}
            if (method.equals("DELETE") && path.startsWith("/tasks/")) {
                int id = Integer.parseInt(path.substring("/tasks/".length()));
                deleteTask(id);
                writePlainResponse(out, 200, "Tarefa deletada");
                return;
            }

            // Not Found
            writePlainResponse(out, 404, "Não encontrado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CONEXÃO
    private static Connection connect() throws Exception {
        String url = "jdbc:mysql://localhost:3306/kanban";
        String user = "root";
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    // JSON UTIL
    private static String extractJsonValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\":") + key.length() + 3;
        int end = json.indexOf("\"", start + 1);
        return json.substring(start + 1, end);
    }

    private static String toJson(List<Map<String, String>> tasks) {
        StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < tasks.size(); i++) {
                Map<String, String> t = tasks.get(i);
                    sb.append("{")
                        .append("\"id\":").append(t.get("id")).append(",")
                            .append("\"titulo\":\"").append(escapeJson(t.get("titulo"))).append("\",")
                            .append("\"descricao\":\"").append(escapeJson(t.get("descricao"))).append("\",")
                            .append("\"status\":\"").append(escapeJson(t.get("status"))).append("\"")
                            .append("}");
        if (i < tasks.size() - 1) sb.append(",");
    }
    sb.append("]");
    return sb.toString();
    }

    // DB MÉTODOS
 private static List<Map<String, String>> getTasks() throws Exception { List<Map<String, String>> tasks = new ArrayList<>(); try (Connection conn = connect()) { Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM tasks"); while (rs.next()) { Map<String, String> task = new HashMap<>(); task.put("id", rs.getString("id")); task.put("titulo", rs.getString("titulo")); task.put("descricao", rs.getString("descricao")); task.put("status", rs.getString("status")); tasks.add(task); } } return tasks; }

    private static void createTask(String body) throws Exception {
        String titulo = extractJsonValue(body, "titulo");
        String descricao = extractJsonValue(body, "descricao");
        String status = extractJsonValue(body, "status");

        try (Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks (titulo, descricao, status) VALUES (?, ?, ?)");
            stmt.setString(1, titulo);
            stmt.setString(2, descricao);
            stmt.setString(3, status);
            stmt.executeUpdate();
        }
    }

    private static void updateTask(int id, String body) throws Exception {
        String descricao = extractJsonValue(body, "descricao");
        String status = extractJsonValue(body, "status");

        try (Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE tasks SET descricao = ?, status = ? WHERE id = ?");
            stmt.setString(1, descricao);
            stmt.setString(2, status);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    private static void deleteTask(int id) throws Exception {
        try (Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // RESPOSTAS
    private static void writeCorsPreflight(OutputStream out) throws IOException {
        out.write(("HTTP/1.1 200 OK\r\n").getBytes());
        out.write("Access-Control-Allow-Origin: *\r\n".getBytes());
        out.write("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n".getBytes());
        out.write("Access-Control-Allow-Headers: Content-Type\r\n".getBytes());
        out.write("\r\n".getBytes());
    }

    private static void writePlainResponse(OutputStream out, int statusCode, String body) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + " OK\r\n").getBytes());
        out.write("Access-Control-Allow-Origin: *\r\n".getBytes());
        out.write("Content-Type: text/plain\r\n".getBytes());
        out.write(("Content-Length: " + body.length() + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(body.getBytes("UTF-8"));
    }

    private static void writeJsonResponse(OutputStream out, int statusCode, String json) throws IOException {
    byte[] jsonBytes = json.getBytes("UTF-8");
    out.write(("HTTP/1.1 " + statusCode + " OK\r\n").getBytes("UTF-8"));
    out.write("Access-Control-Allow-Origin: *\r\n".getBytes("UTF-8"));
    out.write("Content-Type: application/json; charset=UTF-8\r\n".getBytes("UTF-8"));
    out.write(("Content-Length: " + jsonBytes.length + "\r\n").getBytes("UTF-8"));
    out.write("\r\n".getBytes("UTF-8"));
    out.write(jsonBytes);
    }
    
    private static String escapeJson(String value) {
    if (value == null) return "";
    return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}