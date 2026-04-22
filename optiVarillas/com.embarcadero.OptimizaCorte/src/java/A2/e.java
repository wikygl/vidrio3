package a2;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class e {

    /* renamed from: a  reason: collision with root package name */
    public static final char[] f2867a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String a(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[length + length];
        int i4 = 0;
        for (byte b4 : bArr) {
            char[] cArr2 = f2867a;
            cArr[i4] = cArr2[(b4 & 255) >>> 4];
            cArr[i4 + 1] = cArr2[b4 & 15];
            i4 += 2;
        }
        return new String(cArr);
    }

    public static byte[] b(String str) {
        int length = str.length();
        if (length % 2 == 0) {
            byte[] bArr = new byte[length / 2];
            int i4 = 0;
            while (i4 < length) {
                int i5 = i4 + 2;
                bArr[i4 / 2] = (byte) Integer.parseInt(str.substring(i4, i5), 16);
                i4 = i5;
            }
            return bArr;
        }
        throw new IllegalArgumentException("Hex string has odd number of characters");
    }
}
