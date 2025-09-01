package simplexity.simplenicks.saving;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
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

/**
 * Handles all SQL operations for the SimpleNicks plugin.
 * <p>
 * This class uses HikariCP for connection pooling and supports both
 * SQLite and MySQL backends, configured via {@link ConfigHandler}.
 * It manages database schema initialization, migrations, and provides
 * methods to store and retrieve player nickname data.
 * </p>
 *
 * <p><b>Key responsibilities:</b></p>
 * <ul>
 *     <li>Initialize database schema (players, nicknames, schema version).</li>
 *     <li>Insert, update, and delete nickname records.</li>
 *     <li>Query player nickname and login data.</li>
 *     <li>Batch migration of nickname records.</li>
 * </ul>
 */
@SuppressWarnings({"CallToPrintStackTrace", "BooleanMethodIsAlwaysInverted"})
public class SqlHandler {
    private static SqlHandler instance;

    public SqlHandler() {
    }

    /**
     * Returns the instance of the SQL handler
     *
     * @return SqlHandler
     */
    @NotNull
    public static SqlHandler getInstance() {
        if (instance == null) instance = new SqlHandler();
        return instance;
    }

    private static final HikariConfig hikariConfig = new HikariConfig();
    private static HikariDataSource dataSource;
    private final Logger logger = SimpleNicks.getInstance().getSLF4JLogger();
    private static final int SCHEMA_VERSION = 1;

