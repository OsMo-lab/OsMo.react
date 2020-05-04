package com.osmo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;

import android.widget.RelativeLayout;

import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.osmo.Netutil.MyAsyncTask;
import static java.lang.Math.abs;
import static org.osmdroid.util.GeometryMath.DEG2RAD;
import static org.osmdroid.util.constants.GeoConstants.RADIUS_EARTH_METERS;

public class LocalService extends Service implements LocationListener, GpsStatus.Listener, TextToSpeech.OnInitListener, ResultsListener, SensorEventListener//, OsmAndHelper.OnOsmandMissingListener

    {
        public static Device mydev = new Device();
        //public static ArrayList<ColoredGPX> showedgpxList = new ArrayList<ColoredGPX>();
        static boolean connectcompleted =false;
        public static boolean osmandbind=false;
        //static TrackFileAdapter trackFileAdapter;
        //static ArrayList<TrackFile> trackFileList = new ArrayList<TrackFile>();
        long sessionopentime;
        boolean binded = false;
        private SensorManager mSensorManager;
        private Sensor mAccelerometer;
        final double calibration = SensorManager.STANDARD_GRAVITY;
        float currentAcceleration;
        static final int OSMOMOBI_ID = 1;
        Boolean sessionstarted = false;
        Boolean globalsend = false;
        Boolean sos = false;
        Boolean signalisationOn = false;
        int notifyid = 2;
        boolean followmonstarted=false;
        int sendpalyer;
        static int alarmsound;
        int signalonoff;
        static SoundPool soundPool;
        private Netutil.MyAsyncTask starttask;
        private Intent in;
        public boolean mayak = false;
        private boolean glonas = false;
        private boolean playsound = false;
        boolean sendsound = false;
        private boolean vibrate;
        private boolean usecourse;
        private int vibratetime;
        private double brng;
        private double brng_gpx;
        private double prevbrng;
        private double prevbrng_gpx;
        private float speedbearing;
        private float bearing;
        private int speed;
        private boolean beepedon = false;
        private boolean beepedoff = false;
        private boolean gpsbeepedon = false;
        private boolean gpsbeepedoff = false;
        static float  maxspeed = 0;
        int totalclimb;
        int altitude=Integer.MIN_VALUE;
        int prevaltitude=Integer.MIN_VALUE;
        int[] altitudesamples = {Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE};
        float avgspeed;
        float currentspeed;
        long timeperiod = 0;
        float workdistance;
        long workmilli = 0;
        private boolean firstsend = true;
        private boolean sended = true;
        private boolean gpx = false;
        private boolean live = true;
        private int hdop;
        private boolean fileheaderok = false;
        private File fileName = null;
        private int period;
        private int distance;
        private String hash;
        private int n;
        private String position;
        String sendresult = "";
        public static LocationManager myManager;
        private Location prevlocation;
        public static Location currentLocation;
        private Location prevlocation_gpx;
        private Location prevlocation_spd;
        private String URLadr;
        private Vibrator vibrator;
        private PowerManager pm;
        private WakeLock wakeLock;
        private WakeLock LocwakeLock;
        private WakeLock SendwakeLock;
        private int speedbearing_gpx;
        private int bearing_gpx;
        private long lastgpslocationtime = 0;
        private int hdop_gpx;
        private int period_gpx;
        private int distance_gpx;
        private int speed_gpx;
        int sendcounter;
        int writecounter = 0;
        int buffercounter = 0;
        BroadcastReceiver receiver;
        BroadcastReceiver checkreceiver;
        BroadcastReceiver onlinePauseforStartReciever;
        BroadcastReceiver batteryReciever;
        private final IBinder mBinder = new LocalBinder();
        private String gpxbuffer = new String();
        private String satellite = "";
        private String accuracy = "";
        private boolean usebuffer = true;
        private boolean usewake = false;
        static NotificationManager mNotificationManager;
        private String lastsendforbuffer = "First";
        private long lastgpsontime = 0;
        private long lastgpsofftime = 0;
        private long notifyperiod = 30000;
        private AlarmManager am;
        StringBuilder stringBuilder = new StringBuilder();
        PendingIntent pi;
        private Object[] mStartForegroundArgs = new Object[2];
        private Object[] mStopForegroundArgs = new Object[1];
        private String pass;
        private String lastsay = "a";
        Boolean state = false;
        int gpsperiod;
        int gpsdistance;
        long prevnetworklocationtime = 0;
        StringBuilder buffersb = new StringBuilder(327681);
        StringBuilder lastbuffersb = new StringBuilder(327681);
        StringBuilder sendedsb = new StringBuilder(327681);
        private int lcounter = 0;
        private int scounter = 0;
        protected boolean firstgpsbeepedon = false;

        TextToSpeech tts;
        private int langTTSavailable = -1;
        String text;
        //static SharedPreferences settings;
        int batteryprocent = -1;
        int plugged = -1;
        int temperature = -1;
        int voltage = -1;
       // //public static List<Channel> channelList = new ArrayList<Channel>();

        //public static Channel currentChannel;
        public static List<Device> currentchanneldeviceList = new ArrayList<Device>();
        public static ArrayList<String> messagelist = new ArrayList<String>();
        public static ArrayList<String> debuglist = new ArrayList<String>();
        //public static ArrayList<PermLink> simlimkslist = new ArrayList<PermLink>();
        //public static PermLinksAdapter simlinksadapter;
        //public static List<ChatMessage> chatmessagelist = new ArrayList<ChatMessage>();
        public static ArrayList<String> gcmtodolist = new ArrayList<String>();
        public static Device currentDevice;

        //public static ChannelsAdapter channelsAdapter;
        //public static ChannelsDevicesAdapter channelsDevicesAdapter;
        //public static ArrayAdapter<ChatMessage> channelsmessagesAdapter;
        public static ArrayAdapter<String> debugAdapter;
        //public static DeviceChatAdapter chatmessagesAdapter;
        static Context serContext;
        protected static boolean uploadto = false;
        //public static DeviceChange devlistener;
        public static boolean channelsupdated = false;
        public static boolean chatVisible = false;
        public static String currentItemName = "";
        public static ArrayAdapter<String> notificationStringsAdapter;
        public static ArrayList<Map.Entry> speeddistanceEntryList = new ArrayList<Map.Entry>();
        public static ArrayList<Map.Entry> avgspeeddistanceEntryList = new ArrayList<Map.Entry>();
        public static ArrayList<Map.Entry> altitudedistanceEntryList = new ArrayList<Map.Entry>();
        public static ArrayList<String> distanceStringList = new ArrayList<String>();
        MainApplication application ;

        LocationListener followLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                ReactNativeHost reactNativeHost = application.getReactNativeHost();
                ReactInstanceManager reactInstanceManager = reactNativeHost.getReactInstanceManager();
                ReactContext reactContext = reactInstanceManager.getCurrentReactContext();

                if (reactContext != null) {
                    WritableNativeArray params = new WritableNativeArray();
                    params.pushString(locationtoSending(location)+'F');
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("location", params);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        LocationListener singleLocationListener = new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                    {
                        if (where)
                            {

                                //  тогда RCR:12 вроде (пример опять же с батарейки) и json lat lon hdop altitude (остальное не знаю надо ли, можно и speed)
                                //  RCR:12|{"lat"60.534543,"lon":30.1244,"speed":4.2,"hdop":500,"altitude":1200}
                                LocalService.addlog("send on where");
                                JSONObject postjson = new JSONObject();
                                try
                                    {
                                        postjson.put("lat",  OsMoEventEmitter.df6.format(location.getLatitude()));
                                        postjson.put("lon",  OsMoEventEmitter.df6.format(location.getLongitude()));
                                        postjson.put("speed",  OsMoEventEmitter.df0.format(location.getSpeed()));
                                        postjson.put("hdop",  OsMoEventEmitter.df0.format(location.getAccuracy()));
                                        postjson.put("altitude",  OsMoEventEmitter.df0.format(location.getAltitude()));
                                        if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
                                            {
                                                postjson.put("mobile",true);
                                            }
                                    }
                                catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                //myIM.sendToServer("RCR:" + OsMoEventEmitter.WHERE + "|" + postjson.toString(), false);
                                where = false;
                                if (!state)
                                    {
                                        //LocalService.addlog("remove updates because state");
                                        myManager.removeUpdates(singleLocationListener);
                                    }
                                return;
                            }
                    }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras)
                    {
                    }
                @Override
                public void onProviderEnabled(String provider)
                    {
                    }
                @Override
                public void onProviderDisabled(String provider)
                    {
                    }
            };
        static int numberofnotif=0;


        /*
        @Override
        protected void onHandleIntent(Intent intent) {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            Intent customEvent= new Intent("my-custom-event");
            customEvent.putExtra("my-extra-data", "that's it");
            localBroadcastManager.sendBroadcast(customEvent);
        }
        */
        /*
        final static Handler alertHandler = new Handler()
        {

            @Override
            public void handleMessage(Message message)
                {

                    if (log)
                        {
                            Log.d(this.getClass().getName(), "Handle message " + message.toString());
                        }
                    Bundle b = message.getData();
                    if (log)
                        {
                            Log.d(this.getClass().getName(), "deviceU " + b.getInt("deviceU"));
                        }
                    if (b.containsKey("read"))
                        {
                            String str = "";
                            str = b.getString("read");
                            LocalService.addlog(str);
                            if (str.substring(str.length() - 1, str.length()).equals("\n"))
                                {
                                    str = str.substring(0, str.length() - 1);
                                    try
                                        {
                                            myIM.parseEx(new String(str),false);
                                        }
                                    catch (Exception e)
                                        {
                                            e.printStackTrace();
                                            StringWriter sw = new StringWriter();
                                            e.printStackTrace(new PrintWriter(sw));
                                            String exceptionAsString = sw.toString();
                                            LocalService.addlog(exceptionAsString);
                                        }
                                }
                            else
                                {
                                    try
                                        {
                                            myIM.parseEx(new String(str),false);
                                        }
                                    catch (Exception e)
                                        {
                                            e.printStackTrace();
                                            StringWriter sw = new StringWriter();
                                            e.printStackTrace(new PrintWriter(sw));
                                            String exceptionAsString = sw.toString();
                                            LocalService.addlog(exceptionAsString);
                                        }
                                }
                            return;
                        }

                    if (b.containsKey("deviceU") && LocalService.currentDevice != null && LocalService.currentDevice.u == (b.getInt("deviceU")))
                        {
                            LocalService.mNotificationManager.cancel(OsMoEventEmitter.mesnotifyid);
                        }
                    String text = "";
                    if (b.containsKey("MessageText"))
                        {
                            text = b.getString("MessageText");
                        }
                    if (b.containsKey("om_online") && b.getBoolean("om_online", false))
                        {
                        }
                    if (text != null && !text.equals(""))
                        {
                           // Toast.makeText(serContext, text, Toast.LENGTH_SHORT).show();
                            LocalService.messagelist.add(0, text);


                            if(notificationStringsAdapter !=null)
                                {
                                    notificationStringsAdapter.clear();
                                    for (String s:LocalService.messagelist)
                                        {
                                            notificationStringsAdapter.add(s);
                                        }
                                    notificationStringsAdapter.notifyDataSetChanged();
                                }
                            Bundle a = new Bundle();
                            a.putStringArrayList("meslist", LocalService.messagelist);
                            Intent activ = new Intent(serContext, OsMoEventEmitter.class);
                            activ.setAction("notif");
                            activ.putExtras(a);
                            PendingIntent contentIntent = PendingIntent.getActivity(serContext, OsMoEventEmitter.notifyidApp(), activ, 0);
                            Long when = System.currentTimeMillis();
                            numberofnotif++;
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                                    serContext.getApplicationContext(),"default")
                                    .setWhen(when)
                                    .setContentText(text)
                                    .setContentTitle("OsMo.mobi")
                                    .setSmallIcon(android.R.drawable.ic_menu_send)
                                    .setAutoCancel(true)
                                    .setDefaults(Notification.DEFAULT_LIGHTS)
                                    .setContentIntent(contentIntent).setNumber(numberofnotif).setChannelId("silent");
                            if (!OsMoEventEmitter.settings.optBoolean("silentnotify", false))
                                {
                                    notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND).setChannelId("noisy");
                                }
                            Notification notification = notificationBuilder.build();

                            LocalService.mNotificationManager.notify(OsMoEventEmitter.mesnotifyid, notification);
                            if (OsMoEventEmitter.mesactivityVisible)
                                {
                                    try
                                        {
                                            contentIntent.send(serContext, 0, activ);
                                            LocalService.mNotificationManager.cancel(OsMoEventEmitter.mesnotifyid);
                                        }
                                    catch (CanceledException e)
                                        {
                                            if (log)
                                                {
                                                    Log.d(this.getClass().getName(), "pending intent exception" + e);
                                                }
                                            e.printStackTrace();
                                        }
                                }
                        }
                }
        };


         */
        private Float sensivity;
        private int alarmStreamId = 0;
        private int count = 0;
        private int countFix = 0;
        PendingIntent osmodroidLaunchIntent;
        private String strVersionName;
        private String androidver;
        private ObjectInputStream input;
        private boolean bindedremote;
        private boolean bindedlocaly;
        private int pollperiod = 0;
        static boolean paused = false;
        private static boolean log = true;
        String sending = "";
        private long pausemill;
        int intKM;
        boolean where = false;

        NotificationCompat.Builder foregroundnotificationBuilder;
        boolean pro;
        private View linearview;
        private IMapController mapController;
        private long lastsmstime=0;
        //private OsmAndAidlHelper osmand;
        private MapView mMapView;
        private NotificationChannel silentchannel;
        private NotificationChannel noisechannel;
        private int health;

        static String formatInterval(final long l)
            {
                return String.format("%02d:%02d:%02d", l / (1000 * 60 * 60), (l % (1000 * 60 * 60)) / (1000 * 60), ((l % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
            }
        @Override
        public boolean onUnbind(Intent intent)
            {
                if (intent.getAction().equals("OsMoEventEmitter.remote"))
                    {
                        bindedremote = false;
                    }
                else
                    {
                        bindedlocaly = false;
                    }
                if (!bindedremote && !bindedlocaly)
                    {
                        binded = false;
                    }
                if (log)
                    {
                        Log.d(this.getClass().getName(), "on unbind " + binded + "intent=" + intent.getAction() + " bindedremote=" + bindedremote + " bindedlocaly=" + bindedlocaly);
                    }
                return true;
            }
        @Override
        public void onRebind(Intent intent)
            {
                if (intent.getAction().equals("OsMoEventEmitter.remote"))
                    {
                        bindedremote = true;
                        if (!OsMoEventEmitter.settings.optString("key", "").equals(""))
                            {
                                //Netutil.newapicommand((ResultsListener) LocalService.this, "om_device_channel_adaptive:" + OsMoEventEmitter.settings.optString("device", ""));
                            }
                    }
                else
                    {
                        bindedlocaly = true;
                    }
                binded = true;
                Log.d(this.getClass().getName(), "on rebind " + binded + "intent=" + intent.getAction() + " bindedremote=" + bindedremote + " bindedlocaly=" + bindedlocaly);
                super.onRebind(intent);
            }
        @Override
        public IBinder onBind(Intent intent)
            {
                bindedlocaly = true;
                binded = true;
                Log.d(this.getClass().getName(), "on rebind " + binded + "intent=" + intent.getAction() + " bindedremote=" + bindedremote + " bindedlocaly=" + bindedlocaly);

                return mBinder;
            }

        public synchronized void refresh()
            {
                /*
                if (state && myIM.connOpened && !myIM.connecting)
                    {
                        int icon = R.drawable.eyeo;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            icon=R.drawable.eyeo26;
                        }
                        updateNotification(icon);
                    }
                else if (state&&myIM.connecting)
                    {
                        int icon = R.drawable.eyeu;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            icon=R.drawable.eyeu26;
                        }
                        updateNotification(icon);

                    }
                else if (state)
                    {
                        int icon = R.drawable.eyen;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            icon=R.drawable.eyen26;
                        }
                        updateNotification(icon);
                    }
                */
                in.removeExtra("startmessage");
                in.putExtra("position", position + "\n" + satellite + " " + getString(R.string.accuracy) + accuracy);
                in.putExtra("sattelite", satellite + " " + getString(R.string.accuracy) + accuracy);
                in.putExtra("sendresult", sendresult);
                in.putExtra("buffercounter", buffercounter);

                in.putExtra("started", state);
                in.putExtra("globalsend", globalsend);
                in.putExtra("sos", sos);
                in.putExtra("signalisationon", signalisationOn);
                in.putExtra("sendcounter", sendcounter);
                in.putExtra("writecounter", writecounter);
                in.putExtra("currentspeed", OsMoEventEmitter.df0.format(currentspeed * 3.6));
                in.putExtra("avgspeed", OsMoEventEmitter.df1.format(avgspeed * 3600));
                in.putExtra("maxspeed", OsMoEventEmitter.df1.format(maxspeed * 3.6));
                in.putExtra("workdistance", OsMoEventEmitter.df2.format(workdistance / 1000));
                in.putExtra("timeperiod", formatInterval(timeperiod));
                if(altitude!=Integer.MIN_VALUE)
                    {
                        in.putExtra("altitude", OsMoEventEmitter.df0.format(altitude));
                    }
                else
                    {
                        in.putExtra("altitude", "");
                    }

                in.putExtra("totalclimb",OsMoEventEmitter.df0.format(totalclimb));

                /*
                if (myIM != null)

                    {
                        in.putExtra("connect", myIM.connOpened);
                        in.putExtra("connecting", myIM.connecting);
                        in.putExtra("executedlistsize", myIM.executedCommandArryaList.size());
                    }
                    */
                //in.putExtra("traffic", Long.toString((TrafficStats.getUidTxBytes(context.getApplicationInfo().uid)-myIM.startTraffic) / 1024) + OsMoEventEmitter.dot.getDecimalSeparator() + Long.toString((TrafficStats.getUidTxBytes(context.getApplicationInfo().uid)-myIM.startTraffic) % 1000) + "KB " + myIM.connectcount + "|" + myIM.erorconenctcount);

                sendBroadcast(in);
                updatewidgets();
            }


        String getversion()
            {
                androidver = android.os.Build.VERSION.RELEASE;
                strVersionName = getString(R.string.Unknow);
                String version = getString(R.string.Unknow);
                if (log)
                    {
                        Log.d(getClass().getSimpleName(), "startcommand");
                    }
                try
                    {
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        strVersionName = packageInfo.packageName + ' ' + packageInfo.versionName + ' ' + packageInfo.versionCode;
                        version = packageInfo.versionName + ' ' + packageInfo.versionCode;
                    }
                catch (NameNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                return version;
            }

        public void stopcomand()
            {
                if (starttask != null)
                    {
                        starttask.cancel(true);
                        starttask.close();
                    }
            }

        void setPause(boolean pause)
            {
                if (pause)
                    {
                        paused = true;
                        pausemill = System.currentTimeMillis();

                    }
                else
                    {
                        paused = false;
                        workmilli = workmilli + (System.currentTimeMillis() - pausemill);
                    }
            }

        public String getPosition()
            {
                String result = getString(R.string.NotDefined) + "\n" + getString(R.string.speed);
                if (position == null)
                    {
                        return result;
                    }
                else
                    {
                        return position;
                    }
            }

        public String getSendResult()
            {
                return sendresult;
            }

        public int getSendCounter()
            {
                return sendcounter;
            }

        @Override
        public void onCreate()
            {
                super.onCreate();
                application = (MainApplication) this.getApplication();
                Log.d(this.getClass().getName(), "localserviceoncreate");
                //osmand = new OsmAndAidlHelper(this, this);
                ttsManage();
                getversion();
                serContext = LocalService.this;

                mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                /*
                if (OsMoEventEmitter.settings.contains("signalisation"))
                    {
                        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                        signalisationOn = true;
                        if (log)
                            {
                                Log.d(this.getClass().getName(), "Enable signalisation after start ");
                            }
                    }
                */
                myManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                satellite = getString(R.string.Sputniki);
                position = getString(R.string.NotDefined) + "\n" + getString(R.string.speed);
                OsMoEventEmitter.sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
                currentLocation = new Location("");
                prevlocation = new Location("");
                prevlocation_gpx = new Location("");
                currentLocation.setLatitude((double) OsMoEventEmitter.settings.optDouble("lat", 0f));
                currentLocation.setLongitude((double) OsMoEventEmitter.settings.optDouble("lon", 0f));

                ReadPref();

                String alarm = Context.ALARM_SERVICE;
                am = (AlarmManager) getSystemService(alarm);
                Intent intent = new Intent("CHECK_GPS");
                pi = PendingIntent.getBroadcast(this, 0, intent, 0);
                batteryReciever = new BroadcastReceiver()
                {
                    public void onReceive(Context context, Intent intent)
                        {
                            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                            int level = -1;
                            if (rawlevel >= 0 && scale > 0)
                                {
                                    level = (rawlevel * 100) / scale;
                                }
                            batteryprocent = level;
                        }
                };
                registerReceiver(batteryReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                checkreceiver = new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context context, Intent intent)
                        {
                            if (System.currentTimeMillis() > lastgpsontime + notifyperiod && gpsbeepedon)
                                {
                                    if (vibrate)
                                        {
                                            vibrator.vibrate(vibratetime);
                                        }
                                    if (playsound)
                                        {
                                            //soundPool.play(gpson, 1f, 1f, 1, 0, 1f);
                                            if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false))
                                                {
                                                    tts.speak(getString(R.string.gpson), TextToSpeech.QUEUE_ADD, null);
                                                }
                                        }
                                }
                            else
                                {
                                    gpsbeepedon = false;
                                }
                            if (System.currentTimeMillis() > lastgpsofftime + notifyperiod && gpsbeepedoff)
                                {
                                    if (vibrate)
                                        {
                                            //vibrator.vibrate(vibratetime);
                                        }
                                    if (playsound)
                                        {
                                            
                                            if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false))
                                                {
                                                    //tts.speak(getString(R.string.gpsoff), TextToSpeech.QUEUE_ADD, null);
                                                }
                                        }
                                }
                            else
                                {
                                    gpsbeepedoff = false;
                                }
                        }
                };
                receiver = new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context context, Intent intent)
                        {
                            boolean gpxfix = false;
                            gpxfix = intent.getBooleanExtra("enabled", false);
                            if (gpxfix)
                                {
                                    lastgpsontime = System.currentTimeMillis();
                                    gpsbeepedon = true;
                                    if (playsound && !firstgpsbeepedon)
                                        {
                                            firstgpsbeepedon = true;
                                            
                                            if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false))
                                                {
                                                    //tts.speak(getString(R.string.gpson), TextToSpeech.QUEUE_ADD, null);
                                                }
                                        }
                                }
                            else
                                {
                                    lastgpsofftime = System.currentTimeMillis();
                                    gpsbeepedoff = true;
                                }
                        }
                };
                soundPool = new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 0);
                sendpalyer = soundPool.load(this, R.raw.sendsound, 1);
                alarmsound = soundPool.load(this, R.raw.signal, 1);
                signalonoff = soundPool.load(this, R.raw.signalonoff, 1);
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                in = new Intent("OsMo.mobi");
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    silentchannel = new NotificationChannel("silent","OsMo.mobi Silent",NotificationManager.IMPORTANCE_LOW);
                    noisechannel = new NotificationChannel("noisy","OsMo.mobi Noisy",NotificationManager.IMPORTANCE_DEFAULT);
                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    noisechannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attributes);
                    mNotificationManager.createNotificationChannel(silentchannel);
                    mNotificationManager.createNotificationChannel(noisechannel);
                }
                    /*
                    if (!OsMoEventEmitter.settings.getBoolean("ondestroy", false))
                    {
                        List<Channel> loaded = (List<Channel>) loadObject(OsMoEventEmitter.CHANNELLIST, channelList.getClass());
                        if (loaded != null)
                            {
                                Log.d(this.getClass().getName(), "channelList is not empty");
                                channelList.addAll(loaded);
                                for (Channel ch : channelList)
                                    {
                                        for (ColoredGPX cgpx : ch.gpxList)
                                            {
                                                cgpx.initPathOverlay();
                                            }
                                    }
                                //connectcompleted =true;
                                osmAndAddAllChannels();
                            }
                    }
                    */

                    /*
                    ArrayList<String> loadedgcm = (ArrayList<String>) loadObject(OsMoEventEmitter.GCMTODOLIST, gcmtodolist.getClass());
                if(loadedgcm!=null)
                    {
                        gcmtodolist.addAll(loadedgcm);
                    }

                     */

                    /*
                    myIM = new OsMoEventEmitter(null)

                {
                    @Override
                    void ondisconnect()
                        {
                            addlog("ondisconnect, sending="+sending);
                            LocalService.addlog("ondisconnect");
                            if (log)
                                {
                                    Log.d(this.getClass().getName(), "ondisconnect in localservice");
                                }
                            addlog("ondisconnect, sendingbufeer size="+sendingbuffer.size()+ " .buffer.size=" + buffer.size());
                            buffer.addAll(sendingbuffer);
                            sendingbuffer.clear();
                            if (!sending.equals(""))
                                {
                                    buffer.add(sending);
                                    sending = "";
                                    buffercounter++;
                                    String time = OsMoEventEmitter.sdf3.format(new Date(System.currentTimeMillis()));
                                    sendresult = time + " " + getString(R.string.error);
                                    updateNotification(-1);
                                    refresh();
                                }

                        }
                };

                     */
                if (OsMoEventEmitter.settings.optString("newkey", "").equals(""))
                    {
                        sendid();
                    }
                else
                    {
                        if (OsMoEventEmitter.settings.optBoolean("live", false))
                            {
                                //myIM.start();
                            }
                    }
                if (OsMoEventEmitter.settings.optBoolean("started", false)||OsMoEventEmitter.settings.optBoolean("autostartsession", false))
                    {
                        startServiceWork(true);
                    }
                //OsMoEventEmitter.settings.putBoolean("ondestroy", false).commit();

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                linearview = inflater.inflate(R.layout.map, null, false);
                RelativeLayout rl = (RelativeLayout) linearview.findViewById(R.id.relative);
                
                
            }
        void Pong(Context context) throws JSONException
            {
                //myIM.sendToServer("RCR|1", false);
            }
        private String capitalize(String s)
            {
                if (s == null || s.length() == 0)
                    {
                        return "";
                    }
                char first = s.charAt(0);
                if (Character.isUpperCase(first))
                    {
                        return s;
                    }
                else
                    {
                        return Character.toUpperCase(first) + s.substring(1);
                    }
            }
        public String getDeviceName()
            {
                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
                if (model.startsWith(manufacturer))
                    {
                        return capitalize(model);
                    }
                else
                    {
                        return capitalize(manufacturer) + " " + model;
                    }
            }
        
        @Override
        public void onDestroy()
            {
                stopFollow();
                try {
                    //osmand.cleanupResources();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (tts != null)
                    {
                        tts.stop();
                        tts.shutdown();
                        tts = null;
                    }
                mSensorManager.unregisterListener(this);
                if (log)
                    {
                        Log.d(this.getClass().getName(), "Disable signalisation after destroy");
                    }

                    if (state)
                    {
                        stopServiceWork(false);
                    }
                    /*
                if (myIM != null)
                    {
                        myIM.close();
                    }

                     */
                //deleteFile(OsMoEventEmitter.NOTIFIESFILENAME);
                
                stopcomand();
                try
                    {
                        if (receiver != null)
                            {
                                unregisterReceiver(receiver);
                            }
                    }
                catch (Exception e)
                    {
                        if (log)
                            {
                                Log.d(getClass().getSimpleName(), "А он и не зареген");
                            }
                    }
                try
                    {
                        if (checkreceiver != null)
                            {
                                unregisterReceiver(checkreceiver);
                            }
                    }
                catch (Exception e)
                    {
                        if (log)
                            {
                                Log.d(getClass().getSimpleName(), "А он и не зареген");
                            }
                    }
                try
                    {
                        if (onlinePauseforStartReciever != null)
                            {
                                unregisterReceiver(onlinePauseforStartReciever);
                            }
                    }
                catch (Exception e)
                    {
                        if (log)
                            {
                                Log.d(getClass().getSimpleName(), "А он и не зареген");
                            }
                    }
                try
                    {
                        if (batteryReciever != null)
                            {
                                unregisterReceiver(batteryReciever);
                            }
                    }
                catch (Exception e)
                    {
                        if (log)
                            {
                                Log.d(getClass().getSimpleName(), "А он и не зареген");
                            }
                    }
                if (soundPool != null)
                    {
                        soundPool.release();
                    }
                if (!(wakeLock == null) && wakeLock.isHeld())
                    {
                        wakeLock.release();
                    }
                if (!(LocwakeLock == null) && LocwakeLock.isHeld())
                    {
                        LocwakeLock.release();
                    }
                if (!(SendwakeLock == null) && SendwakeLock.isHeld())
                    {
                        SendwakeLock.release();
                    }
                mNotificationManager.cancelAll();
                //OsMoEventEmitter.settings.edit().remove("globalsend").commit();
                //OsMoEventEmitter.settings.edit().putBoolean("ondestroy", true).commit();
                
                super.onDestroy();
                //System.exit(0);
            }
        private void ReadPref()
            {
                if (log)
                    {
                        Log.d(getClass().getSimpleName(), "readpref() localserv");
                    }
                try
                    {
                        pollperiod = Integer.parseInt(OsMoEventEmitter.settings.optString("refreshrate", "0").equals("") ? "0" : OsMoEventEmitter.settings.optString("refreshrate", "0"));
                    }
                catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                    }
                speed = Integer.parseInt(OsMoEventEmitter.settings.optString("speed", "3").equals("") ? "3" : OsMoEventEmitter.settings.optString("speed", "3"));
                period = Integer.parseInt(OsMoEventEmitter.settings.optString("period", "10000").equals("") ? "10000" : OsMoEventEmitter.settings.optString("period", "10000"));
                distance = Integer.parseInt(OsMoEventEmitter.settings.optString("distance", "50").equals("") ? "50" : OsMoEventEmitter.settings.optString("distance", "50"));
                hash = OsMoEventEmitter.settings.optString("hash", "");
                n = Integer.parseInt(OsMoEventEmitter.settings.optString("n", "0").equals("") ? "0" : OsMoEventEmitter.settings.optString("n", "0"));
                speedbearing = Integer.parseInt(OsMoEventEmitter.settings.optString("speedbearing", "2").equals("") ? "2" : OsMoEventEmitter.settings.optString("speedbearing", "2"));
                bearing = Integer.parseInt(OsMoEventEmitter.settings.optString("bearing", "10").equals("") ? "10" : OsMoEventEmitter.settings.optString("bearing", "2"));
                hdop = Integer.parseInt(OsMoEventEmitter.settings.optString("hdop", "30").equals("") ? "30" : OsMoEventEmitter.settings.optString("hdop", "30"));
                gpx = OsMoEventEmitter.settings.optBoolean("gpx", false);
                live = OsMoEventEmitter.settings.optBoolean("live", true);
                vibrate = OsMoEventEmitter.settings.optBoolean("vibrate", false);
                usecourse = OsMoEventEmitter.settings.optBoolean("usecourse", false);
                vibratetime = Integer.parseInt(OsMoEventEmitter.settings.optString("vibratetime", "200").equals("") ? "200" : OsMoEventEmitter.settings.optString("vibratetime", "0"));
                playsound = OsMoEventEmitter.settings.optBoolean("playsound", false);
                period_gpx = Integer.parseInt(OsMoEventEmitter.settings.optString("period_gpx", "0").equals("") ? "0" : OsMoEventEmitter.settings.optString("period_gpx", "0"));
                distance_gpx = Integer.parseInt(OsMoEventEmitter.settings.optString("distance_gpx", "0").equals("") ? "0" : OsMoEventEmitter.settings.optString("distance_gpx", "0"));
                speedbearing_gpx = Integer.parseInt(OsMoEventEmitter.settings.optString("speedbearing_gpx", "0").equals("") ? "0" : OsMoEventEmitter.settings.optString("speedbearing_gpx", "0"));
                bearing_gpx = Integer.parseInt(OsMoEventEmitter.settings.optString("bearing_gpx", "0").equals("") ? "0" : OsMoEventEmitter.settings.optString("bearing", "0"));
                hdop_gpx = Integer.parseInt(OsMoEventEmitter.settings.optString("hdop_gpx", "30").equals("") ? "30" : OsMoEventEmitter.settings.optString("hdop_gpx", "30"));
                speed_gpx = Integer.parseInt(OsMoEventEmitter.settings.optString("speed_gpx", "3").equals("") ? "3" : OsMoEventEmitter.settings.optString("speed_gpx", "3"));
                usebuffer = OsMoEventEmitter.settings.optBoolean("usebuffer", true);
                usewake = OsMoEventEmitter.settings.optBoolean("usewake", false);
                notifyperiod = Integer.parseInt(OsMoEventEmitter.settings.optString("notifyperiod", "30000").equals("") ? "30000" : OsMoEventEmitter.settings.optString("notifyperiod", "30000"));
                sendsound = OsMoEventEmitter.settings.optBoolean("sendsound", false);
            }
        @Override
        public void onStart(Intent intent, int startId)
            {
                //super.onStart(intent, startId);
                if (log)
                    {
                        Log.d(getClass().getSimpleName(), "on start ");
                    }
                updatewidgets();
                handleStart(intent, startId);

            }
        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
            {
                //super.onStartCommand(intent, flags, startId);
                if (log)
                    {
                        Log.d(getClass().getSimpleName(), "on startcommand");
                    }
                updatewidgets();
                handleStart(intent, startId);
                return START_STICKY;
            }
        void handleStart(Intent intent, int startId)
            {
                /*
                if(myIM!=null)
                    {
                        myIM.checkalarmindozemode();
                    }
*/

                Log.d(getClass().getSimpleName(), "on handleStart"+intent);

                if (intent != null)
                    {
                        Bundle bundle = intent.getExtras();
                        if (bundle != null)
                            {
                                for (String key : bundle.keySet())
                                    {
                                        Object value = bundle.get(key);
                                        Log.d(getClass().getSimpleName(), String.format("%s %s (%s)", key,
                                                value.toString(), value.getClass().getName()));
                                        addlog(String.format("%s %s (%s)", key,
                                                value.toString(), value.getClass().getName()));
                                    }
                            }
                        if ( intent.hasExtra("SMS"))
                            {
                                if (intent.getStringExtra("SMS").equals("START") && !state)
                                    {
                                        startServiceWork(true);
                                    }
                                if (intent.getStringExtra("SMS").equals("STOP") && state)
                                    {
                                        stopServiceWork(true);
                                    }
                            }
                        if (intent.hasExtra("ACTION"))
                            {
                                Log.d(getClass().getSimpleName(), "on handleStart intent has ACTION=" + intent.getStringExtra("ACTION"));
                                stopFollow();
                                if (intent.getStringExtra("ACTION").equals("STOP")&& state)
                                    {
                                        stopServiceWork(true);
                                    }
                                if (intent.getStringExtra("ACTION").equals("START")&& !state)
                                    {
                                        startServiceWork(true);
                                    }


                            }
                        if(intent.hasExtra("GCM"))
                            {
                                String toParse = intent.getStringExtra("GCM");
                                JSONObject jsonObject;
                                JSONObject jo = new JSONObject();
                                JSONArray ja = new JSONArray();
                                String command = "";
                                String param = "";
                                String addict = "";
                                try
                                    {
                                        command = toParse.substring(0, toParse.indexOf('|'));
                                    }
                                catch (Exception e1)
                                    {
                                        command = toParse;
                                    }
                                if (command.indexOf(':') != -1)
                                    {
                                        param = command.substring(command.indexOf(':') + 1);
                                        command = command.substring(0, command.indexOf(':'));
                                    }
                                if (toParse.contains("|"))
                                    {
                                        addict = toParse.substring(toParse.indexOf('|') + 1);
                                    }
                                try
                                    {
                                        jo = new JSONObject(addict);
                                    }
                                catch (JSONException e)
                                    {
                                        try
                                            {
                                                if (log)
                                                    {
                                                        Log.d(this.getClass().getName(), "не JSONO ");
                                                    }
                                                ja = new JSONArray(addict);
                                            }
                                        catch (JSONException e1)
                                            {
                                                // TODO Auto-generated catch block
                                                if (log)
                                                    {
                                                        Log.d(this.getClass().getName(), "не JSONA ");
                                                    }
                                            }
                                    }
                                if (command.equals("GPC"))
                                    {
                                        /*
                                        try
                                            {
                                                //myIM.addToChannelChat(Integer.parseInt(param), jo, false);
                                            }
                                        catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }

                                         */
                                    }
                                else
                                    {
                                        if(/*myIM!=null && !myIM.start && */ OsMoEventEmitter.settings.optBoolean("live", true))
                                            {
                                                //myIM.start();
                                                addlog("starr connect because gcm");
                                            }
                                        /* osmo.mobi
                                        try
                                            {
                                                if (connectcompleted)
                                                    {
                                                        addlog("parse because connectcompleted");
                                                        //myIM.parseEx(intent.getStringExtra("GCM"), true);
                                                    }
                                                else
                                                    {
                                                        addlog("addtogcmtodo because not connectcompleted");
                                                        gcmtodolist.add(intent.getStringExtra("GCM"));
                                                        //saveObject(gcmtodolist, OsMoEventEmitter.GCMTODOLIST);
                                                    }
                                            }
                                        catch (JSONException e)
                                            {
                                                StringWriter sw = new StringWriter();
                                                e.printStackTrace(new PrintWriter(sw));
                                                String exceptionAsString = sw.toString();
                                                addlog(exceptionAsString);
                                            }

                                         */
                                    }
                            }
                        else
                            {
                                if (/*myIM != null && !myIM.start && */ OsMoEventEmitter.settings.optBoolean("live", true))
                                    {
                                        //myIM.start();
                                        addlog("starr connect because intent");
                                        startServiceWork(true);
                                    }
                            }

                    }
            }
