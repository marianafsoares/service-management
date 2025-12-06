package services.afip;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility helper to split a command line string into tokens while honoring quoted segments.
 */
public final class CommandLineTokenizer {

    private CommandLineTokenizer() {
        // utility class
    }

    public static List<String> tokenize(String commandLine) {
        List<String> tokens = new ArrayList<>();
        if (commandLine == null) {
            return tokens;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;

        for (int i = 0; i < commandLine.length(); i++) {
            char c = commandLine.charAt(i);
            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else {
                if (Character.isWhitespace(c)) {
                    if (current.length() > 0) {
                        tokens.add(current.toString());
                        current.setLength(0);
                    }
                } else if (c == '\"' || c == '\'') {
                    inQuotes = true;
                    quoteChar = c;
                } else {
                    current.append(c);
                }
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}
