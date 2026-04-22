package B;

import A1.B1;
import A1.D;
import A1.K0;
import D1.f0;
import K1.C0208b;
import S0.M0;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.view.ViewGroup;
import com.google.android.gms.internal.ads.Bt;
import com.google.android.gms.internal.ads.LG;
import com.google.android.gms.internal.ads.Nt;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.XN;
import com.google.android.gms.internal.ads.Xu;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.jm;
import com.google.android.gms.internal.ads.qf;
import com.google.android.gms.internal.ads.wm;
import com.google.android.gms.internal.ads.yG;
import i2.b0;
import java.lang.reflect.Method;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import p2.AbstractC0757f;
import p2.C0756e;
import p2.C0759h;
import p2.C0761j;
import t1.C0801c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class h implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f223j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f224k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f225l;

    public /* synthetic */ h(Object obj, int i4, Object obj2) {
        this.f223j = i4;
        this.f224k = obj;
        this.f225l = obj2;
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x01d4 A[Catch: all -> 0x00ac, TryCatch #2 {all -> 0x00ac, blocks: (B:29:0x008d, B:31:0x00a9, B:35:0x00af, B:37:0x0172, B:40:0x0177, B:42:0x017e, B:44:0x01d4, B:45:0x01e0, B:46:0x0206), top: B:56:0x008d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void a() {
        /*
            Method dump skipped, instructions count: 525
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: B.h.a():void");
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4 = false;
        switch (this.f223j) {
            case 0:
                try {
                    Method method = i.f229d;
                    Object obj = this.f225l;
                    Object obj2 = this.f224k;
                    if (method != null) {
                        method.invoke(obj2, obj, Boolean.FALSE, "AppCompat recreation");
                    } else {
                        i.f230e.invoke(obj2, obj, Boolean.FALSE);
                    }
                    return;
                } catch (RuntimeException e4) {
                    if (e4.getClass() == RuntimeException.class && e4.getMessage() != null && e4.getMessage().startsWith("Unable to stop")) {
                        throw e4;
                    }
                    return;
                } catch (Throwable th) {
                    Log.e("ActivityRecreator", "Exception while invoking performStopActivity", th);
                    return;
                }
            case 1:
                ((C1.p) this.f224k).f384b.f392k.getWindow().setBackgroundDrawable((Drawable) this.f225l);
                return;
            case 2:
                C0208b c0208b = (C0208b) this.f224k;
                c0208b.getClass();
                Xu xu = ((Xu[]) this.f225l)[0];
                if (xu != null) {
                    XN x4 = VN.x(xu);
                    LG lg = c0208b.f1345o;
                    synchronized (lg) {
                        lg.a.addFirst(x4);
                    }
                    return;
                }
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((qf) this.f224k).j.j.loadData((String) this.f225l, "text/html", "UTF-8");
                return;
            case 4:
                wm wmVar = (wm) this.f224k;
                wmVar.getClass();
                Uri parse = Uri.parse((String) this.f225l);
                jm jmVar = ((em) wmVar.b.k).w;
                if (jmVar == null) {
                    E1.m.d("Unable to pass GMSG, no AdWebViewClient for AdWebView!");
                    return;
                } else {
                    jmVar.V(parse);
                    return;
                }
            case 5:
                Nt nt = (Nt) this.f224k;
                Bt bt = nt.d;
                if (bt.G() != null) {
                    if (((ViewGroup) this.f225l) != null) {
                        z4 = true;
                    }
                    int D4 = bt.D();
                    yG yGVar = nt.b;
                    f0 f0Var = nt.a;
                    if (D4 != 2 && bt.D() != 1) {
                        if (bt.D() == 6) {
                            f0Var.L(yGVar.f, "2", z4);
                            f0Var.L(yGVar.f, "1", z4);
                            return;
                        }
                        return;
                    }
                    f0Var.L(yGVar.f, String.valueOf(bt.D()), z4);
                    return;
                }
                return;
            case 6:
                a();
                return;
            case 7:
                ((b0) this.f225l).a();
                ((M0) this.f224k).c();
                return;
            case 8:
                C0761j c0761j = (C0761j) this.f225l;
                try {
                    AbstractC0757f abstractC0757f = (AbstractC0757f) c0761j.f5560k.f((AbstractC0757f) this.f224k);
                    if (abstractC0757f == null) {
                        c0761j.b(new NullPointerException("Continuation returned null"));
                        return;
                    }
                    p2.o oVar = C0759h.f5554b;
                    abstractC0757f.d(oVar, c0761j);
                    abstractC0757f.c(oVar, c0761j);
                    abstractC0757f.a(oVar, c0761j);
                    return;
                } catch (C0756e e5) {
                    if (e5.getCause() instanceof Exception) {
                        c0761j.f5561l.l((Exception) e5.getCause());
                        return;
                    } else {
                        c0761j.f5561l.l(e5);
                        return;
                    }
                } catch (Exception e6) {
                    c0761j.f5561l.l(e6);
                    return;
                }
            default:
                K0 k02 = (K0) this.f225l;
                C0801c c0801c = (C0801c) this.f224k;
                c0801c.getClass();
                try {
                    D d4 = c0801c.f5784c;
                    B1 b12 = c0801c.f5782a;
                    Context context = c0801c.f5783b;
                    b12.getClass();
                    d4.h3(B1.a(context, k02));
                    return;
                } catch (RemoteException e7) {
                    E1.m.e("Failed to load ad.", e7);
                    return;
                }
        }
    }

    public h(C0761j c0761j, AbstractC0757f abstractC0757f) {
        this.f223j = 8;
        this.f225l = c0761j;
        this.f224k = abstractC0757f;
    }
}
