package test;

import java.util.Stack;

public class StackTest {

    public static void main (String ... args) {
        Stack<String> stack = new Stack<>();

        stack.add("1");
        stack.add("2");
        stack.add("3");
        stack.add("4");

        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }
}
