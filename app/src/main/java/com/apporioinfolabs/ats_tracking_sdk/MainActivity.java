package com.apporioinfolabs.ats_tracking_sdk;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.apporioinfolabs.ats_sdk.ATS;
import com.apporioinfolabs.ats_sdk.AtsOnAddMessageListener;
import com.apporioinfolabs.ats_sdk.AtsOnRemoveMessageListener;
import com.apporioinfolabs.ats_sdk.AtsOnTagSetListener;
import com.apporioinfolabs.ats_sdk.AtsOnTripSetListener;
import com.apporioinfolabs.ats_sdk.AtsTagListener;
import com.apporioinfolabs.ats_sdk.utils.LOGS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TEST_ATS_ID = "f3254a3671d22b88_com.apporioinfolabs.ats_tracking_sdk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this, "starting foregorund service.", Toast.LENGTH_SHORT).show();
            startForegroundService(new Intent(this, MyService.class));
        } else { // normal
            Toast.makeText(this, "Starting normal service.", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, MyService.class));
        }


        findViewById(R.id.start_listening_ats_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ATS.startListeningAtsID(""+TEST_ATS_ID,new AtsOnAddMessageListener() {
                    @Override
                    public void onRegistrationSuccess(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRegistrationFailed(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMessage(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        findViewById(R.id.remove_listener_for_ats_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ATS.stopListeningAtsId(""+TEST_ATS_ID, new AtsOnRemoveMessageListener() {
                    @Override
                    public void onRemoveSuccess(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRemoveFailed(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        findViewById(R.id.start_listening_ats_id_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ATS.startListeningAtsID("DRIVER_ATS_ID_two",new AtsOnAddMessageListener() {
                    @Override
                    public void onRegistrationSuccess(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRegistrationFailed(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMessage(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        findViewById(R.id.remove_all_listeners).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ATS.removeAllListeners(new AtsOnRemoveMessageListener() {
                    @Override
                    public void onRemoveSuccess(String message) {
                        LOGS.e(TAG , ""+message);
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRemoveFailed(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        findViewById(R.id.add_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{


                    ATS.setTag("AAAA", new AtsOnTagSetListener() {
                        @Override
                        public void onSuccess(String message) { Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show(); }

                        @Override
                        public void onFailed(String message) { Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show(); }
                    });


                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



        findViewById(R.id.remove_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ATS.removeTag(new AtsOnTagSetListener() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String message) {
                        Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void enableLog(View view) { ATS.enableLogs(true); }

    public void disableLog(View view) { ATS.enableLogs(false); }

    public void showAtsId(View view) {
        Toast.makeText(this, ""+ATS.getAtsid(), Toast.LENGTH_SHORT).show();
    }

    public void listentag(View view) {
        try{
            ATS.listenTagAccordingToRadius("AAAA", 28.411827, 77.042085, 5000, new AtsTagListener() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(String message) {
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdd(String message) {
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChange(String message) {
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRemove(String message) {
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getTag(View view) {
        Toast.makeText(this, ""+ATS.getTag(), Toast.LENGTH_SHORT).show();
    }

    public void stopListeningtag(View view) {
        ATS.stopListeningTag(new AtsOnTagSetListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startTrip(View view) {
        ATS.startTrip("TRIP_ID_TWO", new AtsOnTripSetListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void endTrip(View view) {
        ATS.endTrip("TRIP_ID_TWO", new AtsOnTripSetListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
