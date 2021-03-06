package org.vn.gl;

import org.vn.gl.Touch.ActionTouch;

import android.os.SystemClock;

public class SingerTouchDetector extends BaseObject {
	public SingerTouchDetector() {
		for (int i = 0; i < listTouchs.length; i++) {
			listTouchs[i] = new Touch();
		}
		for (int i = 0; i < mArrayListTouchDown.length; i++) {
			mArrayListTouchDown[i] = new Touch();
		}
	}

	private Touch[] listTouchs = new Touch[2];
	private int touchCurrent = 0;
	private int touchLast = -1;
	private final float DISTANCE_CLICK = 50;// <click | > fling
	private final long TIME_CLICK = 500;// long <click
	private final long TIME_FLING = 1500;// long <fling | > nothing
	private ITouchDetectorListener mTouchDetectorListener = null;

	private Touch[] mArrayListTouchDown = new Touch[3];
	private int indextArrayListTouchDown = 0;
	private int countArrayListTouchDown = 0;

	private Touch mCurrentTouch = new Touch();

	public void touch(ActionTouch pActionTouch, final float x, final float y,
			int indextTouch) {
		boolean touchDown = (pActionTouch == ActionTouch.UP ? false : true);
		mCurrentTouch.actionTouch = pActionTouch;
		mCurrentTouch.timeCurrent = SystemClock.elapsedRealtime();
		mCurrentTouch.touchDown = touchDown;
		mCurrentTouch.x = x;
		mCurrentTouch.y = y;

		if (touchDown) {
			countArrayListTouchDown++;
			if (countArrayListTouchDown > mArrayListTouchDown.length)
				countArrayListTouchDown = mArrayListTouchDown.length;
			indextArrayListTouchDown++;
			if (indextArrayListTouchDown >= mArrayListTouchDown.length)
				indextArrayListTouchDown = 0;
			mArrayListTouchDown[indextArrayListTouchDown].x = x;
			mArrayListTouchDown[indextArrayListTouchDown].y = y;
			mArrayListTouchDown[indextArrayListTouchDown].timeCurrent = mCurrentTouch.timeCurrent;
			if (pActionTouch == ActionTouch.MOVE
					&& mTouchDetectorListener != null) {
				int indextLastTouchDown = indextArrayListTouchDown - 1;
				if (indextLastTouchDown < 0)
					indextLastTouchDown = mArrayListTouchDown.length - 1;
				mTouchDetectorListener.onScroll(x
						- mArrayListTouchDown[indextLastTouchDown].x, y
						- mArrayListTouchDown[indextLastTouchDown].y,
						indextTouch);
			}
		}

		if (listTouchs[touchCurrent].touchDown != touchDown) {
			touchLast = touchCurrent;
			touchCurrent = (touchCurrent == 0 ? 1 : 0);
			listTouchs[touchCurrent].touchDown = touchDown;
			listTouchs[touchCurrent].x = x;
			listTouchs[touchCurrent].y = y;
			listTouchs[touchCurrent].timeCurrent = mCurrentTouch.timeCurrent;
			if (!listTouchs[touchCurrent].touchDown
					&& listTouchs[touchLast].touchDown) {
				long deltaTime = listTouchs[touchCurrent].timeCurrent
						- listTouchs[touchLast].timeCurrent;
				float Vx = listTouchs[touchCurrent].x - listTouchs[touchLast].x;
				float Vy = listTouchs[touchCurrent].y - listTouchs[touchLast].y;
				float distan = Vx * Vx + Vy * Vy;
//				DebugLog.d("DUC", "deltaTime:" + deltaTime + "//distan:"
//						+ distan);
				if (distan < DISTANCE_CLICK) {
					if (deltaTime < TIME_CLICK) {
						if (mTouchDetectorListener != null)
							mTouchDetectorListener.onClick(
									listTouchs[touchCurrent].x,
									listTouchs[touchCurrent].y, indextTouch);
					}
				} else {
					if (mTouchDetectorListener != null
							&& countArrayListTouchDown == mArrayListTouchDown.length) {
						int lastTouch = indextArrayListTouchDown - 1;
						if (lastTouch == -1)
							lastTouch = mArrayListTouchDown.length - 1;
						Vx = listTouchs[touchCurrent].x
								- mArrayListTouchDown[lastTouch].x;
						Vy = listTouchs[touchCurrent].y
								- mArrayListTouchDown[lastTouch].y;
						deltaTime = listTouchs[touchCurrent].timeCurrent
								- mArrayListTouchDown[lastTouch].timeCurrent;
						mTouchDetectorListener.onFling(Vx, Vy, deltaTime,
								indextTouch);
					}
				}
				indextArrayListTouchDown = 0;
				countArrayListTouchDown = 0;
			}
		}
	}

	public Touch getCurrentTouch() {
		return mCurrentTouch;
	}

	public void setTouchDetectorListener(
			ITouchDetectorListener pTouchDetectorListener) {
		mTouchDetectorListener = pTouchDetectorListener;
	}

	public interface ITouchDetectorListener {
		// ===========================================================
		// Methods
		// ===========================================================
		public void touchDown(float x, float y, int indext);

		public void touchMove(float x, float y, int indext);

		public void onScroll(float xOffset, float yOffset, int indext);

		public void touchUp(float x, float y, int indext);

		public void onClick(float x, float y, int indext);

		public void onFling(float Vx, float Vy, float deltaTime, int indext);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
}
