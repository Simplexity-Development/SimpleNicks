package simplexity.simplenicks.util.saving;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("CallToPrintStackTrace")
public class SqlHandler {
    private static SqlHandler instance;

    public SqlHandler() {
    }

    public static SqlHandler getInstance() {
        if (instance == null) instance = new SqlHandler();
        return instance;
    }

    private static final HikariConfig hikariConfig = new HikariConfig();
    private static HikariDataSource dataSource;
    private final Logger logger = SimpleNicks.getSimpleNicksLogger();

    public void init() {
        try (Connection connection = getConnection()) {
            PreparedStatement parentStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS players (
                    uuid VARCHAR(36) PRIMARY KEY,
                    last_login DATETIME NOT NULL
                    );
                    """);
            parentStatement.execute();
            PreparedStatement savedNickStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS saved_nicknames (
                    uuid VARCHAR(36) NOT NULL,
                    nickname VARCHAR(255) NOT NULL,
                    normalized VARCHAR(255) NOT NULL,
                    PRIMARY KEY (uuid, nickname),
                    FOREIGN KEY (uuid)
                    REFERENCES players(uuid)
                    ON DELETE CASCADE
                    );
                    """);
            savedNickStatement.execute();
            PreparedStatement currentNickStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS current_nicknames (
                        uuid VARCHAR(36) NOT NULL,
                        nickname VARCHAR(255) NOT NULL,
                        normalized VARCHAR(255) NOT NULL,
                        PRIMARY KEY (uuid, nickname),
                        FOREIGN KEY (uuid)
                        REFERENCES players(uuid)
                        ON DELETE CASCADE
                    );
                    """);
            currentNickStatement.execute();
        } catch (SQLException e) {
            logger.severe("Issue connecting to database, info below: ");
            e.printStackTrace();
        }
    }

    public boolean playerSaveExists(UUID uuid) {
        String queryString = "SELECT 1 FROM players WHERE uuid = ? LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, String.valueOf(uuid));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.severe("Failed to check if player exists: " + uuid);
            e.printStackTrace();
            return false;
        }
    }

    public boolean nickAlreadyExists(String normalizedName) {
        String queryString = "SELECT 1 FROM current_nicknames WHERE nickname = ? LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, String.valueOf(normalizedName));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.severe("Failed to check if nickname exists: " + normalizedName);
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public List<Nickname> getSavedNicknamesForPlayer(UUID uuid) {
        List<Nickname> savedNicknames = new ArrayList<>();
        String queryString = "SELECT nickname AND normalized FROM saved_nicknames WHERE uuid = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, String.valueOf(uuid));
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            while (resultSet.next()) {
                String nicknameString = resultSet.getString("nickname");
                String normalizedString = resultSet.getString("normalized");
                Nickname nick = new Nickname(nicknameString, normalizedString);
                savedNicknames.add(nick);
            }
        } catch (SQLException e) {
            logger.severe("Failed to get saved nicknames for player: " + uuid);
            e.printStackTrace();
        }
        return savedNicknames;
    }

    public void saveNickname(UUID uuid, String nickname, String normalizedNickname) {
        String saveString = "REPLACE INTO saved_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";
        if (!playerSaveExists(uuid)) addPlayerToPlayers(uuid);
        try (Connection connection = getConnection()) {
            PreparedStatement saveStatement = connection.prepareStatement(saveString);
            saveStatement.setString(1, String.valueOf(uuid));
            saveStatement.setString(2, nickname);
            saveStatement.setString(3, normalizedNickname);
            saveStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed to save nickname: " + nickname + " for UUID: " + uuid);
            e.printStackTrace();
        }
    }

    private void addPlayerToPlayers(UUID uuid) {
        String insertQuery = "REPLACE INTO players (uuid, last_login) VALUES (?, CURRENT_TIMESTAMP)";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, String.valueOf(uuid));
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed to save player: " + uuid);
            e.printStackTrace();
        }
    }

    public void setupConfig() {
        if (!ConfigHandler.getInstance().isMySql()) {
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + SimpleNicks.getInstance().getDataFolder() + "/simplenicks.db?foreign_keys=on");
            dataSource = new HikariDataSource(hikariConfig);
            return;
        }
        hikariConfig.setJdbcUrl("jdbc:mysql://" + ConfigHandler.getInstance().getMySqlIp() + "/" + ConfigHandler.getInstance().getMySqlName());
        hikariConfig.setUsername(ConfigHandler.getInstance().getMySqlUsername());
        hikariConfig.setPassword(ConfigHandler.getInstance().getMySqlPassword());
        dataSource = new HikariDataSource(hikariConfig);
    }

    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


}
