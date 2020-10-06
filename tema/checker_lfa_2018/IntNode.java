import java.io.BufferedWriter;
import java.io.IOException;

public class IntNode implements ValNode{

    Node parent;
    int val;

    IntNode(int val) {
        this.val = val;
    }

    IntNode(Node parent, int val) {
        this(val);
        this.parent = parent;
    }

    @Override
    public int getVal() {
        return val;
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
        writer.write("<IntNode> " + val + "\n");
    }
}
