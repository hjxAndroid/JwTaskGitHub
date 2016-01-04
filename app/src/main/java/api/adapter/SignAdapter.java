package api.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;

import org.w3c.dom.Text;

import api.util.Utils;


public class SignAdapter extends BaseAdapter {
    // 填充数据的list
    private List<Friend> list;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    // 构造器
    public SignAdapter(Context context, List<Friend> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_image_view, null);
            holder.tv_cir = (TextView) convertView.findViewById(R.id.tv_cir);
//            holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
            holder.tv_pic_friend_name = (TextView) convertView.findViewById(R.id.tv_pic_friend_name);
            holder.iv_head_pic = (ImageView) convertView.findViewById(R.id.iv_head_pic);
            holder.tv_pic_friendname = (TextView) convertView.findViewById(R.id.tv_pic_friendname);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == list.size() - 1) {
//            holder.iv_add.setImageDrawable((context.getResources().getDrawable(R.drawable.icon_org_add)));
//            holder.iv_add.setVisibility(View.VISIBLE);
//            Drawable iconAdd = context.getResources().getDrawable(R.drawable.icon_org_add);
//            iconAdd.setBounds(20, 20, 20, 20);
//            holder.tv_cir.setCompoundDrawables(null, iconAdd, null, null);
            holder.tv_cir.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_org_add));
            holder.tv_cir.setVisibility(View.VISIBLE);
            holder.tv_pic_friend_name.setVisibility(View.GONE);
            holder.iv_head_pic.setVisibility(View.GONE);
            holder.tv_pic_friendname.setVisibility(View.GONE);
        } else {
            String friendNickName = list.get(position).getFriend_nickname();
            String friendPic = "";
            if (StrUtils.IsNotEmpty(friendNickName)) {
                if (friendNickName.length() > 2) {
                    friendPic = friendNickName.substring(friendNickName.length() - 2, friendNickName.length());
                } else {
                    friendPic = friendNickName;
                }
            } else {
                friendPic = "姓名";
            }
            if (StrUtils.IsNotEmpty(list.get(position).getPhoto_code())) {
                JwImageLoader.displayImage(Utils.getPicUrl() + list.get(position).getPhoto_code(), holder.iv_head_pic);

                holder.tv_pic_friendname.setVisibility(View.GONE);

//                holder.iv_add.setVisibility(View.GONE);

                holder.tv_cir.setVisibility(View.GONE);

                holder.tv_pic_friend_name.setVisibility(View.VISIBLE);

                holder.iv_head_pic.setVisibility(View.VISIBLE);

                holder.tv_pic_friend_name.setText(friendNickName);
            } else {
                holder.tv_cir.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.tvcircle));

                holder.tv_pic_friendname.setVisibility(View.VISIBLE);

                holder.tv_cir.setVisibility(View.VISIBLE);

                holder.iv_head_pic.setVisibility(View.GONE);

//                holder.iv_add.setVisibility(View.GONE);

                holder.tv_pic_friend_name.setVisibility(View.GONE);

                holder.tv_cir.setText(friendPic);

                holder.tv_pic_friendname.setText(friendPic);

            }
        }


        return convertView;
    }

    public static class ViewHolder {
        public TextView tv_cir;
        public TextView tv_pic_friend_name;
        public ImageView iv_head_pic;
        public TextView tv_pic_friendname;

    }
}