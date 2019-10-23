package www.pdx.life.networkmonitoring.multipe.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by zhenyu on 16/12/13.
 */

public abstract class ItemViewBindingTemplate<T, B extends ViewDataBinding> extends ItemViewBinder<T, BindingHolder<B>> {

    @Override
    protected BindingHolder<B> onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        final int itemLayoutId = getItemLayoutId();
        final B binding = DataBindingUtil.inflate(layoutInflater, itemLayoutId, fillItemLayout(itemLayoutId) ? parent : null, false);
        return new BindingHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder(final BindingHolder<B> holder, final T item) {
        final B binding = holder.getViewDataBinding();
        binding.setVariable(getVariableId(), item);
        binding.executePendingBindings();
    }

    protected abstract int getItemLayoutId();

    protected abstract int getVariableId();

    protected boolean fillItemLayout(int itemLayoutId) {
        return true;
    }

}
