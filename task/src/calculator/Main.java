package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            String input = scanner.nextLine();

            if (input.contains("/exit")) {
                System.out.println("Bye!");
                break;
            }

            if (input.contains("/help")) {
                System.out.println("The program calculates the sum and difference of numbers");
            }

            else if (input.contains("/") && !input.matches("[help|exit]")) {
                System.out.println("Unknown command");
            }

            else if (input.matches("[^0-9]") || input.matches("[\\+-]$")) {
                System.out.println("Invalid expression");
            }

            else if (input.isBlank() || input.isEmpty()) {
                // do nothing
            }

            else {
                calculateExpression(input);
            }
        }
    }

    /*
    this expression now contains numbers and/or operators
     */
    private static void calculateExpression(String expression) {
        if (!checkExpression(expression)) {
            System.out.println("Invalid expression");
            return;
        }

        calculateNumbers(expression.split(" "));
    }

    /*
    the operators must be at least group numbers - 1 or more
    which means each group of digits must have operators in b/w them
     */
    private static boolean checkExpression(String expression) {
        Pattern numbers = Pattern.compile("([\\d]+)");
        Matcher matcher = numbers.matcher(expression);

        int numbersCount = 0;

        while (matcher.find()) {
            numbersCount++;
        }

        Pattern operators = Pattern.compile("[0-9 ]+([\\+-]+)");
        matcher = operators.matcher(expression);

        int operatorsCount = 0;

        while (matcher.find()) {
            operatorsCount++;
        }

        return (operatorsCount >= numbersCount - 1);
    }

    private static void calculateNumbers(String[] input) {
        int result = 0;
        String op = "";

        Pattern addition = Pattern.compile("\\++");
        Pattern subtraction = Pattern.compile("-+");

        Matcher matcher;

        try {
            for (int i = 0; i < input.length; i++) {
                matcher = addition.matcher(input[i]);
                if (matcher.matches()) {
                    op = "+";
                    continue;
                }

                matcher = subtraction.matcher(input[i]);
                if (matcher.matches()) {
                    int length = input[i].split("").length;
                    if (length > 1 && length % 2 == 0) {
                        op = "+";
                    }
                    else {
                        op = "-";
                    }
                    continue;
                }
                // then it is a number
                if (op.equals("+")) {
                    result += Integer.parseInt(input[i]);
                }
                else if (op.equals("-")) {
                    result -= Integer.parseInt(input[i]);
                }
                else {
                    result = Integer.parseInt(input[i]);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid expression");
            return;
        }

        System.out.println(result);
    }
}
