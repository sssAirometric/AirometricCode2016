package com.airometric.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import com.airometric.config.Constants;

import android.os.Environment;

public class FileUtil {

	public static String CURRENT_MO_TESTTIME = "";
	public static String CURRENT_MT_TESTTIME = "";
	public static String CURRENT_EXT_TESTTIME = "";
	public static String CURRENT_FTP_TESTTIME = "";
	public static String CURRENT_UDP_TESTTIME = "";
	public static String CURRENT_PING_TESTTIME = "";
	public static String CURRENT_BROWSER_TESTTIME = "";
	public static String CURRENT_VOIP_TESTTIME = "";

	public static String APP_LOG_DIR = "";
	public static String MO_LOG_DIR = "";
	public static String MT_LOG_DIR = "";
	public static String EXT_LOG_DIR = "";
	public static String FTP_LOG_DIR = "";
	public static String UDP_LOG_DIR = "";
	public static String PING_LOG_DIR = "";
	public static String BROWSER_LOG_DIR = "";
	public static String VOIP_LOG_DIR = "";
	public static String APP_TMP_DIR = "";
	static {

	}

	public static void init() {
		try {
			File app_dir = new File(Environment.getExternalStorageDirectory(),
					Constants.APP_NAME);
			if (!app_dir.exists()) {
				app_dir.mkdirs();
				L.debug("APP folder does not exists and created...");
			}

			APP_LOG_DIR = app_dir.getAbsolutePath();

			// MO Folder
			MO_LOG_DIR = APP_LOG_DIR + "/MO/";
			File app_mo_dir = new File(MO_LOG_DIR);
			if (!app_mo_dir.exists()) {
				app_mo_dir.mkdirs();
				L.debug("MO folder does not exists and created...");
			} else
				L.debug("Cannot create MO folder");

			// MT Folder
			MT_LOG_DIR = APP_LOG_DIR + "/MT/";
			File app_mt_dir = new File(MT_LOG_DIR);
			if (!app_mt_dir.exists()) {
				app_mt_dir.mkdirs();
				L.debug("MT folder does not exists and created...");
			} else
				L.debug("Cannot create MT folder");

			// EXT Folder
			EXT_LOG_DIR = APP_LOG_DIR + "/EXT/";
			File app_ext_dir = new File(EXT_LOG_DIR);
			if (!app_ext_dir.exists()) {
				app_ext_dir.mkdirs();
				L.debug("EXT folder does not exists and created...");
			} else
				L.debug("Cannot create EXT folder");

			// FTP Folder
			FTP_LOG_DIR = APP_LOG_DIR + "/FTP/";
			File app_ftp_dir = new File(FTP_LOG_DIR);
			if (!app_ftp_dir.exists()) {
				app_ftp_dir.mkdirs();
				L.debug("FTP folder does not exists and created...");
			} else
				L.debug("Cannot create FTP folder");

			// UDP Folder
			UDP_LOG_DIR = APP_LOG_DIR + "/UDP/";
			File app_udp_dir = new File(UDP_LOG_DIR);
			if (!app_udp_dir.exists()) {
				app_udp_dir.mkdirs();
				L.debug("UDP folder does not exists and created...");
			} else
				L.debug("Cannot create UDP folder");

			// PING Folder
			PING_LOG_DIR = APP_LOG_DIR + "/PING/";
			File app_ping_dir = new File(PING_LOG_DIR);
			if (!app_ping_dir.exists()) {
				app_ping_dir.mkdirs();
				L.debug("PING folder does not exists and created...");
			} else
				L.debug("Cannot create PING folder");

			// Browser Folder
			BROWSER_LOG_DIR = APP_LOG_DIR + "/BROWSER/";
			File app_browser_dir = new File(BROWSER_LOG_DIR);
			if (!app_browser_dir.exists()) {
				app_browser_dir.mkdirs();
				L.debug("BROWSER folder does not exists and created...");
			} else
				L.debug("Cannot create BROWSER folder");

			// VOIP Folder
			VOIP_LOG_DIR = APP_LOG_DIR + "/VOIP/";
			File app_voip_dir = new File(VOIP_LOG_DIR);
			if (!app_voip_dir.exists()) {
				app_voip_dir.mkdirs();
				L.debug("VOIP folder does not exists and created...");
			} else
				L.debug("Cannot create VOIP folder");

			// TMP Folder
			APP_TMP_DIR = APP_LOG_DIR + "/TMP/";
			File app_tmp_dir = new File(APP_TMP_DIR);
			if (!app_tmp_dir.exists()) {
				app_tmp_dir.mkdirs();
				L.debug("TMP folder does not exists and created...");
			} else
				L.debug("Cannot create TMP folder");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String writeToFile(String file_path, String msg) {
		String sFilePath = null;
		try {

			sFilePath = file_path;
			File file = new File(sFilePath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file.getAbsoluteFile(), true)));
			out.println(msg);
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return sFilePath;
	}

	public static String writeToXMLFile(String file_path, String msg) {
		String sFilePath = null;
		try {

			sFilePath = file_path;
			File file = new File(sFilePath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file.getAbsoluteFile(), true)));
			out.println(msg);
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return sFilePath;
	}

