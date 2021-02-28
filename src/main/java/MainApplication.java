import config.DatabaseConnection;
import config.DatabaseQueries;
import model.Client;
import model.ExtraService;
import model.Room;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MainApplication {

    private static final Connection connection = DatabaseConnection.main();

    private static Client login(String login, String password) {

        Client client = new Client();
        try {
            PreparedStatement statement = connection.prepareStatement(DatabaseQueries.LOGIN_QUERY);
            statement.setString(1,login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                client.setClientID(resultSet.getInt("client_id"));
                client.setClientName(resultSet.getString("client_name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return client;
    }

    private static boolean register(Client client) {

        try {
            PreparedStatement statement = connection.prepareStatement(DatabaseQueries.REGISTER_QUERY);
            statement.setString(1,client.getClientName());
            statement.setString(2,client.getPassword());
            int rows = statement.executeUpdate();
            return rows > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private static ArrayList<Room> showAvailableRooms() {
        ArrayList<Room> roomArrayList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(DatabaseQueries.SELECT_AVAILABLE_ROOMS);
            while (resultSet.next()) {
                Room room = new Room(resultSet.getInt("room_id"),
                        resultSet.getBoolean("isAvailable"),
                        resultSet.getBoolean("isBooked"));
                roomArrayList.add(room);
            }
            for (Room room: roomArrayList) {
                System.out.println(room);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return roomArrayList;
    }

    private static void registerClient(Client client, int choice) {

        Scanner sc = new Scanner(System.in);
        ArrayList<Room> roomArrayList = showAvailableRooms();
        if (roomArrayList.size() > 0) {
            System.out.println("What room you wanna take?");
            int room = sc.nextInt();
            if (roomArrayList.stream().anyMatch(el -> el.getRoomID()==room)) {
                switch (choice) {
                    case 1:
                        try {
                            PreparedStatement statement = connection.prepareStatement(DatabaseQueries.REGISTER_FOR_ONE_DAY);
                            statement.setInt(1,client.getClientID());
                            statement.setInt(2,room);
                            statement.setInt(3,room);
                            int rows = statement.executeUpdate();
                            if (rows > 0) {
                                System.out.println("You have been registered!");
                            } else {
                                System.out.println("Something went wrong");
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        break;
                    case 2:
                        System.out.println("For how many days you want to stay?");
                        int days = sc.nextInt();
                        Date date = new Date();
                        date.setTime(date.getTime()+days*24*60*60*1000);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            PreparedStatement statement = connection.prepareStatement(DatabaseQueries.REGISTER_FOR_SEVERAL_DAYS);
                            statement.setInt(1,client.getClientID());
                            statement.setInt(2, room);
                            statement.setDate(3, java.sql.Date.valueOf(simpleDateFormat.format(date)));
                            statement.setInt(4, days*5000);
                            statement.setInt(5,room);
                            int rows = statement.executeUpdate();
                            if (rows > 0) {
                                System.out.println("You have been registered!");
                            } else {
                                System.out.println("Something went wrong");
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        break;
                }
            } else {
                System.out.println("No such room");
            }
        }

    }

    private static void showAllClients() {

        try {
            int counter = 1;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(DatabaseQueries.ALL_LODGERS);
            while (resultSet.next()) {
                System.out.println("Client #"+counter+" - " + resultSet.getString("client_name") + "" +
                        " is living in room #" + resultSet.getInt("room_id") + " from " +
                        resultSet.getDate("date_from")+ " for " + resultSet.getInt("how_much_days") + " days!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private static void showCertainClient(String name) {

        try {
            PreparedStatement statement = connection.prepareStatement(DatabaseQueries.LODGERS);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet == null) {
                System.out.println("There is no such client right now");
                return;
            }
            while (resultSet.next()) {
                System.out.println("Client " + resultSet.getString("client_name") + "" +
                        " is living in room #" + resultSet.getInt("room_id") + " from " +
                        resultSet.getDate("date_from")+ " for " + resultSet.getInt("how_much_days") + " days!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private static void findClient() {

        Scanner sc = new Scanner(System.in);
        System.out.println(
                "1) Show all clients \n"+
                "2) Find client"
        );
        switch (sc.nextInt()) {
            case 1:
                showAllClients();
                break;
            case 2:
                System.out.println("Who you want to find?");
                showCertainClient(sc.next());
                break;
            default:
                System.out.println("Something went wrong");
                break;
        }


    }

    private static void bookRoom(Client client) {

        Scanner sc = new Scanner(System.in);
        System.out.println("On what day you want to make a book? yyyy-MM-dd Format");
        String date = sc.next();
        System.out.println("For how many days you want to be there?");
        int howMany = sc.nextInt();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            parsedDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            System.out.println("Invalid input");
            return;
        }
        Date date_to = new Date();
        date_to.setTime(parsedDate.getTime() + (howMany * 24 * 60 * 60 * 1000));
        ArrayList<Room> availableRooms = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(DatabaseQueries.SELECT_BOOKED_ROOMS);
            statement.setDate(1, java.sql.Date.valueOf(simpleDateFormat.format(parsedDate)));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Room room = new Room(resultSet.getInt(1), resultSet.getBoolean(2), resultSet.getBoolean(3));
                availableRooms.add(room);
            }
            System.out.println("These rooms are available to book");
            for (Room room: availableRooms) {
                System.out.println(room);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (availableRooms.size() > 0) {
            System.out.println("What room you wanna take?");
            int room = sc.nextInt();
            if (availableRooms.stream().anyMatch(el -> el.getRoomID()==room)) {
                try {
                    PreparedStatement statement = connection.prepareStatement(DatabaseQueries.BOOK_ROOM);
                    statement.setInt(1, client.getClientID());
                    statement.setInt(2, room);
                    statement.setDate(3, java.sql.Date.valueOf(simpleDateFormat.format(parsedDate)));
                    statement.setDate(4, java.sql.Date.valueOf(simpleDateFormat.format(date_to)));
                    statement.setInt(5, howMany*5000);
                    statement.setInt(6,room);
                    int rows = statement.executeUpdate();
                    if (rows > 0) {
                        System.out.println("You have been booked on " + simpleDateFormat.format(parsedDate));
                    } else {
                        System.out.println("Something went wrong");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    private static void extraService(Client client) {

        Scanner sc = new Scanner(System.in);

        try {
            ArrayList<ExtraService> extraServices = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(DatabaseQueries.CHECK_SERVICES);
            while (resultSet.next()) {
                ExtraService extraService = new ExtraService(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3));
                extraServices.add(extraService);
            }
            int counter = 1;
            if (extraServices.size() > 0) {
                for (ExtraService service: extraServices) {
                    System.out.println(counter + ") " + service);
                }
            } else {
                System.out.println("No services yet");
            }
            System.out.println("Pick what you want!");
            int choice = sc.nextInt();
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (extraServices.stream().anyMatch(el -> el.getServiceID()==choice)) {
                PreparedStatement preparedStatement = connection.prepareStatement(DatabaseQueries.INSERT_EXTRA_SERVICE);
                preparedStatement.setInt(1,client.getClientID());
                preparedStatement.setInt(2,choice);
                preparedStatement.setDate(3, java.sql.Date.valueOf(simpleDateFormat.format(date)));
                int rows = preparedStatement.executeUpdate();
                if (rows > 0) {
                    System.out.println("You order has been proceed");
                } else {
                    System.out.println("Something went wrong");
                }
            } else{
                System.out.println("Input mismatch");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private static void passOfTheRoom(Client client) {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DatabaseQueries.GET_YOUR_ROOMS);
            preparedStatement.setInt(1,client.getClientID());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("Your room is " + resultSet.getInt(1));
            }
            preparedStatement = connection.prepareStatement(DatabaseQueries.CHECK_FOR_FARE);
            preparedStatement.setInt(1, client.getClientID());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("You have a delay pay extra money!");
            }
            preparedStatement = connection.prepareStatement(DatabaseQueries.PASS_THE_ROOM);
            preparedStatement.setInt(1, client.getClientID());
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                System.out.println("You successfully passed your room. Thanks!");
            } else {
                System.out.println("You don't have any rooms");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void hotelInterface(Client client) {
        Scanner sc = new Scanner(System.in);
        loop:while(true) {
            System.out.println(
                    "These services are available: \n"+
                    "1)Register a lodger\n"+
                    "2)Available rooms\n"+
                    "3)Book a room\n"+
                    "4)Find lodger\n"+
                    "5)Extra services\n"+
                    "6)Pass off the room\n"+
                    "7)Exit"
            );
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println(
                            "1) For 1 day\n"+
                            "2) For several days "
                    );
                    choice = sc.nextInt();
                    registerClient(client, choice);
                    break;
                case 2:
                    showAvailableRooms();
                    break;
                case 3:
                    bookRoom(client);
                    break;
                case 4:
                    findClient();
                    break;
                case 5:
                    extraService(client);
                    break;
                case 6:
                    passOfTheRoom(client);
                    break;
                case 7:
                    break loop;
                default:
                    System.out.println("Something went wrong");
                    break;
            }
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Hello and Welcome to the HOTEL!");
        loop: while(true) {
            System.out.println(
                    "What do you want to do?\n"+
                            "1) Login\n"+
                            "2) Register\n"+
                            "3) Exit"
            );
            int choice = sc.nextInt();
            String login;
            String password;
            Client client = new Client();
            switch (choice) {
                case 1:
                    System.out.println("Write Name:");
                    login = sc.next();
                    System.out.println("Write Password:");
                    password = sc.next();
                    client = login(login, password);
                    if (client.getClientName() != null) {
                        hotelInterface(client);
                    } else {
                        System.out.println("Not correct login or password");
                    }
                    break;
                case 2:
                    System.out.println("Choose Name:");
                    login = sc.next();
                    System.out.println("Choose Password:");
                    password = sc.next();
                    client.setClientName(login);
                    client.setPassword(password);
                    if (register(client)) {
                        System.out.println("You have been successfully registered!\nNow you may login!");
                    } else {
                        System.out.println("Something went wrong while registration!");
                    }
                    break;
                case 3:
                    break loop;
                default:
                    System.out.println("Something went wrong!");
            }
        }

    }

}
