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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Allows an Android device to execute commands in a shell as the root user.<br>
 * IMPORTANT: Needs the device to be rooted.
 */
public class RootShell {
	protected Process process = null;
	protected DataOutputStream os = null;
	protected DataInputStream is = null;

	private boolean deviceRooted = false;

	/**
	 * RootShell class constructor. Checks if the device is rooted.
	 */
	public RootShell() {
		deviceRooted = checkRootStatus();
	}

	/**
	 * RootShell class destructor. Closes the shell if its not already closed.
	 */
	protected void finalize() {
		if (process != null)
			closeShell();
	}

	/**
	 * Opens a root shell and waits for commands.
	 * 
	 * @return 0 Everything went OK.<br>
	 *         -1 The shell has already been opened.<br>
	 *         -2 The device isn't rooted.<br>
	 *         -3 IOException when running the su command.
	 */
	public int openShell() {
		if (process == null) {
			if (deviceRooted) {
				// Trying to get root access.
				try {
					process = Runtime.getRuntime().exec("su");
				} catch (IOException e) {
					return -3;
				}

				// Getting an output stream to the root shell for introducing
				// commands.
				os = new DataOutputStream(process.getOutputStream());

				// Getting an input stream to the root shell for displaying
				// results.
				is = new DataInputStream(process.getInputStream());

				return 0;
			} else
				return -2;
		} else
			return -1;
	}

	/**
	 * Runs the command in the root shell.
	 * 
	 * @param command
	 *            The command which will be executed in the root shell.
	 * @return 0 Everything went OK.<br>
	 *         -1 The shell wasn't opened.<br>
	 *         -2 The device isn't rooted.<br>
	 *         -4 IOException when running the user command.<br>
	 *         -5 IOException when flushing the DataOutputStream.
	 */
	public int runCommand(String command) {
		if (process != null) {
			if (deviceRooted) {
				try {
					os.writeBytes(command + "\n");
				} catch (IOException e) {
					return -4;
				}

				try {
					os.flush();
				} catch (IOException e) {
					return -5;
				}
				return 0;
			} else
				return -2;
		} else
			return -1;
	}

	/**
	 * Closes a shell which is already open.
	 * 
	 * @return -1 The shell wasn't opened.<br>
	 *         -2 The device isn't rooted.<br>
	 *         -6 IOException when running the exit command.<br>
	 *         -7 InterruptedException when waiting for the process to stop.
	 */
	public int closeShell() {
		if (process != null) {

			if (deviceRooted) {
				try {
					os.writeBytes("exit\n");
				} catch (IOException e1) {
					return -6;
				}

				try {
					process.waitFor();
				} catch (InterruptedException e) {
					return -7;
				}

				process.destroy();

				process = null;
				os = null;
				is = null;

				return 0;
			} else
				return -2;
		} else
			return -1;

	}

	/**
	 * Checks if an Android device is rooted or not.<br>
	 * Code borrowed from: http://www
	 * .stealthcopter.com/blog/2010/01/android-requesting-root-access-in
	 * -your-app/
	 * 
	 * @return true: The device is rooted.<br>
	 *         false: The device isn't rooted.
	 */
	private static boolean checkRootStatus() {
		Process p;
		try {
			// Preform su to get root privileges
			p = Runtime.getRuntime().exec("su");

			// Attempt to write a file to a root-only
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				if (p.exitValue() != 255) {
					return true;
				} else {
					return false;
				}
			} catch (InterruptedException e) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * @return A DataInputStream to the root shell.
	 */
	public DataInputStream getInputStream() {
		return is;
	}

	/**
	 * @return A DataOutputStream to the root shell.
	 */
	public DataOutputStream getOutputStream() {
		return os;
	}

	/**
	 * @return true if the shell is opened.<br>
	 *         false if the shell isn't opened.
	 */
	public boolean getProcessStatus() {
		if (process != null)
			return true;
		else
			return false;
	}
}
