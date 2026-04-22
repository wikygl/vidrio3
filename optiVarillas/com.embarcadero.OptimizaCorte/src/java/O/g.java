package o;

import D1.o0;
import a.InterfaceC0338a;
import a.b;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.AX;
import com.google.android.gms.internal.ads.SK;
import com.google.android.gms.internal.ads.Yb;
import com.google.android.gms.internal.ads.rn;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class g implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    public Context f5404a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a extends f {
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Object, a.b$a$a] */
    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        a.b bVar;
        rn rnVar;
        if (this.f5404a != null) {
            int i4 = b.a.f2854j;
            if (iBinder == null) {
                bVar = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("android.support.customtabs.ICustomTabsService");
                if (queryLocalInterface != null && (queryLocalInterface instanceof a.b)) {
                    bVar = (a.b) queryLocalInterface;
                } else {
                    ?? obj = new Object();
                    obj.f2855j = iBinder;
                    bVar = obj;
                }
            }
            f fVar = new f(bVar, componentName);
            Yb yb = (Yb) ((AX) this).b.get();
            if (yb != null) {
                yb.b = fVar;
                try {
                    bVar.f4();
                } catch (RemoteException unused) {
                }
                o0 o0Var = yb.d;
                if (o0Var != null) {
                    Yb yb2 = (Yb) o0Var.f756j;
                    f fVar2 = yb2.b;
                    if (fVar2 == null) {
                        yb2.a = null;
                    } else if (yb2.a == null) {
                        e eVar = new e(null);
                        a.b bVar2 = fVar2.f5402a;
                        if (bVar2.r1(eVar)) {
                            rnVar = new rn(bVar2, eVar, fVar2.f5403b);
                            yb2.a = rnVar;
                        }
                        rnVar = null;
                        yb2.a = rnVar;
                    }
                    rn rnVar2 = yb2.a;
                    Intent intent = new Intent("android.intent.action.VIEW");
                    if (rnVar2 != null) {
                        intent.setPackage(((ComponentName) rnVar2.m).getPackageName());
                        IBinder asBinder = ((InterfaceC0338a) rnVar2.l).asBinder();
                        Bundle bundle = new Bundle();
                        bundle.putBinder("android.support.customtabs.extra.SESSION", asBinder);
                        PendingIntent pendingIntent = (PendingIntent) rnVar2.n;
                        if (pendingIntent != null) {
                            bundle.putParcelable("android.support.customtabs.extra.SESSION_ID", pendingIntent);
                        }
                        intent.putExtras(bundle);
                    }
                    if (!intent.hasExtra("android.support.customtabs.extra.SESSION")) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putBinder("android.support.customtabs.extra.SESSION", null);
                        intent.putExtras(bundle2);
                    }
                    intent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
                    intent.putExtras(new Bundle());
                    intent.putExtra("androidx.browser.customtabs.extra.SHARE_STATE", 0);
                    Context context = (Context) o0Var.f757k;
                    intent.setPackage(SK.a(context));
                    intent.setData((Uri) o0Var.f758l);
                    context.startActivity(intent, null);
                    Activity activity = (Activity) context;
                    AX ax = yb2.c;
                    if (ax != null) {
                        activity.unbindService(ax);
                        yb2.b = null;
                        yb2.a = null;
                        yb2.c = null;
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        throw new IllegalStateException("Custom Tabs Service connected before an applicationcontext has been provided.");
    }
}
