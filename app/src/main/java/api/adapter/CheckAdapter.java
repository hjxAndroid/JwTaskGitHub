package api.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;

import api.util.Utils;


public class CheckAdapter extends BaseAdapter {
    // 填充数据的list
    private List<Userdept> list;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    List<Userdept> userdepts;
    // 构造器
    public CheckAdapter(List<Userdept> list,List<Userdept> userdepts, Context context) {
        this.context = context;
        this.list = list;
        this.userdepts = userdepts;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }

        for(int i = 0 ;i<list.size();i++){
            for(int j=0;j<userdepts.size();j++){
                if(list.get(i).getUser_code().equals(userdepts.get(j).getUser_code())){
                    getIsSelected().put(i, true);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_check, null);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.nick = (TextView) convertView.findViewById(R.id.tv_nick_name);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            holder.iv_head_pic = (ImageView) convertView.findViewById(R.id.iv_head_pic);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        final ViewHolder holders = holder;
        // 设置list中TextView的显示
        holder.tv.setText(list.get(position).getUsername());
        holder.nick.setText(list.get(position).getNickname());
        JwImageLoader.displayImage(Utils.getPicUrl() + list.get(position).getPhoto_code(), holder.iv_head_pic);
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = holders.cb.isChecked();
                if (check) {
                    getIsSelected().put(position, check);
                } else {
                    getIsSelected().put(position, check);
                }
            }
        });
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        CheckAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tv;
        public TextView nick;
        public CheckBox cb;
        public ImageView iv_head_pic;
    }
}