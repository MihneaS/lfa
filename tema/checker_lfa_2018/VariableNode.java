import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class VariableNode implements ValNode{

    public Map<String, Integer> var_to_val;
    public String name;
    public Node parent;
    public int line;
    BufferedWriter writer;

    public VariableNode(Map<String, Integer> var_to_val, String name, int line, BufferedWriter writer) {
        this.var_to_val = var_to_val;
        this.name = name;
        this.line = line;
        this.writer = writer;
    }

    public VariableNode(Map<String, Integer> var_to_val, Node parent, String name, int line, BufferedWriter writer) {
        this(var_to_val, name, line, writer);
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

    public void setVal(Integer newVal) {
        var_to_val.put(name, newVal);
    }

    String getName() {
        return name;
    }

    @Override
    public void print(int tabs, BufferedWriter writer) throws IOException {
        int itabs = tabs;
        while(itabs-- > 0) {
            writer.write("\t");
        }
        writer.write("<VariableNode> " + name + "\n");
    }

    @Override
    public int getVal() {
        Integer result = var_to_val.get(name);
        if (result == null) {
            try {
                writer.write("UnassignedVar " + (line + 1) + "\n");
                writer.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
