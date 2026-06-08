package net.brabenetz.tools.smart.naming;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.Arrays;

public class SmartNamingBanner implements Banner {

    // patorjk Big font: http://patorjk.com/software/taag/#p=display&f=Big&t=%3C%3C%20Smart%20Naming%20%3E%3E&w=200
    // Lines 1-6: ASCII art; lines 7-8: underscore/dash underlines with spared g descender
    private static final String[] BANNER = {
            "    ____   _____                      _     _   _                 _              ____   ",
            "   / / /  / ____|                    | |   | \\ | |               (_)             \\ \\ \\  ",
            "  / / /  | (___  _ __ ___   __ _ _ __| |_  |  \\| | __ _ _ __ ___  _ _ __   __ _   \\ \\ \\ ",
            " < < <    \\___ \\| '_ ` _ \\ / _` | '__| __| | . ` |/ _` | '_ ` _ \\| | '_ \\ / _` |   > > >",
        "  \\ \\ \\   ____) | | | | | | (_| | |  | |_  | |\\  | (_| | | | | | | | | | | (_| |  / / /   ",
        "   \\_\\_\\ |_____/|_| |_| |_|\\__,_|_|   \\__| |_| \\_|\\__,_|_| |_| |_|_|_| |_|\\__, | /_/_/    ",
            " ____________________________________________________________________________/ |________",
            " -------------------------------------------------------------------------|___/---------",
    };

    private static final String APP_NAME = " :: Smart-Naming ::";

    private static final int STRAP_LINE_SIZE = Arrays.stream(BANNER).map(String::length).max(Integer::compare).orElse(0);

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        for (String line : BANNER) {
            printStream.println(line);
        }
        String version = SmartNamingCommandLineApplication.class.getPackage().getImplementationVersion();
        version = (version != null) ? " v" + version : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + APP_NAME.length())) {
            padding.append(" ");
        }

        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, APP_NAME, AnsiColor.DEFAULT, padding.toString(),
                AnsiStyle.FAINT, version));
        printStream.println();
    }
}