package www.pdx.life.networkmonitoring.multipe.selection;

import java.util.Set;

/**
 * Created by zhenyu on 2017/7/10.
 * 单选和多选模式，。
 */

public interface Selectable {

    enum Mode {
        Single(),
        Multiple()
    }

    public Mode getMode();

    public void setMode(Mode mode);

    public boolean toggle(int pos);

    public boolean isSelected(int pos);

    public boolean select(int pos, boolean selected);

    public void selectRange(int start, int end, boolean selected);

    public void unselectAll();

    public void selectAll();

    public int getSelectedCount();

    public Set<Integer> getSelection();

    boolean isSelectable(int position);

    //public <T> List<T> getSelectedDataList();
}

