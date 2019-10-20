package com.osmo;

import com.osmo.Netutil.MyAsyncTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.app.Notification.Builder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.provider.Settings.System.getString;
import static com.osmo.Netutil.SHA1;
import static java.lang.StrictMath.abs;
import static org.osmdroid.util.GeometryMath.DEG2RAD;
import static org.osmdroid.util.constants.GeoConstants.RADIUS_EARTH_METERS;


public class OsMoEventEmitter extends ReactContextBaseJavaModule implements ResultsListener, LocationListener, GpsStatus.Listener{

    private ReactContext mReactContext;

    static NotificationManager mNotificationManager;

    static final int OSMODROID_ID = 1;
    public static Device mydev = new Device();
    private static final int MIN_UPLOAD_ID = 4;
    private static final int MAX_UPLOAD_ID = 1000;
    private static int RECONNECT_TIMEOUT = 1000 * 30;
    private static JSONObject settings = new JSONObject();


    static int uploadnotifyid = MIN_UPLOAD_ID;

    //Network
    private MyAsyncTask sendidtask;
    volatile private boolean checkadressing = false;
    static long startTraffic = 0;
    int socketRetryInt = 0;
    private int workserverint = -1;
    private String workservername = "";
    Thread connectThread;
    public Socket socket;
    public SSLSocket sslsocket;
    volatile public boolean authed = false;
    volatile protected boolean running = false;
    volatile protected boolean connOpened = false;
    volatile protected boolean connecting = false;
    volatile public boolean needopensession = false;
    volatile public boolean needclosesession = false;
    public BufferedReader rd;
    public PrintWriter wr;
    static long sendBytes = 0;
    static long recievedBytes = 0;
    static Boolean log = false;

    long connectcount = 0;
    long erorconenctcount = 0;
    ArrayList<String> executedCommandArryaList = new ArrayList<String>();

    private IMWriter iMWriter;
    private IMReader iMReader;
    private Thread readerThread;
    private Thread writerThread;

    private Intent in;
    String sendresult = "";


