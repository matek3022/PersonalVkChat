package company.matek3022.personalvkchat.utils;

import android.util.Base64;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by matek on 08.05.2017.
 */

public class CryptUtils {

    private static final String ADD_CHAR = "↕";

    private static void log(Object o) {
        Log.wtf("cryptLog", o.toString());
    }

    private static final byte[][] sBox = {
            {4, 10, 9, 2, 13, 8, 0, 14, 6, 11, 1, 12, 7, 15, 5, 3}
            , {14, 11, 4, 12, 6, 13, 15, 10, 2, 3, 8, 1, 0, 7, 5, 9}
            , {5, 8, 1, 13, 10, 3, 4, 2, 14, 15, 12, 7, 6, 0, 9, 11}
            , {7, 13, 10, 1, 0, 8, 9, 15, 14, 4, 6, 12, 11, 2, 5, 3}
            , {6, 12, 7, 1, 5, 15, 13, 8, 4, 10, 9, 14, 0, 3, 11, 2}
            , {4, 11, 10, 0, 7, 2, 1, 13, 3, 6, 8, 5, 9, 12, 15, 14}
            , {13, 11, 4, 1, 3, 15, 5, 9, 0, 10, 14, 7, 6, 8, 2, 12}
            , {1, 15, 13, 0, 5, 7, 10, 4, 9, 2, 3, 14, 6, 11, 8, 12}
    };

    public static String cryptWritibleString(String str, String key) {
        byte[] cryptUnwritibleCryptString = cryptString(str, key);
        return Base64.encodeToString(cryptUnwritibleCryptString, Base64.DEFAULT);
    }

    public static String decryptWritibleString(String str, String key) {
        try {
            String res = decryptString(Base64.decode(str, Base64.DEFAULT), key);
            return res.equals("") ? str : res;
        } catch (Exception e) {
            return str;
        }
    }

    public static byte[] cryptString(String str, String localKey) {
        String key = localKey;
        if (key.length() != 32) {
            for (int i = key.length(); i < 32; i++) {
                key += "A";
            }
        }
        String string = fixBlockSizes(str);
        byte[] bytesKey = key.getBytes();
        byte[] bytesStr = string.getBytes();
        return Process(bytesStr, bytesKey, sBox, true);
    }

    private static String fixBlockSizes(String str) {
        if (str.getBytes().length % 8 != 0) {
            return fixBlockSizes(str+ADD_CHAR);
        }else {
            return str;
        }
    }

    public static String decryptString(byte[] crypt, String localKey) {
        String key = localKey;
        if (key.length() != 32) {
            for (int i = key.length(); i < 32; i++) {
                key += "A";
            }
        }
        byte[] bytesKey = key.getBytes();
        byte[] decryptByteArray = Process(crypt, bytesKey, sBox, false);
        String str = new String(decryptByteArray);
        str = str.replaceAll(ADD_CHAR, "");
        return str;
    }

    private static byte[] Process(byte[] data, byte[] key, byte[][] sBox, boolean encrypt) {
        byte[] result = new byte[data.length];
        for (int k = 0; k < data.length / 8; k++) {
            int a;
            int b;
            byte[] buffA = new byte[4];
            byte[] buffB = new byte[4];
            for (int i = 0; i < 4; i++) {
                buffA[i] = data[k * 8 + i];
                buffB[i] = data[k * 8 + i + 4];
            }
            a = fromByteArray(buffA);
            b = fromByteArray(buffB);

            int[] subKeys = getSubKeys(key);

            for (int i = 0; i < 32; i++) {
                int keyIndex = getKeyIndex(i, encrypt);
                int subKey = subKeys[keyIndex];
                int fValue = F(b, subKey, sBox);
                int round = a ^ fValue;
                if (i < 31) {
                    a = b;
                    b = round;
                } else {
                    a = round;
                }
            }
            for (int i = 0; i < 8; i++) {
                if (i < 4) {
                    result[k * 8 + i] = toByteArray(a)[i];
                } else {
                    result[k * 8 + i] = toByteArray(b)[i - 4];
                }
            }
        }
        return result;
    }

    private static int[] getSubKeys(byte[] key) {
        int[] keys = new int[8];
        byte[] byteKeys = new byte[4];
        for (int i = 0; i < 8; i++) {
            for (int j = i * 4; j < ((i + 1) * 4); j++) {
                byteKeys[j % 4] = key[j];
            }
            keys[i] = fromByteArray(byteKeys);
        }
        return keys;
    }

    private static int F(int block, int subKey, byte[][] sBox) {
        block = (block + subKey);
        block = Substitute(block, sBox);
        block = (block << 11) | (block >> 21);
        return block;
    }

    private static int Substitute(int value, byte[][] sBox) {
        byte index, sBlock;
        int result = 0;

        for (int i = 0; i < 8; i++) {
            index = (byte) (value >> (4 * i) & 0x0f);
            sBlock = sBox[i][index];
            result |= (int) sBlock << (4 * i);
        }

        return result;
    }

    private static byte[] toByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private static int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static int getKeyIndex(int i, boolean encrypt) {
        return encrypt ? (i < 24) ? i % 8 : 7 - (i % 8)
                : (i < 8) ? i % 8 : 7 - (i % 8);
    }
}
