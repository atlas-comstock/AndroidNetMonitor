/**
 * This file is part of Shark.
 * 
 * Shark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Shark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Shark.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Sergio Jim�nez Feij�o (sergio.jf89@gmail.com)
 */

package com.example.yonghaohu.sniff.shark;

import java.io.File;

import android.os.Environment;

/**
 * Allows an Android app to manage files and folders.
 */
public abstract class FileManager {

	/**
	 * Checks if a file allocated in the external storage does exist.
	 * 
	 * @param dirname
	 *            Name of the directory allocated in the root of the external
	 *            storage
	 * @param filename
	 *            Name of the file allocated inside the directory.
	 * @return true if the file exist.<br>
	 *         false if the file doesn't exist.
	 */
	public static boolean checkFile(String dirname, String filename) {
		File file = new File(Environment.getExternalStorageDirectory(), dirname
				+ "/" + filename);
		if (file.exists())
			return true;
		else
			return false;
	}

	/**
	 * Checks if a directory does exist in the root of the external storage. If
	 * not the directory will be created.
	 * 
	 * @param dirname
	 *            Name of the directory allocated in the root of the external
	 *            storage to be checked.
	 * @return true if the directory was already created.<br>
	 *         false if the directory has been created.
	 */
	public static boolean checkDirectory(String dirname) {
		File file = new File(Environment.getExternalStorageDirectory(), dirname);
		if (file.exists()) {
			return true;
		} else {
			file.mkdirs();
			return false;
		}
	}
}
