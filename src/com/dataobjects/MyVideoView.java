package com.dataobjects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * MyVideoView Represents an object which will be 
 * used to draw the video player at watch tab.
 * Is used in watchtab.xml
 * @extends VideoView overriding the videoView element
 */
@SuppressLint("DrawAllocation")
public class MyVideoView extends VideoView {
	
	/** C'tor */
	public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/** C'tor */
	public MyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** C'tor */
	public MyVideoView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(8);
		getLocalVisibleRect(rect);
		canvas.drawRect(rect, paint);
	}
}
