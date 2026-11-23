
package com.example.loginapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewDatabase {

    public static void main(String[] args) {

        // Database location
        Path databasePath = Paths.get("loginapp.db")
                                 .toAbsolutePath();

        // Check whether database exists
        if (!Files.exists(databasePath)) {
            System.out.println("Database file not found!");
            System.out.println(databasePath);
            return;
        }

        String url = "jdbc:sqlite:" + databasePath;

        System.out.println("==================================");
        System.out.println("       SQLITE DATABASE VIEW       ");
        System.out.println("==================================");
        System.out.println("Database: " + databasePath);

        try (Connection connection =
                DriverManager.getConnection(url);
             Statement statement =
                connection.createStatement()) {

            // Display registered users
            System.out.println("\n==================================");
            System.out.println("        REGISTERED USERS          ");
            System.out.println("==================================");

            String userQuery =
                "SELECT id, username, email FROM users";

            try (ResultSet result =
                    statement.executeQuery(userQuery)) {

                int count = 0;

                while (result.next()) {
                    count++;

                    System.out.println("\nUser ID: "
                            + result.getLong("id"));

                    System.out.println("Username: "
                            + result.getString("username"));

                    System.out.println("Email: "
                            + result.getString("email"));

                    System.out.println("----------------------------------");
                }

                System.out.println("Total users: " + count);
            }

            // Display login activities
            System.out.println("\n==================================");
            System.out.println("        LOGIN ACTIVITIES           ");
            System.out.println("==================================");

            String activityQuery =
                "SELECT id, email, status, activity_time "
                + "FROM login_activities "
                + "ORDER BY activity_time DESC";

            try (ResultSet result =
                    statement.executeQuery(activityQuery)) {

                int count = 0;

                while (result.next()) {
                    count++;

                    System.out.println("\nActivity ID: "
                            + result.getLong("id"));

                    System.out.println("Email: "
                            + result.getString("email"));

                    System.out.println("Status: "
                            + result.getString("status"));

                    System.out.println("Date and Time: "
                            + result.getString("activity_time"));

                    System.out.println("----------------------------------");
                }

                System.out.println("Total login activities: " + count);
            }

        } catch (Exception e) {

            System.out.println("Database error occurred!");
            e.printStackTrace();
        }
    }
}
