package Q0;

import D1.RunnableC0186f;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.internal.play_billing.I1;
import com.google.android.gms.internal.play_billing.O1;
import com.google.android.gms.internal.play_billing.P1;
import com.google.android.gms.internal.play_billing.T1;
import com.google.android.gms.internal.play_billing.X;
import com.google.android.gms.internal.play_billing.X1;
import com.google.android.gms.internal.play_billing.Y1;
import com.google.android.gms.internal.play_billing.Z1;
import java.util.concurrent.Callable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class l implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    public final Object f1984a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public final b f1985b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ a f1986c;

    public /* synthetic */ l(a aVar, b bVar) {
        this.f1986c = aVar;
        this.f1985b = bVar;
    }

    public final void a(com.android.billingclient.api.a aVar) {
        synchronized (this.f1984a) {
            b bVar = this.f1985b;
            if (bVar != null) {
                ((Y0.a) bVar).e(aVar);
            }
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Y1 z12;
        com.google.android.gms.internal.play_billing.u.d("BillingClient", "Billing service connected.");
        a aVar = this.f1986c;
        int i4 = X1.k;
        if (iBinder == null) {
            z12 = null;
        } else {
            Y1 queryLocalInterface = iBinder.queryLocalInterface("com.android.vending.billing.IInAppBillingService");
            if (queryLocalInterface instanceof Y1) {
                z12 = queryLocalInterface;
            } else {
                z12 = new Z1(iBinder);
            }
        }
        aVar.f1930q = z12;
        Callable callable = new Callable() { // from class: Q0.k
            /* JADX WARN: Removed duplicated region for block: B:172:0x037a  */
            /* JADX WARN: Removed duplicated region for block: B:173:0x0389  */
            @Override // java.util.concurrent.Callable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public final java.lang.Object call() {
                /*
                    Method dump skipped, instructions count: 1036
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: Q0.k.call():java.lang.Object");
            }
        };
        RunnableC0186f runnableC0186f = new RunnableC0186f(1, this);
        a aVar2 = this.f1986c;
        if (aVar2.L(callable, 30000L, runnableC0186f, aVar2.I()) == null) {
            a aVar3 = this.f1986c;
            com.android.billingclient.api.a K3 = aVar3.K();
            aVar3.M(m.a(25, 6, K3));
            a(K3);
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        com.google.android.gms.internal.play_billing.u.e("BillingClient", "Billing service disconnected.");
        n nVar = this.f1986c.f1929p;
        T1 p4 = T1.p();
        L0.f fVar = (L0.f) nVar;
        fVar.getClass();
        if (p4 != null) {
            try {
                O1 t3 = P1.t();
                t3.f();
                P1.q(((X) t3).k, (I1) fVar.f1433b);
                t3.f();
                P1.p(((X) t3).k, p4);
                ((o) fVar.f1434c).a((P1) t3.b());
            } catch (Throwable th) {
                com.google.android.gms.internal.play_billing.u.f("BillingLogger", "Unable to log.", th);
            }
        }
        this.f1986c.f1930q = null;
        this.f1986c.f1924k = 0;
        synchronized (this.f1984a) {
            if (this.f1985b != null) {
                Log.d("BILLING_V5_COPY", "onBillingServiceDisconnected: ");
            }
        }
    }
}
