import java.io.BufferedWriter;
import java.io.IOException;

public class GreaterNode implements ValNode, Expr, TwoOperands {

    Node parent;
    ValNode kid_left;
    ValNode kid_right;
    int priority = 6;

    GreaterNode(Node parent) {
        this.parent = parent;
    }

    public GreaterNode() {

    }

    @Override
    public void setNextNode(ValNode node) {
        if (kid_left == null) {
            kid_left = node;
        } else {
            kid_right = node;
        }
    }

    @Override
    public ValNode getLastNode() {
        if (kid_left != null) {
            return  kid_left;
        } else {
            return  kid_right;
        }
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int getVal() {
        if (kid_left.getVal() > kid_right.getVal()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public void print(int tabs, BufferedWriter writer) throws IOException {
        int itabs = tabs;
        while(itabs-- > 0) {
            writer.write("\t");
        }
        writer.write("<GreaterNode> >\n");
        kid_left.print(tabs+1, writer);
        kid_right.print(tabs+1, writer);
    }

    @Override
    public ValNode getLeftNode() {
        return kid_left;
    }

    @Override
    public void setLeftNode(ValNode node) {
        kid_left = node;
    }

    @Override
    public ValNode getRightNode() {
        return kid_right;
    }

    @Override
    public void setRightNode(ValNode node) {
        kid_right = node;
    }
}
