import java.io.BufferedWriter;
import java.io.IOException;

public class NotNode implements ValNode, Expr, OneOperand{

    Node parent;
    ValNode kid;

    public NotNode(Node parent) {
        this.parent = parent;
    }

    @Override
    public void setNextNode(ValNode node) {
        kid = node;
    }

    @Override
    public ValNode getLastNode() {
        return kid;
    }

    @Override
    public int getVal() {
        if (kid.getVal() == 0) {
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
        writer.write("<NotNode> !\n");
        kid.print(tabs+1, writer);
    }

    @Override
    public ValNode getValNode() {
        return kid;
    }

    @Override
    public void setValNode(ValNode node) {
        kid = node;
    }
}
