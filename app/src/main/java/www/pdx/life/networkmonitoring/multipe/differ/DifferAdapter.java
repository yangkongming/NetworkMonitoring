package www.pdx.life.networkmonitoring.multipe.differ;

import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import www.pdx.life.networkmonitoring.multipe.AdapterWrapper;

/**
 * @Date 2018/4/20
 * @Author fangzhenyu
 * @Description 实现Diff功能的Adapter，新数据与原有数据进行比较，实现局部刷新功能，可包装任意现有Adapter
 */
public abstract class DifferAdapter<VH extends RecyclerView.ViewHolder> extends AdapterWrapper<VH> implements Differ, Differ.Callback {

    private DifferDelegate mDifferDelegate;

    public DifferAdapter(MultiTypeAdapter adapter) {
        super(adapter);
        mDifferDelegate = new DifferDelegate(adapter, this);
    }

    @Override
    public void replace(List<?> update) {
        mDifferDelegate.replace(update);
    }

    @Override
    public void replace(List<?> update, ListUpdateCallback updateCallback) {
        mDifferDelegate.replace(update, updateCallback);
    }

}
