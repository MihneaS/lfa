import java.io.BufferedWriter;
import java.io.IOException;

public interface Node {

    Node getParent();
    void setParent(Node parent);
    void print(int tabs, BufferedWriter writer) throws IOException;
}
