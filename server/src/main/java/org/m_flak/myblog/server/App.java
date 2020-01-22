package org.m_flak.myblog.server;

import java.util.Arrays;
import java.util.Properties;
import java.util.logging.*;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.tuple.Tuples;

import org.m_flak.myblog.server.mode.*;

public class App
{
    public static final LogManager logging = LogManager.getLogManager();
    public static final Logger logger = Logger.getLogger("org.m_flak.myblog.server", null);
    public static final Properties properties = new Properties();

    /** In case we are missing config file & pass no arguments...**/
    private static final Pair<String, String> defaultHost =
        Tuples.pair("host", "127.0.0.1");
    private static final Pair<String, String> defaultPort =
        Tuples.pair("port", "8188");
    private static final Pair<String, String> defaultMode =
        Tuples.pair("mode", "server");

    /** Initialize the logger prior to the execution of main() **/
    static {
        App.logging.addLogger(App.logger);
    }

    private static void changeConfigProp(String key, String value) {
        App.logger.info(String.format("CONFIG CHANGE: '%s' to %s.", key, value));
        App.properties.setProperty(key, value);
    }

    private static void checkSetRequiredProps(Pair<String, String> reqProp) {
        if (App.properties.getProperty(reqProp.getOne()) != null) {
            return;
        }

        App.changeConfigProp(reqProp.getOne(), reqProp.getTwo());
    }

    public static void main( String[] args )
    {
        App.logger.info("Starting the myBlog Server...");

        if (args.length > 0) {
            // I want this to be easy... ;)
            if (args.length % 2 != 0) {
                App.logger.severe(
                    String.format("Invalid number of arguments: %d.", args.length)
                );
                App.logger.severe("Proper argument usage: \"--arg1 val --arg2 val\"...");
                return;
            }

            // Begin argument parsing
            MutableList<String> allArgs = FastList.newList(Arrays.asList(args));

            // Seperate args & values like so: -cmd X --> [cmd..], [X...]
            MutableList<String> argSwitches =
                allArgs.partition(dash_arg -> dash_arg.startsWith("-") || dash_arg.startsWith("--"))
                       .getSelected();
            MutableList<String> argValues =
                allArgs.withoutAll(argSwitches);

            // Populate our config properties class with the cmdline args
            for (Pair<String, String> kv : argSwitches.zip(argValues)) {
                String key = kv.getOne().replaceAll("^\\-+", "");
                String val = kv.getTwo();

                App.changeConfigProp(key, val);
            }
        } else {
            App.logger.info("NO ARGUMENTS PROVIDED!");
            App.logger.info("DEFAULTS SHALL BE USED.");
        }

        // Set the defaults for any required params not passed in as args
        App.checkSetRequiredProps(App.defaultHost);
        App.checkSetRequiredProps(App.defaultPort);
        App.checkSetRequiredProps(App.defaultMode);

        String mode = App.properties.getProperty("mode");
        switch (mode) {
            case "server" -> new ServerMode(App.properties, App.logger).enterMode();
            case "migrate" -> new MigrationMode(App.properties, App.logger).enterMode();
            case "shell" -> new ConsoleMode(App.properties, App.logger).enterMode();
        }
    }
}
