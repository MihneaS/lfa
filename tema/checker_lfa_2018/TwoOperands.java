public interface TwoOperands extends Expr{
    ValNode getLeftNode();
    void setLeftNode(ValNode node);
    ValNode getRightNode();
    void setRightNode(ValNode node);
    void setPriority(int priority);
    int getPriority();
}
