package com.team.hbase.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
/**
 * 
 * @author 李晓伟
 * @Create_date 2014-7-7 上午11:35:00
 * @TODO 文件操作工具类(单例模式)
 */
public class FileUtil {
	private String mSDCardRoot;//外部存储设备的目录
	private String mSDState;//SD卡设备状态
	
	private static FileUtil mInstance;
	/**
	 * 
	 * @author 李晓伟
	 * 2014-7-7 上午11:41:56
	 * @return
	 * @TODO 获取实例化
	 */
	public static FileUtil getInstance(){
		if(mInstance==null){
			mInstance=new FileUtil();
		}
		return mInstance;
	}
	
	private FileUtil() {
		// 得到当前外部存储设备的目录
		mSDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;
		// 获取扩展SD卡设备状态
		mSDState = Environment.getExternalStorageState();
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param dir
	 *            目录路径
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createFileInSDCard(String dir, String fileName){
		File file = new File(mSDCardRoot + dir + File.separator + fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dir
	 *            目录路径
	 * @return
	 */
	public File createSDDir(String dir) {
		File dirFile = new File(mSDCardRoot + dir + File.separator);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		return dirFile;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param dir
	 *            目录路径
	 * @param fileName
	 *            文件名称
	 * @return
	 */
	public boolean isFileExist(String dir, String fileName) {
		File file = new File(mSDCardRoot + dir + File.separator + fileName);
		return file.exists();
	}

	/***
	 * 获取文件的路径
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public String getFilePath(String dir, String fileName) {
		return mSDCardRoot + dir + File.separator + fileName;
	}

	/***
	 * 获取SD卡的剩余容量,单位是Byte
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long getSDAvailableSize() {
		if (mSDState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			// 取得sdcard文件路径
			File pathFile = android.os.Environment
					.getExternalStorageDirectory();
			android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
			// 获取SDCard上每个block的SIZE
			long nBlocSize = statfs.getBlockSize();
			// 获取可供程序使用的Block的数量
			long nAvailaBlock = statfs.getAvailableBlocks();
			// 计算 SDCard 剩余大小Byte
			long nSDFreeSize = nAvailaBlock * nBlocSize;
			return nSDFreeSize;
		}
		return 0;
	}

	/**
	 * 将一个字节数组数据写入到SD卡中
	 */
	public boolean write2SD(String dir, String fileName, byte[] bytes) {
		if (bytes == null) {
			return false;
		}
		OutputStream output = null;
		try {
			// 拥有可读可写权限，并且有足够的容量
			if (mSDState.equals(android.os.Environment.MEDIA_MOUNTED)
					&& bytes.length < getSDAvailableSize()) {
				File file = null;
				createSDDir(dir);
				file = createFileInSDCard(dir, fileName);
				output = new BufferedOutputStream(new FileOutputStream(file));
				output.write(bytes);
				output.flush();
				return true;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/***
	 * 从sd卡中读取文件，并且以字节流返回
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public byte[] readFromSD(String dir, String fileName) {
		File file = new File(mSDCardRoot + dir + File.separator + fileName);
		if (!file.exists()) {
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return data;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中 ,从网络上读取图片
	 */
	public File write2SDFromInput(String dir, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			// 拥有可读可写权限，并且有足够的容量
			if (isCanWrite(input)) {
				createSDDir(dir);
				file = createFileInSDCard(dir, fileName);
				output = new BufferedOutputStream(new FileOutputStream(file));
				byte buffer[] = new byte[1024];
				int temp;
				while ((temp = input.read(buffer)) != -1) {
					output.write(buffer, 0, temp);
				}
				output.flush();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				if(input!=null){
					input.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-7-8 上午10:57:03
	 * @param input
	 * @return
	 * @throws IOException
	 * @TODO SD卡是否可写入
	 */
	public boolean isCanWrite(InputStream input) throws IOException{
		return mSDState.equals(android.os.Environment.MEDIA_MOUNTED)&& input.available() < getSDAvailableSize();
	}
	/**
	 * 
	 * @author 李晓伟
	 * 2014-8-22 上午10:02:46
	 * @param file
	 * @TODO 遍历删除文件夹下的文件
	 */
	public void deleteFile(File file){
		if (file.exists() == false) { 
			return; 
		} else { 
			if (file.isFile()) { 
				file.delete(); 
				return; 
			} 
		}
		if (file.isDirectory()) { 
			File[] childFile = file.listFiles(); 
			if (childFile == null || childFile.length == 0) { 
				file.delete(); 
				return; 
			} 
			for (File f : childFile) { 
				deleteFile(f); 
			} 
			file.delete(); 
		} 
	}
}
