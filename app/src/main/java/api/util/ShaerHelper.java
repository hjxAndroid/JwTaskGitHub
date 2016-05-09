package api.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.jeeweel.syl.jwtask.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShaerHelper {
	private Activity mContext;

	public ShaerHelper(Activity mContext, String tittle, String content) {
		this.mContext = mContext;
		addWXPlatform();
		addQZoneQQPlatform();
		setShareContent(tittle, content);
	}
	private void addQZoneQQPlatform() {
		String appId = "1104728221";
		String appKey = "w9Hda93syZO5Z32B";
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext,
				appId, appKey);
		//qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();

		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				mContext, appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}
    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wx0d776ca6a9f94ef7";
        String appSecret = "2a779d7b2443c94858bf18a49a9c0d9f";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mContext, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);

	public void setShareContent(String tittle, String content) {
		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ,SHARE_MEDIA.SINA);
		mController.openShare(mContext, false);

		QQShareContent  qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(content+"----请及时到天天系统查看确认");
		qqShareContent.setShareImage(new UMImage(mContext,
				R.mipmap.ic_launcher));
		qqShareContent.setTitle(tittle);
		mController.setShareMedia(qqShareContent);

		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent
				.setShareContent(content+"----请及时到天天系统查看确认");
		sinaContent.setShareImage(new UMImage(mContext,
				R.drawable.ic_launcher));
		mController.setShareMedia(sinaContent);

		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareImage(new UMImage(mContext,
				R.mipmap.ic_launcher));
		weixinContent.setShareContent(content+"----请及时到天天系统查看确认");
		weixinContent.setTitle(tittle);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia
				.setShareContent(content);
		circleMedia.setTitle(tittle);
		mController.setShareMedia(circleMedia);
	}

	public void performShare(SHARE_MEDIA platform) {
		mController.postShare(mContext, platform, new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				StatusCode.showErrMsg(mContext,eCode,"外网");
			}
		});
	}

}
