package adhdmc.simplenicks.util;

import adhdmc.simplenicks.SimpleNicks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private static Database instance;

    private Connection nicksDatabase;

    private Database() {

    }

    public Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void connect() {
        try {
            String url = "jdbc:sqlite:" + SimpleNicks.getInstance().getDataFolder().getAbsolutePath() + "/db/nicks.db";
            nicksDatabase = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            SimpleNicks.log("There was a problem connecting to the database.");
        }
    }

    public void disconnect() {
        try {
            if (nicksDatabase != null) nicksDatabase.close();
        }
        catch (SQLException e) {
            SimpleNicks.log("There was a problem closing the connection.");
        }
    }

    /**
     * Populates tables if they do not exist.
     * TODO: Provide more information on table structure.
     */
    public void createTables() {

    }

    /**
     * Queries the nickname from the database using a UUID.
     * This is less user-friendly, but can be used by commands sent by users.
     * UUID would be preferred lookup since usernames may change.
     * @param uuid Nickname if found, null otherwise.
     */
    public String queryNickname(UUID uuid) {
        // TODO: IMPLEMENT ME
        return null;
    }

    /**
     * Queries the nickname from the database using a Player Name.
     * This is user-friendly in comparison to UUID, making it easy to look up using commands.
     * If the player is online, queryNickname(UUID) should be used instead since a username can change.
     * @param playerName Nickname if found, null otherwise.
     */
    public String queryNickname(String playerName) {
        // TODO: IMPLEMENT ME
        return null;
    }

}
