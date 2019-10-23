package www.pdx.life.networkmonitoring.multipe.differ;


import android.os.AsyncTask;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;


/**
 * Created by zhenyu on 2017/7/27.
 */

public class DifferDelegate {

    private MultiTypeAdapter mAdapter;
    private Differ.Callback mCallback;
    private int dataVersion = 0;

    public static class DefaultUpdateCallback implements ListUpdateCallback {
        private MultiTypeAdapter mAdapter;

        public DefaultUpdateCallback(MultiTypeAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public void onInserted(int position, int count) {
            mAdapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            mAdapter.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count, Object payload) {
            mAdapter.notifyItemRangeChanged(position, count, payload);
        }
    }

    public DifferDelegate(MultiTypeAdapter adapter, Differ.Callback callback) {
        this.mAdapter = adapter;
        this.mCallback = callback;
    }

    public void replace(final List<?> update, final ListUpdateCallback updateCallback) {
        dataVersion++;
        final List<?> items = mAdapter.getItems();
        if (items == null) {
            if (update == null) {
                return;
            }
            mAdapter.setItems(update);
            mAdapter.notifyDataSetChanged();
        } else if (update == null) {
            int oldSize = items.size();
            mAdapter.setItems(new ArrayList<>(0));
            mAdapter.notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = dataVersion;
            final List<?> oldItems = items;
            new AsyncTask<Pair<List<?>, List<?>>, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Pair<List<?>, List<?>>... pairs) {
                    Pair<List<?>, List<?>> pair = pairs[0];
                    final List<?> oldItems = pair.first;
                    final List<?> update = pair.second;
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            Object oldItem = oldItems.get(oldItemPosition);
                            Object newItem = update.get(newItemPosition);
                            return mCallback.areItemsTheSame(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            Object oldItem = oldItems.get(oldItemPosition);
                            Object newItem = update.get(newItemPosition);
                            return mCallback.areContentsTheSame(oldItem, newItem);
                        }

                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != dataVersion) {
                        // ignore update
                        return;
                    }
                    mAdapter.setItems(update);
                    diffResult.dispatchUpdatesTo(updateCallback);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Pair(oldItems, update));
        }
    }


    public void replace(final List<?> update) {
        replace(update, new DefaultUpdateCallback(mAdapter));
    }

}
