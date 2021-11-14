package com.newproject.tamagotchi_foad;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.health.HealthStats;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Context mainContext;

    /**
     * Shared preferences
     */
    public static final String SHARED_PREFERENCES = "sharedPreferences";// General app code
    public static final String LAST_TIME_ACTIVE = "lastTimeActive";// Time when user last exited the app
    public static final String NOTIFICATION_ID = "notificationID";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /**
     * Firebase data
     */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference playersRef = database.getReference("players");
    DatabaseReference playerRef = playersRef.child("01");

    /**
     * Local Room Database elements
     */
    List<Player> playerList = new ArrayList<>();
    List<Pet> pets = new ArrayList<>();
    List<PetData> petData = new ArrayList<>();
    RoomDB roomDB;

    /**
     * UI elements
     */
    TextView testTextView, statTextView;
    ConstraintLayout mainLayout;
    LinearLayout foodLayout, statLayout;
    ImageView foodView1, foodView2, petImageView;

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
    Pet currentPet;

    /**
     * Decrement timers and tasks
     */
    Timer healthTimer, happinessTimer, affectionTimer, saturationTimer, elapsedTimeTimer;
    TimerTask healthDecrement, happinessDecrement, affectionDecrement, saturationDecrement;

    /**
     * Notification variable declaration
     */
    private NotificationManagerCompat notificationManagerCompat;
    private int notificationId = 0;
    private String notificationChannelId;

    Player player;

    boolean bottomOpened = false, rightOpened = false;
    Timer happinessIncreaseTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainContext = MainActivity.this;

        stopService(new Intent(mainContext, NotificationService.class));

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
        foodView1 = findViewById(R.id.foodImageView1);
        foodView2 = findViewById(R.id.foodImageView2);
        petImageView = findViewById(R.id.petImageView);

        /**
         * Listener assignments
         */
        mainLayoutListener = new MainLayoutListener(mainLayout);
        foodView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item("Food");
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData("Food",mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(foodView1);

                foodView1.startDrag(dragData, myShadow, null, 0);

                CloseFoodMenu();
                return true;
            }
        });
        foodView1.setImageResource(R.drawable.food);
        petImageView.setImageResource(R.drawable.animal1);
        foodView1.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        //Nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        //int x_cord = (int) event.getX();
                        //int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED :
                        //x_cord = (int) event.getX();
                        //y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION  :
                        //x_cord = (int) event.getX();
                        //y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_ENDED   :
                        if(event.getX() > screenWidth/3 && event.getX() < screenWidth/1.5 && event.getY() > screenHeight/3 && event.getY() < screenHeight/1.5) {
                            if(currentPet.getHealth() == 0)
                                SetHealthTimer(currentPet.getHealthLoss());
                            if(currentPet.getSaturation() == 0)
                                SetSaturationTimer(currentPet.getSaturationLoss());
                            if(currentPet.getSaturation() < currentPet.getMaxSaturation())
                                currentPet.setSaturation(currentPet.getSaturation() + 10);
                            if(currentPet.getHealth() < currentPet.getMaxHealth())
                                currentPet.setHealth(currentPet.getHealth() + 10);
                            if(currentPet.getSaturation() > currentPet.getMaxSaturation())
                                currentPet.setSaturation(currentPet.getMaxSaturation());
                            if(currentPet.getHealth() > currentPet.getMaxHealth())
                                currentPet.setHealth(currentPet.getMaxHealth());
                            player.fed();
                            player.addExperience(10);
                            //Food --;
                            DisplayData();
                        }
                        //testTextView.setText(event.getX() + " " + event.getY() + "\n");
                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        //testTextView.setText("ACTION_DROP\n");
                        // Do nothing
                        break;
                    default: break;
                }
                return true;
            }
        });
        ImageView petImg = (ImageView) findViewById(R.id.petImageView);
        petImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentPet.getHealth() > 0) {
                    currentPet.setHealth(currentPet.getHealth() - 5);
                    DisplayData();
                }
            }
        });
        petImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // start your timer
                        happinessIncreaseTimer = new Timer();
                        happinessIncreaseTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                currentPet.setHappiness(currentPet.getHappiness() - 1);
                                DisplayData();
                            }
                        }, 2000, 3000);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // stop your timer.
                    happinessIncreaseTimer.cancel();
                }
                return false;
            }
        });
        /**
         * Shared preference initializing
         */
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /**
         * Local Room Database initializing
         */
        roomDB = RoomDB.getInstance(mainContext);

        /**
         * Decrement timer and task initializing
         */
        healthTimer = new Timer();
        happinessTimer = new Timer();
        affectionTimer = new Timer();
        saturationTimer = new Timer();
        elapsedTimeTimer = new Timer();
        happinessIncreaseTimer = new Timer(); // pet the pet task
        healthDecrement = new TimerTask() {
            @Override
            public void run() {
                currentPet.setHealth(currentPet.getHealth() - 1);
                DisplayData();
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
                DisplayData();
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
                DisplayData();
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
                DisplayData();
                if(currentPet.getSaturation() <= 0) {
                    currentPet.setSaturation(0);
                    saturationTimer.cancel();
                }
            }
        };
        elapsedTimeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(player != null) {
                    player.tickPlayTime();
                    DisplayData();
                    //roomDB.playerDAO().updatePlayTime(1, 0);
                    //roomDB.playerDAO().updatePlayTime(1, player.getPlayTime());
                }
            }
        }, 0, 1000);
        // pet the pet task timer
        happinessIncrease = new TimerTask() {
            @Override
            public void run() {
                currentPet.setHappiness(currentPet.getHappiness() - 1);
                DisplayData();
            }
        };

        /**
         * Notification data

        notificationChannelId = "channel1";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel= new NotificationChannel(notificationChannelId, notificationChannelId, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        notificationManagerCompat = NotificationManagerCompat.from(this);*/

        LoadPrefData();
        RecoverPlayerData();
        CheckDate();
    }

    @Override
    protected void onStop() {
        SavePrefData();

        roomDB.playerDAO().update(player.getUid(), player.getPlayerName(), player.getLevel(), player.getExperience(), player.getScore(), player.getPlayTime(), player.getPatsGiven(), player.getFoodFed());
        roomDB.petDAO().update(currentPet.getUid(), currentPet.getLevel(), currentPet.getExperience(), currentPet.getAffection(), currentPet.getHealth(), currentPet.getHappiness(), currentPet.getSaturation());

        SaveToFirebase();

        Intent notificationServiceIntent = new Intent(mainContext, NotificationService.class);

        notificationServiceIntent.putExtra("Health", currentPet.getHealth());
        notificationServiceIntent.putExtra("Happiness", currentPet.getHappiness());
        notificationServiceIntent.putExtra("Affection", currentPet.getAffection());
        notificationServiceIntent.putExtra("Saturation", currentPet.getSaturation());

        notificationServiceIntent.putExtra("Health decrement", currentPet.getHealthLoss());
        notificationServiceIntent.putExtra("Happiness decrement", currentPet.getHappinessLoss());
        notificationServiceIntent.putExtra("Affection decrement", currentPet.getAffectionLoss());
        notificationServiceIntent.putExtra("Saturation decrement", currentPet.getSaturationLoss());

        getApplicationContext().startForegroundService(notificationServiceIntent);

        //ResetPlayerData();

        super.onStop();
    }

    /**
     * Save Pref data
     */
    protected void SavePrefData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        editor.putString(LAST_TIME_ACTIVE, currentDateAndTime);
        //editor.putInt(NOTIFICATION_ID, notificationId);
        editor.commit();
    }

    /**
     * Update pet stat textView
     */
    protected void DisplayData() {
        SaveToFirebase();
        String s = player.getUid() + player.getPlayerName() +
                "\n" + player.getLevel() + " " + player.getExperience() +
                "/100\n" + player.getPlayTime() + "sec.\n" +
                "Id: " + currentPet.uid +
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
    protected void SetTimers(int nextHealth, int nextHappiness, int nextAffection, int nextSaturation) {
        if(currentPet.getHealth() != 0) {
            if(currentPet.getHealthLoss() != -1) //Spec case: no health loss
                healthTimer.scheduleAtFixedRate(healthDecrement, nextHealth, currentPet.getHealthLoss());
        }
        if(currentPet.getHappiness() != 0) {
            if(currentPet.getHappinessLoss() != -1) //Spec case: no happiness loss
                happinessTimer.scheduleAtFixedRate(happinessDecrement, nextHappiness, currentPet.getHappinessLoss());
        }
        if(currentPet.getAffection() != 0) {
            if(currentPet.getAffectionLoss() != -1) //Spec case: no affection loss
                affectionTimer.scheduleAtFixedRate(affectionDecrement, nextAffection, currentPet.getAffectionLoss());
        }
        if(currentPet.getSaturation() != 0) {
            if(currentPet.getSaturationLoss() != -1) //Spec case: no saturation loss
                saturationTimer.scheduleAtFixedRate(saturationDecrement, nextSaturation, currentPet.getSaturationLoss());
        }
    }

    protected void SetHealthTimer(int nextHealth) {
        if(currentPet.getHealth() != 0) {
            if(currentPet.getHealthLoss() != -1) //Spec case: no health loss
                healthTimer.scheduleAtFixedRate(healthDecrement, nextHealth, currentPet.getHealthLoss());
        }
    }

    protected void SetHappinessTimer(int nextHappiness) {
        if(currentPet.getHappiness() != 0) {
            if(currentPet.getHappinessLoss() != -1) //Spec case: no happiness loss
                happinessTimer.scheduleAtFixedRate(happinessDecrement, nextHappiness, currentPet.getHappinessLoss());
        }
    }

    protected void SetAffectionTimer(int nextAffection) {
        if(currentPet.getAffection() != 0) {
            if(currentPet.getAffectionLoss() != -1) //Spec case: no affection loss
                affectionTimer.scheduleAtFixedRate(affectionDecrement, nextAffection, currentPet.getAffectionLoss());
        }
    }

    protected void SetSaturationTimer(int nextSaturation) {
        if(currentPet.getSaturation() != 0) {
            if(currentPet.getSaturationLoss() != -1) //Spec case: no saturation loss
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
            SetTimers(currentPet.getHealthLoss(), currentPet.getHappinessLoss(), currentPet.getAffectionLoss(), currentPet.getSaturationLoss());
        } else {
            Date now = Calendar.getInstance().getTime();
            long time = 0;
            for(int i = Integer.parseInt(s.substring(0, 4)); i < now.getYear(); i++) {
                if (i % 4 == 0)
                    time = time + 366;
                else
                    time = time + 365;
            }
            if(Integer.parseInt(s.substring(0, 4))%4 == 0 && Integer.parseInt(s.substring(5, 7)) > 2)
                time--;
            switch (Integer.parseInt(s.substring(5, 7))) {
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
                case 1:
                    time = time + 31;
                    break;
                case 2:
                    time = time + 59;
                    break;
                case 3:
                    time = time + 90;
                    break;
                case 4:
                    time = time + 120;
                    break;
                case 5:
                    time = time + 151;
                    break;
                case 6:
                    time = time + 181;
                    break;
                case 7:
                    time = time + 212;
                    break;
                case 8:
                    time = time + 243;
                    break;
                case 9:
                    time = time + 273;
                    break;
                case 10:
                    time = time + 304;
                    break;
                case 11:
                    time = time + 334;
                    break;
                default:
                    break;
            }
            time = (time - Integer.parseInt(s.substring(8, 10)) + now.getDate()) * 24;
            time = (time - Integer.parseInt(s.substring(11, 13)) + now.getHours())*60;
            time = (time - Integer.parseInt(s.substring(14, 16)) + now.getMinutes())*60;
            time = (time - Integer.parseInt(s.substring(17, 19)) + now.getSeconds())*1000;
            //player.setPlayTime(player.getPlayTime() + (int)time/1000); // Only when player is active
            if(currentPet.getHealthLoss() != -1) {
                currentPet.setHealth(currentPet.getHealth() - (int)(time/currentPet.getHealthLoss()));
                if(currentPet.getHealth() <= 0)
                    currentPet.setHealth(0);
            }

            //No exceptions
            if(currentPet.getHappinessLoss() != -1) {
                currentPet.setHappiness(currentPet.getHappiness() - (int) (time / currentPet.getHappinessLoss()));
                if (currentPet.getHappiness() <= 0)
                    currentPet.setHappiness(0);
            }

            if(currentPet.getAffectionLoss() != -1) {
                currentPet.setAffection(currentPet.getAffection() - (int) (time / currentPet.getAffectionLoss()));
                if (currentPet.getAffection() <= 0)
                    currentPet.setAffection(0);
            }

            if(currentPet.getSaturationLoss() != -1) {
                currentPet.setSaturation(currentPet.getSaturation() - (int) (time / currentPet.getSaturationLoss()));
                if (currentPet.getSaturation() <= 0)
                    currentPet.setSaturation(0);
            }
            SetTimers(currentPet.getHealthLoss() - (int)(time%currentPet.getHealthLoss()),
                    currentPet.getHappinessLoss() - (int)(time%currentPet.getHappinessLoss()),
                    currentPet.getAffectionLoss() - (int)(time%currentPet.getAffectionLoss()),
                    currentPet.getSaturationLoss() - (int)(time%currentPet.getSaturationLoss()));
        }
        DisplayData();
    }

    protected void CloseFoodMenu() {
        foodLayout.setY(screenHeight);
        bottomOpened = false;
    }

    protected void CloseStatMenu() {
        statLayout.setX(screenWidth);
        rightOpened = false;
    }

    protected void RecoverPlayerData() {
        playerRef.child("Player Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                player = dataSnapshot.getValue(Player.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        playerRef.child("Pet Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentPet = dataSnapshot.getValue(Pet.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        if(roomDB.playerDAO().getAll().size() == 0) {
            if(player == null) {
                /**
                 * Default player initializing
                 */
                player = new Player();
                roomDB.playerDAO().insert(player);
                playerList = roomDB.playerDAO().getAll();

                /**
                 * Default pet data initializing
                 */
                petData.add(new PetData(0, "None", 100, -1, 100, 300000, 50, 7200000, 100, 300000));
                roomDB.petDataDAO().insert(petData.get(0));
                currentPet = new Pet(petData.get(0));
                roomDB.petDAO().insert(currentPet);

            }
        } else {
            /**
             * Recover data
             */
            player = roomDB.playerDAO().getAll().get(0);
            petData = roomDB.petDataDAO().getAll();
            currentPet = roomDB.petDAO().getAll().get(0);
        }
        SaveToFirebase();
    }

    protected void ResetPlayerData() {
        roomDB.playerDAO().reset(roomDB.playerDAO().getAll());
        roomDB.petDataDAO().reset(roomDB.petDataDAO().getAll());
        roomDB.petDAO().reset(roomDB.petDAO().getAll());

        editor.putString(LAST_TIME_ACTIVE, null);
        editor.putInt(NOTIFICATION_ID, 0);
        editor.commit();
    }

    protected void LoadPrefData() {
        notificationId = sharedPreferences.getInt(NOTIFICATION_ID, 0);
    }

    protected void SaveToFirebase() {
        playerRef.child("Player Data").setValue(player);
        playerRef.child("Pet Data").setValue(currentPet);
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
                            CloseFoodMenu();
                            //!!! sometimes position bugs out. Adding delay lowers possibility
                            //!!! Small contact space to close. Add new similar gesture listener to foodLayout
                        }
                        if(rightOpened && statLayout.getX() != screenWidth - statLayout.getWidth()) {
                            CloseStatMenu();
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
