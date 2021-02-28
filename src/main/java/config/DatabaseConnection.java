package config;

import java.sql.*;

public class DatabaseConnection {

    public static Connection main() {

        Connection connection = null;
        ResultSet resultSet = null;

        String url = DatabaseProperties.getHostname();
        String username = DatabaseProperties.getUsername();
        String password = DatabaseProperties.getPassword();

        try {
            connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(DatabaseQueries.CREATE_ROOM_TABLE);
            stmt.executeUpdate(DatabaseQueries.CREATE_CLIENT_TABLE);
            stmt.executeUpdate(DatabaseQueries.CREATE_SERVICE_TABLE);
            stmt.executeUpdate(DatabaseQueries.CREATE_SERVICES_TABLE);
            stmt.executeUpdate(DatabaseQueries.CREATE_EXTRASERVICE_TABLE);

            resultSet = stmt.executeQuery(DatabaseQueries.SELECT_ROOMS);
            if (!resultSet.next()) {
                stmt.executeUpdate(DatabaseQueries.STARTING_10_ROOMS_INSERT_QUERY);
            }
            resultSet = stmt.executeQuery(DatabaseQueries.CHECK_SERVICES);
            if (!resultSet.next()) {
                stmt.executeUpdate(DatabaseQueries.STARTING_EXTRASERVICES);
            } else {
              resultSet.close();
            }
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;

    }

}
