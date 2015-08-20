package refreshable.solution;

public interface RefreshableTokenBreakingAPI<T> {

    T token();

    T refresh();

    T refresh(String previousToken);
}
