package www.pdx.life.networkmonitoring.multipe.databinding;


import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @Date 2018/4/19
 * @Author fangzhenyu
 * @Description 扩展MultiTypeAdapter添加一些使用起来更加方便的方法
 */
public class MultiTypeAdapterEx extends MultiTypeAdapter {


    public void reload(List<?> data) {
        getItems().clear();
        setItems(data);
        notifyDataSetChanged();
    }


    public <T> T getItem(int position) {
        if (position >= 0 && position <= getItemCount() - 1) {
            return (T) getItems().get(position);
        } else {
            return null;
        }
    }

}
