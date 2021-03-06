package org.vn.herowar;

import org.vn.cache.CurrentGameInfo;
import org.vn.cache.CurrentUserInfo;
import org.vn.constant.CommandClientToServer;
import org.vn.gl.BaseObject;
import org.vn.gl.DebugLog;
import org.vn.gl.DeviceInfo;
import org.vn.gl.GLSurfaceView;
import org.vn.gl.Game;
import org.vn.gl.GameInfo;
import org.vn.gl.Utils;
import org.vn.model.NextTurnMessage;
import org.vn.model.PlayerModel;
import org.vn.network.GlobalMessageHandler.LightWeightMessage;
import org.vn.unit.ActionList;
import org.vn.unit.ActionServerToClient.ActionType;
import org.vn.unit.InputGameInterface.OnEndgameListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HeroWarActivity extends CoreActiity {

	private static final int DIALOG_GAME_CONFIRM = 1;
	// public static final int QUIT_GAME_DIALOG = 0;
	// public static final int VERSION = 14;
	// public static final int MAGESRIKEACTIVITY = 0;
	public static final int DISCONECT = 1;
	public static final int BOARDLISTACTIVITY = 2;
	public static final int RESULT = 3;
	public static final int WAITINGACTIVITY = 4;
	private GLSurfaceView mGLSurfaceView;
	private Game mGame;
	private long mLastTouchTime = 0L;

	private OnEndgameListener onEndgameListener;

	private MediaPlayer mMediaPlayer;

	private LinearLayout mLayoutChat;
	private Button mBtChat;
	private EditText mEditTextChat;
	private int mFlagActivityGoto = -1;
	private CurrentGameInfo mCurrentGameInfo = CurrentGameInfo.getIntance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		{
			mLayoutChat = (LinearLayout) findViewById(R.id.layout_chat);
			mBtChat = (Button) findViewById(R.id.button_chat);
			mEditTextChat = (EditText) findViewById(R.id.editText_chat);
			mLayoutChat.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						chatAll(mEditTextChat.getText().toString());
						turnOffChat();
						return false;
					}
					return false;
				}
			});
			mLayoutChat.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					turnOffChat();
				}

			});
			mEditTextChat
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (event != null) {
								switch (event.getAction()) {
								case KeyEvent.KEYCODE_ENTER:
								case EditorInfo.IME_ACTION_DONE:
									chatAll(mEditTextChat.getText().toString());
									turnOffChat();
									return false;
								}
							}
							switch (actionId) {
							case KeyEvent.KEYCODE_ENTER:
							case EditorInfo.IME_ACTION_DONE:
								chatAll(mEditTextChat.getText().toString());
								turnOffChat();
								return false;
							}
							return false;
						}
					});
			mEditTextChat.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						chatAll(mEditTextChat.getText().toString());
						turnOffChat();
						return false;
					}
					return false;
				}
			});
			mEditTextChat.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					if (!arg1) {
						chatAll(mEditTextChat.getText().toString());
						turnOffChat();
					}
				}
			});
			mBtChat.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					chatAll(mEditTextChat.getText().toString());
					turnOffChat();
				}
			});
		}

		mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
		configGame();
		mGame = new Game();
		mGame.setSurfaceView(mGLSurfaceView);

		try {
			/*
			 * Nếu là lần đầu vào màn hình này thì hiện bảng hướng dẫn lên
			 */
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			DeviceInfo.DEVICE_WIDTH = dm.widthPixels;
			DeviceInfo.DEVICE_HEIGHT = dm.heightPixels;
			mGame.bootstrap(this, dm.widthPixels, dm.heightPixels,
					GameInfo.DEFAULT_WIDTH, GameInfo.DEFAULT_HEIGHT);
		} catch (Exception e) {
			Toast.makeText(HeroWarActivity.this, "Game error",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		mGLSurfaceView.setRenderer(mGame.getRenderer());

		onEndgameListener = new OnEndgameListener() {
			@Override
			public void onEndgameListener() {
				endGame(RESULT);
			}

			// Return back the waiting screen
			@Override
			public void onGLDeletedListener() {
				switch (mFlagActivityGoto) {
				case DISCONECT:
					break;
				case RESULT:
					// mGS.LEAVE_BOARD();
					runOnUiThread(new Runnable() {
						public void run() {
							Intent intent = new Intent(HeroWarActivity.this,
									ResuftActivity.class);
							startActivity(intent);
						}
					});
					break;
				case BOARDLISTACTIVITY:
					// mGS.EXIT_BOARD(CurrentGameInfo.getIntance().boardId);
					mGS.LEAVE_BOARD();
					runOnUiThread(new Runnable() {
						public void run() {
							Intent intent = new Intent(HeroWarActivity.this,
									BoardActivity.class);
							startActivity(intent);
						}
					});
					break;
				case WAITINGACTIVITY:
					runOnUiThread(new Runnable() {
						public void run() {
							Intent intent = new Intent(HeroWarActivity.this,
									WaitingActivity.class);
							startActivity(intent);
						}
					});
					break;
				}

				mediaStop();
				if (mGame != null) {
					mGame.stop();
					mGame = null;
				}
				finish();
			}

			@Override
			public void LoadingUnitComplete() {
			}

			@Override
			public void turnOnchat() {
				runOnUiThread(new Runnable() {
					public void run() {
						mEditTextChat.setText("");
						mLayoutChat.setVisibility(View.VISIBLE);
						InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
						mEditTextChat.requestFocus();
						imm.showSoftInput(mEditTextChat, 0);
					}
				});
			}
		};

		BaseObject.sSystemRegistry.inputGameInterface
				.setOnSelectedItemListener(onEndgameListener);

		showToast(getString(R.string.infor_before_the_game));
	}

	private void configGame() {
		mGLSurfaceView.setEGLConfigChooser(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (mGame != null) {
				mGame.onPause();
				mGame.getRenderer().onPause();
			}
			if (mGLSurfaceView != null)
				mGLSurfaceView.onPause();
			// hack!
			if (mMediaPlayer != null) {
				mMediaPlayer.pause();
			}
		} catch (Exception e) {
			DebugLog.e("MageStrike", "onPause()");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			// Preferences may have changed while we were paused.
			if (mGLSurfaceView != null)
				mGLSurfaceView.onResume();
			if (mGame != null) {
				mGame.setSoundEnabled(GameInfo.ENABLE_SOUND);
				mGame.onResume(this, false);
			}
			// Mở nhạc nền
			if (GameInfo.ENABLE_SOUND == true && mMediaPlayer != null
					&& mMediaPlayer.isPlaying() == false) {
				try {
					mMediaPlayer.setLooping(true);
					mMediaPlayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			DebugLog.e("MageStrike", "onResume()");
		}
	}

	@Override
	protected void onDestroy() {
		try {
			mediaStop();
		} catch (Exception e) {
			DebugLog.e("MageStrike", "onDestroy()");
		}
		super.onDestroy();
	}

	private void mediaStop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_GAME_CONFIRM:
			return new AlertDialog.Builder(this)
					.setMessage(getString(R.string.exit))
					.setPositiveButton(R.string.dialog_ok,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									endGame(BOARDLISTACTIVITY);
								}
							}).setNegativeButton(R.string.dialog_cancel, null)
					.create();
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		default:
			break;
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGame != null && mGame.isBootstrapComplete() && !mGame.isPaused()
				&& BaseObject.sSystemRegistry != null) {
			mGame.onTouchEvent(event);
			if (BaseObject.sSystemRegistry.inputGameInterface != null)
				BaseObject.sSystemRegistry.inputGameInterface.dumpEvent(event);
			// BaseObject.sSystemRegistry.inputGameInterface.onTouchEvent(event);

			final long time = System.currentTimeMillis();
			if (event.getAction() == MotionEvent.ACTION_MOVE
					&& time - mLastTouchTime < 32) {
				// Sleep so that the main thread doesn't get flooded with UI
				// events.
				try {
					Thread.sleep(32);
				} catch (InterruptedException e) {
				}
				mGame.getRenderer().waitDrawingComplete();
			}
			mLastTouchTime = time;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(DIALOG_GAME_CONFIRM);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void endGame(int flagActivityGoto) {
		if (mGame != null && BaseObject.sSystemRegistry != null) {
			mFlagActivityGoto = flagActivityGoto;
			mGame.stopGL();
		}
		mediaStop();
	}

	public void turnOffChat() {
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEditTextChat.getWindowToken(), 0);
			mLayoutChat.setVisibility(View.GONE);
			BaseObject.sSystemRegistry.inputGameInterface.turnOffChat();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void chatAll(String content) {
		if (content.length() > 0) {
			mGS.CHAT_BOARD(content);
		}
	}

	@Override
	public void onMessageReceived(final LightWeightMessage msg) {
		switch (msg.command) {
		case CommandClientToServer.LOST_CONNECTION:
			onDisconnect();
			break;
		case CommandClientToServer.ARMY_SELECTION:
			mGS.LAYOUT_ARMY(BaseObject.sSystemRegistry.characterManager.arrayCharactersMyTeam);
			break;
		case CommandClientToServer.LAYOUT_ARMY:
			if (mCurrentGameInfo.isHost()) {
				mGS.START_GAME();
			} else {
				mGS.READY();
			}
			break;
		case CommandClientToServer.SOMEONE_JOIN_BOARD:
			showToast(getString(R.string.join_in_game, (String) msg.obj));
//			updateStatusBoard();
			break;
		case CommandClientToServer.SOMEONE_LEAVE_BOARD:
			showToast(getString(R.string.leave_in_game, (String) msg.obj));
//			updateStatusBoard();
			if (msg.arg1 == 1) {
				// Go to waiting
				endGame(WAITINGACTIVITY);
			}
			break;
		case CommandClientToServer.MOVE_ARMY:
			// MoveMessage moveMessage = (MoveMessage) msg.obj;
			break;
		case CommandClientToServer.ATTACH:
			// AttackMessage atackMessage = (AttackMessage) msg.obj;
			break;
		case CommandClientToServer.NEXT_TURN:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						NextTurnMessage nextTurnMessage = (NextTurnMessage) msg.obj;
						for (PlayerModel playerModel : mCurrentGameInfo.mListPlayerInGame) {
							if (nextTurnMessage.idPlayerInTurnNext == playerModel.ID) {
								if (playerModel.ID == CurrentUserInfo.mPlayerInfo.ID) {
									showToast(getString(R.string.next_you_turn));
								} else {
									showToast(getString(R.string.next_turn,
											playerModel.name));
								}
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			break;
		case CommandClientToServer.CHAT_BOARD:
			if (mGame.isBootstrapComplete() && !mGame.isPaused()) {
				if (CurrentUserInfo.mPlayerInfo.ID == msg.arg1) {
					BaseObject.sSystemRegistry.unitSreen
							.inputChatRight_Bot((String) msg.obj);
				} else {
					BaseObject.sSystemRegistry.unitSreen
							.inputChatLeft_Bot((String) msg.obj);
				}
			}
			break;
		case CommandClientToServer.END_GAME:
			
			break;
		}
	}

//	private void updateStatusBoard() {
//		if (mCurrentGameInfo.isInGame) {
//			return;
//		}
//		if (mCurrentGameInfo.isHost()) {
//			if (mCurrentGameInfo.isReadyAll()) {
//				if (BaseObject.sSystemRegistry != null) {
//					BaseObject.sSystemRegistry.dialogAddEnemy
//							.setIsEnableReady(true);
//				}
//			} else {
//				if (BaseObject.sSystemRegistry != null) {
//					BaseObject.sSystemRegistry.dialogAddEnemy
//							.setIsEnableReady(false);
//				}
//			}
//		} else {
//			if (mCurrentGameInfo.isIReady()) {
//				if (BaseObject.sSystemRegistry != null) {
//					BaseObject.sSystemRegistry.dialogAddEnemy
//							.setIsEnableReady(true);
//				}
//			} else {
//				if (BaseObject.sSystemRegistry != null) {
//					BaseObject.sSystemRegistry.dialogAddEnemy
//							.setIsEnableReady(false);
//				}
//			}
//		}
//	}

	@Override
	public void onDisconnect() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(HeroWarActivity.this)
						.setMessage("Connection lost")
						.setCancelable(false)
						.setPositiveButton(R.string.dialog_ok,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										endGame(DISCONECT);
									}
								}).show();
			}
		});
	}

	@Override
	protected int getRawBackground() {
		return R.raw.ingame1 + Utils.RANDOM.nextInt(3);
	}
}
