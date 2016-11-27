package moritz.experimental;

import moritz.core.BinopMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class BinExpTree {
    private Map<Integer, BinopMethod> methodMap = new HashMap<>();

    class Node {
        int data;
        Node left, right;

        Node(int instruction) {
            data = instruction;
            left = right = null;
        }
    }

    public int evaluate(Node root){
        if(root.left==null && root.right==null){
            return root.data;
        }
        else{
            int operator = root.data;
            int left = evaluate(root.left);
            int right = evaluate(root.right);

            return methodMap.get(operator).invoke(left, right);
        }
    }

    // Returns root of constructed tree for given
    // postfix expression
    Node constructTree(Queue<Integer> postfix) {
        Stack<Node> st = new Stack();
        Node t, t1, t2;

        // Traverse through every character of
        // input expression
        while(!postfix.isEmpty()) {
            int datum = postfix.poll();
            // If operand, simply push into stack
            if (!isOperator(datum)) {
                t = new Node(datum);
                st.push(t);
            } else // operator
            {
                t = new Node(datum);

                // Pop two top nodes
                // Store top
                t1 = st.pop();      // Remove top
                t2 = st.pop();

                //  make them children
                t.right = t1;
                t.left = t2;

                // System.out.println(t1 + "" + t2);
                // Add this subexpression to stack
                st.push(t);
            }
        }

        //  only element will be root of expression tree
        t = st.peek();
        st.pop();

        return t;
    }

    public boolean isOperator(int datum){
        return true;
    }

}
