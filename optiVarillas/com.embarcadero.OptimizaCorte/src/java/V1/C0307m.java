package V1;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BasePendingResult;
import j$.util.DesugarCollections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import p2.C0758g;

/* renamed from: V1.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0307m {

    /* renamed from: a  reason: collision with root package name */
    public final Map f2596a = DesugarCollections.synchronizedMap(new WeakHashMap());

    /* renamed from: b  reason: collision with root package name */
    public final Map f2597b = DesugarCollections.synchronizedMap(new WeakHashMap());

    public final void a(boolean z4, Status status) {
        HashMap hashMap;
        HashMap hashMap2;
        synchronized (this.f2596a) {
            hashMap = new HashMap(this.f2596a);
        }
        synchronized (this.f2597b) {
            hashMap2 = new HashMap(this.f2597b);
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            if (z4 || ((Boolean) entry.getValue()).booleanValue()) {
                ((BasePendingResult) entry.getKey()).b(status);
            }
        }
        for (Map.Entry entry2 : hashMap2.entrySet()) {
            if (z4 || ((Boolean) entry2.getValue()).booleanValue()) {
                ((C0758g) entry2.getKey()).a(new U1.b(status));
            }
        }
    }
}
