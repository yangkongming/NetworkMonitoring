package www.pdx.life.networkmonitoring.multipe.selection;

/**
 * @Date 2018/4/19
 * @Author fangzhenyu
 * @Description 控制选择模式下是否可以自动刷新Adapter的接口
 */
public interface AutoUpdatable {

    public void setAutoUpdate(boolean autoUpdate);

    public boolean isAutoUpdatable();
}