    /**
     * Initializes the database, creating tables if they do not exist
     * and applying schema migrations when necessary.
     */
    public void init() {
        setupConfig();
        try (Connection connection = getConnection()) {
            debug("Creating schema_version table...");
            PreparedStatement versionStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS schema_version (
                        version INTEGER NOT NULL
                    );
                    """);
            versionStatement.execute();
            PreparedStatement checkVersion = connection.prepareStatement("SELECT COUNT(*) FROM schema_version;");
            ResultSet versionCheck = checkVersion.executeQuery();
            if (versionCheck.next() && versionCheck.getInt(1) == 0) {
                PreparedStatement insertVersion = connection.prepareStatement("INSERT INTO schema_version (version) VALUES (?);");
                insertVersion.setInt(1, SCHEMA_VERSION);
                insertVersion.executeUpdate();
                debug("Inserted initial schema version {}", SCHEMA_VERSION);
            } else {
                int currentVersion = getSchemaVersion();
                debug("Existing schema version found: {}", currentVersion);
                if (currentVersion < SCHEMA_VERSION) {
                    runMigrations(connection, currentVersion);
                }
            }
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

    /**
     * Checks if a normalized nickname is already in use by other players.
     *
     * @param uuidToExclude UUID to ignore in the check (can be {@code null})
     * @param normalized    the normalized nickname to check
     * @return a list of UUIDs using the nickname, or {@code null} if query failed
     */
    @Nullable
    public List<UUID> nickAlreadySavedTo(@Nullable UUID uuidToExclude, @NotNull String normalized) {
        debug("Checking if nickname '{}' is already in use (excluding UUID='{}')", normalized,
                uuidToExclude);
        String queryString = "SELECT uuid FROM current_nicknames WHERE nickname = ?";
        List<UUID> uuidsWithName = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, normalized);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                if (uuid.equals(uuidToExclude)) continue;
                uuidsWithName.add(uuid);
                debug("Nickname found in use by UUID={}", uuid);
            }
            return uuidsWithName;
        } catch (SQLException e) {
            logger.warn("Failed to check if nickname exists: {}", normalized, e);
            return null;
        }
    }

    /**
     * Gets all saved nicknames for a given player.
     *
     * @param uuid the player's UUID
     * @return a list of saved nicknames, or {@code null} if query failed
     */
    @Nullable
    public List<Nickname> getSavedNicknamesForPlayer(@NotNull UUID uuid) {
        debug("Fetching saved nicknames for UUID={}", uuid);
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
                debug("Found saved nickname: '{}' (normalized '{}')", nicknameString, normalizedString);
                savedNicknames.add(nick);
            }
        } catch (SQLException e) {
            logger.warn("Failed to get saved nicknames for player with UUID: {}", uuid, e);
            return null;
        }
        return savedNicknames;
    }

    /**
     * Checks if a player has already saved a specific nickname.
     *
     * @param uuid     the player's UUID
     * @param nickname the nickname string to check
     * @return true if the nickname is already saved, false otherwise
     */

    public boolean userAlreadySavedThisName(@NotNull UUID uuid, @NotNull String nickname) {
        debug("Checking if UUID={} already saved nickname='{}'", uuid, nickname);
        if (!playerSaveExists(uuid)) return false;
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
    }


    /**
     * Gets the player's currently active nickname.
     *
     * @param uuid the player's UUID
     * @return the current nickname, or {@code null} if none is set
     */
    @Nullable
    public Nickname getCurrentNicknameForPlayer(@NotNull UUID uuid) {
        debug("Fetching current nickname for UUID={}", uuid);
        if (!playerSaveExists(uuid)) return null;
        String queryString = "SELECT nickname, normalized FROM current_nicknames WHERE uuid = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement getStatement = connection.prepareStatement(queryString);
            getStatement.setString(1, String.valueOf(uuid));
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                String nickString = resultSet.getString("nickname");
                String normalized = resultSet.getString("normalized");
                debug("Current nickname found: '{}' (normalized '{}')", nickString, normalized);
                return new Nickname(nickString, normalized);
            }
            return null;
        } catch (SQLException e) {
            logger.warn("Failed to get active nickname for UUID: {}", uuid, e);
            return null;
        }

    }

    /**
     * Looks up all UUIDs that currently use a given normalized nickname.
     *
     * @param normalized the normalized nickname
     * @return a list of UUIDs, or {@code null} if query failed
     */
    @Nullable
    public List<UUID> getUuidsOfNickname(@NotNull String normalized) {
        debug("Looking up UUIDs using normalized nickname='{}'", normalized);
        String queryString = "SELECT uuid FROM current_nicknames WHERE normalized = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, normalized);
            ResultSet resultSet = statement.executeQuery();
            List<UUID> uuids = new ArrayList<>();
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("uuid"));
                uuids.add(id);
                debug("Found UUID={} for nickname", id);
            }
            return uuids;
        } catch (SQLException e) {
            logger.warn("Failed to get UUID list from normalized nickname: {}", normalized, e);
            return null;
        }
    }

    /**
     * Saves a nickname for a player in the database.
     *
     * @param uuid      the player's UUID
     * @param username  the last known username
     * @param nickname  the chosen nickname
     * @param normalized normalized form of the nickname
     * @return true if the nickname was saved successfully, false otherwise
     */
    public boolean saveNickname(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname, @NotNull String normalized) {
        debug("Saving nickname '{}' (normalized '{}') for UUID={}, username={}", nickname,
                normalized, uuid, username);
        String saveString = "REPLACE INTO saved_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";
        if (!playerSaveExists(uuid)) {
            if (!updatePlayerTable(uuid, username)) return false;
        }
        try (Connection connection = getConnection()) {
            PreparedStatement saveStatement = connection.prepareStatement(saveString);
            saveStatement.setString(1, String.valueOf(uuid));
            saveStatement.setString(2, nickname);
            saveStatement.setString(3, normalized);
            int rowsChanged = saveStatement.executeUpdate();
            debug("Rows modified when saving nickname: {}", rowsChanged);
            return rowsChanged > 0;
        } catch (SQLException e) {
            logger.warn("Failed to save nickname '{}' for UUID '{}'. Normalized nickname: {}, Username: {} ",
                    nickname, uuid, normalized, username, e);
            return false;
        }
    }


    /**
     * Deletes a saved nickname for a player.
     *
     * @param uuid     the player's UUID
     * @param nickname the nickname to delete
     * @return true if the nickname was deleted, false otherwise
     */
    public boolean deleteNickname(@NotNull UUID uuid, @NotNull String nickname) {
        debug("Deleting nickname '{}' for UUID={}", nickname, uuid);
        if (!playerSaveExists(uuid)) return false;
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
    }


    /**
     * Clears the player's currently active nickname.
     *
     * @param uuid the player's UUID
     * @return true if a nickname was cleared, false otherwise
     */

    public boolean clearActiveNickname(@NotNull UUID uuid) {
        debug("Clearing current nickname for UUID={}", uuid);
        if (!playerSaveExists(uuid)) return false;
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
    }

    /**
     * Sets the player's active nickname, replacing any existing one.
     *
     * @param uuid       the player's UUID
     * @param username   the player's last known username
     * @param nickname   the new nickname
     * @param normalized normalized form of the nickname
     * @return true if the nickname was set, false otherwise
     */

    public boolean setActiveNickname(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname,
                                     @NotNull String normalized) {
        debug("Setting active nickname '{}' (normalized '{}') for UUID={}, username={}", nickname,
                normalized, uuid, username);
        String setQuery = "REPLACE INTO current_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";
        if (!playerSaveExists(uuid)) {
            if (!updatePlayerTable(uuid, username)) return false;
        }
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(setQuery);
            statement.setString(1, String.valueOf(uuid));
            statement.setString(2, nickname);
            statement.setString(3, normalized);
            int rowsModified = statement.executeUpdate();
            debug("Rows affected setting active nickname: {}", rowsModified);
            return rowsModified > 0;
        } catch (SQLException e) {
            logger.warn("Failed to set active nickname '{}' for UUID '{}'. Normalized name: {}, Username: {}",
                    nickname, uuid, normalized, username, e);
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Checks if the player exists in the database.
     *
     * @param uuid the player's UUID
     * @return true if the player exists, false otherwise
     */

    public boolean playerSaveExists(@NotNull UUID uuid) {
        String queryString = "SELECT 1 FROM players WHERE uuid = ? LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, String.valueOf(uuid));
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            debug("Check if player UUID=%s exists: %s", uuid, exists);
            return exists;
        } catch (SQLException e) {
            logger.warn("Failed to check if player with UUID {} exists", uuid, e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the last login timestamp of a player by username.
     *
     * @param username   the player's username
     * @param expiryTime maximum age in milliseconds (negative for no limit)
     * @return last login time, or {@code null} if not found
     */

    @Nullable
    public Long lastLoginOfUsername(@NotNull String username, long expiryTime) {
        debug("Fetching last login for username='%s', expiry=%g", username, expiryTime);
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
    }

    /**
     * Gets the last login timestamp of a player by UUID.
     *
     * @param uuid       the player's UUID
     * @param expiryTime maximum age in milliseconds (negative for no limit)
     * @return last login time, or {@code null} if not found
     */
    @Nullable
    public Long lastLoginOfUuid(@NotNull UUID uuid, long expiryTime) {
        debug("Fetching last login for UUID='%s', expiry=%g", uuid, expiryTime);
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
    }

    /**
     * Inserts or updates a player in the players table.
     *
     * @param uuid     the player's UUID
     * @param username the player's last known username
     * @return true if the row was inserted/updated successfully
     */
    @SuppressWarnings("ExtractMethodRecommender")
    public boolean updatePlayerTable(@NotNull UUID uuid, @NotNull String username) {
        debug("Saving player to players table: UUID=%s, username=%g", uuid, username);
        boolean isMySql = ConfigHandler.getInstance().isMySql();
        String insertQuery;
        if (isMySql) {
            insertQuery = """
                    INSERT INTO players (uuid, last_known_name, last_login)
                    VALUES (?, ?, CURRENT_TIMESTAMP)
                    ON DUPLICATE KEY UPDATE
                        last_known_name = VALUES(last_known_name),
                        last_login = CURRENT_TIMESTAMP
                    """;
        } else {
            insertQuery = """
                    INSERT INTO players (uuid, last_known_name, last_login)
                    VALUES (?, ?, CURRENT_TIMESTAMP)
                    ON CONFLICT(uuid) DO UPDATE SET
                        last_known_name = excluded.last_known_name,
                        last_login = CURRENT_TIMESTAMP
                    """;
        }
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, String.valueOf(uuid));
            statement.setString(2, username.toLowerCase());
            int rowsChanged = statement.executeUpdate();
            debug("Rows affected in savePlayerToPlayers: %d", rowsChanged);
            return rowsChanged > 0;
        } catch (SQLException e) {
            logger.warn("Failed to save player with UUID: {}, username: {}", uuid, username, e);
            return false;
        }
    }

    /**
     * Inserts multiple nickname records in a single batch transaction.
     * <p>
     * This is mainly used during migrations or large imports.
     * </p>
     *
     * @param records list of nickname records
     * @return true if the batch insert succeeded, false otherwise
     */
    public boolean batchInsertNicknames(@NotNull List<NicknameRecord> records) {
        if (records.isEmpty()) return false;
        boolean isMySQL = ConfigHandler.getInstance().isMySql();
        String insertPlayerSQL = isMySQL
                ? "INSERT IGNORE INTO players (uuid, last_known_name, last_login) VALUES (?, ?, ?)"
                : "INSERT OR IGNORE INTO players (uuid, last_known_name, last_login) VALUES (?, ?, ?)";

        String insertCurrentNickSQL = isMySQL
                ? "REPLACE INTO current_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)"
                : "INSERT OR REPLACE INTO current_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";

        String insertSavedNickSQL = isMySQL
                ? "INSERT IGNORE INTO saved_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)"
                : "INSERT OR IGNORE INTO saved_nicknames (uuid, nickname, normalized) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement addPlayerStatement = connection.prepareStatement(insertPlayerSQL);
             PreparedStatement currentNicknameStatement = connection.prepareStatement(insertCurrentNickSQL);
             PreparedStatement savedNicknameStatement = connection.prepareStatement(insertSavedNickSQL)) {

            connection.setAutoCommit(false);
            int batchCount = 0;
            final int COMMIT_THRESHOLD = 500;

            for (NicknameRecord record : records) {
                addPlayerStatement.setString(1, record.uuid().toString());
                addPlayerStatement.setString(2, record.username());
                addPlayerStatement.setString(3, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(record.lastLogin())));
                addPlayerStatement.addBatch();

                if (record.isActiveNickname()) {
                    currentNicknameStatement.setString(1, record.uuid().toString());
                    currentNicknameStatement.setString(2, record.nickname());
                    currentNicknameStatement.setString(3, record.normalized());
                    currentNicknameStatement.addBatch();
                } else {
                    savedNicknameStatement.setString(1, record.uuid().toString());
                    savedNicknameStatement.setString(2, record.nickname());
                    savedNicknameStatement.setString(3, record.normalized());
                    savedNicknameStatement.addBatch();
                }

                batchCount++;

                if (batchCount >= COMMIT_THRESHOLD) {
                    addPlayerStatement.executeBatch();
                    currentNicknameStatement.executeBatch();
                    savedNicknameStatement.executeBatch();
                    connection.commit();
                    batchCount = 0;
                }
            }

            addPlayerStatement.executeBatch();
            currentNicknameStatement.executeBatch();
            savedNicknameStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            logger.error("Error migrating nickname records", e);
            return false;
        }
        return true;
    }

    /**
     * Closes the database connection pool. Used during reloads or shutdown
     */
    public void closeDatabase() {
        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
    }

    /**
     * Configures the HikariCP datasource based on the plugin configuration.
     * Supports SQLite and MySQL.
     */
    public void setupConfig() {
        if (!ConfigHandler.getInstance().isMySql()) {
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + SimpleNicks.getInstance().getDataFolder() + "/simplenicks.db?foreign_keys=on");
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setConnectionTestQuery("PRAGMA journal_mode = WAL;");
            dataSource = new HikariDataSource(hikariConfig);
            debug("Initialized SQLite connection.");
            return;
        }
        hikariConfig.setJdbcUrl("jdbc:mysql://" + ConfigHandler.getInstance().getMySqlIp() + "/" + ConfigHandler.getInstance().getMySqlName());
        hikariConfig.setUsername(ConfigHandler.getInstance().getMySqlUsername());
        hikariConfig.setPassword(ConfigHandler.getInstance().getMySqlPassword());
        hikariConfig.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(hikariConfig);
        debug("Initialized MySQL connection to '{}'", hikariConfig.getJdbcUrl());
    }

    /**
     * Returns the current schema version stored in the database.
     *
     * @return schema version, or -1 if unavailable
     */
    public int getSchemaVersion() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT version FROM schema_version LIMIT 1;");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("version");
            }
        } catch (SQLException e) {
            logger.warn("Failed to get schema version", e);
        }
        return -1;
    }

    private void runMigrations(Connection connection, int fromVersion) {
        try {
            debug("Running migrations from schema version {}", fromVersion);
            PreparedStatement updateVersion = connection.prepareStatement("UPDATE schema_version SET version = ?;");
            updateVersion.setInt(1, SCHEMA_VERSION);
            updateVersion.executeUpdate();
            debug("Schema upgraded to version {}", SCHEMA_VERSION);
        } catch (SQLException e) {
            logger.error("Error running schema migrations", e);
        }
    }

    @NotNull
    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void debug(String message, Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            logger.info("[SQL DEBUG] {}, {}", message, args);
        }
    }


}
