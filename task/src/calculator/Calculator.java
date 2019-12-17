package calculator;

import java.util.ArrayDeque;
import java.util.Deque;
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

//        if (!checkExpression(expression)) {
//            System.out.println("Invalid expression");
//            return;
//        }

        String postfixExpression = convertInfixToPostfix(expression);
        if (postfixExpression != null) {
            calculate(postfixExpression);
        }

//        calculateNumbers(expression.split(" "));
    }

    private void calculate(String expression) {
        Deque<Integer> result = new ArrayDeque<>();
        Pattern alphabets = Pattern.compile("[a-zA-Z]+");

        for (String str : expression.split(" ")) {
            if (Patterns.variables.matcher(str).matches()) {
                // if the incoming element is the name of a variable, push its value into the stack
                if (alphabets.matcher(str).matches()) {
                    result.offerLast(variables.get(str));
                }
                // if the incoming element is a number, push it into the stack
                // (the whole number, not a single digit!)
                else {
                    result.offerLast(Integer.parseInt(str));
                }
            }
            // If the incoming element is an operator, then pop twice to get two numbers and perform
            // the operation; push the result on the stack
            else {
                int num2 = result.removeLast();
                int num1 = result.removeLast();
                if (str.equals("+")) {
                    result.offerLast(num1 + num2);
                    continue;
                }
                if (str.equals("-")) {
                    result.offerLast(num1 - num2);
                    continue;
                }
                if (str.equals("*")) {
                    result.offerLast(num1 * num2);
                    continue;
                }
                if (str.equals("/")) {
                    result.offerLast(num1 / num2);
                    continue;
                }
            }
        }

        System.out.println(result.removeLast());
    }

    private String convertInfixToPostfix(String expression) {

        Matcher matcher = Patterns.expression.matcher(expression);
        StringBuilder postfix = new StringBuilder();
        Deque<String> postfixOperators = new ArrayDeque<>();

        while (matcher.find()) {
            if (matcher.group(1) == null && matcher.group(2) == null
                    && matcher.group(3) == null && matcher.group(4) == null) {
                continue;
            }
            // add operands (numbers and variables) to the result (postfix notation) as they arrive
            if (matcher.group(1) != null) {
                postfix.append(matcher.group(1));
                postfix.append(" ");
            }

            // if the incoming element is a left parenthesis, push it on the stack
            if (matcher.group(3) != null) {
                postfixOperators.offerLast(matcher.group(3));
            }

            // if the incoming element is a right parenthesis, pop the stack and add operators to the result until you
            // see a left parenthesis. Discard the pair of parentheses
            if (matcher.group(4) != null) {
                while (true) {
                    if (postfixOperators.isEmpty()) {
                        System.out.println("Invalid expression");
                        return null;
                    }
                    if (postfixOperators.peekLast().matches("[\\({\\[]")) {
                        postfixOperators.removeLast();
                        break;
                    }
                    postfix.append(postfixOperators.removeLast());
                    postfix.append(" ");
                }
            }

            if (matcher.group(2) != null) {
                String str = sendBackOperator(matcher.group(2));

                if (str == null) {
                    System.out.println("Invalid expression");
                    return null;
                }

                // if the stack is empty or contains a left parenthesis on top, push the incoming operator on the stack
                if (postfixOperators.isEmpty() || postfixOperators.peekLast().matches("[\\({\\[]")) {
                    postfixOperators.offerLast(str);
                    continue;
                }

                // if the incoming operator has higher precedence than the top of the stack, push it on the stack
                if (postfixOperators.peekLast().matches("[\\+-]") && str.matches("[\\*/]")) {
                    postfixOperators.offerLast(str);
                    continue;
                }

                // if the incoming operator has lower or equal precedence than or to the top of the stack, pop the stack
                // and add operators to the result until you see an operator that has a smaller precedence or a left
                // parenthesis on the top of the stack; then add the incoming operator to the stack
                while (true) {
                    if (postfixOperators.isEmpty() || postfixOperators.peekLast().matches("[\\({\\[]")) {
                        postfixOperators.offerLast(str);
                        break;
                    }
                    // equal precedence
                    if (postfixOperators.peekLast().matches("[\\*/]") && str.matches("[\\*/]") ||
                            (postfixOperators.peekLast().matches("[\\+-]") && str.matches("[\\+-]"))) {
                        postfix.append(postfixOperators.removeLast());
                        postfix.append(" ");
                        continue;
                    }
                    // lower precedence
                    if (postfixOperators.peekLast().matches("[\\*/]") && str.matches("[\\+-]")) {
                        postfix.append(postfixOperators.removeLast());
                        postfix.append(" ");
                        continue;
                    }
                }
            }
        }

        // add all the remaining operators
        while (!postfixOperators.isEmpty()) {
            String operator = postfixOperators.removeLast();
            if (operator.matches("[\\({\\[\\)}\\]]")) {
                System.out.println("Invalid expression");
                return null;
            }
            postfix.append(operator);
            postfix.append(" ");
        }

        // remove trailing spaces
        return postfix.toString().trim();
    }

    private String sendBackOperator(String expression) {

        Pattern addition = Pattern.compile("\\++");
        Pattern subtraction = Pattern.compile("-+");
        Pattern multiplication = Pattern.compile("\\*");
        Pattern division = Pattern.compile("/");

        if (multiplication.matcher(expression).matches()) {
            return "*";
        }

        if (division.matcher(expression).matches()) {
            return "/";
        }

        if (addition.matcher(expression).matches()) {
            return "+";
        }

        if (subtraction.matcher(expression).matches()) {
            int length = expression.length();
            if (length > 1 && length % 2 == 0) {
               return "+";
            }
            else {
                return "-";
            }
        }

        return null;
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
