package j0;

import b2.C0355a;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

/* renamed from: j0.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0660f {

    /* renamed from: a  reason: collision with root package name */
    public static final byte[] f4729a = {112, 114, 111, 0};

    /* renamed from: b  reason: collision with root package name */
    public static final byte[] f4730b = {112, 114, 109, 0};

    public static byte[] a(C0656b[] c0656bArr, byte[] bArr) {
        int i4 = 0;
        for (C0656b c0656b : c0656bArr) {
            i4 += ((((c0656b.f4723g * 2) + 7) & (-8)) / 8) + (c0656b.f4722e * 2) + b(bArr, c0656b.f4718a, c0656b.f4719b).getBytes(StandardCharsets.UTF_8).length + 16 + c0656b.f;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(i4);
        if (Arrays.equals(bArr, C0661g.f4733c)) {
            for (C0656b c0656b2 : c0656bArr) {
                j(byteArrayOutputStream, c0656b2, b(bArr, c0656b2.f4718a, c0656b2.f4719b));
                l(byteArrayOutputStream, c0656b2);
                int[] iArr = c0656b2.f4724h;
                int length = iArr.length;
                int i5 = 0;
                int i6 = 0;
                while (i5 < length) {
                    int i7 = iArr[i5];
                    C0355a.i(byteArrayOutputStream, i7 - i6);
                    i5++;
                    i6 = i7;
                }
                k(byteArrayOutputStream, c0656b2);
            }
        } else {
            for (C0656b c0656b3 : c0656bArr) {
                j(byteArrayOutputStream, c0656b3, b(bArr, c0656b3.f4718a, c0656b3.f4719b));
            }
            for (C0656b c0656b4 : c0656bArr) {
                l(byteArrayOutputStream, c0656b4);
                int[] iArr2 = c0656b4.f4724h;
                int length2 = iArr2.length;
                int i8 = 0;
                int i9 = 0;
                while (i8 < length2) {
                    int i10 = iArr2[i8];
                    C0355a.i(byteArrayOutputStream, i10 - i9);
                    i8++;
                    i9 = i10;
                }
                k(byteArrayOutputStream, c0656b4);
            }
        }
        if (byteArrayOutputStream.size() == i4) {
            return byteArrayOutputStream.toByteArray();
        }
        throw new IllegalStateException("The bytes saved do not match expectation. actual=" + byteArrayOutputStream.size() + " expected=" + i4);
    }

    public static String b(byte[] bArr, String str, String str2) {
        Object obj;
        byte[] bArr2 = C0661g.f4735e;
        boolean equals = Arrays.equals(bArr, bArr2);
        byte[] bArr3 = C0661g.f4734d;
        String str3 = "!";
        if (!equals && !Arrays.equals(bArr, bArr3)) {
            obj = "!";
        } else {
            obj = ":";
        }
        if (str.length() <= 0) {
            if ("!".equals(obj)) {
                return str2.replace(":", "!");
            }
            if (":".equals(obj)) {
                return str2.replace("!", ":");
            }
            return str2;
        } else if (str2.equals("classes.dex")) {
            return str;
        } else {
            if (!str2.contains("!") && !str2.contains(":")) {
                if (str2.endsWith(".apk")) {
                    return str2;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                if (Arrays.equals(bArr, bArr2) || Arrays.equals(bArr, bArr3)) {
                    str3 = ":";
                }
                return C.b.c(sb, str3, str2);
            } else if ("!".equals(obj)) {
                return str2.replace(":", "!");
            } else {
                if (":".equals(obj)) {
                    return str2.replace("!", ":");
                }
                return str2;
            }
        }
    }

    public static int[] c(ByteArrayInputStream byteArrayInputStream, int i4) {
        int[] iArr = new int[i4];
        int i5 = 0;
        for (int i6 = 0; i6 < i4; i6++) {
            i5 += (int) C0355a.g(byteArrayInputStream, 2);
            iArr[i6] = i5;
        }
        return iArr;
    }

    public static C0656b[] d(FileInputStream fileInputStream, byte[] bArr, byte[] bArr2, C0656b[] c0656bArr) {
        byte[] bArr3 = C0661g.f;
        if (Arrays.equals(bArr, bArr3)) {
            if (!Arrays.equals(C0661g.f4731a, bArr2)) {
                if (Arrays.equals(bArr, bArr3)) {
                    int g4 = (int) C0355a.g(fileInputStream, 1);
                    byte[] f = C0355a.f(fileInputStream, (int) C0355a.g(fileInputStream, 4), (int) C0355a.g(fileInputStream, 4));
                    if (fileInputStream.read() <= 0) {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(f);
                        try {
                            C0656b[] e4 = e(byteArrayInputStream, g4, c0656bArr);
                            byteArrayInputStream.close();
                            return e4;
                        } catch (Throwable th) {
                            try {
                                byteArrayInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    }
                    throw new IllegalStateException("Content found after the end of file");
                }
                throw new IllegalStateException("Unsupported meta version");
            }
            throw new IllegalStateException("Requires new Baseline Profile Metadata. Please rebuild the APK with Android Gradle Plugin 7.2 Canary 7 or higher");
        } else if (Arrays.equals(bArr, C0661g.f4736g)) {
            int g5 = (int) C0355a.g(fileInputStream, 2);
            byte[] f4 = C0355a.f(fileInputStream, (int) C0355a.g(fileInputStream, 4), (int) C0355a.g(fileInputStream, 4));
            if (fileInputStream.read() <= 0) {
                ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(f4);
                try {
                    C0656b[] f5 = f(byteArrayInputStream2, bArr2, g5, c0656bArr);
                    byteArrayInputStream2.close();
                    return f5;
                } catch (Throwable th3) {
                    try {
                        byteArrayInputStream2.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
            }
            throw new IllegalStateException("Content found after the end of file");
        } else {
            throw new IllegalStateException("Unsupported meta version");
        }
    }

    public static C0656b[] e(ByteArrayInputStream byteArrayInputStream, int i4, C0656b[] c0656bArr) {
        if (byteArrayInputStream.available() == 0) {
            return new C0656b[0];
        }
        if (i4 == c0656bArr.length) {
            String[] strArr = new String[i4];
            int[] iArr = new int[i4];
            for (int i5 = 0; i5 < i4; i5++) {
                int g4 = (int) C0355a.g(byteArrayInputStream, 2);
                iArr[i5] = (int) C0355a.g(byteArrayInputStream, 2);
                strArr[i5] = new String(C0355a.e(byteArrayInputStream, g4), StandardCharsets.UTF_8);
            }
            for (int i6 = 0; i6 < i4; i6++) {
                C0656b c0656b = c0656bArr[i6];
                if (c0656b.f4719b.equals(strArr[i6])) {
                    int i7 = iArr[i6];
                    c0656b.f4722e = i7;
                    c0656b.f4724h = c(byteArrayInputStream, i7);
                } else {
                    throw new IllegalStateException("Order of dexfiles in metadata did not match baseline");
                }
            }
            return c0656bArr;
        }
        throw new IllegalStateException("Mismatched number of dex files found in metadata");
    }

    public static C0656b[] f(ByteArrayInputStream byteArrayInputStream, byte[] bArr, int i4, C0656b[] c0656bArr) {
        String str;
        if (byteArrayInputStream.available() == 0) {
            return new C0656b[0];
        }
        if (i4 == c0656bArr.length) {
            for (int i5 = 0; i5 < i4; i5++) {
                C0355a.g(byteArrayInputStream, 2);
                String str2 = new String(C0355a.e(byteArrayInputStream, (int) C0355a.g(byteArrayInputStream, 2)), StandardCharsets.UTF_8);
                long g4 = C0355a.g(byteArrayInputStream, 4);
                int g5 = (int) C0355a.g(byteArrayInputStream, 2);
                C0656b c0656b = null;
                if (c0656bArr.length > 0) {
                    int indexOf = str2.indexOf("!");
                    if (indexOf < 0) {
                        indexOf = str2.indexOf(":");
                    }
                    if (indexOf > 0) {
                        str = str2.substring(indexOf + 1);
                    } else {
                        str = str2;
                    }
                    int i6 = 0;
                    while (true) {
                        if (i6 >= c0656bArr.length) {
                            break;
                        } else if (c0656bArr[i6].f4719b.equals(str)) {
                            c0656b = c0656bArr[i6];
                            break;
                        } else {
                            i6++;
                        }
                    }
                }
                if (c0656b != null) {
                    c0656b.f4721d = g4;
                    int[] c4 = c(byteArrayInputStream, g5);
                    if (Arrays.equals(bArr, C0661g.f4735e)) {
                        c0656b.f4722e = g5;
                        c0656b.f4724h = c4;
                    }
                } else {
                    throw new IllegalStateException("Missing profile key: ".concat(str2));
                }
            }
            return c0656bArr;
        }
        throw new IllegalStateException("Mismatched number of dex files found in metadata");
    }

    public static C0656b[] g(FileInputStream fileInputStream, byte[] bArr, String str) {
        if (Arrays.equals(bArr, C0661g.f4732b)) {
            int g4 = (int) C0355a.g(fileInputStream, 1);
            byte[] f = C0355a.f(fileInputStream, (int) C0355a.g(fileInputStream, 4), (int) C0355a.g(fileInputStream, 4));
            if (fileInputStream.read() <= 0) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(f);
                try {
                    C0656b[] h4 = h(byteArrayInputStream, str, g4);
                    byteArrayInputStream.close();
                    return h4;
                } catch (Throwable th) {
                    try {
                        byteArrayInputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
            throw new IllegalStateException("Content found after the end of file");
        }
        throw new IllegalStateException("Unsupported version");
    }

    public static C0656b[] h(ByteArrayInputStream byteArrayInputStream, String str, int i4) {
        TreeMap<Integer, Integer> treeMap;
        int i5;
        if (byteArrayInputStream.available() == 0) {
            return new C0656b[0];
        }
        C0656b[] c0656bArr = new C0656b[i4];
        for (int i6 = 0; i6 < i4; i6++) {
            int g4 = (int) C0355a.g(byteArrayInputStream, 2);
            c0656bArr[i6] = new C0656b(str, new String(C0355a.e(byteArrayInputStream, (int) C0355a.g(byteArrayInputStream, 2)), StandardCharsets.UTF_8), C0355a.g(byteArrayInputStream, 4), g4, (int) C0355a.g(byteArrayInputStream, 4), (int) C0355a.g(byteArrayInputStream, 4), new int[g4], new TreeMap());
        }
        for (int i7 = 0; i7 < i4; i7++) {
            C0656b c0656b = c0656bArr[i7];
            int available = byteArrayInputStream.available() - c0656b.f;
            int i8 = 0;
            while (true) {
                int available2 = byteArrayInputStream.available();
                treeMap = c0656b.f4725i;
                if (available2 <= available) {
                    break;
                }
                i8 += (int) C0355a.g(byteArrayInputStream, 2);
                treeMap.put(Integer.valueOf(i8), 1);
                for (int g5 = (int) C0355a.g(byteArrayInputStream, 2); g5 > 0; g5--) {
                    C0355a.g(byteArrayInputStream, 2);
                    int g6 = (int) C0355a.g(byteArrayInputStream, 1);
                    if (g6 != 6 && g6 != 7) {
                        while (g6 > 0) {
                            C0355a.g(byteArrayInputStream, 1);
                            for (int g7 = (int) C0355a.g(byteArrayInputStream, 1); g7 > 0; g7--) {
                                C0355a.g(byteArrayInputStream, 2);
                            }
                            g6--;
                        }
                    }
                }
            }
            if (byteArrayInputStream.available() == available) {
                c0656b.f4724h = c(byteArrayInputStream, c0656b.f4722e);
                int i9 = c0656b.f4723g;
                BitSet valueOf = BitSet.valueOf(C0355a.e(byteArrayInputStream, (((i9 * 2) + 7) & (-8)) / 8));
                for (int i10 = 0; i10 < i9; i10++) {
                    if (valueOf.get(i10)) {
                        i5 = 2;
                    } else {
                        i5 = 0;
                    }
                    if (valueOf.get(i10 + i9)) {
                        i5 |= 4;
                    }
                    if (i5 != 0) {
                        Integer num = treeMap.get(Integer.valueOf(i10));
                        if (num == null) {
                            num = 0;
                        }
                        treeMap.put(Integer.valueOf(i10), Integer.valueOf(i5 | num.intValue()));
                    }
                }
            } else {
                throw new IllegalStateException("Read too much data during profile line parse");
            }
        }
        return c0656bArr;
    }

    /* JADX WARN: Finally extract failed */
    public static boolean i(ByteArrayOutputStream byteArrayOutputStream, byte[] bArr, C0656b[] c0656bArr) {
        long j4;
        ArrayList arrayList;
        int length;
        byte[] bArr2 = C0661g.f4731a;
        int i4 = 0;
        if (Arrays.equals(bArr, bArr2)) {
            ArrayList arrayList2 = new ArrayList(3);
            ArrayList arrayList3 = new ArrayList(3);
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            try {
                C0355a.i(byteArrayOutputStream2, c0656bArr.length);
                int i5 = 2;
                int i6 = 2;
                for (C0656b c0656b : c0656bArr) {
                    C0355a.h(byteArrayOutputStream2, c0656b.f4720c, 4);
                    C0355a.h(byteArrayOutputStream2, c0656b.f4721d, 4);
                    C0355a.h(byteArrayOutputStream2, c0656b.f4723g, 4);
                    String b4 = b(bArr2, c0656b.f4718a, c0656b.f4719b);
                    Charset charset = StandardCharsets.UTF_8;
                    int length2 = b4.getBytes(charset).length;
                    C0355a.i(byteArrayOutputStream2, length2);
                    i6 = i6 + 14 + length2;
                    byteArrayOutputStream2.write(b4.getBytes(charset));
                }
                byte[] byteArray = byteArrayOutputStream2.toByteArray();
                if (i6 == byteArray.length) {
                    C0662h c0662h = new C0662h(1, false, byteArray);
                    byteArrayOutputStream2.close();
                    arrayList2.add(c0662h);
                    ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
                    int i7 = 0;
                    int i8 = 0;
                    while (i7 < c0656bArr.length) {
                        try {
                            C0656b c0656b2 = c0656bArr[i7];
                            C0355a.i(byteArrayOutputStream3, i7);
                            C0355a.i(byteArrayOutputStream3, c0656b2.f4722e);
                            i8 = i8 + 4 + (c0656b2.f4722e * 2);
                            int[] iArr = c0656b2.f4724h;
                            int length3 = iArr.length;
                            int i9 = 0;
                            while (i4 < length3) {
                                int i10 = iArr[i4];
                                C0355a.i(byteArrayOutputStream3, i10 - i9);
                                i4++;
                                i9 = i10;
                            }
                            i7++;
                            i4 = 0;
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                    byte[] byteArray2 = byteArrayOutputStream3.toByteArray();
                    if (i8 == byteArray2.length) {
                        C0662h c0662h2 = new C0662h(3, true, byteArray2);
                        byteArrayOutputStream3.close();
                        arrayList2.add(c0662h2);
                        byteArrayOutputStream3 = new ByteArrayOutputStream();
                        int i11 = 0;
                        int i12 = 0;
                        while (i11 < c0656bArr.length) {
                            try {
                                C0656b c0656b3 = c0656bArr[i11];
                                int i13 = 0;
                                for (Map.Entry<Integer, Integer> entry : c0656b3.f4725i.entrySet()) {
                                    i13 |= entry.getValue().intValue();
                                }
                                ByteArrayOutputStream byteArrayOutputStream4 = new ByteArrayOutputStream();
                                k(byteArrayOutputStream4, c0656b3);
                                byte[] byteArray3 = byteArrayOutputStream4.toByteArray();
                                byteArrayOutputStream4.close();
                                ByteArrayOutputStream byteArrayOutputStream5 = new ByteArrayOutputStream();
                                l(byteArrayOutputStream5, c0656b3);
                                byte[] byteArray4 = byteArrayOutputStream5.toByteArray();
                                byteArrayOutputStream5.close();
                                C0355a.i(byteArrayOutputStream3, i11);
                                int length4 = byteArray3.length + i5 + byteArray4.length;
                                int i14 = i12 + 6;
                                ArrayList arrayList4 = arrayList3;
                                C0355a.h(byteArrayOutputStream3, length4, 4);
                                C0355a.i(byteArrayOutputStream3, i13);
                                byteArrayOutputStream3.write(byteArray3);
                                byteArrayOutputStream3.write(byteArray4);
                                i12 = i14 + length4;
                                i11++;
                                arrayList3 = arrayList4;
                                i5 = 2;
                            } finally {
                                try {
                                    byteArrayOutputStream3.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                        }
                        ArrayList arrayList5 = arrayList3;
                        byte[] byteArray5 = byteArrayOutputStream3.toByteArray();
                        if (i12 == byteArray5.length) {
                            C0662h c0662h3 = new C0662h(4, true, byteArray5);
                            byteArrayOutputStream3.close();
                            arrayList2.add(c0662h3);
                            long j5 = 4;
                            long size = j5 + j5 + 4 + (arrayList2.size() * 16);
                            C0355a.h(byteArrayOutputStream, arrayList2.size(), 4);
                            int i15 = 0;
                            while (i15 < arrayList2.size()) {
                                C0662h c0662h4 = (C0662h) arrayList2.get(i15);
                                int i16 = c0662h4.f4737a;
                                if (i16 != 1) {
                                    if (i16 != 2) {
                                        if (i16 != 3) {
                                            if (i16 != 4) {
                                                if (i16 == 5) {
                                                    j4 = 4;
                                                } else {
                                                    throw null;
                                                }
                                            } else {
                                                j4 = 3;
                                            }
                                        } else {
                                            j4 = 2;
                                        }
                                    } else {
                                        j4 = 1;
                                    }
                                } else {
                                    j4 = 0;
                                }
                                C0355a.h(byteArrayOutputStream, j4, 4);
                                C0355a.h(byteArrayOutputStream, size, 4);
                                boolean z4 = c0662h4.f4739c;
                                byte[] bArr3 = c0662h4.f4738b;
                                if (z4) {
                                    byte[] b5 = C0355a.b(bArr3);
                                    arrayList = arrayList5;
                                    arrayList.add(b5);
                                    C0355a.h(byteArrayOutputStream, b5.length, 4);
                                    C0355a.h(byteArrayOutputStream, bArr3.length, 4);
                                    length = b5.length;
                                } else {
                                    arrayList = arrayList5;
                                    arrayList.add(bArr3);
                                    C0355a.h(byteArrayOutputStream, bArr3.length, 4);
                                    C0355a.h(byteArrayOutputStream, 0L, 4);
                                    length = bArr3.length;
                                }
                                size += length;
                                i15++;
                                arrayList5 = arrayList;
                            }
                            ArrayList arrayList6 = arrayList5;
                            for (int i17 = 0; i17 < arrayList6.size(); i17++) {
                                byteArrayOutputStream.write((byte[]) arrayList6.get(i17));
                            }
                            return true;
                        }
                        throw new IllegalStateException("Expected size " + i12 + ", does not match actual size " + byteArray5.length);
                    }
                    throw new IllegalStateException("Expected size " + i8 + ", does not match actual size " + byteArray2.length);
                }
                throw new IllegalStateException("Expected size " + i6 + ", does not match actual size " + byteArray.length);
            } catch (Throwable th3) {
                try {
                    byteArrayOutputStream2.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        }
        byte[] bArr4 = C0661g.f4732b;
        if (Arrays.equals(bArr, bArr4)) {
            byte[] a4 = a(c0656bArr, bArr4);
            C0355a.h(byteArrayOutputStream, c0656bArr.length, 1);
            C0355a.h(byteArrayOutputStream, a4.length, 4);
            byte[] b6 = C0355a.b(a4);
            C0355a.h(byteArrayOutputStream, b6.length, 4);
            byteArrayOutputStream.write(b6);
            return true;
        }
        byte[] bArr5 = C0661g.f4734d;
        if (Arrays.equals(bArr, bArr5)) {
            C0355a.h(byteArrayOutputStream, c0656bArr.length, 1);
            for (C0656b c0656b4 : c0656bArr) {
                String b7 = b(bArr5, c0656b4.f4718a, c0656b4.f4719b);
                Charset charset2 = StandardCharsets.UTF_8;
                C0355a.i(byteArrayOutputStream, b7.getBytes(charset2).length);
                C0355a.i(byteArrayOutputStream, c0656b4.f4724h.length);
                C0355a.h(byteArrayOutputStream, c0656b4.f4725i.size() * 4, 4);
                C0355a.h(byteArrayOutputStream, c0656b4.f4720c, 4);
                byteArrayOutputStream.write(b7.getBytes(charset2));
                for (Integer num : c0656b4.f4725i.keySet()) {
                    C0355a.i(byteArrayOutputStream, num.intValue());
                    C0355a.i(byteArrayOutputStream, 0);
                }
                for (int i18 : c0656b4.f4724h) {
                    C0355a.i(byteArrayOutputStream, i18);
                }
            }
            return true;
        }
        byte[] bArr6 = C0661g.f4733c;
        if (Arrays.equals(bArr, bArr6)) {
            byte[] a5 = a(c0656bArr, bArr6);
            C0355a.h(byteArrayOutputStream, c0656bArr.length, 1);
            C0355a.h(byteArrayOutputStream, a5.length, 4);
            byte[] b8 = C0355a.b(a5);
            C0355a.h(byteArrayOutputStream, b8.length, 4);
            byteArrayOutputStream.write(b8);
            return true;
        }
        byte[] bArr7 = C0661g.f4735e;
        if (Arrays.equals(bArr, bArr7)) {
            C0355a.i(byteArrayOutputStream, c0656bArr.length);
            for (C0656b c0656b5 : c0656bArr) {
                String b9 = b(bArr7, c0656b5.f4718a, c0656b5.f4719b);
                Charset charset3 = StandardCharsets.UTF_8;
                C0355a.i(byteArrayOutputStream, b9.getBytes(charset3).length);
                TreeMap<Integer, Integer> treeMap = c0656b5.f4725i;
                C0355a.i(byteArrayOutputStream, treeMap.size());
                C0355a.i(byteArrayOutputStream, c0656b5.f4724h.length);
                C0355a.h(byteArrayOutputStream, c0656b5.f4720c, 4);
                byteArrayOutputStream.write(b9.getBytes(charset3));
                for (Integer num2 : treeMap.keySet()) {
                    C0355a.i(byteArrayOutputStream, num2.intValue());
                }
                for (int i19 : c0656b5.f4724h) {
                    C0355a.i(byteArrayOutputStream, i19);
                }
            }
            return true;
        }
        return false;
    }

    public static void j(ByteArrayOutputStream byteArrayOutputStream, C0656b c0656b, String str) {
        Charset charset = StandardCharsets.UTF_8;
        C0355a.i(byteArrayOutputStream, str.getBytes(charset).length);
        C0355a.i(byteArrayOutputStream, c0656b.f4722e);
        C0355a.h(byteArrayOutputStream, c0656b.f, 4);
        C0355a.h(byteArrayOutputStream, c0656b.f4720c, 4);
        C0355a.h(byteArrayOutputStream, c0656b.f4723g, 4);
        byteArrayOutputStream.write(str.getBytes(charset));
    }

    public static void k(ByteArrayOutputStream byteArrayOutputStream, C0656b c0656b) {
        byte[] bArr = new byte[(((c0656b.f4723g * 2) + 7) & (-8)) / 8];
        for (Map.Entry<Integer, Integer> entry : c0656b.f4725i.entrySet()) {
            int intValue = entry.getKey().intValue();
            int intValue2 = entry.getValue().intValue();
            if ((intValue2 & 2) != 0) {
                int i4 = intValue / 8;
                bArr[i4] = (byte) (bArr[i4] | (1 << (intValue % 8)));
            }
            if ((intValue2 & 4) != 0) {
                int i5 = intValue + c0656b.f4723g;
                int i6 = i5 / 8;
                bArr[i6] = (byte) ((1 << (i5 % 8)) | bArr[i6]);
            }
        }
        byteArrayOutputStream.write(bArr);
    }

    public static void l(ByteArrayOutputStream byteArrayOutputStream, C0656b c0656b) {
        int i4 = 0;
        for (Map.Entry<Integer, Integer> entry : c0656b.f4725i.entrySet()) {
            int intValue = entry.getKey().intValue();
            if ((entry.getValue().intValue() & 1) != 0) {
                C0355a.i(byteArrayOutputStream, intValue - i4);
                C0355a.i(byteArrayOutputStream, 0);
                i4 = intValue;
            }
        }
    }
}
