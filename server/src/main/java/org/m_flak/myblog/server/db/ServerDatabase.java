package org.m_flak.myblog.server.db;

import java.lang.Runnable;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.logging.*;

import org.apache.empire.db.DBDatabaseDriver;

import org.m_flak.myblog.server.ServerConstants;
import org.m_flak.myblog.server.db.DatabaseConfig;
import org.m_flak.myblog.server.db.model.Database;

public class ServerDatabase {
    private final Database dbDatabase = new Database();
    private final ExecutorService dbService = Executors.newFixedThreadPool(ServerConstants.MAX_THREADS);
    private final DatabaseConfig dbConfig = new DatabaseConfig();
    private final Logger dbLog = Logger.getLogger("org.m_flak.myblog.server.db", null);

    private Connection dbConnection = null;
    private DBDatabaseDriver dbDriver = null;

    // Singleton instance
    private static ServerDatabase sdInstance = null;

    private ServerDatabase() {
        this.dbConfig.init(null);
    }

    public static ServerDatabase inst() {
        if (ServerDatabase.sdInstance == null) {
            ServerDatabase.sdInstance = new ServerDatabase();
        }

        return ServerDatabase.sdInstance;
    }

    public static boolean isAnInstance() {
        return (ServerDatabase.sdInstance != null);
    }

    public Logger getLogger() {
        return this.dbLog;
    }

    public Connection conn() {
        if (this.dbConnection == null) {
            this.dbLog.info("Creating initial database connection.");
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                this.dbConnection = DriverManager.getConnection(
                    this.dbConfig.getJdbcURL(),
                    this.dbConfig.getJdbcUser(),
                    this.dbConfig.getJdbcPwd()
                );
                this.dbConnection.setAutoCommit(false);

                this.dbLog.info("Initial connection successful.");
            }
            catch (Exception e) {
                this.dbLog.severe("FAILURE: "+this.dbConfig.getJdbcURL());
                this.dbLog.severe(e.toString());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return this.dbConnection;
    }

    public boolean hasConnection() {
        return (this.dbConnection != null);
    }

    public DBDatabaseDriver driv() {
        if (this.dbDriver == null) {
            try {
                this.dbDriver = (DBDatabaseDriver) Class.forName(
                                    this.dbConfig.getDriverClass())
                                .newInstance();
                this.dbConfig.readProperties(
                    this.dbDriver,
                    "properties-mysql",
                    "empireDBDriverProperites"
                );
            }
            catch(Exception e) {
                this.dbLog.severe("FAILURE: "+this.dbConfig.getDriverClass());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return this.dbDriver;
    }

    public WeakReference<Database> db() {
        return new WeakReference<Database>(this.dbDatabase);
    }

    public void runOnDB(Runnable task) {
        try {
            try {
                var future = this.dbService.submit(task);
                future.get();
            }
            catch (ExecutionException ee) {
                /* We need to sleep and retry if Empire-DB shat the bed.
                 * This seems to occur when there too many requests back-to-back.
                 */
                if (ee.getMessage().indexOf("org.apache.empire.db.exceptions.DatabaseNotOpenException") != -1) {
                    Thread.sleep(500L);
                    runOnDB(task);
                }
            }
        }
        catch (Exception e) {
            this.dbLog.severe("Failure running task!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