    final static DecimalFormatSymbols dot = new DecimalFormatSymbols();
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    final static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
    final static DecimalFormat df1 = new DecimalFormat("#######0.0" , DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    final static DecimalFormat df2 = new DecimalFormat("#######0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    final static DecimalFormat df0 = new DecimalFormat("########", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    final static DecimalFormat df6 = new DecimalFormat("########.######", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    private boolean warnedsocketconnecterror = false;

    //Location vars
    public static LocationManager myManager;

    long sessionopentime;
    Boolean state = false;

    static boolean paused = false;
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
    private int distance;
    private float bearing;
    private int speedbearing_gpx;
    private int bearing_gpx;
    private float speedbearing;
    private int speed;

    private int count = 0;
    private int countFix = 0;
    private long lastgpslocationtime = 0;
    long prevnetworklocationtime = 0;
    private int period;

    private String accuracy = "";
    public String motd = "";

    Boolean sessionstarted = false;
    Boolean globalsend = false;
    Boolean sos = false;
    Boolean signalisationOn = false;
    private String satellite = "";


    private String gpxbuffer = new String();
    private boolean usebuffer = false;

    private boolean firstsend = true;
    private boolean sended = true;
    private boolean gpx = false;
    private boolean live = true;
    private int hdop;
    private String lastsay = "a";
    private String position;
    private long lastsmstime=0;

    private int hdop_gpx;
    private int period_gpx;
    private int distance_gpx;
    private int speed_gpx;
    protected boolean firstgpsbeepedon = false;
    private Location prevlocation;
    public static Location currentLocation;
    private Location prevlocation_gpx;
    private Location prevlocation_spd;

    private boolean fileheaderok = false;
    private File fileName = null;

    int sendcounter;
    int writecounter = 0;
    int buffercounter = 0;
    int intKM;
    private int pollperiod = 0;
    private double brng;
    private double brng_gpx;
    private double prevbrng;
    private double prevbrng_gpx;

    String sending = "";

    private int lcounter = 0;
    private int scounter = 0;
    protected static boolean uploadto = false;

    TextToSpeech tts;

    ArrayList<String> buffer = new ArrayList<String>();
    ArrayList<String> sendingbuffer = new ArrayList<String>();

    StringBuilder buffersb = new StringBuilder(327681);
    StringBuilder lastbuffersb = new StringBuilder(327681);
    StringBuilder sendedsb = new StringBuilder(327681);

    public static ArrayList<Entry> speeddistanceEntryList = new ArrayList<Entry>();
    public static ArrayList<Entry> avgspeeddistanceEntryList = new ArrayList<Entry>();
    public static ArrayList<Entry> altitudedistanceEntryList = new ArrayList<Entry>();
    public static ArrayList<String> distanceStringList = new ArrayList<String>();

    Builder foregroundnotificationBuilder;
    boolean pro;



    @Nonnull
    @Override
    public String getName() {
        return "OsMoEventEmitter";
    }

    public OsMoEventEmitter(ReactApplicationContext reactContext) {

        super(reactContext);
        this.mReactContext = reactContext;
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        //Log.d(this.getClass().getSimpleName(), params.toString());

        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }


    @ReactMethod
    public void configure(String config) {
        //Log.d(this.getClass().getSimpleName(), config);

        try {
            JSONObject current  = new JSONObject(config);


            Iterator<?> keys = current.keys();

            while(keys.hasNext() ) {
                String key = (String)keys.next();
                String value = current.optString(key);
                settings.put(key, value);
            }
            if (settings.optBoolean("log")) {
                log = true;
            }

        }
         catch (JSONException e) {

        }

    }

    @ReactMethod
    public void connect() {
        String device = settings.optString("device");
        Log.d(this.getClass().getSimpleName(), "device:" + device);


        running = true;
        connecting = true;
        iMReader = new IMReader();
        iMWriter = new IMWriter();
        connectThread = new Thread(new IMConnect(), "connecter");
        readerThread = new Thread(iMReader, "reader");
        writerThread = new Thread(iMWriter, "writer");
        writerThread.start();

        connectThread.setPriority(Thread.MIN_PRIORITY);
        readerThread.setPriority(Thread.MIN_PRIORITY);
        writerThread.setPriority(Thread.MIN_PRIORITY);

        if (device == null || device == "null") {
            sendid();
        } else {
            if (workserverint == -1) {
                getServerInfo(device);
            } else {
                connectThread.start();
            }

        }
        return;
    }

    @ReactMethod
    public void getMessageOfTheDay() {
        sendToServer("MD", false);

        return;
    }


    @ReactMethod
    public void startSendingCoordinates(Boolean once) {
        return;
    }

    @ReactMethod
    public void stopSendingCoordinates() {
        return;
    }

    @ReactMethod
    public void pauseSendingCoordinates() {
        return;
    }

    private void getServerInfo(String device) {

        if (!checkadressing) {
            checkadressing = true;

            APIcomParams params = null;
            params = new APIcomParams(settings.optString("servUrl") + "app=" + settings.optString("OsmoAppKey") + "&id=" + device, "", "checkaddres");

            sendidtask = new Netutil.MyAsyncTask(this);
            sendidtask.execute(params);
        }
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
    @SuppressLint("MissingPermission")
    public void sendid()
    {
        String version = android.os.Build.VERSION.RELEASE;
        String androidID =  getString(getReactApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        TelephonyManager mngr = (TelephonyManager) getReactApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
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


        APIcomParams params = new APIcomParams(settings.optString("authUrl"), "platform=" + getDeviceName() + version + android.os.Build.PRODUCT + "&app=" + settings.optString("OsmoAppKey") + "&id=" + androidID + "&imei=" + IMEI + "&tz=" + TimeZone.getDefault().getDisplayName(Locale.ENGLISH) + "&locale=" + Locale.getDefault().getISO3Language(), "sendid");
        MyAsyncTask sendidtask = new Netutil.MyAsyncTask(this);
        sendidtask.execute(params);

        Log.d(getClass().getSimpleName(), "sendidtask start to execute");
    }

    public static int uploadnotifyid()
    {
        if (uploadnotifyid < MAX_UPLOAD_ID)
        {
            return uploadnotifyid++;
        }
        else
        {
            return MIN_UPLOAD_ID;
        }
    }

    private void setReconnectOnError()
    {
        try
        {
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException e)
        {
            //writeException(e);
            e.printStackTrace();
        }
        //disablekeepAliveAlarm();
        authed = false;
        connecting = false;
        connOpened = false;

        running = false;
    }
    @Override
    public void onResultsSucceeded(APIComResult result)
    {
        checkadressing = false;
        WritableMap params;

        Log.d(getClass().getSimpleName(), "OnResultsSucceded cmd:" + result.Command + " res:"+ result.rawresponse);

        if (result.Command.equals("checkaddres") && !(result.Jo == null))
        {
            params = Arguments.createMap();
            params.putString("message",result.rawresponse);
            this.sendEvent(this.mReactContext,"onMessageReceived",params);

            socketRetryInt = 0;
            if (result.Jo.has("address"))
            {
                try
                {
                    workservername = result.Jo.optString("address").substring(0, result.Jo.optString("address").indexOf(':'));
                    workserverint = Integer.parseInt(result.Jo.optString("address").substring(result.Jo.optString("address").indexOf(':') + 1));
                    long servertime=result.Jo.optLong("time");
                    if(servertime>0)
                    {
                    }

                    try
                    {
                        connectThread.start();
                    }
                    catch (IllegalThreadStateException e)
                    {
                        Log.d(getClass().getSimpleName(), "Error starting connectThread");
                        setReconnectOnError();
                        e.printStackTrace();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            } else {
                if (result.Jo.optInt("error") > 0) {
                    params = Arguments.createMap();
                    params.putString("error",result.rawresponse);
                    this.sendEvent(this.mReactContext,"onMessageReceived",params);
                }
            }
        } else if (result.Command.equals("sendid") && !(result.Jo == null)) {
            String device = result.Jo.optString("device");
            try {
                settings.put("device", device);
            } catch(JSONException e){

            }
            params = Arguments.createMap();
            params.putString("newkey",device);
            this.sendEvent(this.mReactContext,"onMessageReceived",params);

            this.getServerInfo(device);
        }
        else
        {
            socketRetryInt++;
            Log.d(getClass().getSimpleName(), "herrrr");
            setReconnectOnError();
      }
    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
            //satellite = getString(R.string.Sputniki) + count + ":" + countFix; //+" ("+hasA+"-"+hasE+")";
            count = count1;
            countFix = countFix1;
            refresh();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        //addlog("onGpsStatusChanged "+count1+" "+countFix1);
    }
    public void startServiceWork(boolean opensession)
    {
        if (!paused)
        {
            altitudedistanceEntryList.clear();
            avgspeeddistanceEntryList.clear();;
            speeddistanceEntryList.clear();
            distanceStringList.clear();
            writecounter=0;
            sendingbuffer.clear();
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
            sending = "";

            boolean crtfile = false;
            if (gpx)
            {
                openGPX();
            }
        }
        //setPause(false);
        requestLocationUpdates(this);
        /*
        int icon = R.drawable.eye;
        CharSequence tickerText = getString(R.string.monitoringstarted); //getString(R.string.Working);
        long when = System.currentTimeMillis();
        Intent notificationIntent = new Intent(this, GPSLocalServiceClient.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        osmodroidLaunchIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        foregroundnotificationBuilder = new NotificationCompat.Builder(this,"default");
        foregroundnotificationBuilder.setWhen(System.currentTimeMillis());
        foregroundnotificationBuilder.setContentText(tickerText);
        foregroundnotificationBuilder.setContentTitle("OsMoDroid");
        foregroundnotificationBuilder.setSmallIcon(icon);
        foregroundnotificationBuilder.setContentIntent(osmodroidLaunchIntent);
        foregroundnotificationBuilder.setChannelId("silent");

        Intent is = new Intent(this, this.class);
        is.putExtra("ACTION", "STOP");

        PendingIntent stop = PendingIntent.getService(this, 0, is, PendingIntent.FLAG_UPDATE_CURRENT);
        foregroundnotificationBuilder.addAction(android.R.drawable.ic_delete, getString(R.string.stop_monitoring), stop);
        Notification notification = foregroundnotificationBuilder.build();
        //notification = new Notification(icon, tickerText, when);
        //notification.setLatestEventInfo(getApplicationContext(), "OsMoDroid", getString(R.string.monitoringactive), osmodroidLaunchIntent);
        startForeground(1, notification);
        */
        setstarted(true);
        /*
        if (live)
        {
            if (myIM != null && authed)
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
        }

        if (tts != null && OsMoDroid.settings.getBoolean("usetts", false))
        {
            tts.speak(getString(R.string.letsgo), TextToSpeech.QUEUE_ADD, null);
        }
        */
    }

    public void requestLocationUpdates(LocationListener locationListener, String source, int interval) throws SecurityException
    {
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
        List<String> list = myManager.getAllProviders();
        try {
        if (settings.getBoolean("usegps"))
        {
            if (list.contains(LocationManager.GPS_PROVIDER))
            {
                try {
                    myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pollperiod, 0, locationListener);
                    myManager.addGpsStatusListener(this);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                Log.d(this.getClass().getName(), "GPS провайдер не обнаружен");

            }
        }
        } catch (JSONException e) {

        }

        try {
        if (settings.getBoolean("usenetwork"))
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
                Log.d(this.getClass().getName(), "NETWORK провайдер не обнаружен");
            }
        }
        } catch (JSONException e) {

        }
    }
    private void setstarted(boolean started)
    {
        state = started;
        //refresh();
    }

    private void openGPX()
    {
        boolean crtfile;
        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED))
        {
            File sdDir = android.os.Environment.getExternalStorageDirectory();

            if (!settings.optString("sdpath").equals(""))
            {
                sdDir = new File(settings.optString("sdpath"));
            }
            else
            {
                /*
                SharedPreferences.Editor editor = OsMoDroid.settings.edit();
                editor.putString("sdpath", sdDir.getPath());
                editor.commit();

                 */
            }
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = sdf2.format(new Date());
            fileName = new File(sdDir, "OsMoDroid/");
            fileName.mkdirs();
            if (settings.optString("gpxname").equals(""))
            {
                fileName = new File(sdDir, "OsMoDroid/" + time + ".gpx");
            }
            else
            {
                fileName = new File(sdDir, "OsMoDroid/" + settings.optString("gpxname"));
                fileheaderok = true;
            }

            if (!fileName.exists())
            {
                /*

                try
                {
                    crtfile = fileName.createNewFile();
                    OsMoDroid.editor.putString("gpxname", fileName.getName());
                    OsMoDroid.editor.commit();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                */
                //if(log)Log.d(getClass().getSimpleName(), Boolean.toString(crtfile));
                try
                {
                    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ssZ");
                    time = sdf1.format(new Date(System.currentTimeMillis())) + "Z";
                    FileWriter trackwr = new FileWriter(fileName);
                    trackwr.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    trackwr.write("<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"OsMoDroid\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">");
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
                    //Toast.makeText(this, getString(R.string.CanNotWriteHeader), Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            //Toast.makeText(this, R.string.nomounted, Toast.LENGTH_SHORT).show();
        }
    }
    public void stopServiceWork(Boolean stopsession)
    {
        /*
        OsMoDroid.mFirebaseAnalytics.logEvent("STOP_TRIP",null);
        OsMoDroid.editor.putFloat("lat", (float) currentLocation.getLatitude());
        OsMoDroid.editor.putFloat("lon", (float) currentLocation.getLongitude());
        OsMoDroid.editor.commit();
        */
        firstgpsbeepedon = false;

/*
        if (OsMoDroid.settings.getBoolean("playsound", false))
        {
            if (tts != null && OsMoDroid.settings.getBoolean("usetts", false))
            {
                tts.speak(getString(R.string.monitoring_stopped), TextToSpeech.QUEUE_ADD, null);
            }
        }
  */
        if (live && stopsession)
        {
            //String[] params = {"http://a.t.esya.ru/?act=session_stop&hash="+OsMoDroid.settings.getString("hash", "")+"&n="+OsMoDroid.settings.getString("n", ""),"false","","session_stop"};
            //APIcomParams params = new APIcomParams("http://a.t.esya.ru/?act=session_stop&hash="+OsMoDroid.settings.getString("hash", "")+"&n="+OsMoDroid.settings.getString("n", "")+"&ttl="+OsMoDroid.settings.getString("session_ttl", "30"),null,"session_stop");
            //new Netutil.MyAsyncTask(this).execute(params);
            if (authed)
            {
                if (sendingbuffer.size() == 0 && buffer.size() != 0)
                {
                    sendingbuffer.addAll(buffer.subList(0,buffer.size()>100?100:buffer.size()));
                    buffer.removeAll(sendingbuffer);
                    sendToServer("B|" + new JSONArray(sendingbuffer), false);
                }
                sendToServer("TC", false);
                needclosesession = true;
                needopensession = false;
            }
            else
            {
                needclosesession = true;
                needopensession = false;
            }
            //buffer.clear();
        }
        if (gpx && fileheaderok && stopsession)
        {
            closeGPX();
        }
        if (myManager != null)
        {
            myManager.removeUpdates(this);
        }
        setstarted(false);
        //stopForeground(true);
        //updatewidgets();
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
            //Toast.makeText(LocalService.this, getString(R.string.CanNotWriteEnd), Toast.LENGTH_SHORT).show();
        }
        /*
        OsMoDroid.editor.remove("gpxname");
        OsMoDroid.editor.commit();

         */
        if (fileName.length() > 1024 && uploadto)
        {
            upload(fileName);
        }
        if (fileName.length() < 1024)
        {
            fileName.delete();
            //Toast.makeText(LocalService.this, R.string.tracktoshort, Toast.LENGTH_LONG).show();
        }
    }
    public void upload(File file)
    {
        PendingIntent contentIntent = PendingIntent.getActivity(this.mReactContext, 0, new Intent(), 0);
        Builder notificationBuilder = new Builder(
                mReactContext.getApplicationContext(),"default")
                .setWhen(System.currentTimeMillis())
                .setContentText(file.getName())
                //.setContentTitle(getString(R.string.osmodroiduploadfile))
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setProgress(100, 0, false).setChannelId("silent");
        Notification notification = notificationBuilder.build();
        int uploadid = uploadnotifyid();
        mNotificationManager.notify(uploadid, notification);
        //Netutil.newapicommand((ResultsListener) this, "tr_track_upload:1", file, notificationBuilder, uploadid);
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
            LocalService.channelsDevicesAdapter.notifyDataSetChanged();
        }

         */
        accuracy = Integer.toString((int) location.getAccuracy());
        if (System.currentTimeMillis() < lastgpslocationtime + pollperiod + 30000 && location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
        {
            Log.d(this.getClass().getName(), "У нас есть GPS еще");
            //LocalService.addlog("We still have GPS");
            return;
        }
        else
        {
            //LocalService.addlog("We still have GPS -ELSE");
        }
        if (System.currentTimeMillis() > lastgpslocationtime + pollperiod + 30000 && location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
        {
            Log.d(this.getClass().getName(), "У нас уже нет GPS");
            //LocalService.addlog("Lost GPS till");
            if ((location.distanceTo(prevlocation) > distance && System.currentTimeMillis() > (prevnetworklocationtime + period)))
            {
                prevnetworklocationtime = System.currentTimeMillis();
                sendlocation(location,false);
                return;
            }
            else
            {
                //LocalService.addlog("send on because networklocation - ELSE");
            }
        }
        else
        {
            //LocalService.addlog("Lost GPS till - ELSE");
        }
        if (firstsend)
        {
            Log.d(this.getClass().getName(), "Первая отправка");
            //LocalService.addlog("First send");
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
            //LocalService.addlog("First send - ELSE");
        }
        if (location.getSpeed() >= speed_gpx / 3.6 && (int) location.getAccuracy() < hdop_gpx && prevlocation_spd != null)
        {
            GeoPoint curGeoPoint = new GeoPoint(location);
            GeoPoint prevGeoPoint = new GeoPoint(prevlocation_spd);
            try {
            if (settings.getBoolean("imperial"))
            {
                workdistance = workdistance +distanceBetween(curGeoPoint,prevGeoPoint)/ 1.609344f;//location.distanceTo(prevlocation_spd);
            }
            else
            {
                workdistance = workdistance + distanceBetween(curGeoPoint,prevGeoPoint);//location.distanceTo(prevlocation_spd);
            }
            } catch (JSONException e) {

            }

            try {
            if (settings.getBoolean("imperial"))
            {
                if (settings.getBoolean("ttsavgspeed") && settings.getBoolean("usetts") && tts != null && !tts.isSpeaking() && ((int) workdistance) / 1000/1.609344 > intKM)
                {
                    intKM = (int)( workdistance / 1000/1.609344);
                    //tts.speak(getString(R.string.going) + ' ' + Integer.toString(intKM) + ' ' + "Miles" + ',' + getString(R.string.avg) + ' ' + df1.format(avgspeed * 3600) + ',' + getString(R.string.inway) + ' ' + formatInterval(timeperiod), TextToSpeech.QUEUE_ADD, null);

                }
            }
            else
            {
                if (settings.getBoolean("ttsavgspeed") && settings.getBoolean("usetts") && tts != null && !tts.isSpeaking() && ((int) workdistance) / 1000 > intKM)
                {
                    intKM = (int) workdistance / 1000;
                    //tts.speak(getString(R.string.going) + ' ' + Integer.toString(intKM) + ' ' + "KM" + ',' + getString(R.string.avg) + ' ' + df1.format(avgspeed * 3600) + ',' + getString(R.string.inway) + ' ' + formatInterval(timeperiod), TextToSpeech.QUEUE_ADD, null);
                }
            }
            } catch (JSONException e) {

            }
            //if(log)Log.d(this.getClass().getName(),"Log of Workdistance, Workdistance="+ Float.toString(workdistance)+" location="+location.toString()+" prevlocation_spd="+prevlocation_spd.toString()+" distanceto="+Float.toString(location.distanceTo(prevlocation_spd)));
            prevlocation_spd.set(location);
            GeoPoint geopoint = new GeoPoint(location);
            //if(devlistener!=null){devlistener.onNewPoint(geopoint);}
            mydev.devicePath.add(new SerPoint(new Point(geopoint.getLatitudeE6(), geopoint.getLongitudeE6())));
        }
        if ((int) location.getAccuracy() < hdop_gpx)
        {
            /*
            if(OsMoDroid.settings.getBoolean("imperial",false))
            {
                currentspeed = location.getSpeed()*0.621371f;
                altitude= (int) (location.getAltitude()*3.28084);
            }
            else
            {

             */
                currentspeed = location.getSpeed();
                altitude= (int) location.getAltitude();
            //}


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

            /*
            if(OsMoDroid.settings.getBoolean("imperial",false))
            {
                if (location.getSpeed()*0.621371f > maxspeed)
                {
                    maxspeed = location.getSpeed()*0.621371f;
                }
            }
            else
            {*/
                if (location.getSpeed() > maxspeed)
                {
                    maxspeed = location.getSpeed();
                }

            //}
        }
        //if(log)Log.d(this.getClass().getName(),"workmilli="+ Float.toString(workmilli)+" gettime="+location.getTime());
        //if(log)Log.d(this.getClass().getName(),"diff="+ Float.toString(location.getTime()-workmilli));
        if ((System.currentTimeMillis() - workmilli) > 0)
        {
            avgspeed = workdistance / (System.currentTimeMillis() - workmilli);
            //if(log)Log.d(this.getClass().getName(),"avgspeed="+ Float.toString(avgspeed));
        }
        //if(log)Log.d(this.getClass().getName(), df0.format(location.getSpeed()*3.6).toString());
        //if(log)Log.d(this.getClass().getName(), df0.format(prevlocation.getSpeed()*3.6).toString());
        try {
        if (settings.getBoolean("ttsspeed") && settings.getBoolean("usetts") && tts != null && !tts.isSpeaking() && !(df0.format(location.getSpeed() * 3.6).toString()).equals(lastsay))
        {
            //if(log)Log.d(this.getClass().getName(), df0.format(location.getSpeed()*3.6).toString());
            //if(log)Log.d(this.getClass().getName(), df0.format(prevlocation.getSpeed()*3.6).toString());
            tts.speak(df0.format(location.getSpeed() * 3.6), TextToSpeech.QUEUE_ADD, null);
            lastsay = df0.format(location.getSpeed() * 3.6).toString();
        }
        } catch (JSONException e) {

        }
        position = (df6.format(location.getLatitude()) + ", " + df6.format(location.getLongitude()) + "\nСкорость:" + df1.format(location.getSpeed() * 3.6)) + " Км/ч";
        //position = ( String.format("%.6f", location.getLatitude())+", "+String.format("%.6f", location.getLongitude())+" = "+String.format("%.1f", location.getSpeed()));
        //if (location.getTime()>lastfix+3000)notifygps(false);
        //if (location.getTime()<lastfix+3000)notifygps(true);
        timeperiod = System.currentTimeMillis() - workmilli;


        for (int index = distanceStringList.size(); index <= (int) workdistance; index++)
        {
            distanceStringList.add(Integer.toString(index/1000)+','+Integer.toString(index%1000));
        }
        /*
        Entry e = new Entry((int) workdistance,currentspeed* 3.6f);
        speeddistanceEntryList.add(e);
        Entry avge = new Entry((int) workdistance,avgspeed * 3600f);
        avgspeeddistanceEntryList.add(avge);
        Entry alte = new Entry((int) workdistance,(float) location.getAltitude());
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
                    //position = position + "\n" + getString(R.string.TrackCourseChange) + df1.format(abs(brng_gpx - prevbrng_gpx));
                    refresh();
                    try {
                    if (settings.getBoolean("modeAND_gpx") && (int) location.getAccuracy() < hdop_gpx && location.getSpeed() >= speed_gpx / 3.6 && (location.distanceTo(prevlocation_gpx) > distance_gpx && location.getTime() > (prevlocation_gpx.getTime() + period_gpx) && (location.getSpeed() >= speedbearing_gpx / 3.6 && abs(brng_gpx - prevbrng_gpx) >= bearing_gpx)))
                    {
                        prevlocation_gpx.set(location);
                        prevbrng_gpx = brng_gpx;
                        writegpx(location);
                    }
                    } catch (JSONException e) {

                    }
                    try {
                    if (!settings.getBoolean("modeAND_gpx") && (int) location.getAccuracy() < hdop_gpx && location.getSpeed() >= speed_gpx / 3.6 && (location.distanceTo(prevlocation_gpx) > distance_gpx || location.getTime() > (prevlocation_gpx.getTime() + period_gpx) || (location.getSpeed() >= speedbearing_gpx / 3.6 && abs(brng_gpx - prevbrng_gpx) >= bearing_gpx)))
                    {
                        prevlocation_gpx.set(location);
                        prevbrng_gpx = brng_gpx;
                        writegpx(location);
                    }
                    } catch (JSONException e) {

                    }
                }
                else
                {
                    //if(log)Log.d(this.getClass().getName(), "Пишем трек без курса");
                    try {
                    if (settings.getBoolean("modeAND_gpx") && location.getSpeed() >= speed_gpx / 3.6 && (int) location.getAccuracy() < hdop_gpx && (location.distanceTo(prevlocation_gpx) > distance_gpx && location.getTime() > (prevlocation_gpx.getTime() + period_gpx)))
                    {
                        writegpx(location);
                        prevlocation_gpx.set(location);
                    }
                    } catch (JSONException e) {

                    }
                    try {
                    if (!settings.getBoolean("modeAND_gpx") && location.getSpeed() >= speed_gpx / 3.6 && (int) location.getAccuracy() < hdop_gpx && (location.distanceTo(prevlocation_gpx) > distance_gpx || location.getTime() > (prevlocation_gpx.getTime() + period_gpx)))
                    {
                        writegpx(location);
                        prevlocation_gpx.set(location);
                    }
                    } catch (JSONException e) {

                    }
                }
            }
            Log.d(this.getClass().getName(), "sessionstarted=" + sessionstarted);
            //LocalService.addlog("Session started="+sessionstarted);
            if (live)
            {
                //LocalService.addlog("live and session satrted");
                if (bearing > 0)
                {
                    //LocalService.addlog("bearing>0");
                    //if(log)Log.d(this.getClass().getName(), "Попали в проверку курса для отправки");
                    //if(log)Log.d(this.getClass().getName(), "Accuracey"+location.getAccuracy()+"hdop"+hdop);
                    double lon1 = location.getLongitude();
                    double lon2 = prevlocation.getLongitude();
                    double lat1 = location.getLatitude();
                    double lat2 = prevlocation.getLatitude();
                    double dLon = lon2 - lon1;
                    double y = Math.sin(dLon) * Math.cos(lat2);
                    double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
                    brng = Math.toDegrees(Math.atan2(y, x)); //.toDeg();
                    //position = position + "\n" + getString(R.string.SendCourseChange) + df1.format(abs(brng - prevbrng));
                    refresh();
                    try {
                    if (settings.getBoolean("modeAND") && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance && location.getTime() > (prevlocation.getTime() + period) && (location.getSpeed() >= (speedbearing / 3.6) && abs(brng - prevbrng) >= bearing)))
                    {
                        //LocalService.addlog("modeAND and accuracy and speed");
                        prevlocation.set(location);
                        prevbrng = brng;
                        //if(log)Log.d(this.getClass().getName(), "send(location)="+location);
                        sendlocation(location,true);
                    }
                    else
                    {
                        //LocalService.addlog("modeAND and accuracy and speed -ELSE");
                    }
                    } catch (JSONException e) {

                    }
                    try {
                    if (!settings.getBoolean("modeAND") && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance || location.getTime() > (prevlocation.getTime() + period) || (location.getSpeed() >= (speedbearing / 3.6) && abs(brng - prevbrng) >= bearing)))
                    {
                        //LocalService.addlog("not modeAND and accuracy and speed");
                        prevlocation.set(location);
                        prevbrng = brng;
                        //if(log)Log.d(this.getClass().getName(), "send(location)="+location);
                        sendlocation(location,true);
                    }
                    else
                    {
                        //LocalService.addlog("not modeAND and accuracy and speed - ELSE");
                    }
                    } catch (JSONException e) {

                    }
                }
                else
                {

                    try {
                    if (settings.getBoolean("modeAND") && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance && location.getTime() > (prevlocation.getTime() + period)))
                    {
                        //LocalService.addlog("modeAND and accuracy and speed");
                        //if(log)Log.d(this.getClass().getName(), "Accuracey"+location.getAccuracy()+"hdop"+hdop);
                        prevlocation.set(location);
                        //if(log)Log.d(this.getClass().getName(), "send(location)="+location);
                        sendlocation(location,true);
                    }
                    else
                    {
                        //LocalService.addlog("modeAND and accuracy and speed - ELSE");
                    }
                    } catch (JSONException e) {

                    }
                    try {
                    if (!settings.getBoolean("modeAND") && (int) location.getAccuracy() < hdop && location.getSpeed() >= speed / 3.6 && (location.distanceTo(prevlocation) > distance || location.getTime() > (prevlocation.getTime() + period)))
                    {
                        //LocalService.addlog("not modeAND and accuracy and speed");
                        //if(log)Log.d(this.getClass().getName(), "Accuracey"+location.getAccuracy()+"hdop"+hdop);
                        prevlocation.set(location);
                        //if(log)Log.d(this.getClass().getName(), "send(location)="+location);
                        sendlocation(location,true);
                    }
                    else
                    {
                        //LocalService.addlog("modeAND and accuracy and speed - ELSE");
                    }
                    } catch (JSONException e) {

                    }
                }
            }
            else
            {
                Log.d(this.getClass().getName(), " not !hash.equals() && live&&sessionstarted");
                //LocalService.addlog("live and session satrted - ELSE");
            }
        }
        else
        {
            //LocalService.addlog("Provider=GPS - ELSE");
        }
    }
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
            Log.d(this.getClass().getName(), "Изменился статус провайдера:" + provider + " статус:" + status + " Бандл:" + extras.getInt("satellites"));
    }

    private void sendlocation(Location location, boolean gps)
    {
//http://t.esya.ru/?60.452323:30.153262:5:53:25:hadfDgF:352
//	- 0 = latitudedecimal(9,6) (широта)
//	- 1 = longitudedecimal(9,6) (долгота)
//	- 2 = HDOPfloat (горизонтальная ошибка: метры)
//	- 3 = altitudefloat (высота на уровнем моря: метры)
//	- 4 = speedfloat(1) (скорость: метры в секунду)
//	- 5 = hashstring (уникальный хеш пользователя)
//	- 6 = checknumint(3) (контрольное число к хешу)
        //T|L53.1:30.3S2A4H2B23
        if (authed && sending.equals("")&&sessionstarted)
        {
            sending=locationtoSending(location);
            if(!gps)
            {
                sending=sending+"M";
            }
            sendToServer(sending, false);
        }
        else
        {
            Log.d(this.getClass().getName(), "Отправка не пошла: " + authed + " s " + sending);
            if (usebuffer)
            {
                buffer.add("T|L" + df6.format(location.getLatitude()) + ":" + df6.format(location.getLongitude())
                        + "S" + df1.format(location.getSpeed())
                        + "A" + df0.format(location.getAltitude())
                        + "H" + df0.format(location.getAccuracy())
                        + "C" + df0.format(location.getBearing())
                        + "T" + location.getTime() / 1000
                );
                buffercounter++;
            }
        }
        try {
        if (!authed && settings.getBoolean("sendsms"))
        {
            if(SystemClock.uptimeMillis()>lastsmstime+1000*Integer.parseInt(settings.getString("smsperiod"/*,"300"*/)))
            {
                lastsmstime=SystemClock.uptimeMillis();
                try
                {
                    String messageText = "L" + df6.format(location.getLatitude()) + ":" + df6.format(location.getLongitude())
                            + "S" + df0.format(location.getSpeed())
                            + "A" + df0.format(location.getAltitude())
                            + "H" + df0.format(location.getAccuracy())
                            + "C" + df0.format(location.getBearing());
                    short port = 901;


                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendDataMessage(settings.getString("sendsmsnumber"), null, port, messageText.getBytes(), null, null);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //writeException(e);

                }
            }
            else
            {
                //addlog("Еще рано!");
            }
        }
        else
        {
            //addlog(Boolean.toString(myIM==null)+Boolean.toString(!authed)+Boolean.toString(OsMoDroid.settings.getBoolean("sendsms",false)));
        }
        } catch (JSONException e) {

        }
    }

    public void sendToServer(String str, boolean gui) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("write", str);
        b.putBoolean("pp", str.equals("PP"));
        msg.setData(b);
        if (running) {
            if (iMWriter.handler != null) {
                String[] data = str.split("\\===");
                ArrayList<String> cl = new ArrayList<String>();
                for (int index = 0; index < data.length; index++) {
                    if (data[index].contains("|")) {
                        data[index] = data[index].substring(0, data[index].indexOf('|'));
                    }
                    if (!data[index].equals("PP")) {
                        cl.add(data[index]);
                    }
                }
                Log.d(this.getClass().getName(), " sending " + str);
                executedCommandArryaList.addAll(cl);
                iMWriter.handler.sendMessage(msg);
                refresh();
            } else {
                //LocalService.addlog("panic! handler is null");
                Log.d(this.getClass().getName(), " handler is null!!!");
            }
        } else {
            if (gui) {
                //Toast.makeText(this, localService.getString(R.string.offline_on), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void writegpx(Location location)
    {
        FileWriter trackwr;
        long gpstime = location.getTime();
        Date date = new Date(gpstime);
        // SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ssZ");
        String strgpstime = sdf1.format(date) + "Z";
        writecounter++;
        if ((gpxbuffer).length() < 5000)
        {
            gpxbuffer = gpxbuffer + "<trkpt lat=\"" + df6.format(location.getLatitude()) + "\""
                    + " lon=\"" + df6.format(location.getLongitude())
                    + "\"><ele>" + df0.format(location.getAltitude())
                    + "</ele><time>" + strgpstime
                    + "</time><speed>" + df0.format(location.getSpeed())
                    + "</speed>" + "<hdop>" + df0.format(location.getAccuracy() / 4) + "</hdop>" + "</trkpt>";
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

    public synchronized void refresh()
    {
        if (state && connOpened && !connecting)
        {
            int icon = R.drawable.eyeo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                icon=R.drawable.eyeo26;
            }
            updateNotification(icon);
        }
        else if (state && connecting)
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
        /*
        in.removeExtra("startmessage");
        //in.putExtra("position", position + "\n" + satellite + " " + getString(R.string.accuracy) + accuracy);
        //in.putExtra("sattelite", satellite + " " + getString(R.string.accuracy) + accuracy);
        in.putExtra("sendresult", sendresult);
        in.putExtra("buffercounter", buffercounter);

        in.putExtra("started", state);
        in.putExtra("globalsend", globalsend);
        in.putExtra("sos", sos);
        in.putExtra("signalisationon", signalisationOn);
        in.putExtra("sendcounter", sendcounter);
        in.putExtra("writecounter", writecounter);
        in.putExtra("currentspeed", df0.format(currentspeed * 3.6));
        in.putExtra("avgspeed", df1.format(avgspeed * 3600));
        in.putExtra("maxspeed", df1.format(maxspeed * 3.6));
        in.putExtra("workdistance", df2.format(workdistance / 1000));
        in.putExtra("timeperiod", formatInterval(timeperiod));
        if(altitude!=Integer.MIN_VALUE)
        {
            in.putExtra("altitude", df0.format(altitude));
        }
        else
        {
            in.putExtra("altitude", "");
        }

        in.putExtra("totalclimb",df0.format(totalclimb));
        if (1 == 1 )
        {
            in.putExtra("connect", connOpened);
            in.putExtra("connecting", connecting);
            in.putExtra("executedlistsize", executedCommandArryaList.size());
        }
        in.putExtra("motd", motd);
        in.putExtra("traffic", Long.toString((TrafficStats.getUidTxBytes(mReactContext.getApplicationInfo().uid)-startTraffic) / 1024) + dot.getDecimalSeparator() + Long.toString((TrafficStats.getUidTxBytes(mReactContext.getApplicationInfo().uid)-startTraffic) % 1000) + "KB " + connectcount + "|" + erorconenctcount);
        in.putExtra("pro", pro);

        */
        //sendBroadcast(in);
        //updatewidgets();
    }

    void updateNotification(int icon)
    {
        if (foregroundnotificationBuilder != null)
        {
            //foregroundnotificationBuilder.setContentText(getString(R.string.Sendcount) + sendcounter + ' ' + getString(R.string.writen) + writecounter);
            if (icon != -1)
            {
                foregroundnotificationBuilder.setSmallIcon(icon);
                foregroundnotificationBuilder.setColor(Color.parseColor("#FFA500"));
            }
            mNotificationManager.notify(OSMODROID_ID, foregroundnotificationBuilder.build());
        }
    }

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

    static String formatInterval(final long l)
    {
        return String.format("%02d:%02d:%02d", l / (1000 * 60 * 60), (l % (1000 * 60 * 60)) / (1000 * 60), ((l % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
    }

    private String locationtoSending(Location location) {
        String sending="";
        Log.d(this.getClass().getName(), "Отправка:" + authed + " s " + sending);
        if ((location.getSpeed() * 3.6) >= 6)
        {
            sending =
                    "T|L" + df6.format(location.getLatitude()) + ":" + df6.format(location.getLongitude())
                            + "S" + df0.format(location.getSpeed())
                            + "A" + df0.format(location.getAltitude())
                            + "H" + df0.format(location.getAccuracy())
                            + "C" + df0.format(location.getBearing());
            if (usebuffer)
            {
                sending = sending + "T" + location.getTime() / 1000;
            }
        }
        if ((location.getSpeed() * 3.6) < 6)
        {
            sending =
                    "T|L" + df6.format(location.getLatitude()) + ":" + df6.format(location.getLongitude())
                            + "S" + df0.format(location.getSpeed())
                            + "A" + df0.format(location.getAltitude())
                            + "H" + df0.format(location.getAccuracy());
            if (usebuffer)
            {
                sending = sending + "T" + location.getTime() / 1000;
            }
        }
        if ((location.getSpeed() * 3.6) <= 1)
        {
            sending =
                    "T|L" + df6.format(location.getLatitude()) + ":" + df6.format(location.getLongitude())
                            + "A" + df0.format(location.getAltitude())
                            + "H" + df0.format(location.getAccuracy());
            if (usebuffer)
            {
                sending = sending + "T" + location.getTime() / 1000;
            }
        }
        return sending;
    }

    public class IMWriter implements Runnable
    {
        public Handler handler;
        boolean error = false;
        @Override
        public void run()
        {

            Log.d(this.getClass().getName(), " RUN IWriter");
            Looper.prepare();
            handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    Bundle b = msg.getData();
                    if (running)
                    {
                        if (socket != null && socket.isConnected() && wr != null)
                        {
                            if (!b.getBoolean("pp"))
                            {
                                //setReconnectAlarm(false);
                            }
                            try
                            {
                                Thread.sleep(0);
                            }
                            catch (InterruptedException e)
                            {
                                //writeException(e);
                                e.printStackTrace();
                            }
                            wr.println(b.getString("write"));
                            error = wr.checkError();
                            /*
                            if (log)
                            {
                                Log.d(this.getClass().getName(), "Write " + b.getString("write") + " error=" + error);
                            }
                            LocalService.addlog("SocketWrite " + b.getString("write") + " error=" + error);

                             */
                            if (error)
                            {
                                if (running)
                                {
                                    Log.d(this.getClass().getName(), "set recconect in error in writer");
                                    setReconnectOnError();
                                }
                                //Looper.myLooper().quit();
                            }
                            else
                            {
                                sendBytes = sendBytes + b.getString("write").getBytes().length;
                            }
                        }
                    }
                    else
                    {
                        /*
                        LocalService.addlog("not connected now");
                        if (OsMoDroid.gpslocalserviceclientVisible)
                        {
                            Toast.makeText(localService, localService.getString(R.string.CheckInternet), Toast.LENGTH_SHORT).show();
                        }
                        */
                    }
                    super.handleMessage(msg);
                }
            };
            Looper.loop();
        }
    }

    private class IMReader implements Runnable
    {
        //public Handler handler;
        private StringBuilder stringBuilder = new StringBuilder(1024);
        private String str;
        @Override
        public void run()
        {
            /*
            Looper.prepare();
            handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    if (log) {
                        Log.d(this.getClass().getName(), "Handle message " + msg.toString());
                    }
                    Bundle b = msg.getData();
                    if (b.containsKey("read")) {
                        String str = "";
                        str = b.getString("read");
                        if (str.substring(str.length() - 1, str.length()).equals("\n")) {
                            str = str.substring(0, str.length() - 1);

                        } else {

                        }


                        return;
                    }

                    super.handleMessage(msg);

                }
            };
            */

            while (connOpened && !Thread.currentThread().isInterrupted())
            {
                try
                {
                    stringBuilder.setLength(0);
                    int c = 0;
                    int i = 0;
                    while (!(c == 10) && !Thread.currentThread().isInterrupted())
                    {
                        c = rd.read();
                        if (!(c == -1))
                        {
                            stringBuilder.append((char) c);
                        }
                        else
                        {
                            /*
                            if (log)
                            {
                                Log.d(this.getClass().getName(), "inputstream c=-1 ");
                            }
                            LocalService.addlog("inputstream c=-1 ");
                            */
                            setReconnectOnError();
                            break;
                        }
                        i = i + 1;
                    }
                    if (stringBuilder.length() != 0 && connOpened)
                    {
                        str = stringBuilder.toString();
                        recievedBytes = recievedBytes + str.getBytes().length;
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putString("read", str);
                        msg.setData(b);

                        WritableMap params = Arguments.createMap();
                        params.putString("message", str);
                        sendEvent(mReactContext, "onMessageReceived", params);


                        /*
                        if (handler != null)
                        {
                            handler.sendMessage(msg);
                        }
                        else
                        {
                            if (log)
                            {
                                Log.d(this.getClass().getName(), " alert handler is null!!!");
                            }
                        }

                         */
                    }
                }
                catch (IOException e)
                {
                    if (running)
                    {
                        Log.d(this.getClass().getName(), "set recconectonerror in reader");
                        setReconnectOnError();
                    }
                    //writeException(e);
                    e.printStackTrace();
                }
            }
        }
    }


    private class IMConnect implements Runnable
    {
        @Override
        public void run()
        {

            SocketAddress sockAddr;
            SSLContext sslContext;
            connectcount++;
            try
            {

                InetAddress serverAddr = InetAddress.getByName(workservername);
                sockAddr = new InetSocketAddress(serverAddr, workserverint);

                socket = new Socket();

                socket.connect(sockAddr, RECONNECT_TIMEOUT);
                //setReconnectAlarm(false);
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager()
                        {
                            public X509Certificate[] getAcceptedIssuers()
                            {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }
                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType)
                            {
                            }
                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType)
                            {
                            }
                        }
                };
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)
                {
                    sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null,trustAllCerts,null);
                }
                else
                {
                    sslContext = SSLContext.getDefault();
                }
                SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                //  sslsocket = (SSLSocket) socketFactory.createSocket(socket, workservername,workserverint, false);
                sslsocket = (SSLSocket) socketFactory.createSocket(socket, workservername   ,workserverint, false);
                sslsocket.setUseClientMode(true);
                SSLSession sslSession = sslsocket.getSession();
                //if (log){Log.d(this.getClass().getName(), "Secured=" + sslSession.isValid());}
                if (!sslSession.isValid())
                {
                    throw new Exception();
                }
                //workserverint = -1;
                //workservername = "";
                //LocalService.addlog("SSL TCP Connected");
                rd = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
                wr = new PrintWriter(new OutputStreamWriter(sslsocket.getOutputStream(), "UTF8"), true);


                socketRetryInt = 0;
                connOpened = true;
                connecting = false;
                /*

                localService.alertHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        localService.refresh();
                    }
                });
                */

                readerThread.start();
                sendToServer("AUTH|" + settings.optString("device"), false);
                warnedsocketconnecterror=false;
            }
            catch (final Exception e1)
            {
                erorconenctcount++;
                socketRetryInt++;
                e1.printStackTrace();
                connecting = false;
                setReconnectOnError();

                //LocalService.addlog("could no conenct to socket " + socketRetryInt + e1.getMessage());
                if(socketRetryInt>5)
                {
                    workserverint=-1;
                }

                if (socketRetryInt > 3 && !settings.optBoolean("understand"/*, false*/)&&!warnedsocketconnecterror)
                {
                    warnedsocketconnecterror=true;
                    //localService.notifywarnactivity(localService.getString(R.string.checkfirewall), false, OsMoDroid.NOTIFY_NO_CONNECT);
                }
            }
        }
    }



}
