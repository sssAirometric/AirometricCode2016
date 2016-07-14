package com.airometric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.dialogs.AppAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.runners.TestUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsActivity extends FragmentActivity {

	
	// Google Map
		private GoogleMap googleMap;
		private String sIMEI,sModel,sManufacturer,sCalldropTime;  
		private HashMap<String,String> data;
		int i,resid;
		int signalStrength;
		private TextView txtTitle;
		private Button btnLogout;
		private Preferences pref;
		Button share;
		private ImageView imgGood,imgAvg,imgBad,imgCallDrop;
		//private Context context = this.getApplicationContext();
		private AppActivity aa = new AppActivity();
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//setContentView(R.layout.activity_google_maps);
			pref = new Preferences(this);
			setAppTitle(R.layout.activity_google_maps);
			imgGood = (ImageView)findViewById(R.id.imgGood);
			imgAvg = (ImageView)findViewById(R.id.imgAvg);
			imgBad = (ImageView)findViewById(R.id.imgBad);
			imgCallDrop = (ImageView)findViewById(R.id.imgCallDrop);
			
			
			try {
			data = pref.loadDeviceInfoData(getApplicationContext());
			//Log.i("call Drops",.toString());
			sCalldropTime = pref.loadCallDropTimeData(getApplicationContext());
			//Log.i("GooglemapsActivity page data",data.get("datalength")+","+data);
			sIMEI = data.get("imei");
			sModel = data.get("model");
			sManufacturer = data.get("manufacturer");
			data.get("phoneNumber");
			data.get("phonetype");
			data.get("version");
			resid = R.drawable.mm_20_blue;
			int size = Integer.valueOf(data.get("datalength"));
			double latitude[] = new double[size];
			double longitude[]= new double[size];
			int mcc[] = new int[size];
			int mnc[]= new int[size];
			String[] DItimestamp = new String[size];
			//get Call drop timestamp 
			String[] callDroptimestamp = sCalldropTime.split(",");
			
			ArrayList<Integer> commontimestamp=new ArrayList<Integer>();
			for(int j=0; j<size; j++)
			{
				//if(!data.get("lat"+j).equals("0.0")){
				latitude[j] = Double.parseDouble(data.get("lat"+j));
				//	}
				//if(!data.get("lan"+j).equals("0.0")){
				longitude[j] = Double.parseDouble(data.get("lan"+j));
				if(data.get("mcc"+j).equals(null)) mcc[j]=0; else mcc[j] = Integer.parseInt(data.get("mcc"+j));
				if(data.get("mnc"+j).equals(null)) mnc[j]=0; else mnc[j] = Integer.parseInt(data.get("mnc"+j));
				DItimestamp[j] = data.get("timestamp"+j);
				
				for(int i=1;i<callDroptimestamp.length;i++)
				{ //System.out.println("in for loop for calldroptimes "+ callDroptimestamp[i]);
					if(closeCallTimestamp(data.get("timestamp"+j),callDroptimestamp[i]))
					{//System.out.println("in if loop for calldroptimes ");
					commontimestamp.add(j);
					}
				}
			}
			System.out.println("commontimestamp "+commontimestamp.toString());
			
			// Loading map
				initilizeMap();
				// Changing map type
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				
				// Showing / hiding your current location
				googleMap.setMyLocationEnabled(true);

				// Enable / Disable zooming controls
				googleMap.getUiSettings().setZoomControlsEnabled(false);

				// Enable / Disable my location button
				googleMap.getUiSettings().setMyLocationButtonEnabled(true);

				// Enable / Disable Compass icon
				googleMap.getUiSettings().setCompassEnabled(true);

				// Enable / Disable Rotate gesture
				googleMap.getUiSettings().setRotateGesturesEnabled(true);

				// Enable / Disable zooming functionality
				googleMap.getUiSettings().setZoomGesturesEnabled(true);

				
				// lets place some 10 random markers
				for (int a = 1; a < size; a++) {
					imgCallDrop.setImageResource(R.drawable.dropcallmarker);
					// Adding a marker
					MarkerOptions marker = new MarkerOptions().position(
								new LatLng(latitude[a], longitude[a]));
							//.title("Hello Maps " + a);
					//System.out.println("commontimestamp length "+commontimestamp.length+" "+a +" size "+size);
					
						if(data.get("networktype"+a).equals("wifi"))
						{
							imgGood.setImageResource(R.drawable.wifi_good2);
							imgAvg.setImageResource(R.drawable.wifi_avg2);
							imgBad.setImageResource(R.drawable.wifi_bad2);
							
							if(Integer.valueOf(data.get("rssi"+a)) >= Constants.goodLower_default 
									&& Integer.valueOf(data.get("rssi"+a)) <= Constants.goodUpper_default)
								{
								resid = R.drawable.wifi_good2;
								}
							if(Integer.valueOf(data.get("rssi"+a)) >= Constants.avgLower_default 
									&& Integer.valueOf(data.get("rssi"+a)) <= Constants.avgUpper_default)
							{
							resid = R.drawable.wifi_avg2;
							}
							if(Integer.valueOf(data.get("rssi"+a)) >= Constants.badLower_default 
									&& Integer.valueOf(data.get("rssi"+a)) <= Constants.badUpper_default)
							{
							resid = R.drawable.wifi_bad2;
							}
							
						}
						else if(data.get("networktype"+a).equals("EDGE (2.75G)"))
						{
							imgGood.setImageResource(R.drawable.g2_good2);
							imgAvg.setImageResource(R.drawable.g2_avg2);
							imgBad.setImageResource(R.drawable.g2_bad2);
							
							if(data.get("phonetype").equals("GSM"))
							{
							signalStrength = (Integer.valueOf(data.get("gsmsignalstrength"+a))*Constants.SIGNALSTRENGTH_GSM1)
															+Constants.SIGNALSTRENGTH_GSM;
							if(signalStrength >= Constants.goodLower_default 
									&& signalStrength <= Constants.goodUpper_default)
								resid = R.drawable.g2_good2;
							if(signalStrength >= Constants.avgLower_default 
									&& signalStrength <= Constants.avgUpper_default)
								resid = R.drawable.g2_avg2;
							if(signalStrength >= Constants.badLower_default 
									&& signalStrength <= Constants.badUpper_default)
								resid = R.drawable.g2_bad2;
							}
							else if(data.get("phonetype").equals("CDMA"))
							{
							signalStrength = (Integer.valueOf(data.get("cdmadbm"+a))*Constants.SIGNALSTRENGTH_GSM1)
										+Constants.SIGNALSTRENGTH_GSM;
							
							if(signalStrength >= Constants.goodLower_default 
									&& signalStrength <= Constants.goodUpper_default)
								resid = R.drawable.g2_plus_good2;
							if(signalStrength >= Constants.avgLower_default 
									&& signalStrength <= Constants.avgUpper_default)
								resid = R.drawable.g2_plus_avg2;
							if(signalStrength >= Constants.badLower_default 
									&& signalStrength <= Constants.badUpper_default)
								resid = R.drawable.g2_plus_bad2;
							
							}
						}
						else if(data.get("networktype"+a).equals("UMTS (3G)") 
								|| data.get("networktype"+a).equals("HSPA (3G - Transitional)") 
								|| data.get("networktype"+a).equals("HSDPA (3G - Transitional)"))
						{
							imgGood.setImageResource(R.drawable.g3_good2);
							imgAvg.setImageResource(R.drawable.g3_avg2);
							imgBad.setImageResource(R.drawable.g3_bad2);
							
							if(Integer.valueOf(data.get("LteRsrp"+a)) >= Constants.goodLower_default 
									&& Integer.valueOf(data.get("LteRsrp"+a)) <= Constants.goodUpper_default)
								resid = R.drawable.g3_good2;
							if(Integer.valueOf(data.get("LteRsrp"+a)) <= Constants.avgLower_default 
									&& Integer.valueOf(data.get("LteRsrp"+a)) >= Constants.avgUpper_default)
								resid = R.drawable.g3_avg2;
							if(Integer.valueOf(data.get("LteRsrp"+a)) <= Constants.badLower_default 
									&& Integer.valueOf(data.get("LteRsrp"+a)) >= Constants.badUpper_default)
								resid = R.drawable.g3_bad2;
							
						}
						else if(data.get("networktype"+a).equals("LTE (4G)") 
								|| data.get("networktype"+a).equals(" LTE (4G)") 
								|| data.get("networktype"+a).equals("LTE"))
						{
							imgGood.setImageResource(R.drawable.l_good2);
							imgAvg.setImageResource(R.drawable.l_avg2);
							imgBad.setImageResource(R.drawable.l_bad2);
							
							if(Integer.valueOf(data.get("LteRsrp"+a)) <= Constants.goodLower_Ltersrp 
									&& Integer.valueOf(data.get("LteRsrp"+a)) >= Constants.goodUpper_Ltersrp)
								resid = R.drawable.l_good2;
							if(Integer.valueOf(data.get("LteRsrp"+a)) <= Constants.avgLower_Ltersrp 
									&& Integer.valueOf(data.get("LteRsrp"+a)) >= Constants.avgUpper_Ltersrp)
								resid = R.drawable.l_avg2;
							if(Integer.valueOf(data.get("LteRsrp"+a)) <= Constants.badLower_Ltersrp 
									&& Integer.valueOf(data.get("LteRsrp"+a)) >= Constants.badUpper_Ltersrp)
								resid = R.drawable.l_bad2;
							
						}
					if(commontimestamp.contains(a))
						{
							resid = R.drawable.dropcallmarker;
						}
					// changing marker color
					marker.icon(BitmapDescriptorFactory.fromResource(resid));
					
					googleMap.addMarker(marker);

					// Move the camera to last position with a zoom level
					CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(new LatLng(latitude[0],
										longitude[0])).zoom(15).build();
					googleMap.animateCamera(CameraUpdateFactory
								.newCameraPosition(cameraPosition));
					
					
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onResume() {
			super.onResume();
			initilizeMap();
		}

		/**
		 * function to load map If map is not created it will create it for you
		 * */
		private void initilizeMap() {
			if (googleMap == null) {
				googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
						R.id.map)).getMap();
				 // Setting a custom info window adapter for the google map
		        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
		 
		            // Use default InfoWindow frame
		            @Override
		            public View getInfoWindow(Marker arg0) {
		                return null;
		            }
		 
		            // Defines the contents of the InfoWindow
		            @Override
		            public View getInfoContents(Marker arg0) {
		            	int markerid = Integer.valueOf(arg0.getId().substring(1))+1;
		            	// Getting view from the layout file info_window_layout
		                View v = getLayoutInflater().inflate(R.layout.info_window, null);
		                //System.out.println("marker id-"+markerid);
		                //System.out.println("marker id with data-"+data.get("lat"+markerid));
		                
		                // Getting the position from the marker
		                TextView tvIMEI = (TextView) v.findViewById(R.id.tv_IMEI);
		                TextView tvModel = (TextView) v.findViewById(R.id.tv_Model);
		                TextView tvManuf = (TextView) v.findViewById(R.id.tv_manufacturer);
		                TextView tvTime = (TextView) v.findViewById(R.id.tv_testTime);
		                //TextView tvIMEI = (TextView) v.findViewById(R.id.IMEI_value);
		                
		                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
		                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
		                
		                //Added on 21/06/16
		                TextView tvMcc = (TextView) v.findViewById(R.id.tv_mcc);
		                TextView tvMnc = (TextView) v.findViewById(R.id.tv_mnc);
		                
		                // Setting the latitude
		                tvLat.setText(data.get("lat"+markerid));
		 
		                // Setting the longitude
		                tvLng.setText(data.get("lan"+markerid));
		                
		                // Setting the mcc
		                tvMcc.setText(data.get("mcc"+markerid));
		 
		                // Setting the mnc
		                tvMnc.setText(data.get("mnc"+markerid));
		                
		                
		                tvIMEI.setText(sIMEI);
		                tvModel.setText(sModel);
		                tvManuf.setText(sManufacturer);
		                tvTime.setText(data.get("timestamp"+markerid));
		                // Returning the view containing InfoWindow contents
		                return v;
		 
		            }
		        });
				// check if map is created successfully or not
				if (googleMap == null) {
					Toast.makeText(getApplicationContext(),
							"Sorry! unable to create maps", Toast.LENGTH_SHORT)
							.show();
				}
				
			}
		}

		protected void setAppTitle(int layoutID) {

			try {
				requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
				setContentView(layoutID);
				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
						R.layout.app_titlebar);

			} catch (Exception e) {
				e.printStackTrace();
			}

			txtTitle = (TextView) findViewById(R.id.txtviewTitle);
			txtTitle.setText(Constants.APP_NAME);
			share = (Button) findViewById(R.id.share);
			btnLogout = (Button) findViewById(R.id.btnLogout);
			
			
			String strClassName = getClass().getSimpleName();
			if (strClassName.equalsIgnoreCase(LoginActivity.class.getSimpleName())
					|| strClassName.equalsIgnoreCase(ForgotPasswordActivity.class
							.getSimpleName())) {
				btnLogout.setVisibility(View.GONE);
			} else {
				btnLogout.setVisibility(View.VISIBLE);
				btnLogout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						doLogout();
					}
				});
			}
			share.setVisibility(View.VISIBLE);
			share.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					takeScreenshot();
				}
			});
			// doApplyFont();
		}
		void doLogout() {

			if (pref.isTestRunning()) {
				new AppAlert(this, Messages.LOGOUT_WHILE_TEST_RUNNING, true) {
					@Override
					public void okClickListener() {
						doStopTest();
						pref.setLoggedInStatus(false);					
						pref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, false);
						pref.emptyDeviceInfoData(getContext());
						pref.emptyCallDropTimeData(getContext());
						NotificationUtil.cancelAllNotification(getContext());
						Intent i = new Intent(GoogleMapsActivity.this, LoginActivity.class);
						startActivity(i);
						finish();
					}
				};
			} else {
				new AppAlert(this, Messages.MSG_LOGOUT, true) {
					@Override
					public void okClickListener() {
						pref.setLoggedInStatus(false);
						pref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, false);
						pref.putValue(Preferences.KEY_TEST_NAME, "");
						pref.putValue(Preferences.KEY_SELECTED_MARKET_PALCE, "");
						pref.emptyDeviceInfoData(getContext());
						pref.emptyCallDropTimeData(getContext());
						NotificationUtil.cancelAllNotification(getContext());
						
						// should clear user name and password from preference
						Intent i = new Intent(GoogleMapsActivity.this, LoginActivity.class);
						startActivity(i);
						finish();
					}
				};
			}
		}

		protected void doStopTest() {

			pref.setAsTestCanceled(true);
			
			TestUtil test_util = new TestUtil(aa.activity);
			test_util.stopTestForcely();
		}
		public void takeScreenshot() {
			 SnapshotReadyCallback callback = new SnapshotReadyCallback() 
		        {

		            private Bitmap bitmap;

					@Override
		            public void onSnapshotReady(Bitmap snapshot) 
		            {
		                // TODO Auto-generated method stub
		                bitmap = snapshot;

		                OutputStream fout = null;

		                String filePath = System.currentTimeMillis() + ".jpeg";

		                try 
		                {
		                    fout = openFileOutput(filePath,
		                            MODE_WORLD_READABLE);

		                    // Write the string to the file
		                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
		                    fout.flush();
		                    fout.close();
		                } 
		                catch (FileNotFoundException e) 
		                {
		                    // TODO Auto-generated catch block
		                    Log.d("ImageCapture", "FileNotFoundException");
		                    Log.d("ImageCapture", e.getMessage());
		                    filePath = "";
		                } 
		                catch (IOException e) 
		                {
		                    // TODO Auto-generated catch block
		                    Log.d("ImageCapture", "IOException");
		                    Log.d("ImageCapture", e.getMessage());
		                    filePath = "";
		                }

		                openShareImageDialog(filePath);
		            }
		        };

		        googleMap.snapshot(callback);
			   /*View rootView = this.;
			   rootView.setDrawingCacheEnabled(true);
			   return rootView.getDrawingCache();*/
			}
		public void openShareImageDialog(String filePath) 
		{
		File file = this.getFileStreamPath(filePath);

		if(!filePath.equals(""))
		{
		    final ContentValues values = new ContentValues(2);
		    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
		    final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		    intent.setType("image/jpeg");
		    intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
		    startActivity(Intent.createChooser(intent, "Share Image"));
		}
		else
		{
		            //This is a custom class I use to show dialogs...simply replace this with whatever you want to show an error message, Toast, etc.
			Toast.makeText(this, "Image capture failed!!!", Toast.LENGTH_LONG).show();
		}
		}
		
		@Override
		public void onBackPressed() {
			doLogout();
			/*Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("EXIT", true);
			startActivity(intent);
			finish();
			NotificationUtil.cancelAllNotification(this);
			System.exit(0);*/
			//new ExitAlert(this);
		}
		public static boolean closeCallTimestamp(String starttime, String Endtime) 
		{
				  boolean status = false;
				  
				  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				  SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				  Date d1 = null;
				  Date d2 = null;
				  int seconds = 0;
				  try {
				   d1 = format.parse(starttime);
				   d2 = format.parse(Endtime);
//				   System.out.println("starttime-"+starttime+",,,,,Endtime-"+Endtime);
				   long diff = d1.getTime() - d2.getTime();
				   long diffMilliSeconds = diff / 1000 % 60 % 60;
				   long diffSeconds = diff / 1000 % 60;
				   long diffMinutes = diff / (60 * 1000) % 60;
				   long diffHours = diff / (60 * 60 *  1000) % 24;
				   long diffDays = diff / (24 * 60 * 60 * 1000);
				  
				   
//					System.out.println("d-h-m-s diff by ank - "+diffDays + " days, "+diffHours + " hours, "+diffMinutes + " minutes, "+diffSeconds + " seconds.");

				   seconds = Math.abs((int) diffSeconds + (int) (60 * diffMinutes)+(int)(diffHours*60*60));
				  } catch (Exception e) {
				   e.printStackTrace();
				  }
				  if (seconds >= -5 && seconds <= 15) {
				    status = true;
				   }
				  return status;
		}

		
}
