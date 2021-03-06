package org.vn.unit;

import java.io.InputStream;

import org.vn.gl.BaseObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LogicMap {
	public final boolean[][] mArrayMap;

	public LogicMap(int sizeWidthTile, int sizeHeightTile, byte[][] pMapTile,
			int rIDMap) {
		InputStream is = BaseObject.sSystemRegistry.contextParameters.context
				.getResources().openRawResource(rIDMap);
		Bitmap temp = null;
		temp = BitmapFactory.decodeStream(is);
		int widthMapAlpha = temp.getWidth();
		int heightMapAlpha = temp.getHeight();

		mArrayMap = new boolean[sizeHeightTile][sizeWidthTile];
		for (int y = 0; y < sizeHeightTile; y++) {
			for (int x = 0; x < sizeWidthTile; x++) {
				float posX = BaseObject.sSystemRegistry.mapTiles.arrayMap[y][x].x;
				float posY = BaseObject.sSystemRegistry.cameraSystem.worldMap.mHeightWord
						- BaseObject.sSystemRegistry.mapTiles.arrayMap[y][x].y;
				posX = posX
						/ BaseObject.sSystemRegistry.cameraSystem.worldMap.mWidthWord
						* widthMapAlpha;
				posY = posY
						/ BaseObject.sSystemRegistry.cameraSystem.worldMap.mHeightWord
						* heightMapAlpha;
				if (temp.getPixel((int) posX, (int) posY) == -1) {
					mArrayMap[y][x] = true;
				}
			}
		}
	}

	public void setIsCanMove(int x, int y, boolean b) {
		mArrayMap[y][x] = b;
	}
}
