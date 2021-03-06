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
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Context mainContext;

    /**
     * Shared preferences
     */
    public static final String SHARED_PREFERENCES = "sharedPreferences";// General app code
    public static final String LAST_TIME_ACTIVE = "lastTimeActive";// Time when user last exited the app
    public static final String NOTIFICATION_ID = "notificationID";
    public static final String LAST_PET_INDEX = "lastPetIndex";
    public Long lastPetIndex = Long.valueOf(0);
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /**
     * Firebase data
     */
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference playersRef = database.getReference("players");
    //DatabaseReference playerRef = playersRef.child("01");

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
    TextView testTextView, statTextView, deadNote;
    ConstraintLayout mainLayout;
    LinearLayout foodLayout, statLayout;
    ImageView foodView1, foodView2, foodView3, foodView4, petImageView, backgroundImageView, deadpet;

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
    Timer healthTimer, happinessTimer, affectionTimer, saturationTimer, elapsedTimeTimer, happinessIncreaseTimer, tenthOfSecondTimer;
    TimerTask healthDecrement, happinessDecrement, affectionDecrement, saturationDecrement;

    /**
     * Notification variable declaration
     */
    private NotificationManagerCompat notificationManagerCompat;
    private int notificationId = 0;
    private String notificationChannelId;

    /**
     * Sensor variable declaration
     */
    private SensorManager sensorManager;
    private List<Sensor> deviceSensors;
    private float[] accelerationVector3;
    private float[] speedVector3;
    private float[] distanceVector3;
    private float[] resistanceForceVector3;
    private static float resistanceK = 0.48f;

    Player player;

    boolean bottomOpened = false, rightOpened = false;
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
        foodView3 = findViewById(R.id.foodImageView3);
        foodView4 = findViewById(R.id.foodImageView4);
        petImageView = findViewById(R.id.petImageView);
        backgroundImageView = findViewById(R.id.Background);
        deadpet = findViewById(R.id.dead);
        //deadNote = findViewById(R.id.deathnote);

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
        foodView3.setImageResource(R.drawable.abc_vector_test);
        foodView4.setImageResource(R.drawable.abc_vector_test);
        petImageView.setImageResource(R.drawable.pet_happy);
        deadpet.setImageResource(R.drawable.pet_dead);
        deadpet.setVisibility(View.GONE);
        //deadNote.setText("TEST");
        //backgroundImageView.setImageResource(R.drawable.background1);

        //Feeding the animal
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
                        if(event.getX() > petImageView.getX() && event.getX() < petImageView.getX()+petImageView.getWidth() && event.getY() > petImageView.getY() && event.getY() < petImageView.getY()+petImageView.getHeight()) {
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

                            //Animation if food is given, animal with eat the food for 2 seconds and then go back to previus animation
                            petImageView.setImageResource(R.drawable.pet_eating);

                            new java.util.Timer().schedule(
                                    new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            petImageView.setImageResource(R.drawable.pet_happy);
                                        }
                                    },
                                    2000
                            );
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
        foodView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPetIndex--;
                if(lastPetIndex == -1)
                    lastPetIndex = Long.valueOf(roomDB.petDAO().getAll().size()-1);
                roomDB.petDAO().update(currentPet.getUid(), currentPet.getLevel(), currentPet.getExperience(), currentPet.getAffection(), currentPet.getHealth(), currentPet.getHappiness(), currentPet.getSaturation());
                currentPet = roomDB.petDAO().getAll().get(Math.toIntExact(lastPetIndex));
                DisplayData();
            }
        });
        foodView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPetIndex = (lastPetIndex+1)%roomDB.petDAO().getAll().size();
                roomDB.petDAO().update(currentPet.getUid(), currentPet.getLevel(), currentPet.getExperience(), currentPet.getAffection(), currentPet.getHealth(), currentPet.getHappiness(), currentPet.getSaturation());
                currentPet = roomDB.petDAO().getAll().get(Math.toIntExact(lastPetIndex));
                DisplayData();
            }
        });

        //Poking the pet
        ImageView petImg = (ImageView) findViewById(R.id.petImageView);
        petImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentPet.getHealth() > 0) {
                    currentPet.setHealth(currentPet.getHealth() - 5);
                    petImageView.setImageResource(R.drawable.pet_sad);



                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    petImageView.setImageResource(R.drawable.pet_happy);
                                }
                            },
                            500
                    );

                    DisplayData();
                    checkAlive();
                }
            }
        });
        petImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // start your timer
                        petImageView.setImageResource(R.drawable.pet_petted);
                        happinessIncreaseTimer = new Timer();
                        happinessIncreaseTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                currentPet.setHappiness(currentPet.getHappiness() + 1);
                                player.addExperience(1);
                                player.givePats();
                                DisplayData();
                            }
                        }, 2000, 3000);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // stop your timer.
                    happinessIncreaseTimer.cancel();
                    petImageView.setImageResource(R.drawable.pet_happy);
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
        tenthOfSecondTimer = new Timer();
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

                    DisplayData();//Works on emulators. Broken on phones
                    //roomDB.playerDAO().updatePlayTime(1, 0);
                    //roomDB.playerDAO().updatePlayTime(1, player.getPlayTime());
                }
            }
        }, 0, 1000);
        accelerationVector3 = new float[]{0, 0, 0};
        distanceVector3 = new float[]{0, 0, 0};
        speedVector3 = new float[]{0, 0, 0};
        resistanceForceVector3 = new float[]{0, 0, 0};
        tenthOfSecondTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(int i = 0; i < 3; i++) {
                    speedVector3[i] = speedVector3[i] + -Math.signum(accelerationVector3[i]) * resistanceForceVector3[i] * 0.1f + accelerationVector3[i] * 0.1f;
                    distanceVector3[i] = distanceVector3[i] + speedVector3[i]*0.05f;
                }//petImageView.getHeight()
                if(petImageView.getX() - distanceVector3[0] < 0) {
                    petImageView.setX(0);
                    distanceVector3[0] = 0;
                    speedVector3[0] = 0;
                } else {
                    if(petImageView.getX() + petImageView.getWidth() - distanceVector3[0] > screenWidth) {
                        petImageView.setX(screenWidth - petImageView.getWidth());
                        distanceVector3[0] = 0;
                        speedVector3[0] = 0;
                    } else {
                        petImageView.setX(petImageView.getX() - distanceVector3[0]);
                    }
                }
                if(petImageView.getY() + distanceVector3[1] < 0) {
                    petImageView.setY(0);
                    distanceVector3[1] = 0;
                    speedVector3[1] = 0;
                } else {
                    if(petImageView.getY() + petImageView.getHeight() + distanceVector3[1] > screenHeight) {
                        petImageView.setY(screenHeight - petImageView.getHeight());
                        distanceVector3[1] = 0;
                        speedVector3[1] = 0;
                    } else {
                        petImageView.setY(petImageView.getY() + distanceVector3[1]);
                    }
                }
            }
        }, 0, 100);
        // pet the pet task timer

        //Sensors
        /**
         * Sensor variable initializing
         */
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        LoadPrefData();
        RecoverPlayerData();
        CheckDate();
    }

    public void checkAlive() {
        if (currentPet.getHealth() < 1){
            petImageView.setVisibility(View.GONE);
            deadpet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        SavePrefData();

        roomDB.playerDAO().update(player.getUid(), player.getPlayerName(), player.getLevel(), player.getExperience(), player.getScore(), player.getPlayTime(), player.getPatsGiven(), player.getFoodFed());
        roomDB.petDAO().update(currentPet.getUid(), currentPet.getLevel(), currentPet.getExperience(), currentPet.getAffection(), currentPet.getHealth(), currentPet.getHappiness(), currentPet.getSaturation());

        //SaveToFirebase();

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

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Save Pref data
     */
    protected void SavePrefData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        editor.putString(LAST_TIME_ACTIVE, currentDateAndTime);
        editor.putLong(LAST_PET_INDEX, lastPetIndex);
        //editor.putInt(NOTIFICATION_ID, notificationId);
        editor.commit();
    }

    /**
     * Update pet stat textView
     */
    protected void DisplayData() {
        //SaveToFirebase();
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

        testTextView.setText(String.valueOf(roomDB.playerDAO().getAll().size()) + " " + String.valueOf(roomDB.petDataDAO().getAll().size()) + " " + String.valueOf(roomDB.petDAO().getAll().size()));
    }

    protected void DisplayPhysics() {
        String s = "Acceleration: " + String.valueOf(accelerationVector3[0]) + ", " + String.valueOf(accelerationVector3[1])+ ", " + String.valueOf(accelerationVector3[2]) +
                "\nVelocity: " + String.valueOf(speedVector3[0]) +  ", " + String.valueOf(speedVector3[1]) + ", " + String.valueOf(speedVector3[2]) +
                "\nDistance: " + String.valueOf(distanceVector3[0]) + ", " + String.valueOf(distanceVector3[1]) + ", " +  String.valueOf(distanceVector3[2]);
        testTextView.setText(s);
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
        /*playerRef.child("Player Data").addValueEventListener(new ValueEventListener() {
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
        */
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
                petData.add(new PetData(0, "One pet", 100, -1, 100, 300000, 50, 7200000, 100, 300000));
                petData.add(new PetData(1, "Other Pet", 10, 300000, 100, 300000, 30, 300000, 50, 150000));
                roomDB.petDataDAO().insert(petData.get(0));
                roomDB.petDataDAO().insert(petData.get(1));
                currentPet = new Pet(petData.get(0));
                roomDB.petDAO().insert(currentPet);
                roomDB.petDAO().insert(new Pet(petData.get(1)));
            }
        } else {
            /**
             * Recover data
             */
            player = roomDB.playerDAO().getAll().get(0);
            petData = roomDB.petDataDAO().getAll();
            currentPet = roomDB.petDAO().getAll().get(Math.toIntExact(lastPetIndex));
        }
        //SaveToFirebase();
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
        lastPetIndex = sharedPreferences.getLong(LAST_PET_INDEX, 0);
    }

    protected void SaveToFirebase() {
        //playerRef.child("Player Data").setValue(player);
        //playerRef.child("Pet Data").setValue(currentPet);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)  {
            for(int i = 0; i < 3; i++)
                accelerationVector3[i] = event.values[i]*10f;
            if(petImageView.getY() == 0 || petImageView.getY() + petImageView.getHeight() == screenHeight)
                resistanceForceVector3[0] = Math.abs(accelerationVector3[1]) * resistanceK;
            else
                resistanceForceVector3[0] = 0;
            if(petImageView.getX() == 0 || petImageView.getX() + petImageView.getWidth() == screenWidth)
                resistanceForceVector3[1] = Math.abs(accelerationVector3[0]) * resistanceK;
            else
                resistanceForceVector3[1] = 0;
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)  {
            float sum = 0;
            for(int i = 0; i < event.values.length; i++)
                sum = sum + event.values[i] * event.values[i];
            sum = (float) Math.sqrt(sum);
            if(sum > 10) {
                petImageView.setImageResource(R.drawable.pet_dizzy);
            }
        }

        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE)  {
            /*float sum = 0;
            for(int i = 0; i < event.values.length; i++)
                sum = sum + event.values[0] * event.values[i];
            sum = (float) Math.sqrt(sum);
            */
            float temperature = event.values[0];
            if(temperature < 10) {
                petImageView.setImageResource(R.drawable.pet_cold);
            }
            else if(temperature > 10){
                petImageView.setImageResource(R.drawable.pet_happy);
            }
        }

        if(event.sensor.getType() == Sensor.TYPE_LIGHT)  {
            Calendar rightNow = Calendar.getInstance();
            String currentHour = String.valueOf(rightNow.get(Calendar.HOUR_OF_DAY));
            int currentLux = (int) event.values[0];
            if (currentLux <= 100 && (rightNow.get(Calendar.HOUR_OF_DAY) > 20 || rightNow.get(Calendar.HOUR_OF_DAY) < 7)) {
                mainLayout.setBackgroundColor(Color.parseColor("#191970"));
                petImageView.setImageResource(R.drawable.pet_sleep);
            }
            else if(currentLux > 100){
                mainLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                petImageView.setImageResource(R.drawable.pet_happy);
            }
            //testTextView.setText(currentHour);
        }
        //DisplayPhysics();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
            };
            gestureDetector = new GestureDetector(listener);
            v.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
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
            }
            return gestureDetector.onTouchEvent(event);
        }
    }
}
