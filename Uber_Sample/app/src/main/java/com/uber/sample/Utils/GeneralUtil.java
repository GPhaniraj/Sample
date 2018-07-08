package com.uber.sample.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaExtractor;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uber.sample.BuildConfig;
import com.uber.sample.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class GeneralUtil {
	private static final String UNIQUE_DATE_PATTERN = "yyyyMMdd_HHmmss_SSS";
	public static final String MONTH_FORMAT = "MMM dd";
	public static final String FACEBOOK_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private static final String HTML_START = "<html><head><meta http-equiv='Content-Type' content='text/html' charset='UTF-8' /> %s</head><body>";
	private static final String HTML_FONT_STYLE =  "<style> @font-face { font-family: 'MyFont'; src: url('file:///android_asset/fonts/%s');} p { font-family:'MyFont'} </style>";
	private static final String HTML_END = "</body></html>";
	private static final String CONTENT_PATTERN = "%s<p>%s</p>%s";
    static ArrayList<String> arrayListYears,arrayListMonths,arrayListArmedActive,arrayListGender,arrayListAgeRanges;
	static String createdAtYear = "",createdAtMonth = "";
	static ProgressDialog ringProgressDialog = null;
	static String mFileName = "";
	public static final String noSpaceAlert = "<pre><span style=\"font-size: 14pt;\"><strong>Insufficient storage!<br><br></strong></span>This device doesn't have enough storage" +
			" to record dictation, You can manage your storage settings </pre>";
	// variable to track event time
	static long mLastClickTime = 0;
	static String remUserName = "";

	public static String timestampToDate(long timestamp, String format){
		Date date = new Date((long) 1000 * timestamp);
		DateFormat formatter = new SimpleDateFormat(format, Locale.US);
		return formatter.format(date);
	}

	public static String formatDate(String strDate, String inFormat, String outFormat){
		try{
		DateFormat formatter = new SimpleDateFormat(inFormat, Locale.US);
		Date date = formatter.parse(strDate);
		SimpleDateFormat outgoingFormat = new SimpleDateFormat(outFormat, Locale.US);
		return outgoingFormat.format(date);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (activity.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	public static void showNetworkAlert(Activity activity){

		new AlertDialog.Builder(activity)
                .setTitle("No Internet Connection!")
				.setMessage("Sorry, No Internet connectivity detected.\nPlease reconnect and try again.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
                    }
                })
                .show();

	}

	public static void showNetworkAlertWithFinish(final Activity activity){

		new AlertDialog.Builder(activity)
				.setTitle("No Internet Connection!")
				.setMessage("Sorry, No Internet connectivity detected.\nPlease reconnect and try again.")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						activity.finish();
					}
				})
				.show();

	}





	public static void openSoftKeyboard(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}


	public static void showAlert(Activity activity,String message){

		 new AlertDialog.Builder(activity)
				//.setIcon(android.R.drawable.ic_dialog_alert)
				//.setTitle("Doc-U-Scribe")
				.setMessage(Html.fromHtml(message))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
				.show();

	}

	public static void showAlertWithKeyboard(final Activity activity, String message){

		new AlertDialog.Builder(activity)
				.setMessage(Html.fromHtml(message))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
				.show();
	}


	public static void showAlertWithFinishActivity(final Activity activity, String message){

		new AlertDialog.Builder(activity)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						activity.finish();
					}
				})
				.show();

	}

	public static void showAirplaneModeAlert(final Activity activity){
		String value = "<html> For high quality recording please keep your phone in <font color=#757b86><b><a href=\"http://www.google.com\">Airplane mode.</a></b></font> </html>";
		new AlertDialog.Builder(activity)
				.setIcon(R.mipmap.ic_launcher)
				.setTitle("Doc-U-Scribe")
				.setMessage(Html.fromHtml(value))
				.setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						activity.startActivity(intent);
					}
				})
				.show();

	}

	public static void showWifiAlert(final Activity activity){
		String value = "<html>Dear user,<br> Please can you enable the wifi to submit the dictation</a></b></font> </html>";
		new AlertDialog.Builder(activity)
				.setMessage(Html.fromHtml(value))
				.setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						activity.startActivity(intent);
					}
				})
				.show();

	}

	public static int getBitrate(String audioPath){
		MediaExtractor mediaExtractor = new MediaExtractor();
		try {
			mediaExtractor.setDataSource(audioPath);
			return mediaExtractor.getTrackFormat(0).getInteger("bit-per-sample");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}




	/**
     * Gets the state of Airplane Mode.
     *
     * @param context
     * @return true if enabled.
     */
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;

    }


	public static void showToast(Context context, String message) {
		Toast.makeText(context, Html.fromHtml(message), Toast.LENGTH_SHORT).show();
	}

	public static void showSnackBar(View view,String message){
		Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
		snackbar.show();

	}


	public static String renameAudioFile(String mAudioFileName, String newFileName, Context context) {

		File myDirectory = context.getExternalFilesDir("DictateAudios");
		if (!myDirectory.exists())
			myDirectory.mkdirs();

		String fileRenameTo = myDirectory + "/" + newFileName + ".wav";
		if (mAudioFileName != null && mAudioFileName.length() > 0) {
			File currentFile = new File(mAudioFileName);
			File newFile = new File(fileRenameTo);

			if (rename(currentFile, newFile)) {
				//Success
				Log.i("TAG===========", "Success");
				System.out.println("mRecordingThread.getFileName() ========> " + mAudioFileName);
				System.out.println("mRecordingThread.getFileName() ========> " + newFile.getAbsolutePath());
				mAudioFileName = newFile.getAbsolutePath();
				return mAudioFileName;
			} else {
				Log.i("General Utils =====", "Fail");
				return "";
			}
		} else {
			Log.i("General Utils TAG ====", "Fail Filename empty!");
		}

		return "";
	}

	private static boolean rename(File from, File to) {
		return from.getParentFile().exists() && from.exists() && from.renameTo(to);
	}


	public static void showProgressDialog(final Activity ctx) {
		if (ringProgressDialog == null) {
			ringProgressDialog =  ProgressDialog.show(ctx, null, null, true, false);
			ringProgressDialog.setContentView(R.layout.progress_dialog);
			//ringProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			ringProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			ringProgressDialog.setCancelable(false);
			ringProgressDialog.show();
		}
	}

	public static void dismissDialog() {
		if (ringProgressDialog != null) {
			if (ringProgressDialog.isShowing()) {
				ringProgressDialog.dismiss();
				ringProgressDialog = null;
			}
		}
	}

	public static void showTitleDialog(final Activity ctx,String title,String message) {
		if (ringProgressDialog == null) {
			ringProgressDialog =  ProgressDialog.show(ctx, title, message, true, false);
			ringProgressDialog.setCancelable(false);
			ringProgressDialog.show();
		}
	}


	/**
	 * @return Number of Mega bytes available on External storage
	 */
	public static long getAvailableSpaceInMB(){
		final long SIZE_KB = 1024L;
		final long SIZE_MB = SIZE_KB * SIZE_KB;
		long availableSpace = -1L;
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
		return availableSpace/SIZE_MB;
	}

	public static float getDeviceWidth(Context context){
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
		return dpWidth;
	}

    public static String setAudioFileName(String fileName){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDirectory = new File(root + "/DictateAudios");
        if(!myDirectory.exists())
            myDirectory.mkdirs();
        mFileName = new File(myDirectory + "/" + System.currentTimeMillis() + fileName).getAbsolutePath();
        return mFileName;
    }

	public static String setAudioFileName(Context context,String fileName){
		//String root = Environment.getExternalStorageDirectory().toString();
		//File myDirectory = new File(root + "/DictateAudios");
		File myDirectory = context.getExternalFilesDir("DictateAudios");
		if(!myDirectory.exists())
			myDirectory.mkdirs();
		mFileName = new File(myDirectory + "/" + System.currentTimeMillis() + fileName).getAbsolutePath();
		return mFileName;
	}

	public static String setConstantAudioFileName(Context context,String fileName){
		//String root = Environment.getExternalStorageDirectory().toString();
		//File myDirectory = new File(root + "/DictateAudios");
		File myDirectory = context.getExternalFilesDir("DictateAudios");
		if(!myDirectory.exists())
			myDirectory.mkdirs();
		mFileName = new File(myDirectory + "/" + fileName).getAbsolutePath();
		return mFileName;
	}

	public static void showNetworkError(Context context) {
        //title: 'No Internet Connection',
        //content: 'Sorry, no Internet connectivity detected. Please reconnect and try again.'
		Toast.makeText(context, "Sorry, No Internet connectivity detected. Please reconnect and try again.", Toast.LENGTH_SHORT).show();
    }



	public static String formatDate(Date date, String pattern){
		DateFormat df = new SimpleDateFormat(pattern, Locale.US);
		return df.format(date);
	}

	public static Date stringToDate(String strDate, String format, TimeZone timezone) {
		try {
			DateFormat formatter = new SimpleDateFormat(format, Locale.US);
			if (timezone != null) {
				formatter.setTimeZone(timezone);
			}
			Date date = formatter.parse(strDate);
			return date;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static Date stringToDate(String strDate, String format) {
		return stringToDate(strDate, format, null);
	}

	public static String generateUniqueStringFromDate(){
		return formatDate(new Date(), UNIQUE_DATE_PATTERN);
	}

	public static void sendEmail(Activity context, String subject, String content, String sender, int requestCode) {
		try {
			final Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { sender });
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
			emailIntent.putExtra(Intent.EXTRA_TEXT, content);
			context.startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."), requestCode);
		} catch (Exception t) {
			t.printStackTrace();
		}

	}

	public static void openBrowserIntent(Activity activity, String url){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivity(intent);
		}

	}

	 /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getAppVersionName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo == null? "" : packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

    public static String getAppPackageName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo == null? "" : packageInfo.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void shareApp(Activity context) {
		try {
			String packageName = getAppPackageName(context);
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String buidlHtmlData(String source, String fontName){
		String startTag;
		if(!TextUtils.isEmpty(fontName)){
			startTag = String.format(Locale.getDefault(), HTML_START, HTML_FONT_STYLE);
			startTag = String.format(Locale.getDefault(), startTag, fontName);
		}else{
			startTag = String.format(Locale.getDefault(), HTML_START, "");
		}
		return String.format(Locale.getDefault(), CONTENT_PATTERN, startTag, source, HTML_END);
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		// should check null because in air plan mode it will be null
		return netInfo != null && netInfo.isConnected();
	}

	public static void deleteLocalPath(Context context,String jobNumber) {
		File myDirectory = context.getExternalFilesDir("DictateAudios");
		if(!myDirectory.exists())
			myDirectory.mkdirs();
		String savedFile = myDirectory+"/"+jobNumber+".wav";
		deleteLocalFile(new File(savedFile));
	}

	public static void deleteLocalFile(File filePath){
		File fdelete = new File(filePath.getAbsolutePath());
		if (fdelete.exists()) {
			System.out.println("file Deleted :"+filePath.getAbsolutePath() );
			if (fdelete.delete()) {
				System.out.println("file Deleted :" );
			} else {
				System.out.println("file not Deleted :");
			}
		}
	}

	public static Map<String, List<String>> getQueryParams(String url, String schema) {
	    try {
	        Map<String, List<String>> params = new HashMap<String, List<String>>();
	        String[] urlParts = url.split(schema);
	        if (urlParts.length > 1) {
	            String query = urlParts[1];
	            for (String param : query.split("&")) {
	                String[] pair = param.split("=");
	                String key = URLDecoder.decode(pair[0], "UTF-8");
	                String value = "";
	                if (pair.length > 1) {
	                    value = URLDecoder.decode(pair[1], "UTF-8");
	                }

	                List<String> values = params.get(key);
	                if (values == null) {
	                    values = new ArrayList<String>();
	                    params.put(key, values);
	                }
	                values.add(value);
	            }
	        }

	        return params;
	    } catch (UnsupportedEncodingException ex) {
	        throw new AssertionError(ex);
	    }
	}


	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public static String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";

		// Convert total duration into time
		int hours = (int)( milliseconds / (1000*60*60));
		int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		// Add hours if there
		if(hours > 0){
			finalTimerString = hours + ":";
		}

		// Prepending 0 to seconds if it is one digit
		if(seconds < 10){
			secondsString = "0" + seconds;
		}else{
			secondsString = "" + seconds;}

		finalTimerString = finalTimerString + minutes + ":" + secondsString;

		// return timer string
		return finalTimerString;
	}

	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public static int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;

		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);

		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;

		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress -
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public static int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);

		// return current duration in milliseconds
		return currentDuration * 1000;
	}

	public static String getCurrentRecorderDuration(String audioPath) {


		File fExist = new File(audioPath);
		try {
			if (fExist.exists()) {
				String mediaPath = Uri.parse(audioPath).getPath();
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
				mmr.setDataSource(mediaPath);
				String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				System.out.println("Audio_duration : "+duration);
				mmr.release();
				return duration;
			}else{
				Log.d("seconds===========>", " Audio Not Exist Please check it...");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getCurrentDurationTime(String audioPath) {


		int durationInSeconds = 0;
		File fExist = new File(audioPath);
		try {
			if (fExist.exists()) {
				String mediaPath = Uri.parse(audioPath).getPath();
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
				mmr.setDataSource(mediaPath);
				String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				System.out.println("Audio_duration : "+duration);

				long dur = Long.parseLong(duration);
				String timeString = GeneralUtil.milliSecondsToTimer(dur);
				Log.d("GeneralUtils_Seconds=>", durationInSeconds+" Seconds"+"\n"+audioPath);
				mmr.release();
				return timeString;
			}else{
				Log.d("seconds===========>", " Audio Not Exist Please check it...");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getOnlyDuration(String audioPath) {

		File fExist = new File(audioPath);
		try {
			if (fExist.exists()) {
				String mediaPath = Uri.parse(audioPath).getPath();
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
				mmr.setDataSource(mediaPath);
				String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				System.out.println("Audio_duration : "+duration);

				long dur = Long.parseLong(duration);
				String timeString = milliSecondsToTimer(dur);
				mmr.release();
				return timeString;
			}else{
				Log.d("seconds===========>", " Audio Not Exist Please check it...");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public static JSONObject getDeviceInfoObject(Activity activity){

		/*** Getting Firebase Token to Enable Push Notification ***/
		String fireBaseToken = "";//FirebaseInstanceId.getInstance().getToken();
		System.out.println("Device_Token = "+fireBaseToken);
		fireBaseToken = "c60pWpo1lOg:APA91bFDEWqOVKUvC_tduXIzp9MOPGTbA7fogxAHmkJewQjcUQ3qZ-tZ83q7IETv_LOkED3ViN98n5FSftQWDuNr7ZG5A6Lqg83tN_TsO5e3p3cDuKIxqX8hwmbTr02i7w7M4NxNqZMn";

		/*WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		String macAddress = wifiManager.getConnectionInfo().getMacAddress();*/

		/*** Adding Device Info Details to JSON ***/
		JSONObject deviceInfoObject = new JSONObject();
		try {
			deviceInfoObject.put("DeviceType","Android");
			deviceInfoObject.put("DeviceVersion", Build.VERSION.RELEASE);
			deviceInfoObject.put("AppVersion", BuildConfig.VERSION_NAME);
			deviceInfoObject.put("DeviceName",Build.MANUFACTURER+" "+Build.MODEL);
			deviceInfoObject.put("DeviceToken",fireBaseToken);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return deviceInfoObject;
	}

	public static void multiClicks(){
		// Preventing multiple clicks, using threshold of 1 second
		if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
			return;
		}
		mLastClickTime = SystemClock.elapsedRealtime();
	}


	public static ArrayList<String> getMonths(){
		arrayListMonths = new ArrayList<String>();
		arrayListMonths.add("Birth Month");
		String[] months = new DateFormatSymbols().getMonths();
		for (int i = 0; i < months.length; i++) {
			String month = months[i];
			arrayListMonths .add(months[i]);
		}
		return arrayListMonths;
	}

	public static ArrayList<String> getArmedActive(){
		arrayListArmedActive = new ArrayList<String>();
		arrayListArmedActive.add("Active?");
		arrayListArmedActive.add("Yes");
		arrayListArmedActive.add("No");
		return arrayListArmedActive;
	}

	public static ArrayList<String> getAgeRanges(){
		arrayListAgeRanges = new ArrayList<String>();
		arrayListAgeRanges.add("Select Age Range");
		arrayListAgeRanges.add("18-25");
		arrayListAgeRanges.add("25-35");
		arrayListAgeRanges.add("35-45");
		arrayListAgeRanges.add("45-55");
		arrayListAgeRanges.add("55-65");
		arrayListAgeRanges.add("75+");
		return arrayListAgeRanges;
	}


	public static ArrayList<String> getSearchArmedActive(){
		arrayListArmedActive = new ArrayList<String>();
		arrayListArmedActive.add("Active?");
		arrayListArmedActive.add("Yes");
		arrayListArmedActive.add("No");
		arrayListArmedActive.add("Either");
		return arrayListArmedActive;
	}

	public static ArrayList<String> getGender(){
		arrayListGender = new ArrayList<String>();
		arrayListGender.add("Select Gender");
		arrayListGender.add("Male");
		arrayListGender.add("Female");
		return arrayListGender;
	}

	public static ArrayList<String> getSearchDialogGender(){
		arrayListGender = new ArrayList<String>();
		arrayListGender.add("Male");
		arrayListGender.add("Female");
		arrayListGender.add("Both");
		return arrayListGender;
	}

	public static String getUserCreationDate(String createdAt) {

		try {
			if (createdAt.contains("-")) {

                String[] createdAtSpilt = createdAt.split("-");
                createdAtYear = createdAtSpilt[0];
                createdAtMonth = createdAtSpilt[1];
                switch (createdAtMonth) {

                    case "01":
                        createdAtMonth = "Jan";
                        break;

                    case "02":
                        createdAtMonth = "Feb";
                        break;

                    case "03":
                        createdAtMonth = "Mar";
                        break;

                    case "04":
                        createdAtMonth = "Apr";
                        break;

                    case "05":
                        createdAtMonth = "May";
                        break;

                    case "06":
                        createdAtMonth = "Jun";
                        break;

                    case "07":
                        createdAtMonth = "July";
                        break;

                    case "08":
                        createdAtMonth = "Aug";
                        break;

                    case "09":
                        createdAtMonth = "Sep";
                        break;

                    case "10":
                        createdAtMonth = "Oct";
                        break;

                    case "11":
                        createdAtMonth = "Nov";
                        break;

                    case "12":
                        createdAtMonth = "Dec";
                        break;

                    default:
                        break;
                }

            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Member Since " + createdAtMonth + " " + createdAtYear;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	public static void setGridViewHeightBasedOnChildren(GridView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
		for (int i = 0; i < (listAdapter.getCount()/3); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if(listAdapter.getCount() % 3 == 0){
			params.height = totalHeight + ((listAdapter.getCount() - 1))+100;
		}else{
			params.height = totalHeight + ((listAdapter.getCount() - 1))+450;
		}
		listView.setLayoutParams(params);
		listView.requestLayout();
	}


	/**
	 * Checks if the device is a tablet or a phone
	 *
	 * @param activityContext
	 *            The Activity Context.
	 * @return Returns true if the device is a Tablet
	 */
	public static boolean isTabletDevice(Context activityContext) {
		// Verifies if the Generalized Size of the device is XLARGE to be
		// considered a Tablet
		return (activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean isTabletDeviceCopy(Context activityContext) {
		// Verifies if the Generalized Size of the device is XLARGE to be
		// considered a Tablet
		boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK) >=
				Configuration.SCREENLAYOUT_SIZE_XLARGE);

		// If XLarge, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (xlarge) {
			DisplayMetrics metrics = new DisplayMetrics();
			Activity activity = (Activity) activityContext;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
			// DENSITY_TV=213, DENSITY_XHIGH=320
			if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
					|| metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
					|| metrics.densityDpi == DisplayMetrics.DENSITY_TV
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XXXHIGH) {

				// Yes, this is a tablet!
				return true;
			}
		}

		// No, this is not a tablet!
		return false;
	}


}
