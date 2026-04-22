package L0;

import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class v {
    /* JADX WARN: Removed duplicated region for block: B:50:0x0062 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:22:0x0043 -> B:34:0x005f). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static C0.d a(byte[] r7) {
        /*
            C0.d r0 = new C0.d
            r0.<init>()
            if (r7 != 0) goto L8
            return r0
        L8:
            java.io.ByteArrayInputStream r1 = new java.io.ByteArrayInputStream
            r1.<init>(r7)
            r7 = 0
            java.io.ObjectInputStream r2 = new java.io.ObjectInputStream     // Catch: java.lang.Throwable -> L47 java.io.IOException -> L4b
            r2.<init>(r1)     // Catch: java.lang.Throwable -> L47 java.io.IOException -> L4b
            int r7 = r2.readInt()     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
        L17:
            if (r7 <= 0) goto L36
            java.lang.String r3 = r2.readUTF()     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
            boolean r4 = r2.readBoolean()     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
            C0.d$a r5 = new C0.d$a     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
            r5.<init>(r3, r4)     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
            java.util.HashSet r3 = r0.f311a     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
            r3.add(r5)     // Catch: java.lang.Throwable -> L32 java.io.IOException -> L34
            int r7 = r7 + (-1)
            goto L17
        L32:
            r7 = move-exception
            goto L60
        L34:
            r7 = move-exception
            goto L4f
        L36:
            r2.close()     // Catch: java.io.IOException -> L3a
            goto L3e
        L3a:
            r7 = move-exception
            r7.printStackTrace()
        L3e:
            r1.close()     // Catch: java.io.IOException -> L42
            goto L5f
        L42:
            r7 = move-exception
            r7.printStackTrace()
            goto L5f
        L47:
            r0 = move-exception
            r2 = r7
            r7 = r0
            goto L60
        L4b:
            r2 = move-exception
            r6 = r2
            r2 = r7
            r7 = r6
        L4f:
            r7.printStackTrace()     // Catch: java.lang.Throwable -> L32
            if (r2 == 0) goto L5c
            r2.close()     // Catch: java.io.IOException -> L58
            goto L5c
        L58:
            r7 = move-exception
            r7.printStackTrace()
        L5c:
            r1.close()     // Catch: java.io.IOException -> L42
        L5f:
            return r0
        L60:
            if (r2 == 0) goto L6a
            r2.close()     // Catch: java.io.IOException -> L66
            goto L6a
        L66:
            r0 = move-exception
            r0.printStackTrace()
        L6a:
            r1.close()     // Catch: java.io.IOException -> L6e
            goto L72
        L6e:
            r0 = move-exception
            r0.printStackTrace()
        L72:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: L0.v.a(byte[]):C0.d");
    }

    public static C0.a b(int i4) {
        if (i4 != 0) {
            if (i4 == 1) {
                return C0.a.f299k;
            }
            throw new IllegalArgumentException(I.h.b(i4, "Could not convert ", " to BackoffPolicy"));
        }
        return C0.a.f298j;
    }

    public static C0.j c(int i4) {
        if (i4 != 0) {
            if (i4 != 1) {
                if (i4 != 2) {
                    if (i4 != 3) {
                        if (i4 != 4) {
                            if (Build.VERSION.SDK_INT >= 30 && i4 == 5) {
                                return C0.j.f329o;
                            }
                            throw new IllegalArgumentException(I.h.b(i4, "Could not convert ", " to NetworkType"));
                        }
                        return C0.j.f328n;
                    }
                    return C0.j.f327m;
                }
                return C0.j.f326l;
            }
            return C0.j.f325k;
        }
        return C0.j.f324j;
    }

    public static C0.m d(int i4) {
        if (i4 != 0) {
            if (i4 == 1) {
                return C0.m.f335k;
            }
            throw new IllegalArgumentException(I.h.b(i4, "Could not convert ", " to OutOfQuotaPolicy"));
        }
        return C0.m.f334j;
    }

    public static C0.o e(int i4) {
        if (i4 != 0) {
            if (i4 != 1) {
                if (i4 != 2) {
                    if (i4 != 3) {
                        if (i4 != 4) {
                            if (i4 == 5) {
                                return C0.o.f342o;
                            }
                            throw new IllegalArgumentException(I.h.b(i4, "Could not convert ", " to State"));
                        }
                        return C0.o.f341n;
                    }
                    return C0.o.f340m;
                }
                return C0.o.f339l;
            }
            return C0.o.f338k;
        }
        return C0.o.f337j;
    }

    public static int f(C0.o oVar) {
        int ordinal = oVar.ordinal();
        if (ordinal != 0) {
            int i4 = 1;
            if (ordinal != 1) {
                i4 = 2;
                if (ordinal != 2) {
                    i4 = 3;
                    if (ordinal != 3) {
                        i4 = 4;
                        if (ordinal != 4) {
                            if (ordinal == 5) {
                                return 5;
                            }
                            throw new IllegalArgumentException("Could not convert " + oVar + " to int");
                        }
                    }
                }
            }
            return i4;
        }
        return 0;
    }
}
