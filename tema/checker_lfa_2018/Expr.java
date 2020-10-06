public interface Expr extends Node {
    void setNextNode(ValNode node);
    ValNode getLastNode();
}
