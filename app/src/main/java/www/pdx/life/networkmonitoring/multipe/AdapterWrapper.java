package www.pdx.life.networkmonitoring.multipe;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @Date 2018/4/20
 * @Author fangzhenyu
 * @Description Adapter包装器，继承此类可实现Adapter的基本功能
 */
public class AdapterWrapper<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private RecyclerView.Adapter<VH> mWrappedAdapter;


    public AdapterWrapper(RecyclerView.Adapter adapter) {
        this.mWrappedAdapter = adapter;
    }

    public RecyclerView.Adapter<VH> getWrappedAdapter() {
        return mWrappedAdapter;
    }

    @Override

    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mWrappedAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mWrappedAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mWrappedAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mWrappedAdapter.getItemViewType(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        mWrappedAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return mWrappedAdapter.getItemId(position);
    }

    @Override
    public void onViewRecycled(VH holder) {
        mWrappedAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(VH holder) {
        return mWrappedAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        mWrappedAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        mWrappedAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mWrappedAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mWrappedAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mWrappedAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mWrappedAdapter.onDetachedFromRecyclerView(recyclerView);
    }

}

