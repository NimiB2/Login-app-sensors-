package dev.nimrod.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private final int NUM_OF_STEPS =5;
    private boolean[] tasksCompleted = new boolean[NUM_OF_STEPS];
    private ShapeableImageView[] circles;
    private MaterialTextView[] instructions;

    private SensorManager sensorManager;
    private ExtendedFloatingActionButton recordingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
        if (prefs.contains("tasksCompleted")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
        }

        findViews();
        initViews();

        initSteps();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("tasksCompleted", tasksCompleted);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tasksCompleted = savedInstanceState.getBooleanArray("tasksCompleted");
        updateCircles();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Convert boolean[] to JSON string
        String tasksCompletedJson = convertBooleanArrayToJson(tasksCompleted);

        // Save JSON string to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("tasksCompleted", tasksCompletedJson);
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve JSON string from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppState", MODE_PRIVATE);
        String tasksCompletedJson = prefs.getString("tasksCompleted", null);

        // Parse JSON string back into boolean[]
        if (tasksCompletedJson != null) {
            tasksCompleted = convertJsonToBooleanArray(tasksCompletedJson);
            updateCircles();
        }
    }

    private String convertBooleanArrayToJson(boolean[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < array.length; i++) {
            json.append(array[i]);
            if (i < array.length - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    // Convert JSON string back to boolean[]
    private boolean[] convertJsonToBooleanArray(String json) {
        String[] parts = json.replace("[", "").replace("]", "").split(",");
        boolean[] array = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            array[i] = Boolean.parseBoolean(parts[i].trim());
        }
        return array;
    }

    private void findViews() {
        circles = new ShapeableImageView[]{
                findViewById(R.id.circle1),
                findViewById(R.id.circle2),
                findViewById(R.id.circle3),
                findViewById(R.id.circle4),
                findViewById(R.id.circle5),
        };

        instructions = new MaterialTextView[]{
                findViewById(R.id.text1),
                findViewById(R.id.text2),
                findViewById(R.id.text3),
                findViewById(R.id.text4),
                findViewById(R.id.text5),
        };
        recordingButton = findViewById(R.id.recordingButton);
    }

    private void initViews() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        recordingButton.setVisibility(View.GONE);
        setupCircleClickListeners();

    }
    private void setupCircleClickListeners() {
        String[] descriptions = {
                "Step 1: Increase the temperature to over 30°C.",
                "Step 2: Speak 'Unlock' into the microphone.",
                "Step 3: Expose the device to bright light (>100 lux).",
                "Step 4: Move the phone towards and away from your face quickly",
                "Step 5: Drag this circle to the second position."
        };

        for (int i = 0; i < NUM_OF_STEPS; i++) {
            int index = i;
            circles[i].setOnClickListener(v -> {
                if (instructions[index].getVisibility() == View.VISIBLE) {
                    instructions[index].setVisibility(View.GONE);

                    if (index == 1) {
                        recordingButton.setVisibility(View.GONE);
                    }
                } else {
                    instructions[index].setText(descriptions[index]);
                    instructions[index].setVisibility(View.VISIBLE);

                    if (index == 1) {
                        recordingButton.setVisibility(View.VISIBLE);
                    } else {
                        recordingButton.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void initSteps() {
        setupTemperatureTask();
        setupRecordingButton();
        setupLightSensorTask();
        setupGyroscopeTask();
        setupTouchTask();
    }

    private void updateCircles() {
        for (int i = 0; i < NUM_OF_STEPS; i++) {
            if (tasksCompleted[i]) {
                circles[i].setImageResource(R.drawable.circle_green);
            }
        }
    }


    private void setupTemperatureTask() {
        Sensor tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (tempSensor != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values[0] > 30) {
                        completeTask(0);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            }, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Temperature sensor not available", Toast.LENGTH_SHORT).show();
        }

    }

    private void setupRecordingButton() {
        recordingButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say 'Unlock'");

            startActivityForResult(intent, 10);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null) {
                for (String result : results) {
                    if (result.equalsIgnoreCase("unlock")) {
                        completeTask(1);
                        break;
                    }
                }
            }
        }
    }


    private void setupLightSensorTask() {
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values[0] > 100) {
                        completeTask(2);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            }, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Light sensor not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupGyroscopeTask() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float z = event.values[2];
                    if (Math.abs(z) > 15) {
                        completeTask(3);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void setupTouchTask() {
        circles[4].setOnLongClickListener(v -> {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(null, shadowBuilder, v, 0);
            return true;
        });

        circles[1].setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                completeTask(4); // Assume step 5 is drag-and-drop
            }
            return true;
        });
    }

    private void completeTask(int taskIndex) {
        if (!tasksCompleted[taskIndex]) {
            tasksCompleted[taskIndex] = true;
            circles[taskIndex].setImageResource(R.drawable.circle_green);

            if (taskIndex == 1) {
                instructions[taskIndex].setVisibility(View.GONE);
                recordingButton.setVisibility(View.GONE);
            }

            if (allTasksCompleted()) {
                moveToSuccessPage();
            }
        }
    }

    private boolean allTasksCompleted() {
        for (boolean task : tasksCompleted) {
            if (!task) return false;
        }
        return true;
    }

    private void moveToSuccessPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}