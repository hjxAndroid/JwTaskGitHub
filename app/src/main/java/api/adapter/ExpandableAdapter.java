package api.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;

import java.util.List;

//ExpandableListView的Adapter
public class ExpandableAdapter extends BaseExpandableListAdapter {
    Context context;

    private List<Orgunit> groupArray;

    int size = 0;
    /**
     * 获取布局对象
     */
    private LayoutInflater mInflater;

    public ExpandableAdapter(Context context, List<Orgunit> groupArray) {
        this.context = context;
        this.groupArray = groupArray;
        size = groupArray.size();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return groupArray.get(groupPosition).getChilds().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return groupArray.get(groupPosition).getChilds().size();
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        String string = groupArray.get(groupPosition).getChilds().get(childPosition).getDept_name();
        ChildViewHolder holder = null;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = mInflater.inflate(R.layout.item_child_contact, null);
            holder.value = (TextView) convertView.findViewById(R.id.name);
            holder.img = (ImageView) convertView.findViewById(R.id.iv_xz);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource(R.drawable.icon_android_muticall);
        holder.value.setText(string);
        return convertView;
    }

    // group method stub
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    public int getGroupCount() {
        return groupArray.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Orgunit orgunit = groupArray.get(groupPosition);

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_contact, null);
            holder.value = (TextView) convertView.findViewById(R.id.name);
            holder.img = (ImageView) convertView.findViewById(R.id.iv_xz);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.value.setText(orgunit.getOrg_name());
        holder.img.setBackgroundResource(R.drawable.icon_org);
        return convertView;

    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 缓存控件
     */
    private class ViewHolder {
        private TextView value;
        private ImageView img;
    }

    /**
     * 缓存控件
     */
    private class ChildViewHolder {
        private TextView value;
        private ImageView img;
    }

    public List<Orgunit> getList() {
        return groupArray;
    }

}