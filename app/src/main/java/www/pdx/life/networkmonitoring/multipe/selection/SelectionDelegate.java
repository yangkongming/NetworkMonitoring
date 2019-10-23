package www.pdx.life.networkmonitoring.multipe.selection;

import android.support.v7.widget.RecyclerView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhenyu on 2017/7/27.
 */

public abstract class SelectionDelegate<VH extends RecyclerView.ViewHolder> implements Selectable, AutoUpdatable {

    protected HashSet<Integer> mSelected;
    protected Selectable.Mode mMode;
    protected RecyclerView.Adapter<VH> mAdapter;
    private boolean mAutoUpdatable = true;


    public SelectionDelegate(RecyclerView.Adapter<VH> adapter) {
        this(Mode.Single, adapter);
    }


    public SelectionDelegate(Selectable.Mode mode, RecyclerView.Adapter<VH> adapter) {
        this.mSelected = new HashSet<>();
        this.mMode = mode;
        this.mAdapter = adapter;
    }

    @Override
    public Mode getMode() {
        return mMode;
    }


    public void setMode(Mode mMode) {
        this.mMode = mMode;
        //TODO:模式切换之后更新选中状态
    }

    @Override
    public boolean toggle(int pos) {
        if (isSelectable(pos)) {
            return select(pos, !isSelected(pos));
        } else {
            return false;
        }
    }

    @Override
    public boolean isSelected(int pos) {
        return mSelected.contains(pos);
    }

    @Override
    public boolean select(int pos, boolean selected) {
        if (isSelectable(pos)) {
            switch (getMode()) {
                case Single:
                    if (mSelected.size() > 1) {
                        throw new IllegalArgumentException("selected size can not over 1 in Single mode");
                    }
                    final boolean autoUpdate = isAutoUpdatable();
                    if (selected) {
                        final Iterator<Integer> iterator = mSelected.iterator();
                        while (iterator.hasNext()) {
                            int position = iterator.next();
                            iterator.remove();
                            notifyItemChangedInternal(position, autoUpdate);
                        }
                        mSelected.add(pos);
                        notifyItemChangedInternal(pos, autoUpdate);
                    } else {
                        mSelected.remove(pos);
                        notifyItemChangedInternal(pos, autoUpdate);
                    }
                    break;
                case Multiple:
                    if (selected) {
                        mSelected.add(pos);
                    } else {
                        mSelected.remove(pos);
                    }
                    notifyItemChangedInternal(pos, isAutoUpdatable());
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void selectRange(int start, int end, boolean selected) {
        if (getMode() == Mode.Multiple) {
            for (int i = start; i <= end; i++) {
                if (isSelectable(i)) {
                    if (selected) {
                        mSelected.add(i);
                    } else {
                        mSelected.remove(i);
                    }
                }
            }
            final boolean autoUpdate = isAutoUpdatable();
            notifyItemRangeChangedInternal(start, end - start + 1, autoUpdate);
        } else {
            throw new UnsupportedOperationException("selectRange can only invoke in multiple mode");
        }
    }

    @Override
    public void unselectAll() {
        mSelected.clear();
        notifyItemRangeChangedInternal(0, mAdapter.getItemCount(), isAutoUpdatable());
    }

    @Override
    public void selectAll() {
        if (getMode() == Mode.Multiple) {
            final boolean autoUpdate = isAutoUpdatable();
            final int itemCount = mAdapter.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                if (isSelectable(i)) {
                    mSelected.add(i);
                }
            }
            notifyItemRangeChangedInternal(0, itemCount, autoUpdate);
        } else {
            throw new UnsupportedOperationException("selectAll can only invoke in multiple mode");
        }
    }


    private void notifyItemChangedInternal(int position, boolean autoUpdate) {
        if (autoUpdate) {
            mAdapter.notifyItemChanged(position);
        }
    }

    private void notifyItemRangeChangedInternal(int start, int itemCount, boolean autoUpdate) {
        if (autoUpdate) {
            mAdapter.notifyItemRangeChanged(start, itemCount);
        }
    }


    @Override
    public int getSelectedCount() {
        return mSelected.size();
    }

    @Override
    public Set<Integer> getSelection() {
        return mSelected;
    }

   /* @Override
    public <T> List<T> getSelectedDataList() {
        List<T> list = new ArrayList<>(mSelected.size());
        List<?> items = mMultiTypeAdapter.getItems();
        int size = items != null ? items.size() : 0;
        if (size > 0) {
            for (int position : mSelected) {
                if (position >= 0 && position < size) {
                    Object item = items.get(position);
                    list.add((T) item);
                }
            }
        }
        return list;
    }*/


    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        this.mAutoUpdatable = autoUpdate;
    }

    @Override
    public boolean isAutoUpdatable() {
        return mAutoUpdatable;
    }

}
