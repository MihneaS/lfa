import java.io.BufferedWriter;
import java.io.IOException;

public class BlockNode implements SNode{

    Node parent;
    SNode kid = null;

    BlockNode(Node parent) {
        this.parent = parent;
    }

    @Override
    public void eval() {
        if (kid != null) {
            kid.eval();
        }
    }

    @Override
    public void setNextSNode(SNode node) {
        kid = node;
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
        writer.write("<BlockNode> {}\n");
        if (kid != null) {
            kid.print(tabs + 1, writer);
        }
    }
}
