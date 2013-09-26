package com.example.topic;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnCheckedChangeListener {

	Handler mHandler;

	// runnable method
	class slideRight implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			viewFlipper.showNext();
			
		}
	}

	class slideLeft implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			viewFlipper.showPrevious();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// init
		mHandler = new Handler();

		// image scrollcontainner
		int[] colors = { Color.BLACK, Color.WHITE, Color.GRAY, Color.RED };

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);

		int count = 4;
		while (--count >= 0) {
			LinearLayout aview = new LinearLayout(this);
			aview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			aview.setBackgroundColor(colors[count % 4]);
			viewFlipper.addView(aview);
		}

		// image indicator

		RadioGroup group = (RadioGroup) findViewById(R.id.group);
		group.setOnCheckedChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private ViewFlipper viewFlipper;
	
	private float lastX;

	@Override
	public boolean dispatchTouchEvent(MotionEvent touchevent) {
		// TODO Auto-generated method stub
		switch (touchevent.getAction()) {
		// when user first touches the screen to swap
		case MotionEvent.ACTION_DOWN: {
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float currentX = touchevent.getX();
			
			// if left to right swipe on screen
			if (lastX < currentX) {
				// If no more View/Child to flip
				if (viewFlipper.getDisplayedChild() == 0)
					break;

				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Left and current Screen
				// will go OUT from Right
				viewFlipper.setInAnimation(this, R.animator.in_from_left);
				viewFlipper.setOutAnimation(this, R.animator.out_to_right);
				// Show the next Screen
				viewFlipper.showNext();
				
			}

			// if right to left swipe on screen
			if (lastX > currentX) {
				if (viewFlipper.getDisplayedChild() == 1)
					break;
				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Right and current Screen
				// will go OUT from Left
				viewFlipper.setInAnimation(this, R.animator.in_from_right);
				viewFlipper.setOutAnimation(this, R.animator.out_to_left);
				// Show The Previous Screen
				viewFlipper.showPrevious();
			}
			break;
		}
		}
		return super.dispatchTouchEvent(touchevent);
	}
	
	boolean animationING = false;
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedID) {
		// TODO Auto-generated method stub
		if (!animationING) {
			int index = -1;
			switch (checkedID) {
			case R.id.r1:
				index = 0;
				break;
			case R.id.r2:
				index = 1;
				break;
			case R.id.r3:
				index = 2;
				break;
			case R.id.r4:
				index = 3;
				break;
			}
			Log.d("childID", "id: " + viewFlipper.getDisplayedChild());
			if (index != viewFlipper.getDisplayedChild()) {
				final int count = index - viewFlipper.getDisplayedChild();
				if( count > 0 ){
					viewFlipper.setInAnimation(this, R.animator.in_from_right);
					viewFlipper.setOutAnimation(this, R.animator.out_to_left);
				}else{
					viewFlipper.setInAnimation(this, R.animator.in_from_left);
					viewFlipper.setOutAnimation(this, R.animator.out_to_right);
				}
				
				animationING = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int local_count = count;
						while (true) {
							try {
								Thread.sleep(300);
								if (local_count != 0) {
									if (local_count > 0) {
										mHandler.post(new slideRight());
										local_count--;
									} else {
										mHandler.post(new slideLeft());
										local_count++;
									}
								} else {
									animationING = false;
									return;
								}

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

				}).start();
			}
		}
	}
}
