package cn.com.hesc.pullDownView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.sql.Date;
import java.text.SimpleDateFormat;

import cn.com.hesc.devutilslibrary.R;

/**
 * PullDownView
 * 下拉刷新列表,用法：在layout布局文件里申明：
 *com.hesc.android.fastdevframework.view.pullDownView.PullDownView
	android:id="@+id/consult_pullDownView"
	android:layout_width="fill_parent"
	android:layout_height="fill_parent"
	ListView 相关属性自己设置
*  com.hesc.android.fastdevframework.view.pullDownView.PullDownView
 * 在调用的activity中PullDownView.onPullDownViewUpdateListener可以监听下拉刷新回调，完成后执行resetPullDownViewState();使得下拉还原
 */
@SuppressWarnings("deprecation")
public class PullDownView extends FrameLayout implements OnGestureListener,
		AnimationListener {
	private final static int PULLDOWNSTATE_ORIGIN = 1;
	private final static int PULLDOWNSTATE_BEGIN_PULLDOWN = 2;
	private final static int PULLDOWNSTATE_PULLDOWN_NOT_ENOUGH_WHEN_ACTIONUP = 3;
	private final static int PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_SCROLLING = 4;
	private final static int PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_ACTIONUP = 5;
	private final static int PULLDOWNSTATE_IS_LOADING = 6;
	private final static int PULLDOWNSTATE_BEGIN_LOADING = 7;
	
	private Context context;
	private GestureDetector gestureDetector = new GestureDetector(this);
	private Drawable arrowDownDrawable;
	private Drawable arrowUpDrawable;
	private Animation rotateDownAnimation;
	private Animation rotateUpAnimation;
	private View updateBarView;
	private ImageView arrowImageView;
	private FrameLayout updateContentFrameLayout;
	private ProgressBar progressBar;
	private TextView updateTitleTextView;

	private static int updatebar_height;
	private int dragDistance;
	private int pulldownState = PULLDOWNSTATE_ORIGIN;
	private boolean q = true;
	private boolean isPulling = false;
	private PullDownViewRunnable pullDownViewRunnable = new PullDownViewRunnable(
			this);
	private Date date;
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"MM-dd HH:mm:ss");
	private onPullDownViewUpdateListener updateListener;

	private void initViews() {
		updatebar_height = getResources().getDimensionPixelSize(
				R.dimen.updatebar_height);
		setDrawingCacheEnabled(false);
		setBackgroundDrawable(null);
		setClipChildren(false);
		this.context = getContext();
		this.gestureDetector.setIsLongpressEnabled(true);
		this.arrowDownDrawable = this.context.getResources().getDrawable(
				R.drawable.z_arrow_down);
		this.arrowUpDrawable = this.context.getResources().getDrawable(
				R.drawable.z_arrow_up);
		this.rotateDownAnimation = AnimationUtils.loadAnimation(this.context,
				R.anim.rotate_down);
		this.rotateDownAnimation.setAnimationListener(this);
		this.rotateUpAnimation = AnimationUtils.loadAnimation(this.context,
				R.anim.rotate_up);
		this.rotateUpAnimation.setAnimationListener(this);

		this.updateBarView = LayoutInflater.from(this.context).inflate(
				R.layout.update_bar, null);
		this.updateBarView.setVisibility(View.INVISIBLE);
		addView(this.updateBarView);

		this.arrowImageView = new ImageView(this.context);
		LayoutParams localLayoutParams1 = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.arrowImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.arrowImageView.setLayoutParams(localLayoutParams1);
		this.arrowImageView.setImageResource(R.drawable.z_arrow_down);

		this.updateContentFrameLayout = (FrameLayout) getChildAt(0)
				.findViewById(R.id.update_content_frameLayout);
		this.updateContentFrameLayout.addView(this.arrowImageView);
		LayoutParams localLayoutParams2 = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		localLayoutParams2.gravity = 17;

		this.progressBar = new ProgressBar(this.context, null,
				android.R.attr.progressBarStyleInverse);
		int padding = getResources().getDimensionPixelSize(
				R.dimen.updatebar_padding);
		this.progressBar.setPadding(padding, padding, padding, padding);
		this.progressBar.setLayoutParams(localLayoutParams2);
		this.updateContentFrameLayout.addView(this.progressBar);

		this.updateTitleTextView = (TextView) findViewById(R.id.update_title_textView);
	}

	static void setPullDownStateView(PullDownView pulldownview) {
		pulldownview.setPullDownStateView();
	}

	static boolean setPullDownState(PullDownView pulldownview, float f1, boolean flag) {
		return pulldownview.setPullDownState(f1, flag);
	}

	static boolean setPulling(PullDownView pulldownview, boolean isPulling) {
		pulldownview.isPulling = isPulling;
		return isPulling;
	}

	public void didActionFinished(Date date2) {
		this.date = date2;
		if (this.dragDistance != 0)
			doPullDownViewRunnableWhenPullNotEnough();
		//else
			pulldownState = PULLDOWNSTATE_ORIGIN;
		this.arrowImageView.setImageResource(R.drawable.z_arrow_down);
	}

	public void setPulldownBeginLoading() {
		this.dragDistance = -updatebar_height;
		pulldownState = PULLDOWNSTATE_BEGIN_LOADING;
		this.isPulling = true;
		postDelayed(new Runnable() {
			@Override
			public void run() {
				PullDownView.setPullDownStateView(PullDownView.this);
			}
		}, 10L);
	}

	public void setPulldownStateWhenBeginLoading() {
		pulldownState = PULLDOWNSTATE_BEGIN_LOADING;
		invalidate();
	}

	private boolean setPullDownState(float f1, boolean isDragging) {
		boolean bReturn = false;
		if (this.pulldownState == PULLDOWNSTATE_IS_LOADING) {
			if (f1 < 0.0f) {
				bReturn = true;
				return bReturn;
			} else {
				if (isDragging) {
					this.pulldownState = PULLDOWNSTATE_BEGIN_LOADING;
				}
			}
		}
		if (this.pulldownState == PULLDOWNSTATE_BEGIN_LOADING && f1 < 0.0f && -this.dragDistance >= updatebar_height) {
			bReturn = true;
			// continue;
			return bReturn;
		}
		this.dragDistance = (int) (f1 + (float) this.dragDistance);
		if (this.dragDistance > 0) {
			this.dragDistance = 0;
		}
		if (!isDragging) {
			if (this.pulldownState != PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_ACTIONUP) {
				if (this.pulldownState == PULLDOWNSTATE_IS_LOADING && this.dragDistance == 0) {
					this.pulldownState = PULLDOWNSTATE_ORIGIN;
				} else if (this.pulldownState == PULLDOWNSTATE_PULLDOWN_NOT_ENOUGH_WHEN_ACTIONUP && this.dragDistance == 0) {
					this.pulldownState = PULLDOWNSTATE_ORIGIN;
				} else if (this.pulldownState == PULLDOWNSTATE_BEGIN_LOADING && this.dragDistance == 0) {
					this.pulldownState = PULLDOWNSTATE_ORIGIN;
				}
			} else {
				this.pulldownState = PULLDOWNSTATE_IS_LOADING;
				if (this.updateListener != null) {
					this.updateListener.onPullDownViewUpdate();
				}
			}
			invalidate();
			bReturn = true;
			// continue;
			return bReturn;
		} else {
			// L6
			switch (this.pulldownState) {
			case PULLDOWNSTATE_ORIGIN: {
				if (this.dragDistance < 0) {
					this.pulldownState = PULLDOWNSTATE_BEGIN_PULLDOWN;
					this.progressBar.setVisibility(View.INVISIBLE);
					this.arrowImageView.setVisibility(View.VISIBLE);
				}
				break;
			}
			case PULLDOWNSTATE_BEGIN_PULLDOWN: {
				if (Math.abs(this.dragDistance) >= updatebar_height) {
					this.pulldownState = PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_SCROLLING;
					this.progressBar.setVisibility(View.INVISIBLE);
					this.arrowImageView.setVisibility(View.VISIBLE);
					this.arrowImageView.startAnimation(this.rotateUpAnimation);
				} else if (this.dragDistance == 0) {
					this.pulldownState = PULLDOWNSTATE_ORIGIN;
				}
				break;
			}
			case PULLDOWNSTATE_PULLDOWN_NOT_ENOUGH_WHEN_ACTIONUP:
			case PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_ACTIONUP: {
				if (isDragging) {
					if (Math.abs(this.dragDistance) >= updatebar_height) {
						this.pulldownState = PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_SCROLLING;
						this.progressBar.setVisibility(View.INVISIBLE);
						this.arrowImageView.setVisibility(View.VISIBLE);
						this.arrowImageView
								.startAnimation(this.rotateUpAnimation);
					} else if (Math.abs(this.dragDistance) < updatebar_height) {
						this.pulldownState = PULLDOWNSTATE_BEGIN_PULLDOWN;
						this.progressBar.setVisibility(View.INVISIBLE);
						this.arrowImageView.setVisibility(View.VISIBLE);
						this.arrowImageView
								.startAnimation(this.rotateDownAnimation);
					} else if (this.dragDistance == 0) {
						this.pulldownState = PULLDOWNSTATE_ORIGIN;
					}
				} else if (this.dragDistance == 0) {
					this.pulldownState = PULLDOWNSTATE_ORIGIN;
				}
				invalidate();
				bReturn = true;
				break;
			}
			case PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_SCROLLING: {
				if (Math.abs(this.dragDistance) < updatebar_height) {
					this.pulldownState = PULLDOWNSTATE_BEGIN_PULLDOWN;
					this.progressBar.setVisibility(View.INVISIBLE);
					this.arrowImageView.setVisibility(View.VISIBLE);
					this.arrowImageView
							.startAnimation(this.rotateDownAnimation);
				}
				bReturn = true;
				break;
			}
			case PULLDOWNSTATE_IS_LOADING: {

				break;
			}
			default:
				break;
			}
		}
		return bReturn;
	}

	private boolean detectDragDistanceWhenActionUp() {
		boolean bReturn;
		if (this.dragDistance >= 0) {
			bReturn = false;
		} else {
			bReturn = true;
			if (Math.abs(this.dragDistance) < updatebar_height) {
				this.pulldownState = PULLDOWNSTATE_PULLDOWN_NOT_ENOUGH_WHEN_ACTIONUP;
				doPullDownViewRunnableWhenPullNotEnough();
			} else {
				this.pulldownState = PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_ACTIONUP;
				doPullDownViewRunnableWhenPullEnough();
			}
		}
		return bReturn;
	}

	private void doPullDownViewRunnableWhenPullEnough() {
		this.pullDownViewRunnable.beginScrolling(-this.dragDistance - updatebar_height, 300);
	}

	private void doPullDownViewRunnableWhenPullNotEnough() {
		this.pullDownViewRunnable.beginScrolling(-this.dragDistance, 300);
	}

	private void setPullDownStateView() {		
		View localView1 = getChildAt(0);
		View localView2 = getChildAt(1);

		if (this.date == null) {
			this.date = new Date(System.currentTimeMillis());
		}
		switch (this.pulldownState) {
		case PULLDOWNSTATE_ORIGIN: {
			if (localView1.getVisibility() != View.INVISIBLE) {
				localView1.setVisibility(View.INVISIBLE);
			}
			localView2.offsetTopAndBottom(-localView2.getTop());
			break;
		}
		case PULLDOWNSTATE_BEGIN_PULLDOWN:
		case PULLDOWNSTATE_PULLDOWN_NOT_ENOUGH_WHEN_ACTIONUP: {
			int i2 = localView2.getTop();
			localView2.offsetTopAndBottom(-this.dragDistance - i2);
			if (localView1.getVisibility() != View.VISIBLE) {
				localView1.setVisibility(View.VISIBLE);
			}
			int j2 = localView1.getTop();
			localView1.offsetTopAndBottom(-updatebar_height - this.dragDistance
					- j2);
			this.updateTitleTextView.setText(getResources().getString(
					R.string.drop_down)
					+ "\n"
					+ getContext().getString(R.string.update_time)
					+ ":"
					+ simpleDateFormat.format(this.date));
			break;
		}
		case PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_SCROLLING:
		case PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_ACTIONUP: {
			int i3 = localView2.getTop();
			localView2.offsetTopAndBottom(-this.dragDistance - i3);
			if (localView1.getVisibility() != View.VISIBLE) {
				localView1.setVisibility(View.VISIBLE);
			}
			int i4 = localView1.getTop();
			localView1.offsetTopAndBottom(-updatebar_height - this.dragDistance
					- i4);
			this.updateTitleTextView.setText(getResources().getString(
					R.string.release_update)
					+ "\n"
					+ getContext().getString(R.string.update_time)
					+ ":"
					+ simpleDateFormat.format(this.date));
			break;
		}
		case PULLDOWNSTATE_IS_LOADING:
		case PULLDOWNSTATE_BEGIN_LOADING: {
			int i1 = localView2.getTop();
			localView2.offsetTopAndBottom(-this.dragDistance - i1);
			int i2 = localView1.getTop();
			if (this.progressBar.getVisibility() != View.VISIBLE) {
				this.progressBar.setVisibility(View.VISIBLE);
			}
			if (this.arrowImageView.getVisibility() != View.INVISIBLE) {
				this.arrowImageView.setVisibility(View.INVISIBLE);
			}
			this.updateTitleTextView.setText(getResources().getString(
					R.string.doing_update)
					+ "\n"
					+ getContext().getString(R.string.update_time)
					+ ":"
					+ simpleDateFormat.format(this.date));
			localView1.offsetTopAndBottom(-updatebar_height - this.dragDistance
					- i2);
			if (localView1.getVisibility() != View.VISIBLE) {
				localView1.setVisibility(View.VISIBLE);
			}
			break;
		}

		default:
			break;
		}

		invalidate();
	}

	public PullDownView(Context context) {
		super(context);
		initViews();
	}

	public PullDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.animation.Animation.AnimationListener#onAnimationEnd(android
	 * .view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation arg0) {
		if ((this.pulldownState == PULLDOWNSTATE_BEGIN_PULLDOWN) || (this.pulldownState == PULLDOWNSTATE_PULLDOWN_NOT_ENOUGH_WHEN_ACTIONUP)) {
			this.arrowImageView.setImageDrawable(this.arrowDownDrawable);
		} else {
			this.arrowImageView.setImageDrawable(this.arrowUpDrawable);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.animation.Animation.AnimationListener#onAnimationRepeat(
	 * android.view.animation.Animation)
	 */
	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.animation.Animation.AnimationListener#onAnimationStart(android
	 * .view.animation.Animation)
	 */
	@Override
	public void onAnimationStart(Animation animation) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.
	 * MotionEvent)
	 */
	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.
	 * MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.FrameLayout#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		getChildAt(0).layout(0, -updatebar_height - this.dragDistance,
				getMeasuredWidth(), -this.dragDistance);
		getChildAt(1).layout(0, -this.dragDistance, getMeasuredWidth(),
				getMeasuredHeight() - this.dragDistance);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onLongPress(android.view
	 * .MotionEvent)
	 */
	@Override
	public void onLongPress(MotionEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onScroll(android.view.
	 * MotionEvent, android.view.MotionEvent, float, float)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		float f1 = (float) (0.5D * distanceY);//下拉为负数，上拉为正数
		AdapterView localAdapterView = (AdapterView) getChildAt(1);
		boolean bReturn = false;
		if ((localAdapterView == null) || (localAdapterView.getCount() == 0)
				|| (localAdapterView.getChildCount() == 0)) {
			bReturn = false;
		} else {
			boolean flag1;
			boolean flag2;
			if (localAdapterView.getFirstVisiblePosition() == 0)
				flag1 = true;
			else
				flag1 = false;
			if (flag1) {
				if (localAdapterView.getChildAt(0).getTop() == 0)
					flag2 = true;
				else
					flag2 = false;
			} else {
				flag2 = flag1;
			}
			if (f1 < 0.0F && flag2 || this.dragDistance < 0)
				bReturn = setPullDownState(f1, true);
			else
				bReturn = false;
		}
		return bReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onShowPress(android.view
	 * .MotionEvent)
	 */
	@Override
	public void onShowPress(MotionEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.
	 * view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean bReturn = false;
		if (!this.q) {
			bReturn = super.dispatchTouchEvent(ev);
			return bReturn;
		} else {
			boolean bool1;
			int action;
			if (!this.isPulling) {
				bool1 = this.gestureDetector.onTouchEvent(ev);
				action = ev.getAction();
				if (action == MotionEvent.ACTION_UP) {
					bool1 = detectDragDistanceWhenActionUp();
				} else {
					if (action == MotionEvent.ACTION_CANCEL) {
						bool1 = detectDragDistanceWhenActionUp();
					}
				}
			} else {
				bReturn = true;
				return bReturn;
			}
			// while (true) {
			if (pulldownState == PULLDOWNSTATE_IS_LOADING || pulldownState == PULLDOWNSTATE_BEGIN_LOADING) {

			} else if (((bool1) || (this.pulldownState == PULLDOWNSTATE_BEGIN_PULLDOWN) || (this.pulldownState == PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_SCROLLING)
					|| (this.pulldownState == PULLDOWNSTATE_PULLDOWN_ENOUGH_WHEN_ACTIONUP) || (this.pulldownState == PULLDOWNSTATE_PULLDOWN_NOT_ENOUGH_WHEN_ACTIONUP))
					&& (getChildAt(1).getTop() != 0)) {
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.dispatchTouchEvent(ev);
				setPullDownStateView();
				bReturn = true;
				return bReturn;
			} else {
				setPullDownStateView();
				bReturn = super.dispatchTouchEvent(ev);
			}
			return bReturn;
		}
	}

	public void setUpdateHandle(onPullDownViewUpdateListener paramUpdateListener) {
		this.updateListener = paramUpdateListener;
	}

	public void setUpdateDate(Date paramDate) {
		this.date = paramDate;
	}

	public abstract interface onPullDownViewUpdateListener {
		public abstract void onPullDownViewUpdate();
	}
}
