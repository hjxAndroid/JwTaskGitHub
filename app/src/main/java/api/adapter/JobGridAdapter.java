package api.adapter;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.JobItem;

import java.util.List;


public class JobGridAdapter extends BaseAdapter{
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 数据
     */
    List<JobItem> jobItems;
    /**
     * 获取布局对象
     */
    private LayoutInflater mInflater;
    /**
     * 缓存控件
     */
    private class ViewHolder {
        private TextView value;
        private ImageView imvlaue;
        private ImageView iv_num;
    }
    private TypedArray imagesArrays;
    private int layoutId;
    /**
     * 构造方法
     * @param context 上下文
     * @param jobItems 数据
     */
    public JobGridAdapter(Context context, List<JobItem> jobItems,TypedArray imagesArrays,int layoutId) {
        this.mContext = context;
        this.jobItems = jobItems;
        this.imagesArrays = imagesArrays;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return jobItems.size();
    }

    @Override
    public JobItem getItem(int position) {

        return jobItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("Recycle")
    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            int x =parent.getHeight();
            convertView = mInflater.inflate(layoutId, null);
            viewHolder.value = (TextView) convertView.findViewById(R.id.mode_tv);
            viewHolder.imvlaue = (ImageView) convertView.findViewById(R.id.mode_im);
            viewHolder.iv_num = (ImageView) convertView.findViewById(R.id.iv_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.value.setText(getItem(position).getData());
        viewHolder.imvlaue.setBackgroundDrawable(imagesArrays.getDrawable(position));
        String state = getItem(position).getNews_state();
        if(StrUtils.IsNotEmpty(state)&&state.equals("1")){
            viewHolder.iv_num.setVisibility(View.VISIBLE);
        }else{
            viewHolder.iv_num.setVisibility(View.GONE);
        }
        return convertView;
    }


}
