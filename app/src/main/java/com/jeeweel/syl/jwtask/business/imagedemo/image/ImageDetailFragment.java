package com.jeeweel.syl.jwtask.business.imagedemo.image;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.imagedemo.image.photoview.PhotoViewAttacher;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);

		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {

				getActivity().finish();
			}
		});
		mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setItems(new String[]{"保存"},
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which) {
								mImageView.setDrawingCacheEnabled(true);
								Bitmap imageBitmap = mImageView.getDrawingCache();
								if (imageBitmap != null) {
									new SaveImageTask().execute(imageBitmap);
								}
							}
						});
				builder.show();

				return false;
			}
		});
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
		@Override protected String doInBackground(Bitmap... params) {
		String result = "保存失败";
		try {
			String sdcard = Environment.getExternalStorageDirectory().toString();

			File file = new File(sdcard + "/Download");
			if (!file.exists()) {
				file.mkdirs();
			}

			File imageFile = new File(file.getAbsolutePath(),new Date().getTime()+".jpg");
			FileOutputStream outStream = null;
			outStream = new FileOutputStream(imageFile);
			Bitmap image = params[0];
			image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
			result = "图片成功保存至"+file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getActivity().getApplicationContext(),result,Toast.LENGTH_SHORT).show();

			mImageView.setDrawingCacheEnabled(false);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		ImageLoader.getInstance().displayImage(mImageUrl, mImageView, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "下载错误";
					break;
				case DECODING_ERROR:
					message = "图片无法显示";
					break;
				case NETWORK_DENIED:
					message = "网络有问题，无法下载";
					break;
				case OUT_OF_MEMORY:
					message = "图片太大无法显示";
					break;
				case UNKNOWN:
					message = "未知的错误";
					break;
				}
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
			}
		});
		
	}

}
