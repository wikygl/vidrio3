package b2;

import D1.y0;
import a2.g;
import android.content.Context;
import com.google.gson.internal.i;
import e0.C0405a;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayDeque;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/* renamed from: b2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0355a implements i {

    /* renamed from: j  reason: collision with root package name */
    public static Context f2922j;

    /* renamed from: k  reason: collision with root package name */
    public static Boolean f2923k;

    /* JADX WARN: Removed duplicated region for block: B:36:0x0084  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int a(android.content.Context r9, java.lang.String r10) {
        /*
            int r0 = android.os.Process.myPid()
            int r1 = android.os.Process.myUid()
            java.lang.String r2 = r9.getPackageName()
            int r0 = r9.checkPermission(r10, r0, r1)
            r3 = -1
            if (r0 != r3) goto L15
            goto L86
        L15:
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 23
            if (r0 < r4) goto L20
            java.lang.String r10 = B.k.d(r10)
            goto L21
        L20:
            r10 = 0
        L21:
            r5 = 0
            if (r10 != 0) goto L26
        L24:
            r3 = 0
            goto L86
        L26:
            if (r2 != 0) goto L38
            android.content.pm.PackageManager r2 = r9.getPackageManager()
            java.lang.String[] r2 = r2.getPackagesForUid(r1)
            if (r2 == 0) goto L86
            int r6 = r2.length
            if (r6 > 0) goto L36
            goto L86
        L36:
            r2 = r2[r5]
        L38:
            int r3 = android.os.Process.myUid()
            java.lang.String r6 = r9.getPackageName()
            r7 = 1
            java.lang.Class<android.app.AppOpsManager> r8 = android.app.AppOpsManager.class
            if (r3 != r1) goto L74
            boolean r3 = j$.util.Objects.equals(r6, r2)
            if (r3 == 0) goto L74
            r3 = 29
            if (r0 < r3) goto L67
            android.app.AppOpsManager r0 = B.l.c(r9)
            int r3 = android.os.Binder.getCallingUid()
            int r2 = B.l.a(r0, r10, r3, r2)
            if (r2 == 0) goto L5e
            goto L81
        L5e:
            java.lang.String r9 = B.l.b(r9)
            int r2 = B.l.a(r0, r10, r1, r9)
            goto L81
        L67:
            if (r0 < r4) goto L80
            java.lang.Object r9 = B.k.a(r9, r8)
            android.app.AppOpsManager r9 = (android.app.AppOpsManager) r9
            int r7 = B.k.c(r9, r10, r2)
            goto L80
        L74:
            if (r0 < r4) goto L80
            java.lang.Object r9 = B.k.a(r9, r8)
            android.app.AppOpsManager r9 = (android.app.AppOpsManager) r9
            int r7 = B.k.c(r9, r10, r2)
        L80:
            r2 = r7
        L81:
            if (r2 != 0) goto L84
            goto L24
        L84:
            r9 = -2
            r3 = -2
        L86:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: b2.C0355a.a(android.content.Context, java.lang.String):int");
    }

    public static byte[] b(byte[] bArr) {
        Deflater deflater = new Deflater(1);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);
            deflaterOutputStream.write(bArr);
            deflaterOutputStream.close();
            deflater.end();
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th) {
            deflater.end();
            throw th;
        }
    }

    public static synchronized boolean c(Context context) {
        Boolean bool;
        synchronized (C0355a.class) {
            Context applicationContext = context.getApplicationContext();
            Context context2 = f2922j;
            if (context2 != null && (bool = f2923k) != null && context2 == applicationContext) {
                return bool.booleanValue();
            }
            f2923k = null;
            if (g.a()) {
                f2923k = Boolean.valueOf(y0.e(applicationContext.getPackageManager()));
            } else {
                try {
                    context.getClassLoader().loadClass("com.google.android.instantapps.supervisor.InstantAppsRuntime");
                    f2923k = Boolean.TRUE;
                } catch (ClassNotFoundException unused) {
                    f2923k = Boolean.FALSE;
                }
            }
            f2922j = applicationContext;
            return f2923k.booleanValue();
        }
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [W.b, W.c] */
    public static W.b d(MappedByteBuffer mappedByteBuffer) {
        long j4;
        ByteBuffer duplicate = mappedByteBuffer.duplicate();
        duplicate.order(ByteOrder.BIG_ENDIAN);
        duplicate.position(duplicate.position() + 4);
        int i4 = duplicate.getShort() & 65535;
        if (i4 <= 100) {
            duplicate.position(duplicate.position() + 6);
            int i5 = 0;
            while (true) {
                if (i5 < i4) {
                    int i6 = duplicate.getInt();
                    duplicate.position(duplicate.position() + 4);
                    j4 = duplicate.getInt() & 4294967295L;
                    duplicate.position(duplicate.position() + 4);
                    if (1835365473 == i6) {
                        break;
                    }
                    i5++;
                } else {
                    j4 = -1;
                    break;
                }
            }
            if (j4 != -1) {
                duplicate.position(duplicate.position() + ((int) (j4 - duplicate.position())));
                duplicate.position(duplicate.position() + 12);
                long j5 = duplicate.getInt() & 4294967295L;
                for (int i7 = 0; i7 < j5; i7++) {
                    int i8 = duplicate.getInt();
                    long j6 = duplicate.getInt() & 4294967295L;
                    duplicate.getInt();
                    if (1164798569 == i8 || 1701669481 == i8) {
                        duplicate.position((int) (j6 + j4));
                        ?? cVar = new W.c();
                        duplicate.order(ByteOrder.LITTLE_ENDIAN);
                        int position = duplicate.position() + duplicate.getInt(duplicate.position());
                        cVar.f2629b = duplicate;
                        cVar.f2628a = position;
                        int i9 = position - duplicate.getInt(position);
                        cVar.f2630c = i9;
                        cVar.f2631d = cVar.f2629b.getShort(i9);
                        return cVar;
                    }
                }
            }
            throw new IOException("Cannot read metadata.");
        }
        throw new IOException("Cannot read metadata.");
    }

    public static byte[] e(InputStream inputStream, int i4) {
        byte[] bArr = new byte[i4];
        int i5 = 0;
        while (i5 < i4) {
            int read = inputStream.read(bArr, i5, i4 - i5);
            if (read >= 0) {
                i5 += read;
            } else {
                throw new IllegalStateException(C0405a.c("Not enough bytes to read: ", i4));
            }
        }
        return bArr;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x005d, code lost:
        if (r0.finished() == false) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0062, code lost:
        return r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x006a, code lost:
        throw new java.lang.IllegalStateException("Inflater did not finish");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static byte[] f(java.io.FileInputStream r8, int r9, int r10) {
        /*
            java.util.zip.Inflater r0 = new java.util.zip.Inflater
            r0.<init>()
            byte[] r1 = new byte[r10]     // Catch: java.lang.Throwable -> L2e
            r2 = 2048(0x800, float:2.87E-42)
            byte[] r2 = new byte[r2]     // Catch: java.lang.Throwable -> L2e
            r3 = 0
            r4 = 0
            r5 = 0
        Le:
            boolean r6 = r0.finished()     // Catch: java.lang.Throwable -> L2e
            if (r6 != 0) goto L57
            boolean r6 = r0.needsDictionary()     // Catch: java.lang.Throwable -> L2e
            if (r6 != 0) goto L57
            if (r4 >= r9) goto L57
            int r6 = r8.read(r2)     // Catch: java.lang.Throwable -> L2e
            if (r6 < 0) goto L3b
            r0.setInput(r2, r3, r6)     // Catch: java.lang.Throwable -> L2e
            int r7 = r10 - r5
            int r7 = r0.inflate(r1, r5, r7)     // Catch: java.lang.Throwable -> L2e java.util.zip.DataFormatException -> L30
            int r5 = r5 + r7
            int r4 = r4 + r6
            goto Le
        L2e:
            r8 = move-exception
            goto L8a
        L30:
            r8 = move-exception
            java.lang.String r8 = r8.getMessage()     // Catch: java.lang.Throwable -> L2e
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L2e
            r9.<init>(r8)     // Catch: java.lang.Throwable -> L2e
            throw r9     // Catch: java.lang.Throwable -> L2e
        L3b:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2e
            r8.<init>()     // Catch: java.lang.Throwable -> L2e
            java.lang.String r10 = "Invalid zip data. Stream ended after $totalBytesRead bytes. Expected "
            r8.append(r10)     // Catch: java.lang.Throwable -> L2e
            r8.append(r9)     // Catch: java.lang.Throwable -> L2e
            java.lang.String r9 = " bytes"
            r8.append(r9)     // Catch: java.lang.Throwable -> L2e
            java.lang.String r8 = r8.toString()     // Catch: java.lang.Throwable -> L2e
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L2e
            r9.<init>(r8)     // Catch: java.lang.Throwable -> L2e
            throw r9     // Catch: java.lang.Throwable -> L2e
        L57:
            if (r4 != r9) goto L6b
            boolean r8 = r0.finished()     // Catch: java.lang.Throwable -> L2e
            if (r8 == 0) goto L63
            r0.end()
            return r1
        L63:
            java.lang.String r8 = "Inflater did not finish"
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L2e
            r9.<init>(r8)     // Catch: java.lang.Throwable -> L2e
            throw r9     // Catch: java.lang.Throwable -> L2e
        L6b:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2e
            r8.<init>()     // Catch: java.lang.Throwable -> L2e
            java.lang.String r10 = "Didn't read enough bytes during decompression. expected="
            r8.append(r10)     // Catch: java.lang.Throwable -> L2e
            r8.append(r9)     // Catch: java.lang.Throwable -> L2e
            java.lang.String r9 = " actual="
            r8.append(r9)     // Catch: java.lang.Throwable -> L2e
            r8.append(r4)     // Catch: java.lang.Throwable -> L2e
            java.lang.String r8 = r8.toString()     // Catch: java.lang.Throwable -> L2e
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L2e
            r9.<init>(r8)     // Catch: java.lang.Throwable -> L2e
            throw r9     // Catch: java.lang.Throwable -> L2e
        L8a:
            r0.end()
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: b2.C0355a.f(java.io.FileInputStream, int, int):byte[]");
    }

    public static long g(InputStream inputStream, int i4) {
        byte[] e4 = e(inputStream, i4);
        long j4 = 0;
        for (int i5 = 0; i5 < i4; i5++) {
            j4 += (e4[i5] & 255) << (i5 * 8);
        }
        return j4;
    }

    public static void h(ByteArrayOutputStream byteArrayOutputStream, long j4, int i4) {
        byte[] bArr = new byte[i4];
        for (int i5 = 0; i5 < i4; i5++) {
            bArr[i5] = (byte) ((j4 >> (i5 * 8)) & 255);
        }
        byteArrayOutputStream.write(bArr);
    }

    public static void i(ByteArrayOutputStream byteArrayOutputStream, int i4) {
        h(byteArrayOutputStream, i4, 2);
    }

    public Object k() {
        return new ArrayDeque();
    }
}
