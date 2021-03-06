package org.vn.unit;

import java.util.ArrayList;

import org.vn.cache.CurrentGameInfo;
import org.vn.gl.BaseObject;
import org.vn.gl.DrawableBitmap;
import org.vn.gl.GameInfo;
import org.vn.gl.Priority;
import org.vn.gl.RenderSystem;
import org.vn.gl.Texture;
import org.vn.gl.TextureLibrary;
import org.vn.gl.Vector2;
import org.vn.gl.iBitmapInImageCache;
import org.vn.herowar.R;
import org.vn.model.EnemyType;
import org.vn.model.TouchTouch;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DialogAddEnemy extends BaseObject {
	private Tile mTileSeleted = null;
	private ButtonEnemy[] mButton;
	private ButtonEnemy mButtonSeleted = null;

	public DrawableBitmap mDrawableCoin;

	/** Dialog khung the hien info ben goc trai */
	private Button mDialogInfo;
	public DrawableBitmap mDrawableCharacterInfo;
	public DrawableBitmap mDrawableInfo;

	private Button mBtAddEnemy;

	private Button mBtSkipTurn;

	private boolean isVisible = true;
	public int mMoney;
	public Texture textureBuy_Down;
	public Texture textureBuy_Up;
	public Texture[] textureEnemys;

	public DialogAddEnemy(TextureLibrary textureLibrary) {
		{
			int[] idTextureEnemy = { R.drawable.player_cannon_sleep,
					R.drawable.player_geezer_sleep,
					R.drawable.player_gunslinger_sleep,
					R.drawable.player_indian_sleep,
					R.drawable.player_mexican_sleep,
					R.drawable.player_scout_sleep };
			textureEnemys = new Texture[idTextureEnemy.length];
			for (int i = 0; i < idTextureEnemy.length; i++) {
				textureEnemys[i] = textureLibrary.allocateTexture(
						idTextureEnemy[i], "idTextureEnemy" + i);
			}
		}
		textureBuy_Down = textureLibrary.allocateTextureNotHash(
				R.drawable.bt_buy_down, "bt_buy_down");
		textureBuy_Up = textureLibrary.allocateTextureNotHash(
				R.drawable.bt_buy_up, "bt_buy_up");

		// Khoi tao list enemy
		ArrayList<EnemyType> listEnemytype = new ArrayList<EnemyType>();
		for (EnemyType enemyType : CurrentGameInfo.getIntance().listEnemytype) {
			if (enemyType.armyType != GameInfo.idTypeKing) {
				listEnemytype.add(enemyType);
			}
		}
		mButton = new ButtonEnemy[listEnemytype.size()];
		for (int i = 0; i < mButton.length; i++) {
			mButton[i] = new ButtonEnemy(new DrawableBitmap(textureEnemys[i
					% textureEnemys.length], 100, 100), listEnemytype.get(i)) {
				public boolean onClick() {
					if (mButtonSeleted != this) {
						if (mButtonSeleted != null) {
							mButtonSeleted.mDrawableBitmap.setColorExpressF(1,
									1, 1, 0.5f);
						}
						mButtonSeleted = this;
						mButtonSeleted.mDrawableBitmap.setColorExpressF(1, 1,
								1, 1);
						{
							// Chang bitmap info
							iBitmapInImageCache bmInfor = new iBitmapInImageCache() {

								@Override
								public Bitmap getBitMapResourcesItem() {
									Bitmap bitmap = createBitmapInforEnemy(mEnemyType);
									return bitmap;
								}

							};
							if (mDrawableInfo.getTexture() == null) {
								mDrawableInfo
										.setTexture(sSystemRegistry.longTermTextureLibrary
												.allocateBitmapCache(bmInfor,
														true, "BmInfor"));
							} else {
								mDrawableInfo.changeBitmap(bmInfor, true);
							}

							mDrawableCharacterInfo
									.setTexture(textureEnemys[mEnemyType.armyType
											% textureEnemys.length]);
						}
					}
					return true;
				};

				@Override
				public boolean onFocus() {
					return true;
				}
			};
			mButton[i].pos.set(
					200 + i / 2 * mButton[i].mDrawableBitmap.getWidth(), 10 + i
							% 2 * mButton[i].mDrawableBitmap.getHeight());
			mButton[i].mDrawableBitmap.setColorExpressF(1, 1, 1, 0.5f);
		}

		// Khoi tao infor cho enemy
		mDialogInfo = new Button(new DrawableBitmap(
				textureLibrary.allocateTexture(R.drawable.dialog_back,
						"dialog_back"), 208, 422)) {
			@Override
			public boolean onClick() {
				mBtAddEnemy.checkInTouch();
				return true;
			}

			public boolean onFocus() {
				if (!mBtAddEnemy.checkInTouch()) {
					mBtAddEnemy.setDrawOfffocus();
				} else {
					mBtAddEnemy.setDrawOnfocus();
				}
				return true;
			};
		};
		// Bt andEnemy
		mBtAddEnemy = new Button(new DrawableBitmap(textureBuy_Down, 60, 60)) {
			@Override
			public boolean onClick() {
				addEnemyAt(mTileSeleted, mButtonSeleted.mEnemyType);
				return true;
			}

			public boolean onFocus() {
				return true;
			};

			public void setDrawOfffocus() {
				mDrawableBitmap.setTexture(textureBuy_Down);
			};

			public void setDrawOnfocus() {
				mDrawableBitmap.setTexture(textureBuy_Up);
			};
		};
		mBtAddEnemy.pos
				.set((mDialogInfo.mDrawableBitmap.getWidth() - mBtAddEnemy.mDrawableBitmap
						.getWidth()) - 30, 30);

		mMoney = CurrentGameInfo.getIntance().gold;
		// Bitmap ve~ info
		mDrawableInfo = new DrawableBitmap(null, 180, 180);
		// Bitmap ve~ character tren info
		mDrawableCharacterInfo = new DrawableBitmap(textureEnemys[0], 150, 150);
		mDrawableCoin = new DrawableBitmap(textureLibrary.allocateTexture(
				R.drawable.coin, "coin"), 50, 50);

		mBtSkipTurn = new Button(
				new DrawableBitmap(textureLibrary.allocateTexture(
						R.drawable.skip, "skip"), 70, 70)) {
			@Override
			public boolean onClick() {
				sSystemRegistry.inputGameInterface.mTimeTickInTurn = 0;
				sSystemRegistry.mGS.NEXT_TURN();
				return true;
			}

			public void setDrawOfffocus() {
				mDrawableBitmap.setWidth(70);
				mDrawableBitmap.setHeight(70);
			};

			public void setDrawOnfocus() {
				mDrawableBitmap.setWidth(90);
				mDrawableBitmap.setHeight(90);
			};
		};
		mBtSkipTurn.pos.set(630, GameInfo.DEFAULT_HEIGHT
				- mBtSkipTurn.mDrawableBitmap.getHeight() - 20);
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		RenderSystem render = sSystemRegistry.renderSystem;
		sSystemRegistry.numberDrawableTime.drawNumberWithAlpha(80, 410, mMoney,
				1, false, false, Priority.ButtonOnDialogAddEnemy);
		render.scheduleForDraw(mDrawableCoin, Vector2.TAMP.set(4,
				GameInfo.DEFAULT_HEIGHT - 8 - mDrawableCoin.getHeight()),
				Priority.ButtonOnDialogAddEnemy, false);
		if (isVisible && sSystemRegistry.inputGameInterface.isControl()) {
			if (mTileSeleted != null
					&& mTileSeleted.getCharacterTaget() == null
					&& !mTileSeleted.isColition()) {
				for (Button button : mButton) {
					button.checkInTouch();
					button.draw(render, Priority.DialogAddEnemy);
				}
				if (mButtonSeleted != null) {
					showEnemyselect(render, mButtonSeleted.mEnemyType);
				}
			}
		}

		if (sSystemRegistry.inputGameInterface.isControl()) {
			mBtSkipTurn.checkInTouch();
			mBtSkipTurn.draw(render, Priority.Hind);
		}
	}

	public void setTileSeleted(Tile pTile) {
		UnitCharacterSwordmen myKing = BaseObject.sSystemRegistry.characterManager.myKing;
		if (myKing == null) {
			pTile = null;
		} else if (pTile != null
				&& !UnitCharacterSwordmen.isInSight(myKing.mTileTaget.xTile,
						myKing.mTileTaget.yTile, pTile.xTile, pTile.yTile,
						myKing.mEnemyType.rangeview)) {
			pTile = null;
		}

		mTileSeleted = pTile;
		// Tat seleted
		if (mButtonSeleted != null) {
			mButtonSeleted.mDrawableBitmap.setColorExpressF(1, 1, 1, 0.5f);
			mButtonSeleted = null;
		}
	}

	private void showEnemyselect(RenderSystem render, EnemyType enemyType) {
		mDialogInfo.checkInTouch();
		mDialogInfo.draw(render, Priority.DialogAddEnemy);
		sSystemRegistry.numberDrawableCostInDialogAddEnemy.drawNumberWithAlpha(
				80, 40, enemyType.cost, 1, false, false,
				Priority.ButtonOnDialogAddEnemy);
		mBtAddEnemy.draw(render, Priority.ButtonOnDialogAddEnemy);
		sSystemRegistry.renderSystem.scheduleForDraw(mDrawableInfo,
				Vector2.TAMP.set(
						(mDialogInfo.mDrawableBitmap.getWidth() - mDrawableInfo
								.getWidth()) / 2, 100),
				Priority.ButtonOnDialogAddEnemy, false);
		sSystemRegistry.renderSystem
				.scheduleForDraw(
						mDrawableCharacterInfo,
						Vector2.TAMP.set(
								(mDialogInfo.mDrawableBitmap.getWidth() - mDrawableCharacterInfo
										.getWidth()) / 2, 260),
						Priority.ButtonOnDialogAddEnemy, false);
	}

	public void addEnemyAt(Tile tile, EnemyType enemyType) {

	}

	private class ButtonEnemy extends Button {
		public EnemyType mEnemyType;

		public ButtonEnemy(DrawableBitmap pDrawableBitmap, EnemyType pEnemyType) {
			super(pDrawableBitmap);
			mEnemyType = pEnemyType;
		}

	}

	private class Button {
		public Button(DrawableBitmap pDrawableBitmap) {
			mDrawableBitmap = pDrawableBitmap;
		}

		public final DrawableBitmap mDrawableBitmap;

		public Vector2 pos = new Vector2();

		public void draw(RenderSystem render, int priority) {
			render.scheduleForDraw(mDrawableBitmap, pos, priority, false);
		}

		final public boolean checkInTouch() {
			TouchTouch touchTouch = sSystemRegistry.inputGameInterface.mTouchTouch;

			if (touchTouch.isTouch) {
				float x = touchTouch.xNoCamera;
				float y = touchTouch.yNoCamera;
				if (x < pos.x || y < pos.y
						|| x > pos.x + mDrawableBitmap.getWidth()
						|| y > pos.y + mDrawableBitmap.getHeight()) {
					setDrawOfffocus();
				} else {
					if (touchTouch.isTouchUp) {
						setDrawOfffocus();
						if (onClick()) {
							sSystemRegistry.inputGameInterface.daXuLyTouch();
							return true;
						} else {
							return false;
						}
					} else {
						if (onFocus()
								&& touchTouch.chuaXuLyTouchDownTrongVongLap) {
							sSystemRegistry.inputGameInterface
									.daXuLyTouchTrongVongLap();
							setDrawOnfocus();
							return true;
						} else {
							setDrawOfffocus();
							return false;
						}
					}
				}
			}
			setDrawOfffocus();
			return false;
		}

		/**
		 * Co xu ly thi tra ve true
		 * 
		 * @return
		 */
		public boolean onClick() {
			return false;
		}

		public boolean onFocus() {
			return true;
		}

		public void setDrawOnfocus() {

		}

		public void setDrawOfffocus() {

		}
	}

	private Bitmap createBitmapInforEnemy(EnemyType pEnemyType) {
		Bitmap bitmap = Bitmap.createBitmap(128, 128, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		// canvas.drawCircle(0, 0, pEnemyType.cost * 3, new Paint());
		Paint paint = new Paint();
		float textSize = 14;
		paint.setTextSize(textSize);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		int count = 1;
		textSize++;
		{
			canvas.drawText("" + pEnemyType.armyName, 0, textSize * count,
					paint);
			count++;
		}
		{
			canvas.drawText("Hp:" + pEnemyType.hp, 0, textSize * count, paint);
			count++;
		}
		{
			canvas.drawText("Mana:" + pEnemyType.mana, 0, textSize * count,
					paint);
			count++;
		}
		{
			canvas.drawText("Damage:" + pEnemyType.damageMin + "~"
					+ pEnemyType.damageMax, 0, textSize * count, paint);
			count++;
		}
		{
			canvas.drawText("View:" + pEnemyType.rangeview, 0,
					textSize * count, paint);
			count++;
		}
		{
			canvas.drawText("Move:" + pEnemyType.movecost, 0, textSize * count,
					paint);
			count++;
		}
		{
			canvas.drawText("RangeAttack:" + pEnemyType.rangeattack, 0,
					textSize * count, paint);
			count++;
		}
		// {
		// canvas.drawText("Attackcost:" + pEnemyType.attackcost + "/1time",
		// 0, textSize * count, paint);
		// count++;
		// }

		return bitmap;
	}

	public void setVisible(boolean b) {
		isVisible = b;
	}
}
