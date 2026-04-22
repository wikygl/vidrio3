package K1;

import a3.InterfaceFutureC0346a;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.android.billingclient.api.ProxyBillingActivityV2;
import com.google.android.gms.internal.ads.HN;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import o1.f;
import r1.InterfaceC0782a;
import r1.b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class y implements HN, androidx.activity.result.b, com.google.gson.internal.i, k3.a {

    /* renamed from: j  reason: collision with root package name */
    public final Object f1415j;

    public InterfaceFutureC0346a a() {
        C0208b c0208b = (C0208b) this.f1415j;
        return c0208b.C4(c0208b.f1342l, null, "BANNER", null, null, new Bundle()).b();
    }

    public void c(Object obj) {
        Bundle extras;
        androidx.activity.result.a aVar = (androidx.activity.result.a) obj;
        ProxyBillingActivityV2 proxyBillingActivityV2 = (ProxyBillingActivityV2) this.f1415j;
        proxyBillingActivityV2.getClass();
        Intent intent = aVar.k;
        int i4 = com.google.android.gms.internal.play_billing.u.b(intent, "ProxyBillingActivityV2").a;
        ResultReceiver resultReceiver = proxyBillingActivityV2.E;
        if (resultReceiver != null) {
            if (intent == null) {
                extras = null;
            } else {
                extras = intent.getExtras();
            }
            resultReceiver.send(i4, extras);
        }
        int i5 = aVar.j;
        if (i5 != -1 || i4 != 0) {
            com.google.android.gms.internal.play_billing.u.e("ProxyBillingActivityV2", "External offer dialog finished with resultCode: " + i5 + " and billing's responseCode: " + i4);
        }
        proxyBillingActivityV2.finish();
    }

    @Override // k3.a
    public Object get() {
        InterfaceC0782a interfaceC0782a = (InterfaceC0782a) ((k3.a) this.f1415j).get();
        HashMap hashMap = new HashMap();
        f1.d dVar = f1.d.f3393j;
        Set emptySet = Collections.emptySet();
        if (emptySet != null) {
            Long l2 = 30000L;
            Long l4 = 86400000L;
            hashMap.put(dVar, new o1.c(l2.longValue(), l4.longValue(), emptySet));
            f1.d dVar2 = f1.d.f3395l;
            Set emptySet2 = Collections.emptySet();
            if (emptySet2 != null) {
                Long l5 = 1000L;
                Long l6 = 86400000L;
                hashMap.put(dVar2, new o1.c(l5.longValue(), l6.longValue(), emptySet2));
                f1.d dVar3 = f1.d.f3394k;
                if (Collections.emptySet() != null) {
                    Long l7 = 86400000L;
                    Long l8 = 86400000L;
                    Set unmodifiableSet = Collections.unmodifiableSet(new HashSet(Arrays.asList(f.b.f5438k)));
                    if (unmodifiableSet != null) {
                        hashMap.put(dVar3, new o1.c(l7.longValue(), l8.longValue(), unmodifiableSet));
                        if (interfaceC0782a != null) {
                            if (hashMap.keySet().size() >= f1.d.values().length) {
                                new HashMap();
                                return new o1.b(interfaceC0782a, hashMap);
                            }
                            throw new IllegalStateException("Not all priorities have been configured");
                        }
                        throw new NullPointerException("missing required property: clock");
                    }
                    throw new NullPointerException("Null flags");
                }
                throw new NullPointerException("Null flags");
            }
            throw new NullPointerException("Null flags");
        }
        throw new NullPointerException("Null flags");
    }

    public Object k() {
        Type type = (Type) this.f1415j;
        if (type instanceof ParameterizedType) {
            Type type2 = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type2 instanceof Class) {
                return EnumSet.noneOf((Class) type2);
            }
            throw new RuntimeException("Invalid EnumSet type: " + type.toString());
        }
        throw new RuntimeException("Invalid EnumSet type: " + type.toString());
    }

    public y() {
        this.f1415j = b.a.f5707a;
    }
}
