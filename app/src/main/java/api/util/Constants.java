package api.util;



public class Constants {
    
    public static final String DESCRIPTOR = "com.umeng.share";
	
	private static final String TIPS = "请移步官方网�?";
	private static final String END_TIPS = ", 查看相关说明.";
	public static final String TENCENT_OPEN_URL = TIPS + "http://wiki.connect.qq.com/android_sdk使用说明" 
													+ END_TIPS;
	public static final String PERMISSION_URL = TIPS + "http://wiki.connect.qq.com/openapi权限申请" 
													+ END_TIPS;
	
	public static final String SOCIAL_LINK = "http://www.umeng.com/social";
	public static final String SOCIAL_TITLE = "友盟社会化组件帮助应用快速整合分享功";
	public static final String SOCIAL_IMAGE = "http://www.umeng.com/images/pic/banner_module_social.png";
	
	public static final String SOCIAL_CONTENT = "友盟社会化组件（SDK）让移动应用快�?整合社交分享功能，我们简化了社交平台的接入，为开发�?提供坚实的基�?��务：（一）支持各大主流社交平台，" + 
												"（二）支持图片�?文字、gif动图、音频�?视频；@好友，关注官方微博等功能" +
												"（三）提供详尽的后台用户社交行为分析。http://www.umeng.com/social";
	
	public static final int ERROR = 1001;// 网络异常
	public static final int ROUTE_START_SEARCH = 2000;
	public static final int ROUTE_END_SEARCH = 2001;
	public static final int ROUTE_BUS_RESULT = 2002;// 路径规划中公交模式
	public static final int ROUTE_DRIVING_RESULT = 2003;// 路径规划中驾车模式
	public static final int ROUTE_WALK_RESULT = 2004;// 路径规划中步行模式
	public static final int ROUTE_NO_RESULT = 2005;// 路径规划没有搜索到结果

	public static final int GEOCODER_RESULT = 3000;// 地理编码或者逆地理编码成功
	public static final int GEOCODER_NO_RESULT = 3001;// 地理编码或者逆地理编码没有数据

	public static final int POISEARCH = 4000;// poi搜索到结果
	public static final int POISEARCH_NO_RESULT = 4001;// poi没有搜索到结果
	public static final int POISEARCH_NEXT = 5000;// poi搜索下一页

	public static final int BUSLINE_LINE_RESULT = 6001;// 公交线路查询
	public static final int BUSLINE_id_RESULT = 6002;// 公交id查询
	public static final int BUSLINE_NO_RESULT = 6003;// 异常情况

	/**
	 * 请求获取地址
	 */
	public static final int REQUEST_GET_ADDRESS = 11;
}
