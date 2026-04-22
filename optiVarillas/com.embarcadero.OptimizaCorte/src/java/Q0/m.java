package Q0;

import com.google.android.gms.internal.play_billing.B1;
import com.google.android.gms.internal.play_billing.X;
import com.google.android.gms.internal.play_billing.u1;
import com.google.android.gms.internal.play_billing.v1;
import com.google.android.gms.internal.play_billing.x1;
import com.google.android.gms.internal.play_billing.y1;
import com.google.android.gms.internal.play_billing.z1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class m {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ int f1987a = 0;

    static {
        int i4 = n.f1988a;
    }

    public static v1 a(int i4, int i5, com.android.billingclient.api.a aVar) {
        try {
            u1 s4 = v1.s();
            z1 t3 = B1.t();
            int i6 = aVar.a;
            t3.f();
            B1.p(((X) t3).k, i6);
            String str = aVar.b;
            t3.f();
            B1.q(((X) t3).k, str);
            t3.f();
            B1.s(((X) t3).k, i4);
            s4.f();
            v1.q(((X) s4).k, t3.b());
            s4.f();
            v1.r(((X) s4).k, i5);
            return s4.b();
        } catch (Exception e4) {
            com.google.android.gms.internal.play_billing.u.f("BillingLogger", "Unable to create logging payload", e4);
            return null;
        }
    }

    public static y1 b(int i4) {
        try {
            x1 q4 = y1.q();
            q4.f();
            y1.p(((X) q4).k, i4);
            return q4.b();
        } catch (Exception e4) {
            com.google.android.gms.internal.play_billing.u.f("BillingLogger", "Unable to create logging payload", e4);
            return null;
        }
    }
}
