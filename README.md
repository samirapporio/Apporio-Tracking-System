# ATS (Apporio Tracking System)

[![N|Solid](https://rozgarpatrika.com/wp-content/uploads/2019/06/apporio-infolabs-logo.png)](https://www.apporio.com)

Ats is a universal solution for the developer that is a basic need of creating taxi like application. This Library basically separates three main part related to tracking based application 

  - ###### Tracking For a particualr device or multiple device over a single screen
  - ###### Service for fetching location 
  - ###### Trip Settlement
  
 


### Implementation in Android
- #### Step One 
  Create you developer account  and obtain App Id for the library to work [plugins/dropbox/README.md][PlDb]

- #### Step Two 
  Add jitpack server in youe project level .gradle file
  ```sh 
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```

  In app level gradle add the implementation 
  ```sh
  implementation 'com.github.samirapporio:Apporio-Tracking-System:1.0.1'
  ```
  Allow follwing permission in order to connect your app wtih socket server and fetching location.
   ```sh 
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.INTERNET" />
  ```

- #### Step Three
  In Your Main Application Class  (Initial setup)
  ```sh
    ATS.startInit(this).setAppId("APP_ID_OBTAINED_IN_STEP_ONE")
            .fetchLocationWhenVehicleIsStop(false)
            .enableLogs(true)
            .setLocationInterval(6000)
            .setDeveloperMode(true)
            .setNotificationTittle("APPLICATION_NAME")
            .setNotificationContent("CONTENT WHEN NOTIFICATION IS SHOWING WHILE FETCHING LOCATION")
            .setConnectedStateColor(Color.argb(0, 102, 0 , 204))
            .setDisconnectedColor(Color.argb(0 , 255, 255, 102))
            .setSocketEndPoint("NA")
            .setNotificationIcon(R.drawable.samll_notification_icon)
            .init();
  ```

    

   
   | Method | Description |
   | ------ | ------ |
   | fetchLocationWhenVehicleIsStop(boolean value) | There are some situation where device stop emiting location when device(vehicle) is on same place (device generally do this in order to save battery). This library stores the last location in cashe and execute a different background thread that emit last saved location as per the configuread interval |
  | enableLogs(boolean value) | ATS lib itself has own log mechanism , you can enable or disable it. Search "ATS:" in logact that will throw only. |
  | setLocationInterval(int value) - in miliseconds | This will be the minimum time of interval for fetching location, it is recomended not to set less than 6 sec in order to save battery and internet usage |
  | setDeveloperMode(boolean value) | There are lots of things going inside the library like fetching of location, creation of location stack and auto-synchronisation of stack. If developer mode is true you can visualise all things hapenning inside the library. This is usefull while the field testing like if you needs to know weather device is fetching location or not, location service is running or not , device is connected to server or not , app foregorund status and battery status. |
  | setNotificationTittle(String title) | This works only in live mode of library. As per google service guidline it is mandatory to show a notification in status bar if you running a service for long time. eg. Like in taxi application you can set "Driver Name or application name" |
  | setNotificationContent(String title) | This works only in live mode of library. e.g. in taxi like application you can set "Driver is on duty now." |
  | .setConnectedStateColor(int args)  .setDisconnectedColor(int args) | Library has two color for the background of notification showing in status bar. One when device is conncted to the server and one when it is disconnected. default color is green when device is connected and red when it is disconnected. you can set any colo here according to you applicaiton theme |
  | setNotificationIcon(int icon) | This is just for setting icon when location service is running to show in status bar |
  | .setSocketEndPoint(String endpoint)  |   By defualt after applying app id the application will get connect to the defualt server of application, but if you have server code as well you deploy it on any cloud instance and connect with that server by using this method |



- #### Step Four
  Create you service that extends library location service "AtsLocationServiceClass"
  ```sh
  public class MyService extends AtsLocationServiceClass {

    @Override
    public void onReceiveLocation(Location location) {

    }
  }
  ```
  
  Register this service in you manifest class
  ```sh
   <service android:name=".MyService"/>
  ```
  
  To start this service you can do:
  ```sh
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Toast.makeText(this, "starting foregorund service.", Toast.LENGTH_SHORT).show();
        startForegroundService(new Intent(this, MyService.class));
    } else { // normal
        Toast.makeText(this, "Starting normal service.", Toast.LENGTH_SHORT).show();
        startService(new Intent(this, MyService.class));
 }
  ```


- #### Step Five
  Set your listeners and tag for tracking, methods list are as follows
  ###### getAtsid()
  ```sh
  ATS.getAtsid(); // unique id that will be sync over you backend
  ```
  
  
  
  
  
  
  ###### enableLogs(boolean value)
  ```sh
  ATS.enableLogs(true); // enable disable library logs
  ```
  
  
  
  
  
  
  
  ###### setTag(String tag, AtsOnTagSetListener atsOnTagSetListener)
  ```sh
   ATS.setTag("AAAA", new AtsOnTagSetListener() {
        @Override
        public void onSuccess(String message) {
             Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show(); }
        @Override
            public void onFailed(String message) { Toast.makeText(MainActivity.this,""+message, Toast.LENGTH_SHORT).show(); }
    });
  ```
  
  ###### ATS.getTag()
  ```sh
   Toast.makeText(this, ""+ATS.getTag(), Toast.LENGTH_SHORT).show();
  ```
  
  ###### removeTag(AtsOnTagSetListener atsOnTagSetListener);
  ```sh
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
  ```
  
  ###### startListeningAtsID(String targetAtsIdToListen, AtsOnAddMessageListener atsOnAddMessageListener)
  ```sh
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
  ```
  
  ###### stopListeningAtsId(String targetAtsIdToRemove , AtsOnRemoveMessageListener atsOnRemoveMessageListener)
  ```sh
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
  ```
  
  ###### removeAllListeners(AtsOnRemoveMessageListener atsOnRemoveMessageListener)
  ```sh
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
  ```
  
  ###### listenTagAccordingToRadius(String tag, double latitude, double longitude, int radiusInMeter, AtsTagListener atsTagListener)
  
  ```sh
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
  ```
  
  ###### stopListeningTag(AtsOnTagSetListener atsOnTagSetListener)
  ```sh
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
  ```
  
  ###### startTrip(String tripIdentifier , AtsOnTripSetListener atsOnTripSetListener)
  ```sh
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
  ```
  
  ###### endTrip(String tripIdentifier, AtsOnTripSetListener atsOnTripSetListener)
  ```sh
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
  ```
