package simplexity.simplenicks.saving;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
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
import java.util.concurrent.CompletableFuture;


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
    private final Logger logger = SimpleNicks.getInstance().getSLF4JLogger();

    public void init() {
        try (Connection connection = getConnection()) {
            debug("Creating players table...");
            PreparedStatement parentStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS players (
                    uuid VARCHAR(36) PRIMARY KEY NOT NULL,
                    last_known_name VARCHAR(16) NOT NULL,
                    last_login DATETIME NOT NULL
                    );
                    """);
            parentStatement.execute();
            debug("Creating saved_nicknames table...");
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
            debug("Creating current_nicknames table...");
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
            logger.warn("Issue connecting to database: {} ", e.getMessage(), e);
        }
    }


    public CompletableFuture<List<UUID>> nickAlreadySavedTo(@Nullable UUID uuidToExclude, String normalizedName) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Checking if nickname '{}' is already in use (excluding UUID='{}')", normalizedName, uuidToExclude);
            String queryString = "SELECT uuid FROM current_nicknames WHERE nickname = ?";
            List<UUID> uuidsWithName = new ArrayList<>();
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryString);
                statement.setString(1, String.valueOf(normalizedName));
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    if (uuid.equals(uuidToExclude)) continue;
                    uuidsWithName.add(uuid);
                    debug("Nickname found in use by UUID={}", uuid);
                }
                return uuidsWithName;
            } catch (SQLException e) {
                logger.warn("Failed to check if nickname exists: {}", normalizedName, e);
                return null;
            }
        });
    }

    public CompletableFuture<List<Nickname>> getSavedNicknamesForPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Fetching saved nicknames for UUID={}", uuid);
            List<Nickname> savedNicknames = new ArrayList<>();
            if (!playerSaveExists(uuid).join()) return savedNicknames;
            String queryString = "SELECT nickname, normalized FROM saved_nicknames WHERE uuid = ?";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryString);
                statement.setString(1, String.valueOf(uuid));
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String nicknameString = resultSet.getString("nickname");
                    String normalizedString = resultSet.getString("normalized");
                    Nickname nick = new Nickname(nicknameString, normalizedString);
                    debug("Found saved nickname: '{}' (normalized '{}')", nicknameString, normalizedString);
                    savedNicknames.add(nick);
                }
            } catch (SQLException e) {
                logger.warn("Failed to get saved nicknames for player with UUID: {}", uuid, e);
                return null;
            }
            return savedNicknames;
        });
    }

    public CompletableFuture<Boolean> userAlreadySavedThisName(UUID uuid, String nickname) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Checking if UUID={} already saved nickname='{}'", uuid, nickname);
            if (!playerSaveExists(uuid).join()) return false;
            String queryString = "SELECT nickname FROM saved_nicknames WHERE uuid = ? AND nickname = ?";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryString);
                statement.setString(1, String.valueOf(uuid));
                statement.setString(2, nickname);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                logger.warn("Failed to check if UUID '{}' has already saved the nickname '{}'", uuid, nickname, e);
                return false;
            }
        });
    }


    public CompletableFuture<Nickname> getCurrentNicknameForPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Fetching current nickname for UUID={}", uuid);
            if (!playerSaveExists(uuid).join()) return null;
            String queryString = "SELECT nickname, normalized FROM current_nicknames WHERE uuid = ?";
            try (Connection connection = getConnection()) {
                PreparedStatement getStatement = connection.prepareStatement(queryString);
                getStatement.setString(1, String.valueOf(uuid));
                ResultSet resultSet = getStatement.executeQuery();
                if (resultSet.next()) {
                    String nickString = resultSet.getString("nickname");
                    String normalizedString = resultSet.getString("normalized");
                    debug("Current nickname found: '{}' (normalized '{}')", nickString, normalizedString);
                    return new Nickname(nickString, normalizedString);
                }
                return null;
            } catch (SQLException e) {
                logger.warn("Failed to get active nickname for UUID: {}", uuid, e);
                return null;
            }
        });
    }


    public CompletableFuture<List<UUID>> getUuidsOfNickname(String normalizedName) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Looking up UUIDs using normalized nickname='{}'", normalizedName);
            String queryString = "SELECT uuid FROM current_nicknames WHERE normalized = ?";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryString);
                statement.setString(1, normalizedName);
                ResultSet resultSet = statement.executeQuery();
                List<UUID> uuids = new ArrayList<>();
                while (resultSet.next()) {
                    UUID id = UUID.fromString(resultSet.getString("uuid"));
                    uuids.add(id);
                    debug("Found UUID={} for nickname", id);
                }
                return uuids;
            } catch (SQLException e) {
                logger.warn("Failed to get UUID list from normalized nickname: {}", normalizedName, e);
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> saveNickname(UUID uuid, String username, String nickname, String normalizedNickname) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Saving nickname '{}' (normalized '{}') for UUID={}, username={}", nickname, normalizedNickname, uuid, username);
            String saveString = "REPLACE INTO saved_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";
            if (!playerSaveExists(uuid).join()) {
                if (!updatePlayerTableSqlite(uuid, username).join()) return false;
            }
            try (Connection connection = getConnection()) {
                PreparedStatement saveStatement = connection.prepareStatement(saveString);
                saveStatement.setString(1, String.valueOf(uuid));
                saveStatement.setString(2, nickname);
                saveStatement.setString(3, normalizedNickname);
                int rowsChanged = saveStatement.executeUpdate();
                debug("Rows modified when saving nickname: {}", rowsChanged);
                return rowsChanged > 0;
            } catch (SQLException e) {
                logger.warn("Failed to save nickname '{}' for UUID '{}'. Normalized nickname: {}, Username: {} ",
                        nickname, uuid, normalizedNickname, username, e);
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> deleteNickname(UUID uuid, String nickname) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Deleting nickname '{}' for UUID={}", nickname, uuid);
            if (!playerSaveExists(uuid).join()) return null;
            String deleteQuery = "DELETE FROM saved_nicknames WHERE uuid = ? AND nickname = ?";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(deleteQuery);
                statement.setString(1, String.valueOf(uuid));
                statement.setString(2, nickname);
                int rowsChanged = statement.executeUpdate();
                debug("Rows affected in delete: {}", rowsChanged);
                return rowsChanged > 0;
            } catch (SQLException e) {
                logger.warn("Failed to delete nickname '{}' for UUID '{}'", nickname, uuid, e);
                return false;
            }
        });
    }


    public CompletableFuture<Boolean> clearActiveNickname(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Clearing current nickname for UUID={}", uuid);
            if (!playerSaveExists(uuid).join()) return false;
            String deleteQuery = "DELETE FROM current_nicknames WHERE uuid = ?";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(deleteQuery);
                statement.setString(1, String.valueOf(uuid));
                int rowsAffected = statement.executeUpdate();
                debug("Rows affected in clear: {}", rowsAffected);
                return rowsAffected > 0;
            } catch (SQLException e) {
                logger.warn("Failed to clear active nickname for UUID '{}'", uuid, e);
                return false;
            }
        });
    }


    public CompletableFuture<Boolean> setActiveNickname(UUID uuid, String username, String nicknameString, String normalizedString) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Setting active nickname '{}' (normalized '{}') for UUID={}, username={}", nicknameString, normalizedString, uuid, username);
            String setQuery = "REPLACE INTO current_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";
            if (!playerSaveExists(uuid).join()) {
                if (!updatePlayerTableSqlite(uuid, username).join()) return false;
            }
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(setQuery);
                statement.setString(1, String.valueOf(uuid));
                statement.setString(2, nicknameString);
                statement.setString(3, normalizedString);
                int rowsModified = statement.executeUpdate();
                debug("Rows affected setting active nickname: {}", rowsModified);
                return rowsModified > 0;
            } catch (SQLException e) {
                logger.warn("Failed to set active nickname '{}' for UUID '{}'. Normalized name: {}, Username: {}",
                        nicknameString, uuid, normalizedString, username, e);
                e.printStackTrace();
                return false;
            }
        });
    }


    public CompletableFuture<Boolean> playerSaveExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String queryString = "SELECT 1 FROM players WHERE uuid = ? LIMIT 1";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryString);
                statement.setString(1, String.valueOf(uuid));
                ResultSet resultSet = statement.executeQuery();
                boolean exists = resultSet.next();
                debug("Check if player UUID={} exists: {}", uuid, exists);
                return exists;
            } catch (SQLException e) {
                logger.warn("Failed to check if player with UUID {} exists", uuid, e);
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Long> lastLongOfUsername(String username, long expiryTime) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Fetching last login for username='{}', expiry={}", username, expiryTime);
            String queryString = "SELECT last_login FROM players WHERE last_known_name = ? AND (? < 0 OR last_login >= ?)";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryString);
                long minTimeStamp = System.currentTimeMillis() - expiryTime;
                statement.setString(1, username);
                statement.setLong(2, expiryTime);
                statement.setLong(3, minTimeStamp);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) return resultSet.getLong("last_login");
                return null;
            } catch (SQLException e) {
                logger.warn("Failed to get last login for username: {}, expiry time: {}", username, expiryTime, e);
                return null;
            }
        });
    }

    public CompletableFuture<Long> lastLongOfUuid(UUID uuid, long expiryTime) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Fetching last login for UUID='{}', expiry={}", uuid, expiryTime);
            String queryString = "SELECT last_login FROM players WHERE uuid = ? AND (? < 0 OR last_login >= ?)";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryString);
                long minTimeStamp = System.currentTimeMillis() - expiryTime;
                statement.setString(1, String.valueOf(uuid));
                statement.setLong(2, expiryTime);
                statement.setLong(3, minTimeStamp);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) return resultSet.getLong("last_login");
                return null;
            } catch (SQLException e) {
                logger.warn("Failed to get last login for UUID: {}, with expiry time: {}", uuid, expiryTime, e);
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> updatePlayerTableSqlite(UUID uuid, String username) {
        return CompletableFuture.supplyAsync(() -> {
            debug("Saving player to players table: UUID={}, username={}", uuid, username);
            String insertQuery = "INSERT INTO players (uuid, last_known_name, last_login) " +
                                 "VALUES (?, ?, CURRENT_TIMESTAMP)" +
                                 "ON CONFLICT(uuid) DO UPDATE SET " +
                                 "last_known_name = excluded.last_known_name," +
                                 "last_login = CURRENT_TIMESTAMP";
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(insertQuery);
                statement.setString(1, String.valueOf(uuid));
                statement.setString(2, username.toLowerCase());
                int rowsChanged = statement.executeUpdate();
                debug("Rows affected in savePlayerToPlayers: {}", rowsChanged);
                return rowsChanged > 0;
            } catch (SQLException e) {
                logger.warn("Failed to save player with UUID: {}, username: {}", uuid, username, e);
                return false;
            }
        });
    }

    public void setupConfig() {
        if (!ConfigHandler.getInstance().isMySql()) {
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + SimpleNicks.getInstance().getDataFolder() + "/simplenicks.db?foreign_keys=on");
            dataSource = new HikariDataSource(hikariConfig);
            debug("Initialized SQLite connection.");
            return;
        }
        hikariConfig.setJdbcUrl("jdbc:mysql://" + ConfigHandler.getInstance().getMySqlIp() + "/" + ConfigHandler.getInstance().getMySqlName());
        hikariConfig.setUsername(ConfigHandler.getInstance().getMySqlUsername());
        hikariConfig.setPassword(ConfigHandler.getInstance().getMySqlPassword());
        dataSource = new HikariDataSource(hikariConfig);
        debug("Initialized MySQL connection to '{}'", hikariConfig.getJdbcUrl());
    }

    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void debug(String message, Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            logger.info("[SQL DEBUG] {}, {}", message, args);
        }
    }


}
