package jp.co.soliton.keymanager.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.*;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.R;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nguyenducdat on 4/12/2017.
 */

public class InfoDevice {

	public static final String NAME_FILE_INFO = "info.txt";
	String pathFileInfo = "";
	String newLine = "\n";

	private Context context;
	public static InfoDevice instance;
	public static InfoDevice getInstance(Context context) {
		if (instance == null) {
			synchronized (InfoDevice.class) {
				if (instance == null) {
					instance = new InfoDevice(context);
				}
			}
		}
		instance.context = context;
		return instance;
	}

	private InfoDevice(Context context) {
		pathFileInfo = context.getFilesDir().getPath() + File.separator + NAME_FILE_INFO;
	}

	public String getPathFileInfo() {
		return pathFileInfo;
	}

	public String createFileInfo() {
		StringBuilder outputDataBuilder = getInfoDevice();
		File fileInfo = new File(pathFileInfo);
		try {
			fileInfo.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveFileInfo(fileInfo, outputDataBuilder);
		return outputDataBuilder.toString();
	}

	private StringBuilder getInfoDevice() {
		final StringBuilder outputDataBuilder = new StringBuilder();
		// App Version 表示
		String appVer = context.getResources().getString(R.string.app_name) + " " + context.getResources().getString(R
				.string.main_versionname) + BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUM + (BuildConfig
				.BUILD_TYPE.equals("debug") ? "d" : BuildConfig.BUILD_TYPE.equals("trace") ? "t" : "");
		outputDataBuilder.append(appVer);
		outputDataBuilder.append(newLine);

		outputDataBuilder.append(makeInfoLine("System time", DateUtils.getCurrentDateSystem()));
		outputDataBuilder.append(makeInfoLine("IP address", getIPAddress()));
		outputDataBuilder.append(newLine);

		outputDataBuilder.append(makeInfoLine("BOARD", Build.BOARD));
		outputDataBuilder.append(makeInfoLine("HARDWARE", Build.HARDWARE));
		outputDataBuilder.append(makeInfoLine("MODEL", Build.MODEL));
		outputDataBuilder.append(makeInfoLine("PRODUCT", Build.PRODUCT));
		outputDataBuilder.append(makeInfoLine("VERSION CODENAME", Build.VERSION.CODENAME));
		outputDataBuilder.append(makeInfoLine("VERSION RELEASE", Build.VERSION.RELEASE));
		outputDataBuilder.append(makeInfoLine("VERSION SDK_INT", Build.VERSION.SDK_INT + ""));

		outputDataBuilder.append(newLine);
		outputDataBuilder.append(makeInfoLine("BUILD NUMBER", Build.DISPLAY));
		outputDataBuilder.append(makeInfoLine("KERNEL VERSION", newLine + getFormattedKernelVersion()));
		outputDataBuilder.append(makeInfoLine("Java VM Version", System.getProperty("java.vm.version")));

		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		outputDataBuilder.append(makeInfoLine("has Vibrator", String.valueOf(vibrator.hasVibrator())));

		outputDataBuilder.append(newLine);

		// メモリサイズ取得
		File dataFile = Environment.getDataDirectory();
		StatFs statFs = new StatFs(dataFile.getPath());
		long blockSize;
		long totalBlocks;
		long availableBlocks;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = statFs.getBlockSize();
			totalBlocks = statFs.getBlockCount();
			availableBlocks = statFs.getAvailableBlocks();
		} else {
			blockSize = statFs.getBlockSizeLong();
			totalBlocks = statFs.getBlockCountLong();
			availableBlocks = statFs.getAvailableBlocksLong();
		}


		final String total = Formatter.formatFileSize(context, blockSize * totalBlocks);
		final String free = Formatter.formatFileSize(context, blockSize * availableBlocks);

		outputDataBuilder.append(makeInfoLine("File System Size", total));  // 内部メモリサイズ
		outputDataBuilder.append(makeInfoLine("File System Free Size", free));     // 使用可能な内部メモリサイズ

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		outputDataBuilder.append(makeInfoLine("MemoryClass", sizeToFormat(activityManager.getMemoryClass())));
		outputDataBuilder.append(makeInfoLine("LargeMemoryClass", sizeToFormat(activityManager.getLargeMemoryClass())));

		Runtime runtime = Runtime.getRuntime();
		outputDataBuilder.append(makeInfoLine("totalMemory", sizeToFormat(runtime.totalMemory())));
		outputDataBuilder.append(makeInfoLine("freeMemory", sizeToFormat(runtime.freeMemory())));
		outputDataBuilder.append(makeInfoLine("usedMemory", sizeToFormat((runtime.totalMemory() - runtime.freeMemory()))));
		outputDataBuilder.append(makeInfoLine("maxMemory", sizeToFormat(runtime.maxMemory())));

		outputDataBuilder.append(makeInfoLine("NativeHeapSize", sizeToFormat(Debug.getNativeHeapSize())));
		outputDataBuilder.append(makeInfoLine("NativeHeapAlloc", sizeToFormat(Debug.getNativeHeapAllocatedSize())));
		outputDataBuilder.append(makeInfoLine("NativeHeapFree", sizeToFormat(Debug.getNativeHeapFreeSize())));

		// 画面データ関連
		WindowManager windowManager = ((Activity)context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		outputDataBuilder.append(newLine);
		outputDataBuilder.append(newLine);
		outputDataBuilder.append("----- Display -----");
		outputDataBuilder.append(newLine);

		outputDataBuilder.append(makeInfoLine("density", metrics.density + ""));
		outputDataBuilder.append(makeInfoLine("densityDpi", metrics.densityDpi + ""));
		outputDataBuilder.append(makeInfoLine("scaledDensity", metrics.scaledDensity + ""));
		outputDataBuilder.append(makeInfoLine("widthPixels", metrics.widthPixels + ""));
		outputDataBuilder.append(makeInfoLine("heightPixels", metrics.heightPixels + ""));
		outputDataBuilder.append(makeInfoLine("xdpi", metrics.xdpi + ""));
		outputDataBuilder.append(makeInfoLine("ydpi", metrics.ydpi + ""));

		outputDataBuilder.append(makeInfoLine("Density", getDensity(metrics.densityDpi)));

		outputDataBuilder.append(newLine);
		outputDataBuilder.append(newLine);
		return outputDataBuilder;
	}

	private String sizeToFormat(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + units[digitGroups];
	}

	private boolean saveFileInfo(File file, StringBuilder stringBuilder) {
		try{
			FileWriter fwriter = new FileWriter(file, false);// true to append // false to overwrite.
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write(stringBuilder.toString());
			bwriter.close();
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	private String makeInfoLine(String str1, String str2) {
		return str1 + " : " + str2 + "\n";
	}

	private String getIPAddress() {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						//boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						boolean isIPv4 = sAddr.indexOf(':')<0;
						if (isIPv4) {
							return sAddr;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	private String getFormattedKernelVersion() {
		String procVersionStr;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/version"), 256);
			procVersionStr = reader.readLine();
			final String PROC_VERSION_REGEX =
					"Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
							"\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
							"(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
							"(#\\d+) " +              /* group 3: "#1" */
							"(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
							"((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

			Pattern pattern = Pattern.compile(PROC_VERSION_REGEX);
			Matcher matcher = pattern.matcher(procVersionStr);

			if (!matcher.matches()) {
				return "Unavailable";
			} else if (matcher.groupCount() < 4) {
				return "Unavailable";
			} else {
				return (new StringBuffer(matcher.group(1)).append(newLine)
						.append(matcher.group(2)).append(" ")
						.append(matcher.group(3)).append(newLine)
						.append(matcher.group(4))
				).toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unavailable";
	}

	private String getDensity(int densityDpi) {
		String buf;
		switch (densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				buf = "DENSITY_LOW";
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				buf = "DENSITY_MEDIUM";
				break;
			case DisplayMetrics.DENSITY_HIGH:
				buf = "DENSITY_HIGH";
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				buf = "DENSITY_XHIGH";
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				buf = "DENSITY_XXHIGH";
				break;
			case DisplayMetrics.DENSITY_XXXHIGH:
				buf = "DENSITY_XXXHIGH";
				break;
			case DisplayMetrics.DENSITY_400:
				buf = "DENSITY_400";
				break;
			case DisplayMetrics.DENSITY_TV:
				buf = "DENSITY_TV";
				break;
			default:
				buf = "(not find type)";
				break;
		}
		return buf;
	}
}
