package mindbalanceapp;

import java.sql.Connection;

public class TestDBConnection {
    public static void main(String[] args) {
        try (Connection c = DB.getConnection()) {
            System.out.println("Connected to DB: " + c.getCatalog());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("DB connection failed: " + e.getMessage());
        }
    }
}
