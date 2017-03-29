package SnappingSwipingRecyclerView;

/**
 * @author carl
 */
public interface SnappyScrollCalculator {
    int computeScrollToItemIndex(int velocityX, int velocityY);
}
