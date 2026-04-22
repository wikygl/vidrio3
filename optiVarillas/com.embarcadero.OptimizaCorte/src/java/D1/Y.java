package D1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.gms.internal.ads.CZ;
import com.google.android.gms.internal.ads.yZ;
import java.util.ArrayList;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class Y extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f659a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f660b;

    public /* synthetic */ Y(int i4, Object obj) {
        this.f659a = i4;
        this.f660b = obj;
    }

    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        switch (this.f659a) {
            case 0:
                Z z4 = (Z) this.f660b;
                synchronized (z4) {
                    try {
                        ArrayList arrayList = new ArrayList();
                        for (Map.Entry entry : z4.f662b.entrySet()) {
                            if (((IntentFilter) entry.getValue()).hasAction(intent.getAction())) {
                                arrayList.add((BroadcastReceiver) entry.getKey());
                            }
                        }
                        int size = arrayList.size();
                        for (int i4 = 0; i4 < size; i4++) {
                            ((BroadcastReceiver) arrayList.get(i4)).onReceive(context, intent);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                return;
            default:
                if (!isInitialStickyBroadcast()) {
                    CZ cz = (CZ) this.f660b;
                    cz.b(yZ.c(context, intent, cz.h, cz.g));
                    return;
                }
                return;
        }
    }
}
