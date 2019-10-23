package www.pdx.life.networkmonitoring.multipe.differ;

import android.support.v7.util.ListUpdateCallback;

import java.util.List;

/**
 * Created by zhenyu on 2017/7/10.
 */

public interface Differ {

    public void replace(List<?> update);

    public void replace(List<?> update, ListUpdateCallback updateCallback);

    public interface Callback {
        boolean areItemsTheSame(Object oldItem, Object newItem);

        boolean areContentsTheSame(Object oldItem, Object newItem);
    }
}

