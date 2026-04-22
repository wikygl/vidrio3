package j2;

import W1.C0324l;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;
import m2.C0737a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class o extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ String f4822n = "am";

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ String f4823o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ Context f4824p;

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ Bundle f4825q;

    /* renamed from: r  reason: collision with root package name */
    public final /* synthetic */ G f4826r;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public o(G g4, String str, Context context, Bundle bundle) {
        super(g4, true);
        this.f4826r = g4;
        this.f4823o = str;
        this.f4824p = context;
        this.f4825q = bundle;
    }

    @Override // j2.C
    public final void a() {
        boolean z4;
        String str;
        String str2;
        String str3;
        boolean z5;
        InterfaceC0675e c0673c;
        try {
            String str4 = this.f4822n;
            if (this.f4823o != null && str4 != null) {
                try {
                    Class.forName("com.google.firebase.analytics.FirebaseAnalytics");
                } catch (ClassNotFoundException unused) {
                    z4 = true;
                }
            }
            z4 = false;
            InterfaceC0675e interfaceC0675e = null;
            if (z4) {
                str3 = this.f4823o;
                str2 = this.f4822n;
                str = this.f4826r.f4784a;
            } else {
                str = null;
                str2 = null;
                str3 = null;
            }
            C0324l.d(this.f4824p);
            G g4 = this.f4826r;
            Context context = this.f4824p;
            g4.getClass();
            try {
                IBinder b4 = DynamiteModule.c(context, DynamiteModule.c, "com.google.android.gms.measurement.dynamite").b("com.google.android.gms.measurement.internal.AppMeasurementDynamiteService");
                int i4 = AbstractBinderC0674d.f4795k;
                if (b4 != null) {
                    IInterface queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.measurement.api.internal.IAppMeasurementDynamiteService");
                    if (queryLocalInterface instanceof InterfaceC0675e) {
                        c0673c = (InterfaceC0675e) queryLocalInterface;
                    } else {
                        c0673c = new C0673c(b4);
                    }
                    interfaceC0675e = c0673c;
                }
            } catch (DynamiteModule.a e4) {
                g4.a(e4, true, false);
            }
            g4.f4790h = interfaceC0675e;
            if (this.f4826r.f4790h == null) {
                Log.w(this.f4826r.f4784a, "Failed to connect to measurement client.");
                return;
            }
            int a4 = DynamiteModule.a(this.f4824p, "com.google.android.gms.measurement.dynamite");
            int d4 = DynamiteModule.d(this.f4824p, "com.google.android.gms.measurement.dynamite", false);
            int max = Math.max(a4, d4);
            if (d4 < a4) {
                z5 = true;
            } else {
                z5 = false;
            }
            h hVar = new h(61000L, max, z5, str, str2, str3, this.f4825q, C0737a.a(this.f4824p));
            InterfaceC0675e interfaceC0675e2 = this.f4826r.f4790h;
            C0324l.d(interfaceC0675e2);
            interfaceC0675e2.d3(new c2.b(this.f4824p), hVar, this.f4773j);
        } catch (Exception e5) {
            this.f4826r.a(e5, true, false);
        }
    }
}
