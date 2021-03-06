package org.vn.unit;

import org.vn.gl.BaseObject;
import org.vn.gl.DrawableBitmap;
import org.vn.gl.RenderSystem;
import org.vn.gl.TextureLibrary;
import org.vn.gl.Utils;
import org.vn.gl.Vector2;
import org.vn.gl.iBitmapInImageCache;
import org.vn.gl.game.CameraSystem;
import org.vn.herowar.R;

import android.graphics.Bitmap;
import android.graphics.Color;

public class DialogChatInGameCameraRelative {
	private DrawableBitmap mChatOfChar;
	private float mTimeShowChat = 0;

	public DialogChatInGameCameraRelative(TextureLibrary textureLibrary) {
		mChatOfChar = new DrawableBitmap(textureLibrary.allocateTextureNotHash(
				R.drawable.dialog_back, "khungchat"), 72, 36);
	}

	public void inputChat(final String content, int time) {
		iBitmapInImageCache bitmapInImageCache = new iBitmapInImageCache() {

			@Override
			public Bitmap getBitMapResourcesItem() {
				Bitmap bitmapKhungChat = Utils.decodeRawResource(
						BaseObject.sSystemRegistry.contextParameters.context
								.getResources(), R.drawable.dialog_back);
				Utils.setText(bitmapKhungChat, content, 22,
						Color.argb(255, 100, 50, 50), 10);
				return bitmapKhungChat;
			}
		};

		mChatOfChar.changeBitmap(bitmapInImageCache, true);
		mTimeShowChat = time;
	}

	public void drawChat(float timeDelta, RenderSystem render, float x,
			float y, int priority) {
		if (mTimeShowChat > 0) {
			CameraSystem cameraSystem = BaseObject.sSystemRegistry.cameraSystem;
			Vector2 drawChat = new Vector2(x + 10, y + 30);
			if (drawChat.x < cameraSystem.getX())
				drawChat.x = cameraSystem.getX();
			if (drawChat.y < cameraSystem.getY())
				drawChat.y = cameraSystem.getY();
			float rightChatMax = cameraSystem.getX()
					+ BaseObject.sSystemRegistry.contextParameters.gameWidthAfter
					- mChatOfChar.getWidth();
			float topChatMax = cameraSystem.getY()
					+ BaseObject.sSystemRegistry.contextParameters.gameHeightAfter
					- mChatOfChar.getHeight();
			if (drawChat.x > rightChatMax)
				drawChat.x = rightChatMax;
			if (drawChat.y > topChatMax)
				drawChat.y = topChatMax;

			if (mTimeShowChat > 1) {
				mChatOfChar.setColorExpressF(1, 1, 1, 1);
			} else {
				mChatOfChar.setColorExpressF(mTimeShowChat, mTimeShowChat,
						mTimeShowChat, mTimeShowChat);
			}

			render.scheduleForDraw(mChatOfChar, drawChat, priority, true);
			mTimeShowChat -= timeDelta;
		}
	}
}
