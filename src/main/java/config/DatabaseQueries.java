package config;

public class DatabaseQueries {

    public static String CREATE_CLIENT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "client(client_id SERIAL PRIMARY KEY," +
            "client_name VARCHAR(32) UNIQUE," +
            "password VARCHAR(32))";
    public static String CREATE_ROOM_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "room(room_id SERIAL PRIMARY KEY," +
            "isAvailable boolean," +
            "isBooked boolean)";
    public static String CREATE_SERVICE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "service(service_id SERIAL PRIMARY KEY," +
            "client_id integer REFERENCES client(client_id)," +
            "room_id integer REFERENCES room(room_id)," +
            "date_from date NOT NULL DEFAULT CURRENT_DATE," +
            "date_to date NOT NULL DEFAULT CURRENT_DATE+1 CHECK(date_to > date_from)," +
            "price integer NOT NULL DEFAULT 5000)";
    public static String CREATE_SERVICES_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "services(service_id SERIAL PRIMARY KEY," +
            "type_of_service VARCHAR(32)," +
            "price int)";
    public static String CREATE_EXTRASERVICE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "extraservice(service_id SERIAL PRIMARY KEY," +
            "client_id integer REFERENCES client(client_id)," +
            "type_of_service integer REFERENCES services(service_id)," +
            "order_date date NOT NULL DEFAULT CURRENT_DATE)";
    public static String STARTING_10_ROOMS_INSERT_QUERY = "INSERT INTO room(isAvailable,isBooked) " +
            "VALUES(true,false),(true,false),(true,false),(true,false),(true,false)," +
            "(true,false),(true,false),(true,false),(true,false),(true,false)";
    public static String STARTING_EXTRASERVICES = "INSERT INTO services(type_of_service, price) " +
            "VALUES('lunch', 1500),('alcohol', 2000),('fruits', 800),('swimming pool', 1000)";


    //Room table queries
    public static String SELECT_ROOMS = "SELECT * FROM room";
    public static String SELECT_AVAILABLE_ROOMS = "SELECT * FROM room WHERE isAvailable=true";
    public static String SELECT_BOOKED_ROOMS = "SELECT room.* from room LEFT JOIN service ON room.room_id = service.room_id " +
            "WHERE (room.isAvailable = true AND room.isBooked = false) OR " +
            "(room.isBooked = false AND service.date_to < ?)";

    //Service table queries
    public static String BOOK_ROOM = "INSERT INTO service(client_id, room_id, date_from, date_to, price)" +
            "VALUES(?,?,?,?,?);" +
            "UPDATE room SET isBooked = true WHERE room_id = ?";
    public static String REGISTER_FOR_ONE_DAY = "INSERT INTO service(client_id, room_id) VALUES(?, ?);" +
            "UPDATE room SET isAvailable = false WHERE room_id = ?";
    public static String REGISTER_FOR_SEVERAL_DAYS = "INSERT INTO service(client_id, room_id, date_to, price) VALUES(?, ?, ?, ?);" +
            "UPDATE room SET isAvailable = false WHERE room_id = ?";
    public static String LODGERS = "SELECT client.client_name, service.room_id, service.date_from, " +
            "service.date_to - service.date_from AS how_much_days from service " +
            "JOIN client ON client.client_id = service.client_id WHERE LOWER(client.client_name) = LOWER(?) AND (CURRENT_DATE BETWEEN date_from AND date_to)";
    public static String ALL_LODGERS = "SELECT client.client_name, service.room_id, service.date_from, " +
            "service.date_to - service.date_from AS how_much_days from service " +
            "JOIN client ON client.client_id = service.client_id WHERE CURRENT_DATE BETWEEN date_from AND date_to";
    public static String GET_YOUR_ROOMS = "select service.room_id from service join room on service.room_id = room.room_id " +
            "where room.isAvailable = false AND service.date_from <= CURRENT_DATE AND client_id = ?";
    public static String CHECK_FOR_FARE = "select service.room_id from service join room on service.room_id = room.room_id " +
            "where room.isAvailable = false AND service.date_to < CURRENT_DATE AND client_id = ?";
    public static String PASS_THE_ROOM = "update room set isAvailable = true where room_id = (" +
            "select service.room_id from service join room on service.room_id = room.room_id " +
            "where room.isAvailable = false AND service.date_from <= CURRENT_DATE AND client_id = ? " +
            ")";

    //Client table queries
    public static String LOGIN_QUERY = "SELECT * FROM client WHERE client_name = ? AND password = ?";
    public static String REGISTER_QUERY = "INSERT INTO client(client_name, password) VALUES(?,?)";

   //Extra Services
   public static String CHECK_SERVICES = "SELECT * FROM services";
   public static String INSERT_EXTRA_SERVICE = "INSERT INTO extraservice(client_id,type_of_service, order_date) " +
           "VALUES(?,?,?)";

}