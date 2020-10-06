import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.HashMap;

public class ParserFake {
    ParserFake() {
        root = new MainNode();
        current_snode = new SequenceNode(root);
        root.setNextSNode(current_snode);
        try {
            arbore = new BufferedWriter(new FileWriter("arbore"));
            output = new BufferedWriter(new FileWriter("output"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum State {
        NO_STATE, INITIALIZATION, ATRIBUTION
    }

    enum StatementState {
        NO_STATE, IF_START, IF_T, IF_O, WHILE_START, WHILE_B, CONDITION
    }

    HashMap<String, Integer> var_to_value = new HashMap<String, Integer>();
    Stack<ValNode> aValNodes = new Stack<ValNode>();
    Stack<ValNode> bValNodes = aValNodes;//new Stack<ValNode>();
    MainNode root;
    SNode current_snode;
    Stack<Expr> exprs = new Stack<Expr>();
    Stack<Expr> root_exprs = new Stack<Expr>();
    Stack<Stack<Expr>> past_exprs = new Stack<>();
    State state = State.INITIALIZATION;
    StatementState stmState = StatementState.NO_STATE;
    Stack<StatementState> past_stmStates = new Stack<StatementState>();

    BufferedWriter arbore;
    BufferedWriter output;

    boolean to_end = false;
    int line_error;

    void new_expr_env(Expr root) {
        past_exprs.push(exprs);
        root_exprs.push(root);
        exprs = new Stack<>();
    }

    void restore_expr_env() {
        exprs = past_exprs.pop();
        root_exprs.pop();
    }

    void clear_expr_env() {
        past_exprs.clear();
        root_exprs.clear();
        exprs = new Stack<>();
    }

    Expr get_last_expr() {
        if (!exprs.empty()) {
            return exprs.pop();
        } else {
            return root_exprs.peek();
        }
    }

    void init_var(String new_var) {
        var_to_value.put(new_var, null);
    }
    void on_new_int(String new_int) {
        on_anode(new IntNode(Integer.parseInt(new_int)));
    }

    void on_new_var(String new_var, int line) {
        if (state == State.INITIALIZATION) {
            init_var(new_var);
        } else {
            VariableNode varNode = new VariableNode(var_to_value, new_var, line, output);
            if (state == State.NO_STATE && stmState == StatementState.NO_STATE) {
                on_assignment(varNode);
            } else {
                on_anode(varNode);
            }

            if (!to_end) {
                if (!var_to_value.containsKey(new_var)) {
                    to_end = true;
                    line_error = line;
                }
            }

        }
    }

    void on_assignment(VariableNode var) {
        AssignmentNode assNode = new AssignmentNode(current_snode, var);
        var.setParent(assNode);
        new_expr_env(assNode);
        current_snode.setNextSNode(assNode);
        state = State.ATRIBUTION;
    }

    void on_end_cmd(String str) {
        if (state == State.ATRIBUTION) {
            ValNode anode = aValNodes.pop();
            Expr expr = get_last_expr();
            expr.setNextNode(anode);
            anode.setParent(expr);
            SNode new_seq = new SequenceNode(current_snode);
            current_snode.setNextSNode(new_seq);
            current_snode = new_seq;
            clear_expr_env();
        }
        state = State.NO_STATE;
    }

    void on_bnode(ValNode node) {
        on_valNode(node, bValNodes);
    }

    void on_anode(ValNode node) {
        on_valNode(node, aValNodes);
    }

    void on_valNode(ValNode node, Stack<ValNode> dest) {
        ValNode to_be_pushed = null;
        if (exprs.empty() || exprs.peek() instanceof OneOperand) {
            to_be_pushed = node;
        } else {
            Expr expr = exprs.pop();
            if (expr instanceof TwoOperands) {
                TwoOperands texpr = (TwoOperands) expr;
                expr = (Expr) texpr.getParent();
                texpr.setRightNode(node);
                to_be_pushed = (ValNode) texpr;
            }
            if (!( expr instanceof OneOperand)) {
                do {
                    expr = (Expr) get_last_expr();
                } while (!(expr instanceof OneOperand));
                to_be_pushed = expr.getLastNode();
                exprs.push(expr);
            }
        }
        if (to_be_pushed == null) {
            System.err.println("error in on_anode: to_be_pushed is null");
            System.exit(1);
        }
        dest.push(to_be_pushed);
    }

    void on_if() {
        IfNode ifNode = new IfNode(current_snode);
        current_snode.setNextSNode(ifNode);
        current_snode = ifNode;
        past_stmStates.push(stmState);
        stmState = StatementState.IF_START;
    }

    void on_while() {
        WhileNode whNode = new WhileNode(current_snode);
        current_snode.setNextSNode(whNode);
        current_snode = whNode;
        past_stmStates.push(stmState);
        stmState = StatementState.WHILE_START;
    }

    void on_two_op(TwoOperands op, ValNode lastVal) {
        Expr expr = get_last_expr();
        op.setParent(expr);
        if (lastVal instanceof TwoOperands &&
                op.getPriority() > ((TwoOperands) lastVal).getPriority()) {
            op.setLeftNode(((TwoOperands) lastVal).getRightNode());
            ((TwoOperands) lastVal).setRightNode((ValNode) op);
            lastVal.setParent(expr);
            op.setParent(lastVal);
            expr.setNextNode(lastVal);
            exprs.push(op);
        } else {
            op.setNextNode(lastVal);
            lastVal.setParent(op);
            expr.setNextNode((ValNode) op);
            exprs.push(op);
        }
    }

    void on_plus() {
        ValNode aNode = aValNodes.pop();
        PlusNode pNode = new PlusNode();
        on_two_op(pNode, aNode);
    }

    void on_div(int line) {
        ValNode aNode = aValNodes.pop();
        DivNode dNode = new DivNode(line, output);
        on_two_op(dNode, aNode);
    }

    void on_greater() {
        ValNode aNode = aValNodes.pop();
        GreaterNode grNode = new GreaterNode();
        on_two_op(grNode, aNode);
    }

    void on_not() {
        Expr expr = get_last_expr();
        NotNode nNode = new NotNode(expr);
        expr.setNextNode(nNode);
        exprs.push(expr);
        new_expr_env(nNode);
    }

    void on_and() {
        ValNode bNode = bValNodes.pop();
        AndNode andNode = new AndNode();
        on_two_op(andNode, bNode);
    }

    void on_bracket_open() {
        if (stmState == StatementState.IF_START) {
            past_stmStates.push(stmState);
            stmState = StatementState.CONDITION;
            BracketNode brNode = new BracketNode(current_snode);
            ((IfNode)current_snode).condition = brNode;
            new_expr_env(brNode);
        } else if (stmState == StatementState.WHILE_START) {
            past_stmStates.push(stmState);
            stmState = StatementState.CONDITION;
            BracketNode brNode = new BracketNode(current_snode);
            ((WhileNode)current_snode).condition = brNode;
            new_expr_env(brNode);
        } else {
            if (stmState == StatementState.CONDITION) {
                past_stmStates.push(stmState);
                stmState = StatementState.CONDITION;
            }
            Expr root = root_exprs.peek();
            if (root instanceof NotNode) {
                BracketNode brNode = new BracketNode(root);
                ((NotNode) root).kid = brNode;
                root_exprs.pop();
                root_exprs.push(brNode);
            } else {
                new_expr_env(new BracketNode());
            }
        }
    }

    void on_bracket_close() {
        if (stmState == StatementState.CONDITION) {
            stmState = past_stmStates.pop();
            ValNode valNode = aValNodes.pop();
            if (stmState == StatementState.IF_START) {
                stmState = StatementState.IF_T;
                //((BracketNode)((IfNode)current_snode).condition).kid = valNode;
                Expr expr = get_last_expr();
                expr.setNextNode(valNode);
                clear_expr_env();
                return;
            } else if (stmState == StatementState.WHILE_START) {
                stmState = StatementState.WHILE_B;
                Expr expr = get_last_expr();
                expr.setNextNode(valNode);
                clear_expr_env();
                return;
            }
        }
        // assert(current_expr instaceof BracketNode && current_expr.isFullRecv())
        Expr expr = get_last_expr();
        Node expr_parent = expr.getParent();
        if (expr_parent != null && expr_parent instanceof NotNode) {
            aValNodes.push((ValNode) expr.getParent());
        } else {
            aValNodes.push((ValNode) expr);
        }
        restore_expr_env();
        //if ( !(current_snode instanceof  IfNode) /*|| !(current_snode instanceof WhileNode)*/ || !past_exprs.empty())
        //   exprs.push(past_exprs.pop());
    }

    void on_block_open() {
        BlockNode blNode = null;
        if (stmState == StatementState.IF_T) {
            blNode = new BlockNode(current_snode);
            ((IfNode) current_snode).then = blNode;
        } else if (stmState == StatementState.IF_O) {
            blNode = new BlockNode(current_snode);
            ((IfNode) current_snode).otherwise = blNode;
        } else if (stmState == StatementState.WHILE_B) {
            blNode = new BlockNode(current_snode);
            ((WhileNode) current_snode).body = blNode;
        }
        SequenceNode sqNode = new SequenceNode(blNode);
        blNode.setNextSNode(sqNode);
        current_snode = sqNode;
        past_stmStates.push(stmState);
        stmState = StatementState.NO_STATE;
    }

    void on_block_close() { //TODO if in if si while in while si combinate
        stmState = past_stmStates.pop();
        if (stmState == StatementState.IF_T) {
            stmState = StatementState.IF_O;
            // assert(current_snode instanceof SequenceNode);
            Node snode_parent = current_snode.getParent();
            Node snode_gparent = snode_parent.getParent();
            if (snode_gparent instanceof IfNode) {
                ((BlockNode)((IfNode) snode_gparent).then).kid = null;
                current_snode = (SNode) snode_gparent;
            } else {
                if (snode_gparent instanceof BlockNode) {
                    ((BlockNode) snode_gparent).kid = ((SequenceNode) snode_parent).kid_left;
                } else { //assert(snode_gparent instanceof SequenceNode)
                    ((SequenceNode)snode_gparent).kid_right = ((SequenceNode) current_snode).kid_left;
                }
                while (!(snode_gparent instanceof  IfNode)) {
                    snode_gparent = snode_gparent.getParent();
                }
                current_snode = (SNode) snode_gparent;
            }
        } else if (stmState == StatementState.IF_O) {
            // assert(current_snode instanceof SequenceNode);
            Node snode_parent = current_snode.getParent();
            Node snode_gparent = snode_parent.getParent();
            if (snode_gparent instanceof IfNode) {
                ((BlockNode)((IfNode) snode_gparent).otherwise).kid = null;
            } else {
                if (snode_gparent instanceof BlockNode) {
                    ((BlockNode) snode_gparent).kid = ((SequenceNode) snode_parent).kid_left;
                } else { //assert(snode_gparent instanceof SequenceNode)
                    ((SequenceNode)snode_gparent).kid_right = ((SequenceNode) snode_parent).kid_left;
                }
            }
            stmState = past_stmStates.pop();
            while(!(snode_gparent instanceof IfNode)) {
                snode_gparent = snode_gparent.getParent();
            }
            IfNode ifNode = (IfNode) snode_gparent;
            current_snode = (SNode) ifNode.getParent();
            if (current_snode instanceof SequenceNode) {   // todo wut?
                SNode new_seq = new SequenceNode(current_snode);
                current_snode.setNextSNode(new_seq);
                current_snode = new_seq;
            }
        } else if (stmState == StatementState.WHILE_B) {
            // assert(current_snode instanceof SequenceNode);
            Node snode_parent = current_snode.getParent();
            Node snode_gparent = snode_parent.getParent();
            if (snode_gparent instanceof WhileNode) {
                ((BlockNode)((WhileNode) snode_gparent).body).kid = null;
            } else {
                if (snode_gparent instanceof BlockNode) {
                    ((BlockNode) snode_gparent).kid = ((SequenceNode) snode_parent).kid_left;
                } else { //assert(snode_gparent instanceof SequenceNode)
                    ((SequenceNode)snode_gparent).kid_right = ((SequenceNode) snode_parent).kid_left;
                }
            }
            stmState = past_stmStates.pop();
            while(!(snode_gparent instanceof WhileNode)) {
                snode_gparent = snode_gparent.getParent();
            }
            WhileNode whNode = (WhileNode) snode_gparent;
            current_snode = (SNode) whNode.getParent();
            if (current_snode instanceof SequenceNode) {   // todo wut?
                SNode new_seq = new SequenceNode(current_snode);
                current_snode.setNextSNode(new_seq);
                current_snode = new_seq;
            }
        }
    }

    void on_true() {
        BoolNode bNode = new BoolNode(true);
        on_bnode(bNode);
    }

    void on_false() {
        BoolNode bNode = new BoolNode(false);
        on_bnode(bNode);
    }

    void finish() {
        current_snode = (SNode) current_snode.getParent();
        ((SequenceNode) current_snode.getParent()).kid_right = ((SequenceNode) current_snode).kid_left;
        try {
            root.print(0, arbore);
            arbore.close();
            if (to_end) {
                output.write("UnassignedVar " + (line_error + 1) + "\n");
                output.close();
                System.exit(0);
            }
            root.eval();
            print_vars(output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void print_vars(BufferedWriter writer) throws IOException {
        for (Map.Entry<String, Integer> entry: var_to_value.entrySet()) {
            String key = entry.getKey();
            Integer val = entry.getValue();
            String val_str;
            if (val == null) {
                val_str = "null";
            } else {
                val_str = Integer.toString(val);
            }
            writer.write(key + "=" + val_str + "\n");
        }
    }
}
