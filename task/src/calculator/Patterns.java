package calculator;

import java.util.regex.Pattern;

public class Patterns {

    public static Pattern commands = Pattern.compile("(^/)(help|exit)$");
    public static Pattern variables = Pattern.compile("[\\w]+");
    public static Pattern expression = Pattern.compile("(\\w+)?([\\/\\*\\+-]+)?([\\(\\[{])?([}\\]\\)])?");
}
