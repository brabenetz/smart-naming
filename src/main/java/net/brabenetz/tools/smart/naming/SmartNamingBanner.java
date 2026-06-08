package net.brabenetz.tools.smart.naming;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.Arrays;

public class SmartNamingBanner implements Banner {

    private static final String[] BANNER = {
            "  _____  __  __    _    ____ _____ ",
            " / ____| |  \\/  |  / \\  |  _ \\_   _|",
            "| (___   | |\\/| | / _ \\ | |_) || |  ",
            " \\___ \\  | |  | |/ ___ \\|  _ < | |  ",
            " ____) | | |  | / ___ \\| |_) || |_ ",
            "|_____/  |_|  |_/_/   \\_\\____/_____|",
            "                                    ",
            "    _   _    _    _   _ _____ ___  ",
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