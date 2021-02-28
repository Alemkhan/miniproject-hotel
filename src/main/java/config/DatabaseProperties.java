package config;

public class DatabaseProperties {

    private static String hostname = "jdbc:postgresql://localhost:5432/hotel";
    private static String username = "postgres";
    private static String password = "1337";

    public static String getHostname() {
        return hostname;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}