/*
        public void osmandmanage()
            {

                if(OsMoEventEmitter.settings.getBoolean("osmand",false))
                    {
                        osmand.bindService();
                        osmAndAddAllChannels();
                    }
                else
                    {
                        for(Channel ch: channelList)
                            {
                                osmAndDeleteChannel(ch);
                            }
                        try {
                            osmand.cleanupResources();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            }

        public void applyPreference()
            {

                ReadPref();
                ttsManage();
                manageGPSFixAlarm();
                if (state)
                    {
                        myManager.removeUpdates(this);
                        if (gpx && !fileheaderok)
                            {
                                openGPX();
                            }
                        if (!gpx && fileheaderok)
                            {
                                closeGPX();
                            }
                        requestLocationUpdates(LocalService.this);
                    }
                if (myIM.start == false && live)
                    {
                        myIM.start();
                    }
                if (myIM.start == true && !live)
                    {
                        myIM.close();
                    }
                if (log)
                    {
                        Log.d(getClass().getSimpleName(), "applyPreferecne end");

                    }
                addlog("apply pref");
            }
*/
        public void startFollow(String text)
        {
            try {
                JSONObject jo = new JSONObject(text);
                //RC:7|{"interval":5, "source": "gps", "message": "Вася наблюдает за вами"}
                int interval = jo.optInt("interval", 5);
                String source = jo.optString("source", "all");
                String message = jo.optString("message", "Server watch you");


            if(!state)
            {
                myManager.removeUpdates(followLocationListener);
                requestLocationUpdates(followLocationListener,  source , interval);
                int icon = R.drawable.eye;
                long when = System.currentTimeMillis();
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction(Intent.ACTION_MAIN);
                notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                osmodroidLaunchIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                foregroundnotificationBuilder = new NotificationCompat.Builder(this,"default");
                foregroundnotificationBuilder.setWhen(System.currentTimeMillis());
                foregroundnotificationBuilder.setContentText(message);
                foregroundnotificationBuilder.setContentTitle("OsMo.mobi");
                foregroundnotificationBuilder.setSmallIcon(icon);
                foregroundnotificationBuilder.setContentIntent(osmodroidLaunchIntent);
                foregroundnotificationBuilder.setChannelId("noisy");

                Intent is = new Intent(this, LocalService.class);
                is.putExtra("ACTION", "STOP");

                PendingIntent stop = PendingIntent.getService(this, 0, is, PendingIntent.FLAG_UPDATE_CURRENT);
                foregroundnotificationBuilder.addAction(android.R.drawable.ic_delete, getString(R.string.stop_monitoring), stop);
                Notification notification = foregroundnotificationBuilder.build();
                startForeground(OSMOMOBI_ID, notification);
                followmonstarted=true;
                addlog("follow started");

            }
            } catch (JSONException e) {
                addlog("incorrect data in command -json not parsed");
                return;
            }

        }
        public void stopFollow()
        {
            if(followmonstarted)
            {
                myManager.removeUpdates(followLocationListener);
                stopForeground(true);
                followmonstarted=false;
                addlog("follow stoped");
            }

        }

        public void startServiceWork(boolean opensession)
            {
                addlog("startservicework opensession="+opensession);
                //OsMoEventEmitter.mFirebaseAnalytics.logEvent("TRIP_START",null);
                if (!paused)
                    {
                        altitudedistanceEntryList.clear();
                        avgspeeddistanceEntryList.clear();;
                        speeddistanceEntryList.clear();
                        distanceStringList.clear();
                        writecounter=0;
                        totalclimb=0;
                        altitude=Integer.MIN_VALUE;



                        firstsend = true;
                        avgspeed = 0;
                        maxspeed = 0;
                        intKM = 0;
                        workdistance = 0;
                        timeperiod = 0;
                        workmilli = 0;
                        buffercounter = 0;
                        buffersb.setLength(0);
                        lastbuffersb.setLength(0);
                        sendedsb.setLength(0);
                        lcounter = 0;
                        scounter = 0;
                        sendcounter = 0;
                        sended = true;
                        mydev.devicePath.clear();
                        mydev.iprecomputed=0;
                        sending = "";
                        ReadPref();
                        if (OsMoEventEmitter.settings.optBoolean("playsound", false))
                            {
                                //soundPool.play(startsound, 1f, 1f, 1, 0, 1f);
                                if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false))
                                    {
                                        //tts.speak(getString(R.string.monitoring_started), TextToSpeech.QUEUE_ADD, null);
                                    }
                            }
                        manageGPSFixAlarm();
                        boolean crtfile = false;
                        if (gpx)
                            {
                                openGPX();
                            }
                    }
                setPause(false);
                requestLocationUpdates(LocalService.this);
                int icon = R.drawable.eye;
                CharSequence tickerText = getString(R.string.monitoringstarted); //getString(R.string.Working);
                long when = System.currentTimeMillis();
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction(Intent.ACTION_MAIN);
                notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                osmodroidLaunchIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                foregroundnotificationBuilder = new NotificationCompat.Builder(this,"default");
                foregroundnotificationBuilder.setWhen(System.currentTimeMillis());
                foregroundnotificationBuilder.setContentText(tickerText);
                foregroundnotificationBuilder.setContentTitle("OsMo.mobi");
                foregroundnotificationBuilder.setSmallIcon(icon);
                foregroundnotificationBuilder.setContentIntent(osmodroidLaunchIntent);
                foregroundnotificationBuilder.setChannelId("silent");

                Intent is = new Intent(this, LocalService.class);
                is.putExtra("ACTION", "STOP");

                PendingIntent stop = PendingIntent.getService(this, 0, is, PendingIntent.FLAG_UPDATE_CURRENT);
                foregroundnotificationBuilder.addAction(android.R.drawable.ic_delete, getString(R.string.stop_monitoring), stop);
                Notification notification = foregroundnotificationBuilder.build();

                startForeground(OSMOMOBI_ID, notification);
                setstarted(true);
                if (live)
                    {
                        /*
                        if (myIM != null && myIM.authed)
                            {
                                if(opensession) {
                                    sessionopentime = System.currentTimeMillis() / 1000;
                                    myIM.sendToServer("TO|"+sessionopentime, false);
                                    myIM.needopensession = true;
                                    myIM.needclosesession = false;
                                }
                            }
                        else
                            {
                                if(opensession) {
                                    myIM.needopensession = true;
                                    myIM.needclosesession = false;
                                }
                            }

                         */
                    }
                if (log)
                    {
                        Log.d(getClass().getSimpleName(), "notify:" + notification.toString());
                    }
                if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false))
                    {
                        //tts.speak(getString(R.string.letsgo), TextToSpeech.QUEUE_ADD, null);
                    }
                updatewidgets();
            }
        private void manageGPSFixAlarm()
            {
                int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
                long triggerTime = SystemClock.elapsedRealtime() + notifyperiod;
                if (playsound || vibrate)
                    {
                        am.setRepeating(type, triggerTime, notifyperiod, pi);
                    }
                else
                    {
                        am.cancel(pi);
                    }
                registerReceiver(receiver, new IntentFilter("android.location.GPS_FIX_CHANGE"));
                registerReceiver(checkreceiver, new IntentFilter("CHECK_GPS"));
            }
        private static String convertToHex(byte[] data)
            {
                StringBuilder buf = new StringBuilder();
                for (byte b : data)
                    {
                        int halfbyte = (b >>> 4) & 0x0F;
                        int two_halfs = 0;
                        do
                            {
                                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                                halfbyte = b & 0x0F;
                            } while (two_halfs++ < 1);
                    }
                return buf.toString();
            }
        public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
            {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(text.getBytes("iso-8859-1"), 0, text.length());
                byte[] sha1hash = md.digest();
                return convertToHex(sha1hash);
            }
        @SuppressLint("MissingPermission")
        public void sendid()
            {
                //OsMoEventEmitter.editor.putString("p", "");
                //OsMoEventEmitter.editor.putString("u", "");
                //OsMoEventEmitter.editor.commit();
                String version = android.os.Build.VERSION.RELEASE;
                String androidID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = null;
                try
                    {

                            IMEI = SHA1(mngr.getDeviceId());

                    }
                catch (Exception e)
                    {
                       //writeException(e);
                    }
                if (version == null)
                    {
                        version = "unknown";
                    }
                if (androidID == null)
                    {
                        androidID = "unknown";
                    }
                if (IMEI == null)
                    {
                        IMEI = "unknown";
                    }

                APIcomParams params = new APIcomParams(OsMoEventEmitter.settings.optString("authUrl"), "platform=" + getDeviceName() + version + android.os.Build.PRODUCT + "&app=" + OsMoEventEmitter.settings.optString("OsmoAppKey") + "&id=" + androidID + "&imei=" + IMEI + "&tz=" + TimeZone.getDefault().getDisplayName(Locale.ENGLISH) + "&locale=" + Locale.getDefault().getISO3Language(), "sendid");
                MyAsyncTask sendidtask = new Netutil.MyAsyncTask(this);
                sendidtask.execute(params);
                Log.d(getClass().getSimpleName(), "sendidtask start to execute");

            }
        private void ttsManage()
            {
                if (OsMoEventEmitter.settings.optBoolean("usetts", false) && tts == null)
                    {
                        tts = new TextToSpeech(this,
                                (OnInitListener) this  // TextToSpeech.OnInitListener
                        );
                    }
                if (!OsMoEventEmitter.settings.optBoolean("usetts", false) && tts != null)
                    {
                        tts.stop();
                        tts.shutdown();
                        tts = null;
                    }
            }
        public void requestLocationUpdates(LocationListener locationListener, String source, int interval) throws SecurityException
        {
            addlog("poll period "+interval*1000);
            addlog("Source "+source);
            List<String> list = myManager.getAllProviders();
            switch (source)
            {
                case ("gps"):
                    if (list.contains(LocationManager.GPS_PROVIDER)) {
                        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval * 1000, 0, locationListener);
                    }
                    break;
                case ("net"):
                    if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                        myManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval * 1000, 0, locationListener);
                    }
                    break;
                case ("all"):
                    if (list.contains(LocationManager.GPS_PROVIDER)) {
                        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval * 1000, 0, locationListener);
                    }
                    if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                        myManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval * 1000, 0, locationListener);
                    }

                    break;
            }
        }
        public void requestLocationUpdates(LocationListener locationListener)
            {
                if (log)
                    {
                        Log.d(this.getClass().getName(), "Запускаем провайдера по настройкам");
                    }
                if (log)
                    {
                        Log.d(this.getClass().getName(), "Период опроса:" + pollperiod);
                    }
                addlog("Период опроса:" + pollperiod);
                List<String> list = myManager.getAllProviders();
                if (OsMoEventEmitter.settings.optBoolean("usegps", true))
                    {
                        if (list.contains(LocationManager.GPS_PROVIDER))
                            {
                                try {
                                    myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pollperiod, 0, locationListener);
                                    myManager.addGpsStatusListener(LocalService.this);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        else
                            {
                                if (log)
                                    {
                                        Log.d(this.getClass().getName(), "GPS провайдер не обнаружен");
                                    }
                            }
                    }
                if (OsMoEventEmitter.settings.optBoolean("usenetwork", true))
                    {
                        if (list.contains(LocationManager.NETWORK_PROVIDER))
                            {
                                try {
                                    myManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, pollperiod, 0, locationListener);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        else
                            {
                                if (log)
                                    {
                                        Log.d(this.getClass().getName(), "NETWORK провайдер не обнаружен");
                                    }
                            }
                    }
            }
        /**
         *
         */
        private void openGPX()
            {
                String sdState = android.os.Environment.getExternalStorageState();
                if (sdState.equals(android.os.Environment.MEDIA_MOUNTED))
                    {
                        File sdDir = android.os.Environment.getExternalStorageDirectory();
                        if (!OsMoEventEmitter.settings.optString("sdpath", "").equals(""))
                            {
                                sdDir = new File(OsMoEventEmitter.settings.optString("sdpath", ""));
                            }
                        else
                            {
                                /*
                                Editor editor = OsMoEventEmitter.settings.edit();
                                editor.putString("sdpath", sdDir.getPath());
                                editor.commit();

                                 */
                            }
                        String time = OsMoEventEmitter.sdf2.format(new Date());
                        fileName = new File(sdDir, "OsMo.mobi/");
                        fileName.mkdirs();
                        if (OsMoEventEmitter.settings.optString("gpxname", "").equals(""))
                            {
                                fileName = new File(sdDir, "OsMo.mobi/" + time + ".gpx");
                            }
                        else
                            {
                                fileName = new File(sdDir, "OsMo.mobi/" + OsMoEventEmitter.settings.optString("gpxname", ""));
                                fileheaderok = true;
                            }

                if (!fileName.exists())
                    {
                        /*
                        try
                            {
                                crtfile = fileName.createNewFile();
                                OsMoEventEmitter.editor.putString("gpxname", fileName.getName());
                                OsMoEventEmitter.editor.commit();

                            }
                        catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        */
                        try
                            {
                                // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ssZ");
                                time = OsMoEventEmitter.sdf1.format(new Date(System.currentTimeMillis())) + "Z";
                                FileWriter trackwr = new FileWriter(fileName);
                                trackwr.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                                trackwr.write("<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"OsMo.mobi\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">");
                                trackwr.write("<time>" + time + "</time>");
                                trackwr.write("<trk>");
                                trackwr.write("<name>" + time + "</name>");
                                trackwr.write("<trkseg>");
                                trackwr.flush();
                                trackwr.close();
                                fileheaderok = true;
                            }
                        catch (Exception e)
                            {
                                //e.printStackTrace();
                                Toast.makeText(LocalService.this, getString(R.string.CanNotWriteHeader), Toast.LENGTH_SHORT).show();
                            }
                    }
                }
                else
                    {
                        Toast.makeText(LocalService.this, R.string.nomounted, Toast.LENGTH_SHORT).show();
                    }
            }
        public void stopServiceWork(Boolean stopsession)
            {
                addlog("Stopservicework,stop session "+stopsession);
                //OsMoEventEmitter.mFirebaseAnalytics.logEvent("STOP_TRIP",null);
                //OsMoEventEmitter.editor.putFloat("lat", (float) currentLocation.getLatitude());
                //OsMoEventEmitter.editor.putFloat("lon", (float) currentLocation.getLongitude());
                //OsMoEventEmitter.editor.commit();
                firstgpsbeepedon = false;

                if (OsMoEventEmitter.settings.optBoolean("playsound", false))
                    {

                        if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false))
                            {
                                //tts.speak(getString(R.string.monitoring_stopped), TextToSpeech.QUEUE_ADD, null);
                            }
                    }
                am.cancel(pi);
                if (live && stopsession)
                    {

                        /*
                        if (myIM.authed)
                            {
                                if (sendingbuffer.size() == 0 && buffer.size() != 0)
                                    {
                                        sendingbuffer.addAll(buffer.subList(0,buffer.size()>100?100:buffer.size()));
                                        buffer.removeAll(sendingbuffer);
                                        myIM.sendToServer("B|" + new JSONArray(sendingbuffer), false);
                                    }
                                myIM.sendToServer("TC", false);
                                myIM.needclosesession = true;
                                myIM.needopensession = false;
                            }
                        else
                            {
                                myIM.needclosesession = true;
                                myIM.needopensession = false;
                            }

                         */
                    }
                if (gpx && fileheaderok && stopsession)
                    {
                        closeGPX();
                    }
                if (myManager != null)
                    {
                        myManager.removeUpdates(this);
                        addlog("removeUpdates");
                    }
                setstarted(false);
                stopForeground(true);
                updatewidgets();
            }
        /**
         *
         */
        private void closeGPX()
            {
                try
                    {
                        FileWriter trackwr = new FileWriter(fileName, true);
                        String towright = gpxbuffer;
                        trackwr.write(towright.replace(",", "."));
                        gpxbuffer = "";
                        trackwr.write("</trkseg></trk></gpx>");
                        trackwr.flush();
                        trackwr.close();
                        fileheaderok = false;
                    }
                catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(LocalService.this, getString(R.string.CanNotWriteEnd), Toast.LENGTH_SHORT).show();
                    }
                //OsMoEventEmitter.editor.remove("gpxname");
                //OsMoEventEmitter.editor.commit();
                if (fileName.length() > 1024 && uploadto)
                    {
                        upload(fileName);
                    }
                if (fileName.length() < 1024)
                    {
                        fileName.delete();
                        Toast.makeText(LocalService.this, R.string.tracktoshort, Toast.LENGTH_LONG).show();
                    }
            }
        public void upload(File file)
            {
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                        serContext.getApplicationContext(),"default")
                        .setWhen(System.currentTimeMillis())
                        .setContentText(file.getName())
                        .setContentTitle(getString(R.string.osmodroiduploadfile))
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setProgress(100, 0, false).setChannelId("silent");
                Notification notification = notificationBuilder.build();
                int uploadid = OsMoEventEmitter.uploadnotifyid();
                LocalService.mNotificationManager.notify(uploadid, notification);
                //Netutil.newapicommand((ResultsListener) LocalService.this, "tr_track_upload:1", file, notificationBuilder, uploadid);
            }
        private void setstarted(boolean started)
            {
                //OsMoEventEmitter.editor.putBoolean("started", started);
                //OsMoEventEmitter.editor.commit();
                state = started;
                refresh();
            }
        public void onLocationChanged(Location location)
            {

                if (!state)
                    {
                        //LocalService.addlog("remove updates because state");
                        myManager.removeUpdates(this);
                    }
                currentLocation.set(location);
                /*
                if (LocalService.channelsDevicesAdapter != null && LocalService.currentChannel != null)
                    {
                        if (log)
                            {
                                Log.d(this.getClass().getName(), "Adapter:" + LocalService.channelsDevicesAdapter.toString());
                            }
                        LocalService.channelsDevicesAdapter.notifyDataSetChanged();
                    }

                 */
                accuracy = Integer.toString((int) location.getAccuracy());
                if (System.currentTimeMillis() < lastgpslocationtime + pollperiod + 30000 && location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
                    {
                        if (log)
                            {
                                Log.d(this.getClass().getName(), "У нас есть GPS еще");
                            }

                        return;
                    }
                else
                    {


                    }
                if (System.currentTimeMillis() > lastgpslocationtime + pollperiod + 30000 && location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
                    {
                        if (log)
                            {
                                Log.d(this.getClass().getName(), "У нас уже нет GPS");
                            }

                        if ((location.distanceTo(prevlocation) > distance && System.currentTimeMillis() > (prevnetworklocationtime + period)))
                            {
                                LocalService.addlog("send on because networklocation");
                                prevnetworklocationtime = System.currentTimeMillis();
                                sendlocation(location,false);
                                return;
                            }
                        else
                            {

                            }
                    }
                else
                    {

                    }
                if (firstsend)
                    {
                        if (log)
                            {
                                Log.d(this.getClass().getName(), "Первая отправка");
                            }

                        sendlocation(location,true);
                        prevlocation.set(location);
                        prevlocation_gpx.set(location);
                        prevlocation_spd = new Location("");
                        prevlocation_spd.set(location);
                        prevbrng = brng;
                        workmilli = System.currentTimeMillis();
                        firstsend = false;
                    }
                else
                    {

                    }
                if (location.getSpeed() >= speed_gpx / 3.6 && (int) location.getAccuracy() < hdop_gpx && prevlocation_spd != null)
                    {
                        GeoPoint curGeoPoint = new GeoPoint(location);
                        GeoPoint prevGeoPoint = new GeoPoint(prevlocation_spd);
                        if (OsMoEventEmitter.settings.optBoolean("imperial", false))
                            {
                                workdistance = workdistance +distanceBetween(curGeoPoint,prevGeoPoint)/ 1.609344f;//location.distanceTo(prevlocation_spd);
                            }
                        else
                            {
                                workdistance = workdistance + distanceBetween(curGeoPoint,prevGeoPoint);//location.distanceTo(prevlocation_spd);
                            }
                        if (OsMoEventEmitter.settings.optBoolean("imperial", false))
                            {
                            if (OsMoEventEmitter.settings.optBoolean("ttsavgspeed", false) && OsMoEventEmitter.settings.optBoolean("usetts", false) && tts != null && !tts.isSpeaking() && ((int) workdistance) / 1000/1.609344 > intKM)
                                {
                                    intKM = (int)( workdistance / 1000/1.609344);
                                    //tts.speak(getString(R.string.going) + ' ' + Integer.toString(intKM) + ' ' + "Miles" + ',' + getString(R.string.avg) + ' ' + OsMoEventEmitter.df1.format(avgspeed * 3600) + ',' + getString(R.string.inway) + ' ' + formatInterval(timeperiod), TextToSpeech.QUEUE_ADD, null);

                                }
                            }
                        else
                            {
                                if (OsMoEventEmitter.settings.optBoolean("ttsavgspeed", false) && OsMoEventEmitter.settings.optBoolean("usetts", false) && tts != null && !tts.isSpeaking() && ((int) workdistance) / 1000 > intKM)
                                    {
                                        intKM = (int) workdistance / 1000;
                                        //tts.speak(getString(R.string.going) + ' ' + Integer.toString(intKM) + ' ' + "KM" + ',' + getString(R.string.avg) + ' ' + OsMoEventEmitter.df1.format(avgspeed * 3600) + ',' + getString(R.string.inway) + ' ' + formatInterval(timeperiod), TextToSpeech.QUEUE_ADD, null);
                                    }
                            }
                        prevlocation_spd.set(location);
                        GeoPoint geopoint = new GeoPoint(location);

                        mydev.devicePath.add(new SerPoint(new Point(geopoint.getLatitudeE6(), geopoint.getLongitudeE6())));
                    }
                if ((int) location.getAccuracy() < hdop_gpx)
                    {
                        if(OsMoEventEmitter.settings.optBoolean("imperial",false))
                            {
                                currentspeed = location.getSpeed()*0.621371f;
                                altitude= (int) (location.getAltitude()*3.28084);
                            }
                        else
                            {
                                currentspeed = location.getSpeed();
                                altitude= (int) location.getAltitude();
                            }


                        boolean filled=true;
                        int summ=0;
                        int meanaltitude=Integer.MIN_VALUE;


                        altitudesamples[altitudesamples.length-1] = altitude;

                                for( int index =0; index < altitudesamples.length-1 ; index++ )
                                    {

                                        altitudesamples[index]=altitudesamples[index+1];
                                        if(altitudesamples[index]==Integer.MIN_VALUE)
                                            {
                                                filled=false;
                                            }
                                        summ=summ+altitudesamples[index];
                                    }


                        if(filled)
                            {
                                meanaltitude = summ / altitudesamples.length;
                                if (prevaltitude == Integer.MIN_VALUE)
                                    {
                                        prevaltitude = meanaltitude;
                                    }
                                else
                                    {
                                        if (abs(meanaltitude - prevaltitude) > 5)
                                            {
                                                if (meanaltitude > prevaltitude)
                                                    {
                                                        totalclimb = totalclimb + meanaltitude - prevaltitude;
                                                    }
                                                prevaltitude = meanaltitude;
                                            }
                                    }
                            }

                        if(OsMoEventEmitter.settings.optBoolean("imperial",false))
                            {
                                if (location.getSpeed()*0.621371f > maxspeed)
                                    {
                                        maxspeed = location.getSpeed()*0.621371f;
                                    }
                            }
                        else
                            {
                                if (location.getSpeed() > maxspeed)
                                    {
                                        maxspeed = location.getSpeed();
                                    }

                            }
                    }

                if ((System.currentTimeMillis() - workmilli) > 0)
                    {
                        avgspeed = workdistance / (System.currentTimeMillis() - workmilli);

                    }

                if (OsMoEventEmitter.settings.optBoolean("ttsspeed", false) && OsMoEventEmitter.settings.optBoolean("usetts", false) && tts != null && !tts.isSpeaking() && !(OsMoEventEmitter.df0.format(location.getSpeed() * 3.6).toString()).equals(lastsay))
                    {

                        tts.speak(OsMoEventEmitter.df0.format(location.getSpeed() * 3.6), TextToSpeech.QUEUE_ADD, null);
                        lastsay = OsMoEventEmitter.df0.format(location.getSpeed() * 3.6).toString();
                    }
                position = (OsMoEventEmitter.df6.format(location.getLatitude()) + ", " + OsMoEventEmitter.df6.format(location.getLongitude()) + "\nСкорость:" + OsMoEventEmitter.df1.format(location.getSpeed() * 3.6)) + " Км/ч";
                timeperiod = System.currentTimeMillis() - workmilli;


                for (int index = distanceStringList.size(); index <= (int) workdistance; index++)
                    {
                        distanceStringList.add(Integer.toString(index/1000)+','+Integer.toString(index%1000));
                    }
                /*
                Map.Entry e = new Map.Entry((int) workdistance,currentspeed* 3.6f);
                speeddistanceEntryList.add(e);
                Map.Entry avge = new Map.Entry((int) workdistance,avgspeed * 3600f);
                avgspeeddistanceEntryList.add(avge);
                Map.Entry alte = new Map.Entry((int) workdistance,(float) location.getAltitude());
                altitudedistanceEntryList.add(alte);
                 */

                //speeddistanceEntryList.add(e);
                refresh();
                if (location.getProvider().equals(LocationManager.GPS_PROVIDER))
                    {
                        //LocalService.addlog("Provider=GPS");
                        lastgpslocationtime = System.currentTimeMillis();
                        if (gpx && fileheaderok)
                            {
                                if (bearing_gpx > 0)
                                    {
                                        //if(log)Log.d(this.getClass().getName(), "Пишем трек с курсом");
                                        double lon1 = location.getLongitude();
                                        double lon2 = prevlocation_gpx.getLongitude();
                                        double lat1 = location.getLatitude();
                                        double lat2 = prevlocation_gpx.getLatitude();
                                        double dLon = lon2 - lon1;
                                        double y = Math.sin(dLon) * Math.cos(lat2);
                                        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
                                        brng_gpx = Math.toDegrees(Math.atan2(y, x)); //.toDeg();
                                        position = position + "\n" + getString(R.string.TrackCourseChange) + OsMoEventEmitter.df1.format(abs(brng_gpx - prevbrng_gpx));
                                        refresh();
                                        if (OsMoEventEmitter.settings.optBoolean("modeAND_gpx", false) && (int) location.getAccuracy() < hdop_gpx && location.getSpeed() >= speed_gpx / 3.6 && (location.distanceTo(prevlocation_gpx) > distance_gpx && location.getTime() > (prevlocation_gpx.getTime() + period_gpx) && (location.getSpeed() >= speedbearing_gpx / 3.6 && abs(brng_gpx - prevbrng_gpx) >= bearing_gpx)))
                                            {
                                                prevlocation_gpx.set(location);
                                                prevbrng_gpx = brng_gpx;
                                                writegpx(location);
                                            }
                                        if (!OsMoEventEmitter.settings.optBoolean("modeAND_gpx", false) && (int) location.getAccuracy() < hdop_gpx && location.getSpeed() >= speed_gpx / 3.6 && (location.distanceTo(prevlocation_gpx) > distance_gpx || location.getTime() > (prevlocation_gpx.getTime() + period_gpx) || (location.getSpeed() >= speedbearing_gpx / 3.6 && abs(brng_gpx - prevbrng_gpx) >= bearing_gpx)))
                                            {
                                                prevlocation_gpx.set(location);
                                                prevbrng_gpx = brng_gpx;
                                                writegpx(location);
                                            }
                                    }
                                else
                                    {

                                        if (OsMoEventEmitter.settings.optBoolean("modeAND_gpx", false) && location.getSpeed() >= speed_gpx / 3.6 && (int) location.getAccuracy() < hdop_gpx && (location.distanceTo(prevlocation_gpx) > distance_gpx && location.getTime() > (prevlocation_gpx.getTime() + period_gpx)))
                                            {
                                                writegpx(location);
                                                prevlocation_gpx.set(location);
                                            }
                                        if (!OsMoEventEmitter.settings.optBoolean("modeAND_gpx", false) && location.getSpeed() >= speed_gpx / 3.6 && (int) location.getAccuracy() < hdop_gpx && (location.distanceTo(prevlocation_gpx) > distance_gpx || location.getTime() > (prevlocation_gpx.getTime() + period_gpx)))
                                            {
                                                writegpx(location);
                                                prevlocation_gpx.set(location);
                                            }
                                    }
                            }
                        if (log)
                            {
                                sessionstarted = OsMoEventEmitter.settings.optBoolean("sessionStarted",false);
                                Log.d(this.getClass().getName(), "sessionstarted=" + sessionstarted);
                            }

                        if (live)
                            {

                                if (bearing > 0)
                                    {

                                        double lon1 = location.getLongitude();
                                        double lon2 = prevlocation.getLongitude();
                                        double lat1 = location.getLatitude();
                                        double lat2 = prevlocation.getLatitude();
                                        double dLon = lon2 - lon1;
                                        double y = Math.sin(dLon) * Math.cos(lat2);
                                        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
                                        brng = Math.toDegrees(Math.atan2(y, x)); //.toDeg();
                                        position = position + "\n" + getString(R.string.SendCourseChange) + OsMoEventEmitter.df1.format(abs(brng - prevbrng));
                                        refresh();
                                        if (OsMoEventEmitter.settings.optBoolean("modeAND", false) && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance && location.getTime() > (prevlocation.getTime() + period) && (location.getSpeed() >= (speedbearing / 3.6) && abs(brng - prevbrng) >= bearing)))
                                            {

                                                prevlocation.set(location);
                                                prevbrng = brng;

                                                sendlocation(location,true);
                                            }
                                        else
                                            {

                                            }
                                        if (!OsMoEventEmitter.settings.optBoolean("modeAND", false) && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance || location.getTime() > (prevlocation.getTime() + period) || (location.getSpeed() >= (speedbearing / 3.6) && abs(brng - prevbrng) >= bearing)))
                                            {

                                                prevlocation.set(location);
                                                prevbrng = brng;

                                                sendlocation(location,true);
                                            }
                                        else
                                            {

                                            }
                                    }
                                else
                                    {

                                        if (OsMoEventEmitter.settings.optBoolean("modeAND", false) && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance && location.getTime() > (prevlocation.getTime() + period)))
                                            {

                                                prevlocation.set(location);

                                                sendlocation(location,true);
                                            }
                                        else
                                            {

                                            }
                                        if (!OsMoEventEmitter.settings.optBoolean("modeAND", false) && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance || location.getTime() > (prevlocation.getTime() + period)))
                                            {
                                                prevlocation.set(location);
                                                sendlocation(location,true);
                                            }
                                        else
                                            {

                                            }
                                    }
                            }
                        else
                            {
                                if (log)
                                    {
                                        Log.d(this.getClass().getName(), " not !hash.equals() && live&&sessionstarted");
                                    }

                            }
                    }
                else
                    {

                    }
            }
        public void onProviderDisabled(String provider)
            {
                if (log)
                    {
                        Log.d(this.getClass().getName(), "Выключен провайдер:" + provider);
                    }
                addlog("Выключен провайдер:" + provider);
            }
        public void onProviderEnabled(String provider)
            {
                if (log)
                    {
                        Log.d(this.getClass().getName(), "Включен провайдер:" + provider);
                    }
                addlog("Включен провайдер:" + provider);
            }
        public void onStatusChanged(String provider, int status, Bundle extras)
            {
                if (log)
                    {
                        Log.d(this.getClass().getName(), "Изменился статус провайдера:" + provider + " статус:" + status + " Бандл:" + extras.getInt("satellites"));
                    }

            }

        void internetnotify(boolean internet)
            {
                if (!internet)
                    {
                        if (!beepedoff)
                            {

                                if (vibrate)
                                    {
                                        vibrator.vibrate(vibratetime);
                                    }

                                if (playsound)
                                    {

                                        if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false)&&state)
                                            {
                                                //tts.speak(getString(R.string.inetoff), TextToSpeech.QUEUE_ADD, null);
                                            }
                                    }
                                if (log)
                                    {
                                        Log.d(this.getClass().getName(), "Интернет пропал");
                                    }
                                beepedoff = true;
                                beepedon = false;
                            }
                    }
                else
                    {
                        if (!beepedon)
                            {

                                if (log)
                                    {
                                        Log.d(this.getClass().getName(), "Интернет появился");
                                    }

                                if (vibrate)
                                    {
                                        vibrator.vibrate(vibratetime);
                                    }

                                if (playsound)
                                    {

                                        if (tts != null && OsMoEventEmitter.settings.optBoolean("usetts", false)&&state)
                                            {
                                                tts.speak(getString(R.string.ineton), TextToSpeech.QUEUE_ADD, null);
                                            }
                                    }
                                beepedon = true;
                                beepedoff = false;
                            }
                    }
            }

        private void writegpx(Location location)
            {
                FileWriter trackwr;
                long gpstime = location.getTime();
                Date date = new Date(gpstime);

                String strgpstime = OsMoEventEmitter.sdf1.format(date) + "Z";
                writecounter++;
                if ((gpxbuffer).length() < 5000)
                    {
                        gpxbuffer = gpxbuffer + "<trkpt lat=\"" + OsMoEventEmitter.df6.format(location.getLatitude()) + "\""
                                + " lon=\"" + OsMoEventEmitter.df6.format(location.getLongitude())
                                + "\"><ele>" + OsMoEventEmitter.df0.format(location.getAltitude())
                                + "</ele><time>" + strgpstime
                                + "</time><speed>" + OsMoEventEmitter.df0.format(location.getSpeed())
                                + "</speed>" + "<hdop>" + OsMoEventEmitter.df0.format(location.getAccuracy() / 4) + "</hdop>" + "</trkpt>";
                    }
                else
                    {
                        try
                            {
                                trackwr = new FileWriter(fileName, true);
                                String towright = gpxbuffer;
                                trackwr.write(towright);//.replace(",", "."));
                                trackwr.flush();
                                trackwr.close();
                                gpxbuffer = "";
                            }
                        catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                    }
            }

        private void sendlocation(Location location, boolean gps)
            {
                sessionstarted = OsMoEventEmitter.settings.optBoolean("sessionStarted",false);
                //LocalService.addlog("void sendlocation");
                if (log)
                    {
                        Log.d(this.getClass().getName(), "void sendlocation");
                    }
                ReactNativeHost reactNativeHost = application.getReactNativeHost();
                ReactInstanceManager reactInstanceManager = reactNativeHost.getReactInstanceManager();
                ReactContext reactContext = reactInstanceManager.getCurrentReactContext();


//http://t.esya.ru/?60.452323:30.153262:5:53:25:hadfDgF:352
//	- 0 = latitudedecimal(9,6) (широта)
//	- 1 = longitudedecimal(9,6) (долгота)
//	- 2 = HDOPfloat (горизонтальная ошибка: метры)
//	- 3 = altitudefloat (высота на уровнем моря: метры)
//	- 4 = speedfloat(1) (скорость: метры в секунду)
//	- 5 = hashstring (уникальный хеш пользователя)
//	- 6 = checknumint(3) (контрольное число к хешу)
                //T|L53.1:30.3S2A4H2B23

                sending=locationtoSending(location);
                if(!gps)
                    {
                        sending=sending+"M";
                    }
                LocalService.addlog(" Sending:" + sending);

                if (reactContext != null) {

                    JSONObject json = new JSONObject();
                    JSONObject data = new JSONObject();

                    try {

                        json.put("command", sending);


                        data.put("speed", currentspeed * 3.6);
                        data.put("distance", workdistance / 1000);
                        data.put("time", formatInterval(timeperiod));

                        json.put("data", data);

                    } catch (JSONException je) {

                    }

                    WritableMap params;
                    params = Arguments.createMap();
                    params.putString("location",json.toString());

                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("onMessageReceived", params);
                }

                if (log)
                    {
                        Log.d(this.getClass().getName(), "GPS websocket sendlocation");
                    }

            }

        private String locationtoSending(Location location) {
            String sending="";
            if (log)
                {
                    Log.d(this.getClass().getName(), "Отправка:  " + sending);
                }
            if ((location.getSpeed() * 3.6) >= 6)
                {
                    sending =
                            "T|L" + OsMoEventEmitter.df6.format(location.getLatitude()) + ":" + OsMoEventEmitter.df6.format(location.getLongitude())
                                    + "S" + OsMoEventEmitter.df0.format(location.getSpeed())
                                    + "A" + OsMoEventEmitter.df0.format(location.getAltitude())
                                    + "H" + OsMoEventEmitter.df0.format(location.getAccuracy())
                                    + "C" + OsMoEventEmitter.df0.format(location.getBearing());
                    if (usebuffer)
                        {
                            sending = sending + "T" + location.getTime() / 1000;
                        }
                }
            if ((location.getSpeed() * 3.6) < 6)
                {
                    sending =
                            "T|L" + OsMoEventEmitter.df6.format(location.getLatitude()) + ":" + OsMoEventEmitter.df6.format(location.getLongitude())
                                    + "S" + OsMoEventEmitter.df0.format(location.getSpeed())
                                    + "A" + OsMoEventEmitter.df0.format(location.getAltitude())
                                    + "H" + OsMoEventEmitter.df0.format(location.getAccuracy());
                    if (usebuffer)
                        {
                            sending = sending + "T" + location.getTime() / 1000;
                        }
                }
            if ((location.getSpeed() * 3.6) <= 1)
                {
                    sending =
                            "T|L" + OsMoEventEmitter.df6.format(location.getLatitude()) + ":" + OsMoEventEmitter.df6.format(location.getLongitude())
                                    + "A" + OsMoEventEmitter.df0.format(location.getAltitude())
                                    + "H" + OsMoEventEmitter.df0.format(location.getAccuracy());
                    if (usebuffer)
                        {
                            sending = sending + "T" + location.getTime() / 1000;
                        }
                }
                return sending;
        }

        public void onGpsStatusChanged(int event)
            {
                int MaxPrn = 0;
                int count1 = 0;
                int countFix1 = 0;
                boolean hasA = false;
                boolean hasE = false;
                try {
                    GpsStatus xGpsStatus = myManager.getGpsStatus(null);
                    Iterable<GpsSatellite> iSatellites = xGpsStatus.getSatellites();
                    Iterator<GpsSatellite> it = iSatellites.iterator();
                    while (it.hasNext())
                        {
                            GpsSatellite oSat = (GpsSatellite) it.next();
                            count1 = count1 + 1;
                            hasA = oSat.hasAlmanac();
                            hasE = oSat.hasEphemeris();
                            if (oSat.usedInFix())
                                {
                                    countFix1 = countFix1 + 1;
                                    if (oSat.getPrn() > MaxPrn)
                                        {
                                            MaxPrn = oSat.getPrn();
                                        }
                                    //Log.e("A fost folosit ", "int fix!");
                                }
                        }
                    satellite = getString(R.string.Sputniki) + count + ":" + countFix; //+" ("+hasA+"-"+hasE+")";
                    count = count1;
                    countFix = countFix1;
                    refresh();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                    {
                        langTTSavailable = tts.setLanguage(Locale.getDefault());
                        if (langTTSavailable == TextToSpeech.LANG_MISSING_DATA || langTTSavailable == TextToSpeech.LANG_NOT_SUPPORTED)
                            {
                                if (log)
                                    {
                                        Log.d(this.getClass().getName(), "No TTS for system language");
                                    }
                            }
                        else if (langTTSavailable >= 0 && OsMoEventEmitter.settings.optBoolean("usetts", false))
                            {
                                if (log)
                                    {
                                        Log.d(this.getClass().getName(), "TTS succefully start");
                                    }
                            }
                    }
                else
                    {
                        if (log)
                            {
                                Log.d(this.getClass().getName(), "TTS succefully inited");
                            }
                    }
            }



            void notifywarnactivity(String info, boolean supportButton, int mode)
            {
                if (!OsMoEventEmitter.gpslocalserviceclientVisible)
                    {
                        Long when = System.currentTimeMillis();
                        Intent notificationIntent = new Intent(this, MainActivity.class);
                        notificationIntent.removeExtra("info");
                        notificationIntent.putExtra("info", info);
                        notificationIntent.removeExtra("supportButton");
                        notificationIntent.putExtra("supportButton", supportButton);
                        notificationIntent.removeExtra("mode");
                        notificationIntent.putExtra("mode", mode);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent contentIntent = PendingIntent.getActivity(this, OsMoEventEmitter.notifyidApp(), notificationIntent, 0);
                        NotificationCompat.Builder notificationBuilder = null;
                        if (OsMoEventEmitter.settings.optBoolean("silentnotify", false))
                            {
                                notificationBuilder = new NotificationCompat.Builder(
                                        getApplicationContext(),"default")
                                        .setWhen(when)
                                        .setContentText(info)
                                        .setContentTitle("OsMo.mobi")
                                        .setSmallIcon(R.drawable.warn)
                                        .setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_LIGHTS)
                                        .setContentIntent(contentIntent).setChannelId("silent");
                            }
                        else
                            {
                                notificationBuilder = new NotificationCompat.Builder(
                                        getApplicationContext(),"default")
                                        .setWhen(when)
                                        .setContentText(info)
                                        .setContentTitle("OsMo.mobi")
                                        .setSmallIcon(R.drawable.warn)
                                        .setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND).setChannelId("noisy")
                                        .setContentIntent(contentIntent);
                            }
                        Notification notification = notificationBuilder.build();
                        mNotificationManager.notify(OsMoEventEmitter.warnnotifyid, notification);
                    }
                else
                    {
                        Intent notificationIntent = new Intent(this, MainActivity.class);
                        notificationIntent.removeExtra("info");
                        notificationIntent.putExtra("info", info);
                        notificationIntent.removeExtra("supportButton");
                        notificationIntent.putExtra("supportButton", supportButton);
                        notificationIntent.removeExtra("mode");
                        notificationIntent.putExtra("mode", mode);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        getApplication().startActivity(notificationIntent);
                    }
            }
        public void onResultsSucceeded(APIComResult result)
            {
                JSONArray a = null;
                if (result.Jo == null && result.ja == null)
                    {
                        if (log)
                            {
                                Log.d(getClass().getSimpleName(), "notifwar1 Команда:" + result.Command + " Ответ сервера:" + result.rawresponse + getString(R.string.query) + result.url);
                            }
                        if (OsMoEventEmitter.gpslocalserviceclientVisible)
                            {
                                Toast.makeText(LocalService.this, R.string.esya_ru_notrespond, Toast.LENGTH_LONG).show();
                            }
                    }
                if (result.Command.equals("sendid"))
                    {
                        if (!(result.Jo == null))
                            {
                                if (log)
                                    {
                                        Log.d(getClass().getSimpleName(), "sendid response:" + result.Jo.toString());
                                    }
                                if (result.Jo.has("device"))
                                    {
                                        /*
                                        try
                                            {

                                                try {
                                                    //FirebaseInstanceId.getInstance().deleteInstanceId();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                //OsMoEventEmitter.editor.putString("newkey", result.Jo.optString("device"));
                                                //OsMoEventEmitter.editor.commit();


                                            }
                                        catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }

                                         */
                                    }
                            }
                        else
                            {
                                notifywarnactivity(getString(R.string.warnhash), true, OsMoEventEmitter.NOTIFY_ERROR_SENDID);
                            }
                    }
            }
        public void playAlarmOn()
            {
                if (alarmStreamId == 0)
                    {
                        alarmStreamId = soundPool.play(alarmsound, 1f, 1f, 1, -1, 1f);
                    }
                if (log)
                    {
                        Log.d(this.getClass().getName(), "play alarm on ");
                    }
            }
        public void playAlarmOff()
            {
                soundPool.stop(alarmStreamId);
                alarmStreamId = 0;
                if (log)
                    {
                        Log.d(this.getClass().getName(), "play alarm off ");
                    }
            }

            public void updatewidgets()
            {

                /*
                Log.d(getClass().getSimpleName(), "on updatewidgets state="+state);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

                ComponentName thisWidget = new ComponentName(this,OsMoWidget.class);
                int[] allWidgetIds =  appWidgetManager.getAppWidgetIds(thisWidget);
                Intent is1 = new Intent(this, LocalService.class);
                for (int widgetId : allWidgetIds) {
                    RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.os_mo_widget);
                    if(state)
                        {
                            remoteViews.setImageViewResource(R.id.imageButtonWidget, R.drawable.on);
                            is1.putExtra("ACTION", "STOP");
                            Log.d(getClass().getSimpleName(), "on updatewidgets set action=STOP state=" + state);
                        }
                    else
                        {
                            remoteViews.setImageViewResource(R.id.imageButtonWidget, R.drawable.off);
                            is1.putExtra("ACTION", "START");
                            Log.d(getClass().getSimpleName(), "on updatewidgets set action=START state=" + state);
                        }
                    PendingIntent stop1 = PendingIntent.getService(this, 0, is1, PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteViews.setOnClickPendingIntent(R.id.imageButtonWidget, stop1);
                    appWidgetManager.updateAppWidget(widgetId, remoteViews);

                }
                thisWidget = new ComponentName(this,DoubleOsmoWidget.class);
                allWidgetIds =  appWidgetManager.getAppWidgetIds(thisWidget);
                Intent is2 = new Intent(this, LocalService.class);
                for (int widgetId : allWidgetIds) {
                    RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.double_osmo_widget);
                    if(state)
                        {
                            remoteViews.setImageViewResource(R.id.imageButtonWidget, R.drawable.on);
                            is2.putExtra("ACTION", "STOP");
                            Log.d(getClass().getSimpleName(), "on updatewidgets set action=STOP state=" + state);
                        }
                    else
                        {
                            remoteViews.setImageViewResource(R.id.imageButtonWidget, R.drawable.off);
                            is2.putExtra("ACTION", "START");
                            Log.d(getClass().getSimpleName(), "on updatewidgets set action=START state=" + state);
                        }
                    PendingIntent stop2 = PendingIntent.getService(this, 0, is2, PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteViews.setOnClickPendingIntent(R.id.imageButtonWidget, stop2);
                    remoteViews.setTextViewText(R.id.textViewWidget, OsMoEventEmitter.df2.format(workdistance / 1000)+'\n'+OsMoEventEmitter.df0.format(avgspeed*3600)+'\n'+formatInterval(timeperiod));

                    Intent notificationIntent = new Intent(this, GPSLocalServiceClient.class);
                    notificationIntent.setAction(Intent.ACTION_MAIN);
                    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    osmodroidLaunchIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                    remoteViews.setOnClickPendingIntent(R.id.textViewWidget, osmodroidLaunchIntent);

                    appWidgetManager.updateAppWidget(widgetId, remoteViews);

                }
                thisWidget = new ComponentName(this,SOSWidget.class);
                allWidgetIds =  appWidgetManager.getAppWidgetIds(thisWidget);
                Intent is3 = new Intent(this, LocalService.class);
                for (int widgetId : allWidgetIds) {
                    RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.soswidget);

                    is3.putExtra("GCM", "WIDGETSOS");

                    PendingIntent stop3 = PendingIntent.getService(this, 101, is3, PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteViews.setOnClickPendingIntent(R.id.soswidgetbutton, stop3);

                    appWidgetManager.updateAppWidget(widgetId, remoteViews);

                }

                 */

            }
        public void onAccuracyChanged(Sensor sensor, int accuracy)
            {
                // TODO Auto-generated method stub
            }
        public void onSensorChanged(SensorEvent event)
            {
                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];
                double a = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
                        + Math.pow(z, 2)));
                currentAcceleration = abs((float) (a - calibration));
                addlog("Current accseleration="+Float.toString(currentAcceleration));
                try
                    {
                        sensivity = ((float) Integer.parseInt(OsMoEventEmitter.settings.optString("sensivity", "5"))) / 10f;
                    }
                catch (NumberFormatException e)
                    {
                        sensivity = 0.5f;
                    }
                /*
                if (OsMoEventEmitter.settings.contains("signalisation") && OsMoEventEmitter.settings.getLong("signalisation", 0) + 60000 < System.currentTimeMillis() && currentAcceleration > sensivity)
                    {
                        //OsMoEventEmitter.editor.putLong("signalisation", System.currentTimeMillis());
                        //OsMoEventEmitter.editor.commit();

                        Intent is = new Intent(this, LocalService.class);
                        is.putExtra("GCM","NEEDSENDALARM");
                        handleStart(is,0);
                        if (log)
                            {
                                Log.d(this.getClass().getName(), "Alarm Alarm Alarm " + Float.toString(currentAcceleration));
                            }
                    }

                 */
            }
        void saveObject(Object obj, String filename)
            {
                try
                    {
                        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                        ObjectOutputStream output = new ObjectOutputStream(fos);
                        output.writeObject(obj);
                        output.flush();
                        output.close();
                    }
                catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }
        Object loadObject(String filename, Class type)
            {
                try
                    {
                        input = new ObjectInputStream(openFileInput(filename));
                        return type.cast(input.readObject());
                    }
                catch (StreamCorruptedException e)
                    {
                        e.printStackTrace();
                    }
                catch (Exception e)
                    {
                        e.printStackTrace();
                        addlog("object not loaded from file - excepion");

                    }
                return null;
            }
        void updateNotification(int icon)
            {
                if (foregroundnotificationBuilder != null)
                    {
                        foregroundnotificationBuilder.setContentText(getString(R.string.Sendcount) + sendcounter + ' ' + getString(R.string.writen) + writecounter);
                        if (icon != -1)
                            {
                                foregroundnotificationBuilder.setSmallIcon(icon);
                                foregroundnotificationBuilder.setColor(Color.parseColor("#FFA500"));
                            }
                        mNotificationManager.notify(OSMOMOBI_ID, foregroundnotificationBuilder.build());
                    }
            }

        public class LocalBinder extends Binder
            {
                LocalService getService()
                    {
                        return LocalService.this;
                    }
            }
        static void addlog(final String str)
            {
                Log.d("OsMo", str);
                /*
                alertHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                        {


                        }
                });

                 */
            }
        ArrayList<String> emptyList = new ArrayList<>();



        public static float distanceBetween(final IGeoPoint that,final IGeoPoint other) {

            final double a1 = DEG2RAD * that.getLatitude();
            final double a2 = DEG2RAD * that.getLongitude();
            final double b1 = DEG2RAD * other.getLatitude();
            final double b2 = DEG2RAD * other.getLongitude();

            final double cosa1 = Math.cos(a1);
            final double cosb1 = Math.cos(b1);

            final double t1 = cosa1 * Math.cos(a2) * cosb1 * Math.cos(b2);

            final double t2 = cosa1 * Math.sin(a2) * cosb1 * Math.sin(b2);

            final double t3 = Math.sin(a1) * Math.sin(b1);

            final double tt = Math.acos(t1 + t2 + t3);
            if(Float.isNaN((float)tt))
            {
                return 0f;
            }
            return  ((float)RADIUS_EARTH_METERS) * (float)tt;
        }
    }







