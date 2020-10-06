public interface IncompleteExpr {
    void completeExpr(ValNode node);
    ValNode getCompletion();
}
