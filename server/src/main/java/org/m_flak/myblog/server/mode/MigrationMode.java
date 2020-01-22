package org.m_flak.myblog.server.mode;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.InputMismatchException;

public class MigrationMode implements RunMode {
    private Properties config;
    private Logger log;

    public MigrationMode(Properties config, Logger log) {
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
        this.log.info("Running in Migration Mode.");
        this.log.info("Starting the migration console...");
        System.out.println(
        """
        \n
        ---------------------------------------------
        Welcome to Migration Mode.
        Migration Mode is for managing your database.
        ---------------------------------------------
        Available commands:
        migrate - run migrations
        exit - exit migration mode and shutdown
        \n
        """
        );

        Scanner userInput = new Scanner(System.in);

        while (true) {
            System.out.println("server> ");
            String command = "";

            try {
                command = userInput.nextLine();
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid command.\n"+e.getMessage());
                continue;
            }

            if (command.toLowerCase().matches("(exit+)")) {
                System.out.println("Shutting down...");
                break;
            }
            else if (command.toLowerCase().matches("(migrate+)")) {
                System.out.println("Preparing to run migrations on database...");
            }
        }

        userInput.close();
    }
}
