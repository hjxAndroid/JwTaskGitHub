package api.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * <p>Title:     不滚动的gridview       </p>
 * <p>Description:                     </p>
 * <p>@author: hrx                 </p>
 * <p>Copyright: 	   				</p>
 * <p>Company: 	       			 	 </p>
 * <p>Create Time: 			           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GridNoScrollView extends GridView {

	public GridNoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridNoScrollView(Context context) {
		super(context);
	}

	public GridNoScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
