package com.airometric.utility;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FTP {
	private static final String TAG = "FTP";
	public static FTPClient mFTPClient = null;
	Handler progressHandler = null;

	public FTP(Handler handler) {
		progressHandler = handler;
		
	}

	// Method to connect to FTP server:
	public boolean ftpConnect(String host, String username, String password,
			int port) {
		try {
			mFTPClient = new FTPClient();

			mFTPClient.connect(host, port);
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				boolean status = mFTPClient.login(username, password);
				mFTPClient.enterLocalPassiveMode();
				mFTPClient
						.setFileTransferMode(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

				return status;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Error: could not connect to host " + host);
		}

		return false;
	}

	public FTPClient getClient() {
		return mFTPClient;
	}

	// Method to disconnect from FTP server:
	public boolean ftpDisconnect() {
		try {
			mFTPClient.logout();
			mFTPClient.disconnect();
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error occurred while disconnecting from ftp server.");
		}

		return false;
	}

	// Method to get current working directory:

	public String ftpGetCurrentWorkingDirectory() {
		try {
			String workingDir = mFTPClient.printWorkingDirectory();
			return workingDir;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not get current working directory.");
		}

		return null;
	}

	// Method to change working directory:

	public boolean ftpChangeDirectory(String directory_path) {
		try {
			mFTPClient.changeWorkingDirectory(directory_path);
		} catch (Exception e) {
			Log.d(TAG, "Error: could not change directory to " + directory_path);
		}

		return false;
	}

	// Method to list all files in a directory:

	public void ftpPrintFilesList(String dir_path) {
		try {
			FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
			int length = ftpFiles.length;

			for (int i = 0; i < length; i++) {
				String name = ftpFiles[i].getName();
				boolean isFile = ftpFiles[i].isFile();

				if (isFile) {
					Log.i(TAG, "File : " + name);
				} else {
					Log.i(TAG, "Directory : " + name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to create new directory:

	public boolean ftpMakeDirectory(String new_dir_path) {
		try {
			boolean status = mFTPClient.makeDirectory(new_dir_path);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not create new directory named "
					+ new_dir_path);
		}

		return false;
	}

	// Method to delete/remove a directory:

	public boolean ftpRemoveDirectory(String dir_path) {
		try {
			boolean status = mFTPClient.removeDirectory(dir_path);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not remove directory named " + dir_path);
		}

		return false;
	}

	// Method to delete a file:

	public boolean ftpRemoveFile(String filePath) {
		try {
			boolean status = mFTPClient.deleteFile(filePath);
			return status;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// Method to rename a file:

	public boolean ftpRenameFile(String from, String to) {
		try {
			boolean status = mFTPClient.rename(from, to);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Could not rename file: " + from + " to: " + to);
		}

		return false;
	}

	// Method to download a file from FTP server:
	public boolean ftpDownload(String srcFilePath, String desFilePath) {
		boolean status = false;
		try {
			FileOutputStream desFileStream = new FileOutputStream(desFilePath);
			status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
			desFileStream.close();

			return status;
		} catch (Exception e) {
			Log.d(TAG, "download failed");
		}

		return status;
	}

	// Method to upload a file to FTP server:
	public boolean ftpUpload1(String srcFilePath, String desFileName,
			String desDirectory) {
		boolean status = false;
		try {
			FileInputStream srcFileStream = new FileInputStream(srcFilePath);

			// change working directory to the destination directory
			if (ftpChangeDirectory(desDirectory)) {
				status = mFTPClient.storeFile(desFileName, srcFileStream);
			}

			srcFileStream.close();
			return status;
		} catch (Exception e) {
			Log.d(TAG, "upload failed");
		}

		return status;
	}

	// Method to upload a file to FTP server:
	public boolean ftpUpload(String srcFilePath, String desFileName,
			String desDirectory) {
		boolean status = false;
		try {
			mFTPClient.changeWorkingDirectory(desDirectory);

			if (mFTPClient.getReplyString().contains("250")) {
				mFTPClient
						.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
				BufferedInputStream buffIn = null;
				buffIn = new BufferedInputStream(new FileInputStream(
						srcFilePath));
				mFTPClient.enterLocalPassiveMode();
				// FileInputStream srcFileStream = new
				// FileInputStream(srcFilePath);
				ProgressInputStream progressInput = new ProgressInputStream(
						buffIn, progressHandler);

				status = mFTPClient.storeFile(desFileName, progressInput);
				buffIn.close();
			}
			return status;
		} catch (Exception e) {
			Log.d(TAG, "upload failed");
		}

		return status;
	}

	class ProgressInputStream extends InputStream {

		/* Key to retrieve progress value from message bundle passed to handler */
		public static final String PROGRESS_UPDATE = "progress_update";

		private static final int TEN_KILOBYTES = 1024 * 10;

		private InputStream inputStream;
		private Handler handler;

		private long progress;
		private long lastUpdate;

		private boolean closed;

		public ProgressInputStream(InputStream inputStream, Handler handler) {
			this.inputStream = inputStream;
			this.handler = handler;

			this.progress = 0;
			this.lastUpdate = 0;

			this.closed = false;
		}

		@Override
		public int read() throws IOException {
			int count = inputStream.read();
			return incrementCounterAndUpdateDisplay(count);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int count = inputStream.read(b, off, len);
			return incrementCounterAndUpdateDisplay(count);
		}

		@Override
		public void close() throws IOException {
			super.close();
			if (closed)
				throw new IOException("already closed");
			closed = true;
		}

		private int incrementCounterAndUpdateDisplay(int count) {
			if (count > 0)
				progress += count;
			lastUpdate = maybeUpdateDisplay(progress, lastUpdate);
			return count;
		}

		private long maybeUpdateDisplay(long progress, long lastUpdate) {
			if (progress - lastUpdate > TEN_KILOBYTES) {
				lastUpdate = progress;
				sendLong(PROGRESS_UPDATE, progress);
			}
			return lastUpdate;
		}

		public void sendLong(String key, long value) {
			Bundle data = new Bundle();
			data.putLong(key, value);

			Message message = Message.obtain();
			message.setData(data);
			handler.sendMessage(message);
		}
	}

	public String getWorkingDir() {
		try {
			return mFTPClient.printWorkingDirectory();
		} catch (IOException e) {
			return null;
		}
	}

	public String getServerError() {
		try {
			return mFTPClient.getReplyString();
		} catch (Exception e) {
			return null;
		}
	}
}