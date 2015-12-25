package api.viewpage;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.imagedemo.image.ImagePagerActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import api.util.Utils;

/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements CBPageAdapter.Holder<String>{
    private ImageView imageView;
    private DisplayImageOptions options;
    
    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(final Context context, final int position, final String data) {
    	//配置图片加载及显示选项（还有一些其他的配置，查阅doc文档吧）
		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.banner_default)    //在ImageView加载过程中显示图片
			.showImageForEmptyUri(R.drawable.banner_default)  //image连接地址为空时
			.showImageOnFail(R.drawable.banner_default)  //image加载失败
			.cacheInMemory(true)  //加载图片时会在内存中加载缓存
			.cacheOnDisc(true)   //加载图片时会在磁盘中加载缓存
			.displayer(new RoundedBitmapDisplayer(10))  //设置用户加载图片task(这里是圆角图片显示)
			.build();
    	//imageView.setImageResource(R.drawable.ic_default_adimage);
		final String[] datas = data.split(",");
		ImageLoader.getInstance().displayImage(datas[0],imageView,options);
		if(datas!=null&&datas.length>=1){
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, ImagePagerActivity.class);
					// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, datas);
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
					context.startActivity(intent);
				}
			});
		}
    }
}