	public static String writeToSDFile(String file_path, String msg) {
		String sFilePath = null;
		try {

			L.debug("writeToSDFile " + file_path);

			sFilePath = file_path;

			File file = new File(sFilePath);

			file.createNewFile();

			 L.debug("outFileName " + sFilePath);

			OutputStream myOutput = new FileOutputStream(sFilePath);

			byte[] buffer = new byte[1024];
			int length;

			InputStream myInput = new DataInputStream(new ByteArrayInputStream(
					msg.getBytes()));
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myInput.close();

			myOutput.flush();
			myOutput.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return sFilePath;
	}

	public static String writeToLogcatFile(String file_path, String msg,
			String sTag) {
		String sFilePath = null;
		try {

			String sLogContentLine = "";
			sLogContentLine = TimeUtil.getCurrentDateAndTime();
			sLogContentLine += " " + sTag;
			sLogContentLine += " " + msg;

			// L.debug("writeToLogcatFile():: sLogContentLine -> "
			// + sLogContentLine);
			sFilePath = file_path;
			File file = new File(sFilePath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file.getAbsoluteFile(), true)));
			out.println(sLogContentLine);
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return sFilePath;
	}

	public static String writeToLogcatFile1(String file_path, String msg,
			String sTag) {
		String sFilePath = null;
		try {

			// L.debug("writeToLogcatFile():: file_path -> " + file_path);

			String sLogContentLine = "";
			sLogContentLine = TimeUtil.getCurrentDateAndTime();
			sLogContentLine += " " + sTag;
			sLogContentLine += " " + msg;

			L.debug("writeToLogcatFile():: sLogContentLine -> "
					+ sLogContentLine);

			sFilePath = file_path;

			File file = new File(sFilePath);

			file.createNewFile();

			OutputStream myOutput = new FileOutputStream(sFilePath);

			byte[] buffer = new byte[1024];
			int length;

			InputStream myInput = new DataInputStream(new ByteArrayInputStream(
					msg.getBytes()));
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myInput.close();

			myOutput.flush();
			myOutput.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return sFilePath;
	}

	public static String readFile(String sFilePath) {
		String sFileContent = "";
		// Get the text file
		File file = new File(sFilePath);

		if (file.exists()) // check if file exist
		{
			// Read text from file
			StringBuilder text = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
				}
			} catch (IOException e) {
			}
			// Set the text
			sFileContent = text.toString();
		} else {
			sFileContent = null;
		}

		return sFileContent;
	}

	public static void clearDirectory(File fileOrDirectory) {

		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				clearDirectory(child);
		if (fileOrDirectory.isFile())
			fileOrDirectory.delete();
	}

	public static void delete(String filepath) {
		File file = new File(filepath);
		if (file.exists())
			file.delete();
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}
}
