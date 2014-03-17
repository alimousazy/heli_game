package org.freesource.rectholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	static final int FRAME_PER_SECOUND = 30;
	static final int DOWN_SPEED = 3;
	static final int REC_MAX_HEIGHT = 100;
	static final int REC_MAX_WIDTH = 30;
	static final int MAX_COUNT = 150;
	int REC_WIDTH = REC_MAX_WIDTH;
	int REC_HIGHT = REC_MAX_HEIGHT;
	int OBS_WIDTH = 30;
	int OBS_HIGHT = 40;
	int mToAdd = 10;
	int mTopRectStart = -1;
	Paint mBackGroundPaint;
	Paint mDrawPaint;
	Paint mRedPaint;
	List<Rect> Obstecles;
	int global_counter = 0;
	Bitmap mHel1;
	Bitmap mHel2;
	boolean mFlip = true;
	Handler mHandler = null;
	Runnable mInvalidate;
	List<Integer> mTopRects;
	Map<String, Float[]> mPosition;

	public DrawingView(Context c) {
		super(c, null);
	}

	public DrawingView(Context c, AttributeSet attr) {
		super(c, attr);
		mBackGroundPaint = new Paint();
		mDrawPaint = new Paint();
		Obstecles = new ArrayList<Rect>();
		mRedPaint = new Paint();
		mRedPaint.setColor(Color.RED);
		mRedPaint.setStyle(Paint.Style.STROKE);
		mTopRects = new ArrayList<Integer>();
		mBackGroundPaint.setColor(0xFFC3CCB5);
		mDrawPaint.setColor(Color.BLACK);
		mHel1 = BitmapFactory.decodeResource(getResources(), R.drawable.heli);
		mHel2 = BitmapFactory.decodeResource(getResources(), R.drawable.heli2);
		mInvalidate = new Runnable() {

			@Override
			public void run() {
				invalidate();
			}
		};
		// new Rect(left, top, right, bottom)
		Obstecles.add(new Rect());
		Obstecles.add(new Rect());
		Obstecles.add(new Rect());
		Obstecles.add(new Rect());
		Obstecles.add(new Rect());
		mHandler = new Handler();
		mPosition = new HashMap<String, Float[]>();
	}

	@Override
	public void onDraw(Canvas c) {
		int left;
		int right;
		int top;
		int buttom;
		REC_HIGHT = this.getHeight() / 8;
		REC_WIDTH = this.getWidth() / 30;
		OBS_HIGHT = (this.getHeight() - mHel1.getHeight()) / Obstecles.size();
		boolean isCollesion = false;
		Float[] helPos = mPosition.get("hel_pos");
		if (helPos == null) {
			helPos = new Float[] { (float) this.getWidth() / 2,
					(float) this.getHeight() / 2 };
		}
		c.drawPaint(mBackGroundPaint);
		if (global_counter == 0) {
			for (Rect r : Obstecles) {
				int r_heigh = ((int) (Math.random() * getHeight()))
						% (this.getHeight() - REC_MAX_HEIGHT);
				r.set(getWidth() - OBS_WIDTH, r_heigh, getWidth(), r_heigh
						+ OBS_HIGHT);
			}
		}
		for (Rect r : Obstecles) {
			c.drawRect(r, mDrawPaint);
		}

		for (Rect r : Obstecles) {
			// r.set(left, top, right, bottom)
			left = r.left - getWidth() / MAX_COUNT;
			top = r.top;
			right = r.right - getWidth() / MAX_COUNT;
			buttom = r.bottom;
			if (!isCollesion) {
				isCollesion = isThereCollision((int) helPos[0].floatValue(),
						(int) helPos[1].floatValue(), left - mHel1.getWidth(),
						top, buttom, OBS_WIDTH);
			}
			r.set(left, top, right, buttom);
		}

		Bitmap helRef = mHel1;
		if (mFlip) {
			helRef = mHel2;
		}
		if (Math.random() < 0.05 || mTopRects.isEmpty()) {
			if (mTopRectStart == -1)
				mTopRectStart = REC_HIGHT;
			mTopRects.clear();
			for (int i = 0; i < this.getWidth(); i += REC_WIDTH) {
				mTopRects.add(mTopRectStart);
				if (mTopRectStart < 20) {
					mToAdd = 10;
				}
				if (mTopRectStart > REC_HIGHT) {
					mToAdd = -10;
				}
				mTopRectStart += mToAdd;
			}
		}
		for (int i = 0, l = 0; i < this.getWidth(); i += REC_WIDTH, l++) {
			int x = (int) helPos[0].floatValue();
			if (x >= i
					&& x <= REC_WIDTH + i
					&& helPos[1] >= (this.getHeight() - mTopRects.get(l + 1)
							- helRef.getHeight() - 10)) {
				isCollesion = true;
			}
			// c.drawRect(left, top, right, bottom, paint)
			c.drawRect(i, this.getHeight() - mTopRects.get(l), REC_WIDTH + i,
					this.getHeight(), mDrawPaint);
		}
		c.drawBitmap(helRef, helPos[0], helPos[1], mBackGroundPaint);
		mFlip = !mFlip;
		if (helPos[1] < (getHeight() - 40)) {
			helPos[1] += DOWN_SPEED;
		}
		mPosition.put("hel_pos", helPos);
		if (!isCollesion) {
			mHandler.postDelayed(mInvalidate, FRAME_PER_SECOUND);
		} else {
			c.drawCircle(helPos[0] + mHel1.getHeight(), helPos[1],
					mHel1.getWidth() - 5, mRedPaint);
		}
		global_counter = ((global_counter + 1) % MAX_COUNT);
	}

	public boolean isThereCollision(int x, int y, int left, int top,
			int buttom, int width) {
		if (x >= left && x <= width + left && y >= top && y <= buttom) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Float[] helPos = mPosition.get("hel_pos");
		switch (event.getActionIndex()) {
		case MotionEvent.ACTION_DOWN:
			if (helPos[1] - DOWN_SPEED * 8 > 0) {
				helPos[1] -= DOWN_SPEED * 8;
			}
		}
		return true;
	}
}
