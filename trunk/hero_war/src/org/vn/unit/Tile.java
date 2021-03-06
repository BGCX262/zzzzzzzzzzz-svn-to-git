package org.vn.unit;

import org.vn.gl.BaseObject;
import org.vn.gl.DrawableBitmap;
import org.vn.gl.Priority;
import org.vn.gl.Texture;
import org.vn.gl.Vector2;

public class Tile extends BaseObject {
	/**
	 * Không cho bất kì ai đặt chân lên ô này.
	 */
	public static final byte ALLOW_NONE = 0;
	/**
	 * Chi cho phep linh bo di wa o dat nay. Linh bo bao gom: Ki binh, Bo binh
	 * và Lính cung.
	 */
	public static final byte ALLOW_INFANTRY = 1;
	/**
	 * Cho phép các phuong tiện nhu xe ban da, ... (Cai nay de nang cap sau nay
	 * neu co).
	 */
	public static final byte ALLOW_VEHICLES = 2;
	/**
	 * Cho phep ca xe va linh deu di qua duoc cell nay.
	 */
	public static final byte ALLOW_VEHICLES_INFANTRY = 3;
	/**
	 * Cell này dành cho tướng. Mất ô này thì user thua.
	 */
	public static final byte ALLOW_GENERAL = 4;

	public int xTile, yTile;
	public float x, y;
	private Vector2 pos = new Vector2();
	public boolean isSetPos = false;
	private DrawableBitmap mDrawableBitmap;
	private boolean mIsForcus = false;
	private UnitCharacter mUnitCharacterTaget = null;

	public Tile(Texture texture, float width, float height, int _xTile,
			int _yTile) {
		mDrawableBitmap = new DrawableBitmap(texture, width, height);
		xTile = _xTile;
		yTile = _yTile;
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		if (mIsForcus) {
			mDrawableBitmap.setColorExpressF(1, 1, 1, 1);
		} else {
			mDrawableBitmap.setColorExpressF(1, 1, 0, 1);
		}
		pos.set(x - mDrawableBitmap.getWidth() / 2,
				y - mDrawableBitmap.getHeight() / 2);
		sSystemRegistry.renderSystem.scheduleForDraw(mDrawableBitmap, pos,
				Priority.Map, true);
		// if (mIsForcus) {
		// mDrawableBitmap.setColorExpressF(1, 1, 1, 1);
		// pos.set(x - mDrawableBitmap.getWidth() / 2,
		// y - mDrawableBitmap.getHeight() / 2);
		// sSystemRegistry.renderSystem.scheduleForDraw(mDrawableBitmap, pos,
		// Priority.Map, true);
		// }
	}

	@Override
	public void reset() {
	}

	public void enableForcus() {
		mIsForcus = true;
	}

	public void disableForcus() {
		mIsForcus = false;
	}

	public boolean isForcus() {
		return mIsForcus;
	}

	public void setCharacterTaget(UnitCharacter pUnitCharacter) {
		mUnitCharacterTaget = pUnitCharacter;
	}

	public UnitCharacter getCharacterTaget() {
		return mUnitCharacterTaget;
	}

	public boolean isColition() {
		return sSystemRegistry.logicMap.mArrayMap[yTile][xTile];
	}
}
