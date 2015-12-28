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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows an Android device to get the name and status of all it's interfaces by
 * parsing the standard output of running "netcfg" command in a root shell.<br>
 * IMPORTANT: The device needs to be rooted.
 */
public class TCPdumpInterface {

	protected String ifName = null;
	protected boolean isUp = false;

	/**
	 * TCPdumpInterface class constructor. Sets the interface name.
	 * 
	 * @param ifName
	 *            The interface name.
	 */
	public TCPdumpInterface(String ifName) {
		setIfname(ifName);
	}

	/**
	 * TCPdump class constructor. Sets the interface name and it's status.
	 * 
	 * @param ifName
	 *            The interface name.
	 * @param isUp
	 *            The interface status.
	 */
	public TCPdumpInterface(String ifName, boolean isUp) {
		this(ifName);
		setStatus(isUp);
	}

	/**
	 * Interface name setter.
	 * 
	 * @param ifName
	 *            The interface name.
	 */
	public void setIfname(String ifName) {
		this.ifName = ifName;
	}

	/**
	 * Interface status setter.
	 * 
	 * @param isUp
	 *            The interface status.
	 */
	public void setStatus(boolean isUp) {
		this.isUp = isUp;
	}

	/**
	 * Interface name getter.
	 * 
	 * @return The interface name.
	 */
	public String getIfname() {
		return ifName;
	}

	/**
	 * Interface status getter.
	 * 
	 * @return The interface status.<br>
	 *         true if the interface is UP.<br>
	 *         false if the interface is DOWN.
	 */
	public boolean getStatus() {
		return isUp;
	}

	/**
	 * @param ifArray
	 *            The interface list.
	 * @return A String[] with the names of the interfaces.
	 */
	public static String[] getIfnames(List<TCPdumpInterface> ifArray) {
		String[] stringArray = new String[ifArray.size()];
		for (int i = 0; i < ifArray.size(); i++) {
			stringArray[i] = ifArray.get(i).getIfname();
		}
		return stringArray;
	}

	/**
	 * @param ifArray
	 *            The interface list.
	 * @return A boolean[] with the status of the interfaces.
	 */
	public static boolean[] getStatus(List<TCPdumpInterface> ifArray) {
		boolean[] booleanArray = new boolean[ifArray.size()];
		for (int i = 0; i < ifArray.size(); i++) {
			booleanArray[i] = ifArray.get(i).getStatus();
		}
		return booleanArray;
	}

	/**
	 * Calls netcfg command as root and parses the info about the interfaces.
	 * 
	 * @return A list with the interfaces available.
	 */
	public static List<TCPdumpInterface> listInterfaces() {

		// Creating and opening a new root shell.
		RootShell rootshell = new RootShell();
		if (rootshell.openShell() != 0) {
			return null;
		}

		// Running netcfg command.
		if (rootshell.runCommand("netcfg") != 0) {
			return null;
		}

		// Creating a buffer.
		byte[] buffer = new byte[4096];

		// Creating an int which will store the number of chars read.
		int bytesRead;

		// Reading to the buffer.
		try {
			bytesRead = rootshell.getInputStream().read(buffer);
		} catch (IOException e1) {
			return null;
		}

		// Closing the root shell.
		if (rootshell.closeShell() != 0) {
			return null;
		}

		// Casting from the buffer to a string.
		String stringBuffer = new String(buffer);

		// Parsing the string.

		// Creating an interface list.
		List<TCPdumpInterface> ifList = new ArrayList<TCPdumpInterface>();

		// Adding the "any" interface which is "virtual" and won't be
		// showed in netcfg's output.
		ifList.add(new TCPdumpInterface("any", true));

		// Creating an string and a boolean to store the info that will be
		// parsed.
		String ifName;
		boolean ifStatus;

		int i = 0;
		while (i < bytesRead) {

			ifName = null;
			ifStatus = false;

			int j = i;
			// The first character of the interface name is at i.

			// Now looking for the last character.
			while (stringBuffer.charAt(j) != ' ') {
				j++;
			}
			// The last character of the interface name is at j.

			// Extracting the interface name from chars between i and j.
			ifName = stringBuffer.substring(i, j);
			// Setting i to the last character evaluated.
			i = j;

			// Now looking for the interface status.
			while (stringBuffer.charAt(i) == ' ') {
				i++;
			}

			if (stringBuffer.charAt(i) == 'U') {
				// The interface is UP.
				ifStatus = true;
			}

			else if (stringBuffer.charAt(i) == 'D') {
				// The interface is DOWN.
				ifStatus = false;
			}

			ifList.add(new TCPdumpInterface(ifName, ifStatus));

			while (stringBuffer.charAt(i) != '\n') {
				i++;
			}
			i++;
		}
		return ifList;
	}

	/**
	 * @param ifStatus
	 *            The interface status. True if UP or false if DOWN.
	 * @return A list with the interfaces that have the selected status.
	 */
	public static List<TCPdumpInterface> listInterfaces(boolean ifStatus) {

		List<TCPdumpInterface> ifList;

		if ((ifList = listInterfaces()) == null) {
			return null;
		}

		List<TCPdumpInterface> resultList = new ArrayList<TCPdumpInterface>();

		for (int i = 0; i < ifList.size(); i++) {
			if (ifList.get(i).isUp == ifStatus) {
				resultList.add(ifList.get(i));
			}
		}

		return resultList;
	}
}
