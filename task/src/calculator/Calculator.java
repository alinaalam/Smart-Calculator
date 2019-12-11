package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    private Map<String, Integer> variables;

    public Calculator() {
        this.variables = new HashMap<>();
    }

    public void processExpression(String expression) {
        if (Patterns.variables.matcher(expression).matches()) {
           addVariable(expression);
        }

        else {
            calculateExpression(expression);
        }
    }

    private void addVariable(String input) {
        if (variables.containsKey(input)) {
            System.out.println(variables.get(input));
        }
        else {
            System.out.println("Unknown variable");
        }
    }

    /*
    check for variable assignment
     */
    public void calculateExpression(String expression) {
        if (checkForVariableAssignment(expression)) {
            return;
        }

        if (!checkExpression(expression)) {
            System.out.println("Invalid expression");
            return;
        }

        calculateNumbers(expression.split(" "));
    }

    private boolean checkForVariableAssignment(String expression) {
        Pattern assignment = Pattern.compile("([a-zA-Z]+)[ ]*=[ ]*([0-9]+|[a-zA-Z]+)");
        Matcher matcher = assignment.matcher(expression);

        if (matcher.matches()) {
            String variable = matcher.group(1);
            int value;

            try {
                value = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                String var = matcher.group(2);
                if (variables.containsKey(var)) {
                    value = variables.get(var);
                }
                else {
                    System.out.println("Unknown variable");
                    return true;
                }
            }
            variables.getOrDefault(variable, 0);
            variables.put(variable, value);
            return true;
        }

        // a1 = 8
        assignment = Pattern.compile("([\\w]+)[ ]*=[ ]*([0-9]+)");
        matcher = assignment.matcher(expression);

        if (matcher.matches()) {
            System.out.println("Invalid identifier");
            return true;
        }

        // a = 7 = 8
        Pattern equality = Pattern.compile("[=]");
        if (equality.matcher(expression).find()) {
            System.out.println("Invalid assignment");
            return true;
        }

        return false;
    }

    /*
    the operators must be at least group numbers - 1 or more
    which means each group of digits must have operators in b/w them
     */
    private boolean checkExpression(String expression) {
        Pattern numbers = Pattern.compile("([\\w]+)");
        Matcher matcher = numbers.matcher(expression);

        int numbersCount = 0;

        while (matcher.find()) {
            numbersCount++;
        }

        Pattern operators = Pattern.compile("[\\w ]+([\\+-]+)");
        matcher = operators.matcher(expression);

        int operatorsCount = 0;

        while (matcher.find()) {
            operatorsCount++;
        }

        return (operatorsCount >= numbersCount - 1);
    }

    private void calculateNumbers(String[] input) {
        int result = 0;
        String op = "";

        Pattern addition = Pattern.compile("\\++");
        Pattern subtraction = Pattern.compile("-+");
        Pattern numbers = Pattern.compile("\\d+");
        Pattern alphabets = Pattern.compile("[a-zA-Z]+");

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
                // then it is a number or a variable
                Matcher m = alphabets.matcher(input[i]);
                int value;

                if (m.matches()) {
                    value = variables.get(input[i]);
                }
                else {
                    value = Integer.parseInt(input[i]);
                }

                if (op.equals("+")) {
                    result += value;
                }
                else if (op.equals("-")) {
                    result -= value;
                }
                else {
                    result = value;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid expression");
            return;
        }

        System.out.println(result);
    }
}
