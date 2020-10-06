import java.io.BufferedWriter;
import java.io.IOException;

public class SequenceNode implements SNode{

    Node parent;
    SNode kid_left;
    SNode kid_right;

    SequenceNode(Node parent) {
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    private void setKidLeft(SNode node) {
        kid_left = node;
    }
    private void setKidRight(SNode node) {
        kid_right = node;
    }

    @Override
    public void eval() {
        kid_left.eval();
        kid_right.eval();
    }

    @Override
    public void setNextSNode(SNode node) {
        if (kid_left == null) {
            setKidLeft(node);
        } else {
            setKidRight(node);
        }
    }

    @Override
    public void print(int tabs, BufferedWriter writer) throws IOException {
        int itabs = tabs;
        while(itabs-- > 0) {
            writer.write("\t");
        }
        writer.write("<SequenceNode>\n");
        kid_left.print(tabs+1, writer);
        kid_right.print(tabs+1, writer);
    }
}
