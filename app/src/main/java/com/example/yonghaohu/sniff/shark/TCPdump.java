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

/**
 * Allows an Android app to run a TCPdump process as the root user.<br>
 * IMPORTANT: Needs the device to be rooted.
 */
public class TCPdump extends RootShell {

	protected static final String tcpdumpBinaryPath = "/data/data/com.example.yonghaohu.sniff/files/tcpdump";

	/**
	 * Parameterless TCPdump class constructor. Calls the superclass
	 * constructor.
	 */
	public TCPdump() {
		super();
	}

	/**
	 * TCPdump class constructor. Opens a root shell and launches TCPdump on it
	 * with the given parameters.
	 * 
	 * @param params
	 *            The parameters that TCPdump will use. For example: -i
	 *            [interface name] -s [snaplen size] -w [filename]
	 * @throws IOException
	 */
	public TCPdump(String params) {
		this();
		start(params);
	}

	/**
	 * TCPdump class destructor. Stops TCPdump if its not already stopped.
	 */
	protected void finalize() {
		if (getProcessStatus() == true)
			stop();
	}

	/**
	 * Launches a TCPdump process on a root shell with the given parameters.
	 * 
	 * @param params
	 *            The parameters that TCPdump will use. For example: -i
	 *            [interface name] -s [snaplen size] -w [filename]
	 * 
	 * @return 0 Everything went OK.<br>
	 *         -1 TCPdump is already running.<br>
	 *         -2 The device isn't rooted.<br>
	 *         -3 Error when running the su command.<br>
	 *         -4 Error when running the TCPdump command.<br>
	 *         -5 Error when flushing the DataOutputStream.
	 * @throws IOException
	 */
	public int start(String params) {
		int r;
		if ((r = openShell()) != 0) {
			return r;
		}
		return runCommand(tcpdumpBinaryPath + " " + params + "&");
	}

	/**
	 * Stops a TCPdump process which is currently running.
	 * 
	 * @return 0: Everything went OK.<br>
	 *         -1: TCPdump wasn't running.<br>
	 *         -2: The device isn't rooted.<br>
	 *         -4: Error when running the killall command.<br>
	 *         -5: Error when flushing the DataOutputStream.<br>
	 *         -6: Error when closing the shell.<br>
	 *         -7: Error when waiting for the process to finish.
	 */
	public int stop() {
		int r;
		if ((r = runCommand("killall tcpdump")) != 0) {
			return r;
		}
		return closeShell();
	}
}
