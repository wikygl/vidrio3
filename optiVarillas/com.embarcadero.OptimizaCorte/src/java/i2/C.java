package i2;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C {

    /* renamed from: a  reason: collision with root package name */
    public static String f3674a;

    /* JADX WARN: Removed duplicated region for block: B:33:0x0028 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static synchronized java.lang.String a(android.content.Context r7) {
        /*
            r0 = 0
            r1 = 1
            java.lang.Class<i2.C> r2 = i2.C.class
            monitor-enter(r2)
            java.lang.String r3 = i2.C.f3674a     // Catch: java.lang.Throwable -> L20
            if (r3 != 0) goto L4f
            android.content.ContentResolver r7 = r7.getContentResolver()     // Catch: java.lang.Throwable -> L20
            if (r7 != 0) goto L11
            r7 = 0
            goto L17
        L11:
            java.lang.String r3 = "android_id"
            java.lang.String r7 = android.provider.Settings.Secure.getString(r7, r3)     // Catch: java.lang.Throwable -> L20
        L17:
            if (r7 == 0) goto L22
            boolean r3 = i2.I.a()     // Catch: java.lang.Throwable -> L20
            if (r3 == 0) goto L24
            goto L22
        L20:
            r7 = move-exception
            goto L53
        L22:
            java.lang.String r7 = "emulator"
        L24:
            r3 = 0
        L25:
            r4 = 3
            if (r3 >= r4) goto L4b
            java.lang.String r4 = "MD5"
            java.security.MessageDigest r4 = java.security.MessageDigest.getInstance(r4)     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            byte[] r5 = r7.getBytes()     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            r4.update(r5)     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            java.lang.String r5 = "%032X"
            java.math.BigInteger r6 = new java.math.BigInteger     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            byte[] r4 = r4.digest()     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            r6.<init>(r1, r4)     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            r4[r0] = r6     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            java.lang.String r7 = java.lang.String.format(r5, r4)     // Catch: java.lang.Throwable -> L20 java.security.NoSuchAlgorithmException -> L49 java.lang.ArithmeticException -> L4b
            goto L4d
        L49:
            int r3 = r3 + r1
            goto L25
        L4b:
            java.lang.String r7 = ""
        L4d:
            i2.C.f3674a = r7     // Catch: java.lang.Throwable -> L20
        L4f:
            java.lang.String r7 = i2.C.f3674a     // Catch: java.lang.Throwable -> L20
            monitor-exit(r2)
            return r7
        L53:
            monitor-exit(r2)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: i2.C.a(android.content.Context):java.lang.String");
    }
}
