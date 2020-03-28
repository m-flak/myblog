package org.m_flak.myblog.server.mode;

import java.lang.Runnable;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.InputMismatchException;

import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBRecord;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import org.m_flak.myblog.server.db.ServerDatabase;
import org.m_flak.myblog.server.sec.PasswordEncryptor;
import org.m_flak.myblog.server.sec.EncryptedPassword;

import static org.m_flak.myblog.server.db.methods.UserMethods.createUser;

public class ConsoleMode implements RunMode {
    private final Properties config;
    private final Logger log;

    public ConsoleMode(Properties config, Logger log) {
        this.config = config;
        this.log = log;
    }

    @Override
    public int enterMode() {
        this.run();
        return 0;
    }

    @Override
    public void run() {
        this.log.info("Running in Console Mode.");
        this.log.info("Starting interactive command shell...");
        System.out.println(
        """
        \n
        ------------------------------------------------------
        Welcome to the Command Shell.
        Here you can execute commands to manage the server or
         to perform maintenance tasks.
        ------------------------------------------------------
        Available commands:
        create user - create a new user
        show users - show registered users
        exit - exit the command shell and shutdown
        \n
        """
        );

        Scanner userInput = new Scanner(System.in);

        while (true) {
            System.out.println("server> ");
            String command;

            try {
                command = userInput.nextLine();
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid command.\n"+e.getMessage());
                continue;
            }

            if (command.toLowerCase().matches("(create\\suser+)")) {
                String username = "";
                String email = "";
                String password = "";
                String aboutUser = "";

                // User name
                while (true) {
                    try {
                        System.out.println("Enter User Name:");
                        username = userInput.nextLine();
                    }
                    catch (InputMismatchException e) {
                        System.out.println("Error: "+e.getMessage());
                    }
                    finally {
                        if (username.length() > 0) {
                            break;
                        }
                        continue;
                    }
                }

                // Email
                while (true) {
                    try {
                        System.out.println("Enter User Email:");
                        email = userInput.nextLine();
                    }
                    catch (InputMismatchException e) {
                        System.out.println("Error: "+e.getMessage());
                    }
                    finally {
                        if (email.length() > 0) {
                            break;
                        }
                        continue;
                    }
                }

                // Password
                while (true) {
                    try {
                        System.out.println("Enter User Password:");
                        password = userInput.nextLine();
                    }
                    catch (InputMismatchException e) {
                        System.out.println("Error: "+e.getMessage());
                    }
                    finally {
                        if (password.length() > 0) {
                            break;
                        }
                        continue;
                    }
                }

                // Create encrypted password, clear plaintext password from mem
                PasswordEncryptor pency = new PasswordEncryptor();
                EncryptedPassword pass = pency.encrypt(new String(password));
                // remove cleartext from mem ASAP
                password = "";

                // "About Me"
                while (true) {
                    try {
                        System.out.println("Enter User's \"About Me\" [PRESS ENTER WHEN DONE]:");
                        aboutUser = userInput.nextLine();
                    }
                    catch (InputMismatchException e) {
                        System.out.println("Error: "+e.getMessage());
                    }
                    finally {
                        if (aboutUser.length() > 0) {
                            break;
                        }
                        continue;
                    }
                }

                this.log.info(String.format("Create User: '%s' w/ address '%s'.",
                                            username,
                                            email
                             ));

                final String finUser = username;
                final String finEmail = email;
                final EncryptedPassword finPass = pass;
                final String finAbout = aboutUser;

                ServerDatabase.inst().runOnDB(new Runnable() {
                    @Override
                    public void run() {
                        var con = ServerDatabase.inst().conn();
                        var driv = ServerDatabase.inst().driv();
                        var db = ServerDatabase.inst().db().get();

                        db.open(driv, con);

                        DBRecord newUser = createUser(
                            finUser,
                            finEmail,
                            finAbout,
                            finPass.getPassword(),
                            db,
                            con
                        );

                        if (newUser.getState() == DBRecord.State.Valid) {
                            ServerDatabase.inst().getLogger().info(
                                    String.format("Created user '%s' with ID: %d.",
                                            finUser,
                                            newUser.getValue(0))
                            );

                            db.commit(con);
                        }
                        else {
                            ServerDatabase.inst().getLogger().severe(
                                "FAILURE WHEN CREATING USER!"
                            );
                        }

                        db.close(con);
                    }
                });
            }

            if (command.toLowerCase().matches("(show\\susers+)")) {
                System.out.println("\nLooking up users...\n");

                ServerDatabase.inst().runOnDB(new Runnable() {
                    @Override
                    public void run() {
                        var con = ServerDatabase.inst().conn();
                        var driv = ServerDatabase.inst().driv();
                        var db = ServerDatabase.inst().db().get();

                        db.open(driv, con);

                        // Count number of users
                        DBCommand countCmd = db.createCommand();
                        countCmd.select(db.T_USERS.count());
                        long countUsers =
                            db.querySingleLong(countCmd, 0L, con);

                        if (countUsers > 0L) {
                            DBCommand idCmd = db.createCommand();
                            idCmd.select(db.T_USERS.C_USER_ID);
                            DBCommand unameCmd = db.createCommand();
                            unameCmd.select(db.T_USERS.C_NAME);
                            DBCommand emailCmd = db.createCommand();
                            emailCmd.select(db.T_USERS.C_EMAIL);

                            MutableListFactoryImpl factory = new MutableListFactoryImpl();

                            MutableList<Long> idees =
                                factory.withAll(db.querySimpleList(long.class, idCmd, con));

                            MutableList<String> usernames =
                                factory.withAll(db.querySimpleList(String.class, unameCmd, con));

                            MutableList<String> emails =
                                factory.withAll(db.querySimpleList(String.class, emailCmd, con));

                            /* I reeeeeaaaaaallly miss Python ;_; */

                            // Zip -> (User, Email)
                            var userEmailZipped = usernames.zip(emails);

                            // Zip Again -> (ids, (User, Email))
                            for (var idUE : idees.zip(userEmailZipped)) {
                                // output
                                System.out.println(String.format(
                                        "\nID: %d | Name: %s | Email: %s",
                                        idUE.getOne(),
                                        idUE.getTwo().getOne(),
                                        idUE.getTwo().getTwo()
                                ));
                            }
                        }
                        else {
                            System.out.println("No users were found in the database.");
                        }

                        db.close(con);
                    }
                });
            }

            if (command.toLowerCase().matches("(exit+)")) {
                System.out.println("Shutting down...");
                break;
            }

        }

        userInput.close();
    }
}
