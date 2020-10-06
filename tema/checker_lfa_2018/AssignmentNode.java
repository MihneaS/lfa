import java.io.BufferedWriter;
import java.io.IOException;

public class AssignmentNode implements SNode, Expr, OneOperand{

    Node parent;
    VariableNode var;
    ValNode val;
    int priority;

    AssignmentNode(Node parent, VariableNode var){
        this.parent = parent;
        this.var = var;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
    }

    void setVal(ValNode node) {
        val = node;
    }

    @Override
    public void eval() {
        var.setVal(val.getVal());
    }

    @Override
    public void setNextSNode(SNode node) {
    }

    @Override
    public void print(int tabs, BufferedWriter writer) throws IOException {
        int itabs = tabs;
        while(itabs-- > 0) {
            writer.write("\t");
        }
        writer.write("<AssignmentNode> =\n");
        var.print(tabs+1, writer);
        val.print(tabs+1, writer);
    }

    @Override
    public void setNextNode(ValNode node) {
        val = node;
    }

    @Override
    public ValNode getLastNode() {
        return val;
    }

    @Override
    public ValNode getValNode() {
        return val;
    }

    @Override
    public void setValNode(ValNode node) {
        val = node;
    }
}
