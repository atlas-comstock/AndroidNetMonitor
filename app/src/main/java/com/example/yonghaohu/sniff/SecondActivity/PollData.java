/**
 * Created by yonghaohu on 15/10/19.
 */
//https://stackoverflow.com/questions/18731656/monitor-the-ip-addresses-from-connections-being-made-from-all-apps-using-the-net
package com.example.yonghaohu.sniff.SecondActivity;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class PollData extends TestCase {

    /**
     * Address patterns used to check whether we're checking the right column in /proc/net.
     */
    private static final List<String> ADDRESS_PATTERNS = new ArrayList<String>(2);

    static {
        ADDRESS_PATTERNS.add("[0-9A-F]{8}:[0-9A-F]{4}");
        ADDRESS_PATTERNS.add("[0-9A-F]{32}:[0-9A-F]{4}");
    }

    /**
     * Ports that are allowed to be listening on the emulator.
     */
    private static final List<String> EXCEPTION_PATTERNS = new ArrayList<String>(6);

    static {
        // IPv4 exceptions
        EXCEPTION_PATTERNS.add("00000000:15B3"); // 0.0.0.0:5555   - emulator port
        EXCEPTION_PATTERNS.add("0F02000A:15B3"); // 10.0.2.15:5555 - net forwarding for emulator
        EXCEPTION_PATTERNS.add("[0-9A-F]{6}7F:[0-9A-F]{4}"); // IPv4 Loopback

        // IPv6 exceptions
        EXCEPTION_PATTERNS.add("[0]{31}1:[0-9A-F]{4}"); // IPv6 Loopback
        EXCEPTION_PATTERNS.add("[0]{16}[0]{4}[0]{4}[0-9A-F]{6}7F:[0-9A-F]{4}"); // IPv4-6 Conversion
        EXCEPTION_PATTERNS.add("[0]{16}[F]{4}[0]{4}[0-9A-F]{6}7F:[0-9A-F]{4}"); // IPv4-6 Conversion
    }

    public String testNoListeningTcpPorts( ArrayList<Integer> socket_inode) {
        return assertNoListeningPorts("/proc/net/tcp", true, socket_inode);
    }

    public String testNoListeningTcp6Ports( ArrayList<Integer> socket_inode) {
        return  assertNoListeningPorts("/proc/net/tcp6", true, socket_inode);
    }

    public void testNoListeningUdpPorts(ArrayList<Integer> pid) throws Exception {
        assertNoListeningUdpPorts("/proc/net/udp", pid);
    }

    public void testNoListeningUdp6Ports(ArrayList<Integer> pid) throws Exception {
        assertNoListeningUdpPorts("/proc/net/udp6", pid);
    }

    private static final int RETRIES_MAX = 6;

    /**
     * UDP tests can be flaky due to DNS lookups.  Compensate.
     */
    private static void assertNoListeningUdpPorts(String procFilePath, ArrayList<Integer> pid) throws Exception {
        for (int i = 0; i < RETRIES_MAX; i++) {
            try {
                assertNoListeningPorts(procFilePath, false,  pid);
                return;
            } catch (ListeningPortsAssertionError e) {
                if (i == RETRIES_MAX - 1) {
                    throw e;
                }
                Thread.sleep(2 * 1000 * i);
            }
        }
        throw new IllegalStateException("unreachable");
    }

    private static String assertNoListeningPorts(String procFilePath, boolean isTcp, ArrayList<Integer> socket_inode) {
    /*
     * Sample output of "cat /proc/net/tcp" on emulator:
     *
     * sl  local_address rem_address   st tx_queue rx_queue tr tm->when retrnsmt   uid  ...
     * 0: 0100007F:13AD 00000000:0000 0A 00000000:00000000 00:00000000 00000000     0   ...
     * 1: 00000000:15B3 00000000:0000 0A 00000000:00000000 00:00000000 00000000     0   ...
     * 2: 0F02000A:15B3 0202000A:CE8A 01 00000000:00000000 00:00000000 00000000     0   ...
     *
     */
        File procFile = new File(procFilePath);
        Scanner scanner = null;
        String res_content = new String();
        try {
            scanner = new Scanner(procFile);
            //for(int z=0; z<socket_inode.size(); ++z)
            //    res_content += "socket: "+socket_inode.get(z);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Skip column headers
                if (line.startsWith("sl")) {
                    continue;
                }

                String[] fields = line.split("\\s+");
                int i=0, index;
                HexConvert hexconvert = new HexConvert();
                while (i < fields.length) {
            //        res_content += "files of inode: "+(Integer.parseInt(fields[i+9]) + "\n");
                    index = socket_inode.indexOf(Integer.parseInt(fields[i + 9]));
                    Log.d("Find", fields[i+9]);
                    if(index != -1) {
                        res_content += "number_of_entry "+fields[i+0] + "\n";
                        res_content += "local_IPv4_address "+fields[i+1] + "\n";
                        res_content += "local_IPv4_address "+hexconvert.hexa2decIpAndPort(fields[i+1]) + "\n";
                        res_content += "remote_IPv4_address "+fields[i+2] + "\n";
                        res_content += "remote_IPv4_address "+hexconvert.hexa2decIpAndPort(fields[i+2]) + "\n";
                        res_content += "connection_state" + fields[i+3] + "\n";
                        res_content += "transmit_receive_queue"+ fields[i+4]+ "\n";
                        res_content += "timer_active"+fields[i+5]+ "\n";
                        res_content += "number_of_unrecovered_RTO_timeouts:"+fields[i+6]+ "\n";
                        res_content += "uid: "+fields[i+7]+ "\n";
                        res_content += "unanswered_0-window_probes: "+fields[i+8]+ "\n";
                        res_content += "inode : "+fields[i+9]+ "\n";
                        res_content += "socket_reference_count: "+fields[i+10]+ "\n";
                        res_content += "location_of_socket_in_memory:  "+fields[i+11]+ "\n";
                        res_content += "retransmit_timeout: "+fields[i+12]+ "\n";
                        res_content +="predicted_tick_of_soft_clock: "+ fields[i+13]+ "\n";
                        res_content +="ack"+ fields[i+14]+ "\n";
                        res_content +="sending_congestion_window: "+ fields[i+15]+ "\n";
                        res_content +="slowstart: "+ fields[i+16]+ "\n\n";
                    }
                    i += 17;
                }
                Log.d("Result!", res_content);

                final int expectedNumColumns = 12;
                assertTrue(procFilePath + " should have at least " + expectedNumColumns
                        + " columns of output " + fields, fields.length >= expectedNumColumns);

                String localAddress = fields[1];
                String state = fields[3];
                Log.d("LocalAddress", localAddress);
                Log.d("LocalAddress", localAddress);

                assertTrue(procFilePath + " should have an IP address in the second column",
                        isAddress(localAddress));

                if (!isException(localAddress) && isPortListening(state, isTcp)) {
                    throw new ListeningPortsAssertionError(
                            "Found port listening on " + localAddress + " in " + procFilePath);
                }
            }
        } catch (FileNotFoundException notFound) {
            fail("Could not open file " + procFilePath + " to check for listening ports.");
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return  res_content;
    }

    private static boolean isAddress(String localAddress) {
        return isPatternMatch(ADDRESS_PATTERNS, localAddress);
    }

    private static boolean isException(String localAddress) {
        return isPatternMatch(EXCEPTION_PATTERNS, localAddress);
    }

    private static boolean isPatternMatch(List<String> patterns, String input) {
        for (String pattern : patterns) {
            if (Pattern.matches(pattern, input)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPortListening(String state, boolean isTcp) {
        // 0A = TCP_LISTEN from include/net/tcp_states.h
        String listeningState = isTcp ? "0A" : "07";
        return listeningState.equals(state);
    }

    private static class ListeningPortsAssertionError extends AssertionFailedError {
        private ListeningPortsAssertionError(String msg) {
            super(msg);
        }
    }

   /*
     *  parse /proc/pid/fd/*
     */
    public ArrayList<Integer> ParseOutput(String parse_file) {
        ArrayList<Integer> res_of_socket = new ArrayList<Integer>();
        if(parse_file.indexOf("socket") != -1){
            Log.d("Socket", parse_file);
            parse_file = parse_file.trim();

            // 按指定模式在字符串查找
            String pattern = "socket:\\S(\\d+)\\S";
            // 创建 Pattern 对象
            Pattern r = Pattern.compile(pattern);
            // 现在创建 matcher 对象
            Matcher m = r.matcher(parse_file);
            while (m.find()&&m.group(1)!=null) {
                res_of_socket.add(Integer.parseInt(m.group(1)));
            }
        }
        return res_of_socket;
    }

}
/*
    public ArrayList<Integer> ParsePid(int pid) {
        String path = "/proc/" + pid + "/fd/";
        File dir = new File(path);
        File file[] = dir.listFiles();
        ArrayList<Integer> res_of_socket = new ArrayList<Integer>();
        if(file == null)
            return null;
        if (file.length == 0)
            return null;
        String parse_file = new String("");
//        File processFile = new File(path);
//        Scanner scanner = null;
        NdkJniUtils jni = new NdkJniUtils();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory())
                Log.d("Parse", "dir");
            else {
                parse_file = jni.getCLanguageString(file[i].getAbsolutePath());
                if(parse_file.indexOf("socket") != -1){
                    Log.d("Socket", parse_file);
                    parse_file = parse_file.trim();
                    String res = "";
                    if(parse_file != null && !"".equals(parse_file)) {
                        for (int k = 0; k < parse_file.length(); k++) {
                            if (Character.isDigit(parse_file.charAt(k)))
                                res += parse_file.charAt(k);
                        }
                        res_of_socket.add(Integer.parseInt(res));
                    }
                }else {
                    Log.d("NDK", parse_file);
                }
                //               try {
//                    scanner = new Scanner(file[i].getAbsolutePath());
//                    while (scanner.hasNextLine()) {
//                        String line = scanner.nextLine().trim();
//                        Log.d("Parse ", line);
//                        res += "file: " + file[i].getAbsolutePath()+": "+line+"\n";
//                        ///https://developer.xamarin.com/api/member/Android.Systems.Os.Readlink(System.String)/
//
//                        // Skip column headers
//                        // if (line.startsWith("sl")) {
//                        //     continue;
//                        // }
//                        //String[] fields = line.split("\\s+");
//                        //for (String n :fields ) {
//                        //    Log.d("fields", n);
//                        //}
//                    }
//                }catch (IOError a) {
//                    Log.d("Parse ", "false");
//                    return "error!!";
//                }
//                finally {
//                    if (scanner != null)
//                        scanner.close();
//                }
//            }
            }
        }

        return res_of_socket;
    }
*/
