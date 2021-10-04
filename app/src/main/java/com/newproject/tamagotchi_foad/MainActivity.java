package com.newproject.tamagotchi_foad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    /**
     * Shared preferences
     */
    public static final String SHARED_PREFERENCES = "sharedPreferences";// General app code
    public static final String LAST_TIME_ACTIVE = "lastTimeActive";// Time when user last exited the app
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /**
     * Firebase data
     */
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("test");
    //myRef.push().setValue(2);

    /**
     * UI elements
     */
    TextView testTextView, statTextView;
    ConstraintLayout mainLayout;
    LinearLayout foodLayout, statLayout;

    /**
     * TouchListeners
     */
    MainLayoutListener mainLayoutListener;

    /**
     * Screen parameters
     */
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int screenHeight, screenWidth;

    /**
     * Pet data and pet objects
     */
    PetData petData;
    Pet currentPet;

    /**
     * Decrement timers and tasks
     */
    Timer healthTimer, happinessTimer, affectionTimer, saturationTimer;
    TimerTask healthDecrement, happinessDecrement, affectionDecrement, saturationDecrement;

    boolean bottomOpened = false, rightOpened = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Hide Action Bar and (maybe) Status Bar
         * !!! Change styles in both themes
         */
        Objects.requireNonNull(getSupportActionBar()).hide();// getSupportActionBar().hide();
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
        statLayout = findViewById(R.id.statLayout);
        foodLayout.setX(0);
        foodLayout.setY(screenHeight);
        statLayout.setX(screenWidth);
        statLayout.setY(0);
        statTextView = findViewById(R.id.statTextView);

        /**
         * Touch listener assignments
         */
        mainLayoutListener = new MainLayoutListener(mainLayout);

        /**
         * Shared preference initializing
         */
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //testTextView.setText(sharedPreferences.getString(LAST_TIME_ACTIVE, "First time?") + "\n" + Calendar.getInstance().getTime().toString());

        /**
         * Default pet data initializing
         */
        petData = new PetData(0, "None", 100, -1, 100, 300000, 50, 3600000, 100, 300000);
        currentPet = new Pet(petData);

        /**
         * Decrement timer and task initializing
         */
        healthTimer = new Timer();
        happinessTimer = new Timer();
        affectionTimer = new Timer();
        saturationTimer = new Timer();
        healthDecrement = new TimerTask() {
            @Override
            public void run() {
                currentPet.setHealth(currentPet.getHealth() - 1);
                displayPetData();
                if(currentPet.getHealth() <= 0) {
                    currentPet.setHealth(0);
                    healthTimer.cancel();
                }
            }
        };
        happinessDecrement = new TimerTask() {
            @Override
            public void run() {
                currentPet.setHappiness(currentPet.getHappiness() - 1);
                displayPetData();
                if(currentPet.getHappiness() <= 0) {
                    currentPet.setHappiness(0);
                    happinessTimer.cancel();
                }
            }
        };
        affectionDecrement = new TimerTask() {
            @Override
            public void run() {
                currentPet.setAffection(currentPet.getAffection() - 1);
                displayPetData();
                if(currentPet.getAffection() <= 0) {
                    currentPet.setAffection(0);
                    affectionTimer.cancel();
                }
            }
        };
        saturationDecrement = new TimerTask() {
            @Override
            public void run() {
                currentPet.setSaturation(currentPet.getSaturation() - 1);
                displayPetData();
                if(currentPet.getSaturation() <= 0) {
                    currentPet.setSaturation(0);
                    saturationTimer.cancel();
                }
            }
        };

        CheckDate();
    }

    @Override
    protected void onDestroy() {
        //Date save for decrementing at start
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        editor.putString(LAST_TIME_ACTIVE, currentDateAndTime);
        editor.commit();

        super.onDestroy();
    }

    /**
     * Update pet stat textView
     */
    protected void displayPetData() {
        String s = "Id: " + currentPet.id +
                "\nName: " + currentPet.name +
                "\nLevel: " + currentPet.getLevel() + " " + currentPet.getExperience() +
                "/100\nAffection: " + currentPet.getAffection() +
                "/100\nHealth: " + currentPet.getHealth() + "/" + currentPet.getMaxHealth() +
                "\nHappiness: " + currentPet.getHappiness() + "/" + currentPet.getMaxHappiness() +
                "\nSaturation: " + currentPet.getSaturation() + "/" + currentPet.getMaxSaturation();
        statTextView.setText(s);
    }

    /**
     * Sets timers for decrement
     * @param nextHealth 1st delay for health
     * @param nextHappiness 1st delay for happiness
     * @param nextAffection 1st delay for...
     * @param nextSaturation 1st...
     */
    protected void setTimers(int nextHealth, int nextHappiness, int nextAffection, int nextSaturation) {
        if(currentPet.getHealth() != 0) {
            if(currentPet.getHealthLoss() != -1) //Spec case: no health loss
                healthTimer.scheduleAtFixedRate(healthDecrement, nextHealth, currentPet.getHealthLoss());
        }
        if(currentPet.getHappiness() != 0) {
            happinessTimer.scheduleAtFixedRate(happinessDecrement, nextHappiness, currentPet.getHappinessLoss());
        }
        if(currentPet.getAffection() != 0) {
            affectionTimer.scheduleAtFixedRate(affectionDecrement, nextAffection, currentPet.getAffectionLoss());
        }
        if(currentPet.getSaturation() != 0) {
            saturationTimer.scheduleAtFixedRate(saturationDecrement, nextSaturation, currentPet.getSaturationLoss());
        }
    }

    /**
     * Checks last online time
     * Determines decrement values over offline time
     * Starts timers with determined default delays
     */
    protected void CheckDate() {
        String s = sharedPreferences.getString(LAST_TIME_ACTIVE, "First time?");
        if(s.equals("First time?")) {
            setTimers(currentPet.getHealthLoss(), currentPet.getHappinessLoss(), currentPet.getAffectionLoss(), currentPet.getSaturationLoss());
        } else {
            Date now = Calendar.getInstance().getTime();
            long time = 0;
            for(int i = Integer.parseInt(s.substring(0, 3)); i < now.getYear(); i++) {
                if (i % 4 == 0)
                    time = time + 366;
                else
                    time = time + 365;
            }
            if(Integer.parseInt(s.substring(0, 3))%4 == 0 && Integer.parseInt(s.substring(5, 6)) > 2)
                time--;
            switch (Integer.parseInt(s.substring(5, 6))) {
                case 2:
                    time = time - 31;
                    break;
                case 3:
                    time = time - 59;
                    break;
                case 4:
                    time = time - 90;
                    break;
                case 5:
                    time = time - 120;
                    break;
                case 6:
                    time = time - 151;
                    break;
                case 7:
                    time = time - 181;
                    break;
                case 8:
                    time = time - 212;
                    break;
                case 9:
                    time = time - 243;
                    break;
                case 10:
                    time = time - 273;
                    break;
                case 11:
                    time = time - 304;
                    break;
                case 12:
                    time = time - 334;
                    break;
                default:
                    break;
            }
            if(now.getYear() % 4 == 0)
                time++;
            switch (now.getMonth()) {
                case 2:
                    time = time + 31;
                    break;
                case 3:
                    time = time + 59;
                    break;
                case 4:
                    time = time + 90;
                    break;
                case 5:
                    time = time + 120;
                    break;
                case 6:
                    time = time + 151;
                    break;
                case 7:
                    time = time + 181;
                    break;
                case 8:
                    time = time + 212;
                    break;
                case 9:
                    time = time + 243;
                    break;
                case 10:
                    time = time - 273;
                    break;
                case 11:
                    time = time - 304;
                    break;
                case 12:
                    time = time - 334;
                    break;
                default:
                    break;
            }
            time = (time - Integer.parseInt(s.substring(8, 9)) + now.getDay()) * 24;
            time = (time - Integer.parseInt(s.substring(11, 12)) + now.getHours())*60;
            time = (time - Integer.parseInt(s.substring(14, 15)) + now.getMinutes())*60;
            time = (time - Integer.parseInt(s.substring(17, 18)) + now.getSeconds())*1000;
            if(currentPet.getHealthLoss() != -1)
                currentPet.setHealth(currentPet.getHealth() - (int)(time/currentPet.getHealthLoss()));
            if(currentPet.getHealth() <= 0)
                currentPet.setHealth(0);

            //No exceptions
            currentPet.setHappiness(currentPet.getHappiness() - (int)(time/currentPet.getHappinessLoss()));
            if(currentPet.getHappiness() <= 0)
                currentPet.setHappiness(0);

            currentPet.setAffection(currentPet.getAffection() - (int)(time/currentPet.getAffectionLoss()));
            if(currentPet.getAffection() <= 0)
                currentPet.setAffection(0);

            currentPet.setSaturation(currentPet.getSaturation() - (int)(time/currentPet.getSaturationLoss()));
            if(currentPet.getSaturation() <= 0)
                currentPet.setSaturation(0);
            setTimers((int)(time%currentPet.getHealthLoss()), (int)(time%currentPet.getHappinessLoss()), (int)(time%currentPet.getAffectionLoss()), (int)(time%currentPet.getSaturationLoss()));
        }
        displayPetData();
        testTextView.setText("Done");
    }

    // Pet touch could probably be implemented in a similar way, different function to overwrite
    private class MainLayoutListener implements View.OnTouchListener {
        GestureDetector gestureDetector;

        MainLayoutListener(View v) {
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                /**
                 * Swipe function
                 * @param e1 point at down
                 * @param e2 point at present
                 * @param distanceX diff e2 - e1
                 * @param distanceY or perhaps e2 - e2(previous)
                 * @return super
                 */
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    float diffX = e2.getX() - e1.getX(), diffY = e2.getY() - e1.getY();
                    int threshold = 20;

                    try {
                        if (Math.abs(diffX) < Math.abs(diffY)) {
                            if(Math.abs(diffY) > threshold) {
                                if(!rightOpened && e1.getY() > screenHeight - foodLayout.getHeight()) {
                                    bottomOpened = true;
                                    if(e2.getY() < screenHeight - foodLayout.getHeight())
                                        foodLayout.setY(screenHeight - foodLayout.getHeight());
                                    else
                                        foodLayout.setY(e2.getY());
                                    if(foodLayout.getY() == screenHeight)
                                        bottomOpened = false;
                                }
                            }
                        } else {
                            if(Math.abs(diffX) > threshold) {
                                if(!bottomOpened && e1.getX() >= screenWidth - statLayout.getWidth()) {
                                    rightOpened = true;
                                    if(e2.getX() < screenWidth - statLayout.getWidth())
                                        statLayout.setX(screenWidth - statLayout.getWidth());
                                    else
                                        statLayout.setX(e2.getX());
                                    if(statLayout.getX() == screenWidth)
                                        rightOpened = false;
                                }
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
                 * @return false
                 */
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try {
                        if(bottomOpened && foodLayout.getY() != screenHeight - foodLayout.getHeight()) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    foodLayout.setY(screenHeight);
                                    bottomOpened = false;
                                }
                            }, 100);
                            //!!! sometimes position bugs out. Adding delay lowers possibility
                            //!!! Small contact space to close. Add new similar gesture listener to foodLayout
                        }
                        if(rightOpened && statLayout.getX() != screenWidth - statLayout.getWidth()) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    statLayout.setX(screenWidth);
                                    rightOpened = false;
                                }
                            }, 100);
                            //!!! Fling does not register sometimes
                            //!!! Small contact space to close. Add new similar gesture listener to foodLayout
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
