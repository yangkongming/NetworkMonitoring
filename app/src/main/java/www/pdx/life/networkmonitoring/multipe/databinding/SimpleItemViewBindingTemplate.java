package www.pdx.life.networkmonitoring.multipe.databinding;


import android.databinding.ViewDataBinding;

/**
 * Created by zhenyu on 16/12/13.
 */

public class SimpleItemViewBindingTemplate<T, B extends ViewDataBinding> extends ItemViewBindingTemplate<T, B> {

    private int mItemLayoutId;
    private int mVariableId;

    public SimpleItemViewBindingTemplate(int itemLayoutId, int variableId) {
        this.mItemLayoutId = itemLayoutId;
        this.mVariableId = variableId;
    }

    @Override
    protected int getItemLayoutId() {
        return mItemLayoutId;
    }

    @Override
    protected int getVariableId() {
        return mVariableId;
    }

}
