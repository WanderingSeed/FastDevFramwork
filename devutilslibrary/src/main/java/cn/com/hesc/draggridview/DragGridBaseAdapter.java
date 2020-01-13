package cn.com.hesc.draggridview;

/**
 * 定义拖动GridView的适配器，自定义的适配器要实现此功能
 */
public interface DragGridBaseAdapter {
    /**
     * 重新排列数据
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition);


    /**
     * 设置某个item隐藏
     * @param hidePosition
     */
    public void setHideItem(int hidePosition);


    /**
     * 拖拽完成
     */
    public void finishDrag();
}
