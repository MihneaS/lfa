import java.io.BufferedWriter;
import java.io.IOException;

public class WhileNode implements SNode{

    Node parent;
    ValNode condition = null;
    SNode body = null;

    WhileNode(Node parent) {
        this.parent = parent;
    }

    @Override
    public void eval() {
        while (condition.getVal() == 1) {
            body.eval();
        }
    }

    @Override
    public void setNextSNode(SNode node) {
        body = node;
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
        writer.write("<WhileNode> while\n");
        condition.print(tabs+1, writer);
        body.print(tabs+1, writer);
    }
}
