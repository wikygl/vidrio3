package W1;

import h2.BinderC0439b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class G extends BinderC0439b {
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0052, code lost:
        if (r7.f2759j >= r5.f2759j) goto L19;
     */
    @Override // h2.BinderC0439b
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean B(int r10, android.os.Parcel r11, android.os.Parcel r12) {
        /*
            r9 = this;
            r0 = -1
            r1 = 0
            r2 = 1
            if (r10 == r2) goto L93
            r3 = 2
            if (r10 == r3) goto L78
            r3 = 3
            if (r10 == r3) goto Ld
            r10 = 0
            return r10
        Ld:
            int r10 = r11.readInt()
            android.os.IBinder r3 = r11.readStrongBinder()
            android.os.Parcelable$Creator<W1.Q> r4 = W1.Q.CREATOR
            android.os.Parcelable r4 = h2.C0440c.a(r11, r4)
            W1.Q r4 = (W1.Q) r4
            h2.C0440c.b(r11)
            r11 = r9
            W1.M r11 = (W1.M) r11
            W1.b r5 = r11.f2651j
            java.lang.String r6 = "onPostInitCompleteWithConnectionInfo can be called only once per call togetRemoteService"
            W1.C0324l.e(r5, r6)
            W1.C0324l.d(r4)
            r5.f2703v = r4
            boolean r5 = r5 instanceof f2.c
            if (r5 == 0) goto L57
            W1.d r5 = r4.f2661m
            W1.m r6 = W1.C0325m.a()
            if (r5 != 0) goto L3d
            r5 = r1
            goto L3f
        L3d:
            W1.n r5 = r5.f2717j
        L3f:
            monitor-enter(r6)
            if (r5 != 0) goto L4a
            W1.n r5 = W1.C0325m.f2757c     // Catch: java.lang.Throwable -> L48
        L44:
            r6.f2758a = r5     // Catch: java.lang.Throwable -> L48
        L46:
            monitor-exit(r6)
            goto L57
        L48:
            r10 = move-exception
            goto L55
        L4a:
            W1.n r7 = r6.f2758a     // Catch: java.lang.Throwable -> L48
            if (r7 == 0) goto L44
            int r7 = r7.f2759j     // Catch: java.lang.Throwable -> L48
            int r8 = r5.f2759j     // Catch: java.lang.Throwable -> L48
            if (r7 >= r8) goto L46
            goto L44
        L55:
            monitor-exit(r6)
            throw r10
        L57:
            android.os.Bundle r4 = r4.f2658j
            W1.b r5 = r11.f2651j
            java.lang.String r6 = "onPostInitComplete can be called only once per call to getRemoteService"
            W1.C0324l.e(r5, r6)
            W1.b r5 = r11.f2651j
            r5.getClass()
            W1.O r6 = new W1.O
            r6.<init>(r5, r10, r3, r4)
            W1.K r10 = r5.f
            int r3 = r11.f2652k
            android.os.Message r0 = r10.obtainMessage(r2, r3, r0, r6)
            r10.sendMessage(r0)
            r11.f2651j = r1
            goto Lc7
        L78:
            r11.readInt()
            android.os.Parcelable$Creator r10 = android.os.Bundle.CREATOR
            android.os.Parcelable r10 = h2.C0440c.a(r11, r10)
            android.os.Bundle r10 = (android.os.Bundle) r10
            h2.C0440c.b(r11)
            java.lang.Exception r10 = new java.lang.Exception
            r10.<init>()
            java.lang.String r11 = "GmsClient"
            java.lang.String r0 = "received deprecated onAccountValidationComplete callback, ignoring"
            android.util.Log.wtf(r11, r0, r10)
            goto Lc7
        L93:
            int r10 = r11.readInt()
            android.os.IBinder r3 = r11.readStrongBinder()
            android.os.Parcelable$Creator r4 = android.os.Bundle.CREATOR
            android.os.Parcelable r4 = h2.C0440c.a(r11, r4)
            android.os.Bundle r4 = (android.os.Bundle) r4
            h2.C0440c.b(r11)
            r11 = r9
            W1.M r11 = (W1.M) r11
            W1.b r5 = r11.f2651j
            java.lang.String r6 = "onPostInitComplete can be called only once per call to getRemoteService"
            W1.C0324l.e(r5, r6)
            W1.b r5 = r11.f2651j
            r5.getClass()
            W1.O r6 = new W1.O
            r6.<init>(r5, r10, r3, r4)
            W1.K r10 = r5.f
            int r3 = r11.f2652k
            android.os.Message r0 = r10.obtainMessage(r2, r3, r0, r6)
            r10.sendMessage(r0)
            r11.f2651j = r1
        Lc7:
            r12.writeNoException()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: W1.G.B(int, android.os.Parcel, android.os.Parcel):boolean");
    }
}
