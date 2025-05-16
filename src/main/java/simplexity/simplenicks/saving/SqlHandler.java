package simplexity.simplenicks.saving;

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

@SuppressWarnings({"CallToPrintStackTrace", "BooleanMethodIsAlwaysInverted"})
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
                        PRIMARY KEY (uuid),
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


    @Nullable
    public UUID nickAlreadySavedTo(@Nullable UUID uuidToExclude, String normalizedName) {
        String queryString = "SELECT 1 FROM current_nicknames WHERE nickname = ? "
                             + ((uuidToExclude != null) ? "AND uuid != ? " : "") + "LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, String.valueOf(normalizedName));
            if (uuidToExclude != null) statement.setString(2, String.valueOf(uuidToExclude));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException e) {
            logger.severe("Failed to check if nickname exists: " + normalizedName);
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Nullable
    public List<Nickname> getSavedNicknamesForPlayer(UUID uuid) {
        List<Nickname> savedNicknames = new ArrayList<>();
        if (!playerSaveExists(uuid)) return savedNicknames;
        String queryString = "SELECT nickname, normalized FROM saved_nicknames WHERE uuid = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, String.valueOf(uuid));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String nicknameString = resultSet.getString("nickname");
                String normalizedString = resultSet.getString("normalized");
                Nickname nick = new Nickname(nicknameString, normalizedString);
                savedNicknames.add(nick);
            }
        } catch (SQLException e) {
            logger.severe("Failed to get saved nicknames for player: " + uuid);
            e.printStackTrace();
            return null;
        }
        return savedNicknames;
    }

    public boolean userAlreadySavedThisName(UUID uuid, String nickname) {
        if (!playerSaveExists(uuid)) return false;
        String queryString = "SELECT nickname FROM saved_nicknames WHERE uuid = ? AND nickname = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, String.valueOf(uuid));
            statement.setString(2, nickname);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.warning("Failed to check if UUID '" + uuid + "' has already saved the nickname '" + nickname + "'");
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public Nickname getCurrentNicknameForPlayer(UUID uuid) {
        if (!playerSaveExists(uuid)) return null;
        String queryString = "SELECT nickname, normalized FROM current_nicknames WHERE uuid = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement getStatement = connection.prepareStatement(queryString);
            getStatement.setString(1, String.valueOf(uuid));
            ResultSet resultSet = getStatement.executeQuery();
            if (!resultSet.next()) return null;
            String nickString = resultSet.getString("nickname");
            String normalizedString = resultSet.getString("normalized");
            return new Nickname(nickString, normalizedString);
        } catch (SQLException e) {
            logger.warning("Failed to get active nickname for UUID: " + uuid);
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public List<UUID> getUuidsOfNickname(String normalizeName) {
        String queryString = "SELECT uuid FROM current_nicknames WHERE normalized = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, normalizeName);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return new ArrayList<>();
            List<UUID> uuids = new ArrayList<>();
            while (resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString("uuid")));
            }
            return uuids;
        } catch (SQLException e) {
            logger.warning("Failed to get UUID list from normalized nickname");
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveNickname(UUID uuid, String nickname, String normalizedNickname) {
        String saveString = "REPLACE INTO saved_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";
        if (!playerSaveExists(uuid)) addPlayerToPlayers(uuid);
        try (Connection connection = getConnection()) {
            PreparedStatement saveStatement = connection.prepareStatement(saveString);
            saveStatement.setString(1, String.valueOf(uuid));
            saveStatement.setString(2, nickname);
            saveStatement.setString(3, normalizedNickname);
            int rowsModified = saveStatement.executeUpdate();
            return rowsModified > 0;
        } catch (SQLException e) {
            logger.severe("Failed to save nickname: " + nickname + " for UUID: " + uuid);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteNickname(UUID uuid, String nickname) {
        if (!playerSaveExists(uuid)) return false;
        String deleteQuery = "DELETE FROM saved_nicknames WHERE uuid = ? AND nickname = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, String.valueOf(uuid));
            statement.setString(2, nickname);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.warning("Failed to delete nickname '" + nickname + "' for UUID '" + uuid + "'");
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearActiveNickname(UUID uuid) {
        if (!playerSaveExists(uuid)) return true;
        String deleteQuery = "DELETE FROM current_nicknames WHERE uuid = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, String.valueOf(uuid));
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.warning("Failed to clear active nickname for UUID: " + uuid);
            e.printStackTrace();
            return false;
        }
    }

    public boolean setActiveNickname(UUID uuid, String nicknameString, String normalizedString) {
        String setQuery = "REPLACE INTO current_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";
        if (!playerSaveExists(uuid)) addPlayerToPlayers(uuid);
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(setQuery);
            statement.setString(1, String.valueOf(uuid));
            statement.setString(2, nicknameString);
            statement.setString(3, normalizedString);
            int rowsModified = statement.executeUpdate();
            return rowsModified > 0;
        } catch (SQLException e) {
            logger.warning("Failed to set active nickname '" + nicknameString + "' for UUID '" + uuid + "'");
            e.printStackTrace();
            return false;
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
