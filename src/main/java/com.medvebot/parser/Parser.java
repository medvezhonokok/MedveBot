package com.medvebot.parser;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Stack;

/**
 * @author : medvezhonokok
 * @mailto : nocap239@gmail.com
 * @created : 21.06.2022, вторник
 **/
public class Parser {
    private static String string;
    public static final Stack<BigInteger> stack = new Stack<>();

    public Parser(String rpn) {
        string = rpn;
        parse();
    }

    @SneakyThrows
    public String parse() {
        StringBuilder sb = new StringBuilder();
        String[] s = string.split(" ");

        for (String value : s) {
            if (isNumber(value)) {
                stack.push(new BigInteger(value));
            } else {
                if (Objects.equals(value, "+")) {
                    BigInteger first = stack.pop();
                    BigInteger second = stack.pop();
                    stack.push(first.add(second));
                }

                if (Objects.equals(value, "*")) {
                    BigInteger first = stack.pop();
                    BigInteger second = stack.pop();
                    stack.push(first.multiply(second));
                }

                if (Objects.equals(value, "-")) {
                    BigInteger first = stack.pop();
                    BigInteger second = stack.pop();
                    stack.push(second.subtract(first));
                }

                if (Objects.equals(value, "/")) {
                    BigInteger first = stack.pop();
                    BigInteger second = stack.pop();
                    stack.push(second.divide(first));
                }

                if (Objects.equals(value, "%")) {
                    BigInteger first = stack.pop();
                    BigInteger second = stack.pop();
                    stack.push(second.mod(first));
                }

                if (Objects.equals(value, "~")) {
                    BigInteger a = stack.pop();
                    stack.push(a.sqrt());
                }
            }
        }

        for (BigInteger bi : stack) {
            sb.append(bi).append(" ");
        }

        stack.clear();
        return sb.toString();
    }

    private boolean isNumber(String que) {
        int count = 0;

        for (int i = 0; i < que.length(); i++) {
            if (que.charAt(i) < '0' || que.charAt(i) > '9') {
                count++;
            }
        }

        if (que.length() == 1) {
            return que.charAt(0) >= '0' && que.charAt(0) <= '9';
        } else if (que.length() > 1) {
            if (que.charAt(0) == '-' || que.charAt(0) == '+') {
                return count == 1;
            }
        }

        return count == 0;
    }
}
