import java.io.BufferedWriter;
import java.io.IOException;

public class MainNode implements SNode{

    SNode kid;

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public void setParent(Node parent) {
    }

    @Override
    public void eval() {
        kid.eval();
    }

    @Override
    public void setNextSNode(SNode node) {
        setKid(node);
    }

    @Override
    public void print(int tabs, BufferedWriter writer) throws IOException {
        writer.write("<MainNode>\n");
        kid.print(1, writer);
    }

    private void setKid(SNode kid) {
        this.kid = kid;
    }
}
