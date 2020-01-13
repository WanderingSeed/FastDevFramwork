package cn.com.hesc.pullDownView;

import android.widget.Scroller;

/**
 * 线程
 */
public class PullDownViewRunnable implements Runnable {
	private PullDownView pullDownView;
	private Scroller scroller;
	private int c;

	public PullDownViewRunnable(PullDownView pullDownView) {
		super();
		this.pullDownView = pullDownView;
		this.scroller = new Scroller(pullDownView.getContext());
	}

	private void removeCallbacks() {
		this.pullDownView.removeCallbacks(this);
	}

	public void beginScrolling(int paramInt1, int paramInt2) {
		int k;
		if (paramInt1 == 0) {
			k = paramInt1 - 1;
		} else {
			k = paramInt1;
		}
		removeCallbacks();
		this.c = 0;
		this.scroller.startScroll(0, 0, -k, 0, paramInt2);
		PullDownView.setPulling(this.pullDownView, true);
		this.pullDownView.post(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Scroller localScroller = this.scroller;
		boolean bool = localScroller.computeScrollOffset();
		int i = localScroller.getCurrX();		
		int j = this.c - i;
		PullDownView.setPullDownState(this.pullDownView,j, false);
		PullDownView.setPullDownStateView(this.pullDownView);
		if (bool) {
			this.c = i;
			this.pullDownView.post(this);
		} else {
			PullDownView.setPulling(this.pullDownView, false);
			this.pullDownView.removeCallbacks(this);
		}
	}

}
