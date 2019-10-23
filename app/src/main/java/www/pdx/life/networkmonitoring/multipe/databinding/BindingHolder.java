package www.pdx.life.networkmonitoring.multipe.databinding;


import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhenyu on 16/12/8.
 */

public class BindingHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private B mBinding;

    public BindingHolder(View itemView) {
        super(itemView);
    }

    public BindingHolder(View itemView, B binding) {
        super(itemView);
        setViewDataBinding(binding);
    }

    public void setViewDataBinding(B binding) {
        this.mBinding = binding;
    }

    public B getViewDataBinding() {
        return mBinding;
    }
}
