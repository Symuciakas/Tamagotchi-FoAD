package com.newproject.tamagotchi_foad;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    /**
     * Firebase data
     */
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("test");
    //myRef.push().setValue(2);

    /**
     * UI elements
     */
    TextView testTextView;
    ConstraintLayout mainLayout;
    LinearLayout foodLayout;

    MainLayoutListener mainLayoutListener;

    /**
     * Screen parameters
     */
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int screenHeight, screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Hide Action Bar and (maybe) Status Bar
         * !!! Change styles in both themes
         */
        getSupportActionBar().hide();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**
         * Assign screen data values
         */
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        /**
         * Set layout
         */
        setContentView(R.layout.main);

        /**
         * Assign UI values
         */
        mainLayout = findViewById(R.id.mainLayout);
        testTextView = findViewById(R.id.testTextView); //TextView for testing
        foodLayout = findViewById(R.id.foodLayout);
        foodLayout.setY(screenHeight);

        mainLayoutListener = new MainLayoutListener(mainLayout);


    }

    // Pet touch could probably be implemented in a similar way, different function to overwrite
    private class MainLayoutListener implements View.OnTouchListener {
        GestureDetector gestureDetector;

        MainLayoutListener(View v) {
            int threshold = 100;
            int velocity_threshold = 100;

            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                /**
                 * Swipe function
                 * @param e1 point of down
                 * @param e2 point at present
                 * @param distanceX = e2.getX() - e1.getX()
                 * @param distanceY = e2.getY() - e1.getY()
                 * @return
                 */
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    try {
                        if(e1.getY() > screenHeight - foodLayout.getHeight()) {
                            if(e2.getY() < screenHeight - foodLayout.getHeight()){
                                foodLayout.setY(screenHeight - foodLayout.getHeight());
                                testTextView.setText("Open");
                            } else {
                                foodLayout.setY(e2.getY());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                /**
                 * From contact to release
                 * @param e1 coordinates of touch
                 * @param e2 coordinates of release
                 * @param velocityX at point of release i think
                 * @param velocityY at point of release i think
                 * @return
                 */
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try {
                        if(e1.getY() > screenHeight - foodLayout.getHeight()) {
                            if(foodLayout.getY() > screenHeight - foodLayout.getHeight() && foodLayout.getY() < screenHeight) {
                                foodLayout.setY(screenHeight);
                                testTextView.setText("Closed");
                                //!!! sometimes position bugs out
                                // Small contact space
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            };
            gestureDetector = new GestureDetector(listener);
            v.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }
}
