package simplexity.simplenicks.util.saving;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            PreparedStatement childStatement = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS nicknames (
                    uuid VARCHAR(36) NOT NULL,
                    nickname VARCHAR(255) NOT NULL,
                    in_use BOOLEAN NOT NULL DEFAULT 0,
                    normalized VARCHAR(255) NOT NULL,
                    PRIMARY KEY (uuid, nickname),
                    FOREIGN KEY (uuid)
                    REFERENCES players(uuid)
                    ON DELETE CASCADE
                    );
                    """);
            childStatement.execute();
        } catch (SQLException e) {
            logger.severe("Issue connecting to database, info below: ");
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
