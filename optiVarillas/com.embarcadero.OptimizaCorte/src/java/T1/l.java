package T1;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import h2.C0441d;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressLint({"HandlerLeak"})
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class l extends g2.g {

    /* renamed from: a  reason: collision with root package name */
    public final Context f2362a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ e f2363b;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public l(T1.e r1, android.content.Context r2) {
        /*
            r0 = this;
            r0.f2363b = r1
            android.os.Looper r1 = android.os.Looper.myLooper()
            if (r1 != 0) goto Ld
            android.os.Looper r1 = android.os.Looper.getMainLooper()
            goto L11
        Ld:
            android.os.Looper r1 = android.os.Looper.myLooper()
        L11:
            r0.<init>(r1)
            android.content.Context r1 = r2.getApplicationContext()
            r0.f2362a = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: T1.l.<init>(T1.e, android.content.Context):void");
    }

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        PendingIntent activity;
        int i4 = message.what;
        if (i4 != 1) {
            Log.w("GoogleApiAvailability", "Don't know how to handle this message: " + i4);
            return;
        }
        int i5 = f.f2353a;
        e eVar = this.f2363b;
        Context context = this.f2362a;
        int c4 = eVar.c(context, i5);
        AtomicBoolean atomicBoolean = i.f2356a;
        if (c4 != 1 && c4 != 2 && c4 != 3 && c4 != 9) {
            return;
        }
        Intent b4 = eVar.b(c4, context, "n");
        if (b4 == null) {
            activity = null;
        } else {
            activity = PendingIntent.getActivity(context, 0, b4, C0441d.f3586a | 134217728);
        }
        eVar.g(context, c4, activity);
    }
}
