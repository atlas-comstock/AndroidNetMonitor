package com.example.yonghaohu.sniff.SecondActivity;

/**
 * Created by yonghaohu on 15/12/4.
 */
public class HexConvert {
    /**
     * B80D01200000000067452301EFCDAB89 -> 2001:0db8:0000:0000:0123:4567:89ab:cdef *
     */
    public static String toRegularHexa(String hexaIP) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hexaIP.length(); i = i + 8) {
            String word = hexaIP.substring(i, i + 8);
            for (int j = word.length() - 1; j >= 0; j = j - 2) {
                result.append(word.substring(j - 1, j + 1));
                result.append((j == 5) ? ":" : "");//in the middle
            }
            result.append(":");
        }
        return result.substring(0, result.length() - 1).toString();
    }

    /**
     * 0100A8C0 -> 192.168.0.1
     */
    public static String hexa2decIPv4(String hexa) {
        StringBuilder result = new StringBuilder();
        //reverse Little to Big
        for (int i = hexa.length() - 1; i >= 0; i = i - 2) {
            String wtf = hexa.substring(i - 1, i + 1);
            result.append(Integer.parseInt(wtf, 16));
            result.append(".");
        }
        //remove last ".";
        return result.substring(0, result.length() - 1).toString();
    }

    /**
     * 0000000000000000FFFF00008370E736 -> 0.0.0.0.0.0.0.0.0.0.255.255.54.231.112.131
     * 0100A8C0 -> 192.168.0.1
     */
    public static String hexa2decIP(String hexa) {
        StringBuilder result = new StringBuilder();
        if (hexa.length() == 32) {
            for (int i = 0; i < hexa.length(); i = i + 8) {
                result.append(hexa2decIPv4(hexa.substring(i, i + 8)));
                result.append(".");
            }
        } else {
            if (hexa.length() != 8) {
                return "0.0.0.0";
            }
            return hexa2decIPv4(hexa);
        }
        //remove last ".";
        return result.substring(0, result.length() - 1).toString();
    }
    /**
     * Simple hexa to dec, for ports
     * 01BB -> 403
     */
    public static String hexa2decPort(String hexa) {
        StringBuilder result = new StringBuilder();
        result.append(Integer.parseInt(hexa, 16));
        return result.toString();
    }

    public String hexa2decIpAndPort(String hexa) {
//        return "Nope";
        String[] fields = hexa.split(":");
        if(fields.length < 1)
            return "nope";
        StringBuilder result = new StringBuilder();
        result.append(hexa2decIP(fields[0]));
        result.append(":");
        result.append(hexa2decPort(fields[1]));
        return result.toString();
    }

}
