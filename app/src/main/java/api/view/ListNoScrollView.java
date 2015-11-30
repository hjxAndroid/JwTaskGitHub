package api.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * <p>Title:     				       </p>
 * <p>Description:                     </p>
 * <p>@author: 			                 </p>
 * <p>Copyright: 					    </p>
 * <p>Company: 	 						  </p>
 * <p>Create Time:	      				   </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ListNoScrollView extends ListView {

	public ListNoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListNoScrollView(Context context) {
		super(context);
	}

	public ListNoScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
