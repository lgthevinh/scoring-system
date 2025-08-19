package org.thingai.scoringsystem.util;

public class ByteUtil {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String bytesToBase64(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] base64ToBytes(String base64) {
        return java.util.Base64.getDecoder().decode(base64);
    }

    public static String bytesToBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    public static byte[] binaryStringToBytes(String binary) {
        int len = binary.length();
        byte[] data = new byte[len / 8];
        for (int i = 0; i < len; i += 8) {
            data[i / 8] = (byte) Integer.parseInt(binary.substring(i, i + 8), 2);
        }
        return data;
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static byte[] stringToBytes(String str) {
        return str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

}
