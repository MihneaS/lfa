import java.io.BufferedWriter;
import java.io.IOException;

public class IfNode implements SNode{

    Node parent;
    ValNode condition = null;
    SNode then = null;
    SNode otherwise = null;

    public IfNode(Node parent) {
        this.parent = parent;
    }

    @Override
    public void eval() {
        if (condition.getVal() == 1) {
            then.eval();
        } else {
            otherwise.eval();
        }
    }

    @Override
    public void setNextSNode(SNode node) {
        if (then == null) {
            then = node;
        } else {
            otherwise = node;
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
        writer.write("<IfNode> if\n");
        condition.print(tabs+1, writer);
        then.print(tabs+1, writer);
        otherwise.print(tabs+1, writer);
    }
}
