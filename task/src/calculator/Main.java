package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();

        while(true) {
            String input = scanner.nextLine();
            Matcher matcher = Patterns.commands.matcher(input);

            if (matcher.matches()) {
                if (matcher.group(2).equals("exit")) {
                    System.out.println("Bye!");
                    break;
                }
                else if (matcher.group(2).equals("help")) {
                    System.out.println("The program calculates the sum and difference of numbers");
                }
            }

            else if (input.contains("/")) {
                System.out.println("Unknown command");
            }

            else if (input.isBlank() || input.isEmpty()) {
                continue;
            }

            else {
                calculator.processExpression(input);
            }
        }
    }
}
