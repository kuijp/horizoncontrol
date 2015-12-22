package nl.kuijp.horizoncontrol;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandSender {

    private static Options options = new Options();
    private static CommandLine cmdLine;

    public static void main(final String[] args) throws Exception {
        setupOptions();
        validateArgs(args);

        String vncKey = cmdLine.getOptionValue("k");
        String vncHost = cmdLine.getOptionValue("h");
        Integer vncPort = Integer.parseInt(cmdLine.getOptionValue("p"));

        VncSender vncSender = new VncSender(vncHost, vncPort);
        vncSender.sendKey(Keys.getByName(vncKey));
    }

    private static void setupOptions() {
        Option host = OptionBuilder.withArgName("hostname").hasArg().withDescription("hostname or ip-address to send it to").create("h");
        host.setRequired(true);
        options.addOption(host);

        Option port = OptionBuilder.withArgName("port").hasArg().withDescription("port to connect to f.i. 5900").create("p");
        port.setRequired(true);
        options.addOption(port);

        Option key = OptionBuilder.withArgName("key").hasArgs().withDescription("Key to send, (can be use multiple times)").create("k");
        key.setRequired(true);
        options.addOption(key);

        Option help = OptionBuilder.withArgName("help").hasArgs().withDescription("Print this help message").create("help");
        options.addOption(help);
    }

    private static void printHelp() {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(150);
        formatter.setOptionComparator(null);
        formatter.printHelp("java -jar horizoncontrol.jar [-help] -h <hostname> -p <port> -k <key> ", "", options, "");
    }


    private static void validateArgs(String[] args) {
        try {
            CommandLineParser parser = new GnuParser();
            // parse the command line arguments
            cmdLine = parser.parse(options, args);
            if (cmdLine.hasOption("help")) {
                printHelp();
                System.exit(0);
            }
        } catch (ParseException exp) {
            printHelp();
            System.err.println("Parsing failed.  Reason: " + exp.getMessage() + "\n");
            System.exit(-1);
        }
    }
}
