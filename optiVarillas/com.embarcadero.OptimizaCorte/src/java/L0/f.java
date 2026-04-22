package L0;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import androidx.work.impl.WorkDatabase;
import com.google.android.gms.internal.ads.Gf;
import com.google.android.gms.internal.ads.LA;
import com.google.android.gms.internal.ads.d1;
import com.google.android.gms.internal.ads.x3;
import com.google.android.gms.internal.play_billing.I1;
import com.google.android.gms.internal.play_billing.O1;
import com.google.android.gms.internal.play_billing.P1;
import com.google.android.gms.internal.play_billing.X;
import com.google.android.gms.internal.play_billing.v1;
import com.google.android.gms.internal.play_billing.y1;
import f1.C0412b;
import g1.C0423a;
import i2.C;
import i2.C0463i;
import i2.C0478y;
import i2.C0479z;
import i2.EnumC0477x;
import i2.I;
import i2.b0;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import m0.AbstractC0731g;
import m0.AbstractC0735k;
import m0.C0733i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class f implements Q0.n {

    /* renamed from: b  reason: collision with root package name */
    public Object f1433b;

    /* renamed from: c  reason: collision with root package name */
    public final Object f1434c;

    public /* synthetic */ f(Object obj, Object obj2) {
        this.f1433b = obj;
        this.f1434c = obj2;
    }

    public Long a(String str) {
        C0733i b4 = C0733i.b("SELECT long_value FROM Preference where `key`=?", 1);
        b4.i(str, 1);
        AbstractC0731g abstractC0731g = (AbstractC0731g) this.f1433b;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            Long l2 = null;
            if (g4.moveToFirst() && !g4.isNull(0)) {
                l2 = Long.valueOf(g4.getLong(0));
            }
            return l2;
        } finally {
            g4.close();
            b4.k();
        }
    }

    public void b(d dVar) {
        AbstractC0731g abstractC0731g = (AbstractC0731g) this.f1433b;
        abstractC0731g.b();
        abstractC0731g.c();
        try {
            ((e) this.f1434c).e(dVar);
            abstractC0731g.h();
        } finally {
            abstractC0731g.f();
        }
    }

    public void c(v1 v1Var) {
        if (v1Var == null) {
            return;
        }
        try {
            O1 t3 = P1.t();
            t3.f();
            P1.q(((X) t3).k, (I1) this.f1433b);
            t3.f();
            P1.r(((X) t3).k, v1Var);
            ((Q0.o) this.f1434c).a((P1) t3.b());
        } catch (Throwable th) {
            com.google.android.gms.internal.play_billing.u.f("BillingLogger", "Unable to log.", th);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11, types: [java.lang.Object, com.google.android.gms.internal.ads.d1] */
    /* JADX WARN: Type inference failed for: r0v24, types: [java.util.List] */
    /* JADX WARN: Type inference failed for: r11v6, types: [com.google.android.gms.internal.ads.LA, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v1, types: [i2.z, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v5, types: [com.google.android.gms.internal.ads.Gf, java.lang.Object] */
    public C0479z d(Activity activity, X2.b bVar) {
        boolean z4;
        Bundle bundle;
        String str;
        ArrayList arrayList;
        Window window;
        View decorView;
        WindowInsets rootWindowInsets;
        DisplayCutout displayCutout;
        List<Rect> boundingRects;
        List list;
        PackageInfo packageInfo;
        long j4;
        ArrayList arrayList2 = new ArrayList();
        Application application = (Application) this.f1433b;
        Context applicationContext = application.getApplicationContext();
        if (I.a() || arrayList2.contains(C.a(applicationContext))) {
            z4 = true;
        } else {
            z4 = false;
        }
        ?? obj = new Object();
        obj.f3827e = Collections.emptyMap();
        obj.f3830i = Collections.emptyList();
        String str2 = null;
        if (!TextUtils.isEmpty(null)) {
            str = null;
        } else {
            try {
                bundle = application.getPackageManager().getApplicationInfo(application.getPackageName(), 128).metaData;
            } catch (PackageManager.NameNotFoundException unused) {
                bundle = null;
            }
            if (bundle != null) {
                str = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            } else {
                str = null;
            }
            if (TextUtils.isEmpty(str)) {
                throw new b0("The UMP SDK requires a valid application ID in your AndroidManifest.xml through a com.google.android.gms.ads.APPLICATION_ID meta-data tag.\nExample AndroidManifest:\n    <meta-data\n        android:name=\"com.google.android.gms.ads.APPLICATION_ID\"\n        android:value=\"ca-app-pub-0000000000000000~0000000000\">", 3);
            }
        }
        obj.f3823a = str;
        if (!z4) {
            arrayList = Collections.emptyList();
        } else {
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(EnumC0477x.f3817j);
            arrayList = arrayList3;
        }
        obj.f3830i = arrayList;
        obj.f3827e = ((C0463i) this.f1434c).a();
        obj.f3826d = Boolean.valueOf(bVar.f2817a);
        int i4 = Build.VERSION.SDK_INT;
        obj.f3825c = Locale.getDefault().toLanguageTag();
        ?? obj2 = new Object();
        ((d1) obj2).j = 1;
        ((d1) obj2).l = Integer.valueOf(i4);
        ((d1) obj2).k = Build.MODEL;
        ((d1) obj2).j = 2;
        obj.f3824b = obj2;
        Configuration configuration = application.getResources().getConfiguration();
        application.getResources().getConfiguration();
        ?? obj3 = new Object();
        ((Gf) obj3).d = Collections.emptyList();
        ((Gf) obj3).a = Integer.valueOf(configuration.screenWidthDp);
        ((Gf) obj3).b = Integer.valueOf(configuration.screenHeightDp);
        ((Gf) obj3).c = Double.valueOf(application.getResources().getDisplayMetrics().density);
        if (i4 < 28) {
            list = Collections.emptyList();
        } else {
            if (activity == null) {
                window = null;
            } else {
                window = activity.getWindow();
            }
            if (window == null) {
                decorView = null;
            } else {
                decorView = window.getDecorView();
            }
            if (decorView != null) {
                rootWindowInsets = decorView.getRootWindowInsets();
            } else {
                rootWindowInsets = null;
            }
            if (rootWindowInsets != null) {
                displayCutout = rootWindowInsets.getDisplayCutout();
            } else {
                displayCutout = null;
            }
            if (displayCutout != null) {
                displayCutout.getSafeInsetBottom();
                ArrayList arrayList4 = new ArrayList();
                boundingRects = displayCutout.getBoundingRects();
                for (Rect rect : boundingRects) {
                    if (rect != null) {
                        C0478y c0478y = new C0478y();
                        c0478y.f3820b = Integer.valueOf(rect.left);
                        c0478y.f3821c = Integer.valueOf(rect.right);
                        c0478y.f3819a = Integer.valueOf(rect.top);
                        c0478y.f3822d = Integer.valueOf(rect.bottom);
                        arrayList4.add(c0478y);
                    }
                }
                list = arrayList4;
            } else {
                list = Collections.emptyList();
            }
        }
        ((Gf) obj3).d = list;
        obj.f = obj3;
        try {
            packageInfo = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException unused2) {
            packageInfo = null;
        }
        ?? obj4 = new Object();
        ((LA) obj4).a = application.getPackageName();
        CharSequence applicationLabel = application.getPackageManager().getApplicationLabel(application.getApplicationInfo());
        if (applicationLabel != null) {
            str2 = applicationLabel.toString();
        }
        ((LA) obj4).b = str2;
        if (packageInfo != null) {
            if (Build.VERSION.SDK_INT >= 28) {
                j4 = packageInfo.getLongVersionCode();
            } else {
                j4 = packageInfo.versionCode;
            }
            ((LA) obj4).c = Long.toString(j4);
        }
        obj.f3828g = obj4;
        R2.d dVar = new R2.d(18);
        dVar.f2063k = "2.2.0";
        obj.f3829h = dVar;
        return obj;
    }

    public void e(y1 y1Var) {
        if (y1Var == null) {
            return;
        }
        try {
            O1 t3 = P1.t();
            t3.f();
            P1.q(((X) t3).k, (I1) this.f1433b);
            t3.f();
            P1.s(((X) t3).k, y1Var);
            ((Q0.o) this.f1434c).a((P1) t3.b());
        } catch (Throwable th) {
            com.google.android.gms.internal.play_billing.u.f("BillingLogger", "Unable to log.", th);
        }
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [Q0.o, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Object, C3.C] */
    public f(Context context, I1 i12) {
        x3 c4;
        C0412b c0412b;
        ?? obj;
        Set set;
        ?? obj2 = new Object();
        try {
            i1.v.b(context);
            c4 = i1.v.a().c(C0423a.f3481e);
            c0412b = new C0412b("proto");
            obj = new Object();
            set = (Set) c4.j;
        } catch (Throwable unused) {
            obj2.f1989a = true;
        }
        if (set.contains(c0412b)) {
            obj2.f1990b = new i1.t((i1.j) ((i1.s) c4.k), c0412b, obj, (i1.u) c4.l);
            this.f1434c = obj2;
            this.f1433b = i12;
            return;
        }
        throw new IllegalArgumentException(String.format("%s is not supported byt this factory. Supported encodings are: %s.", c0412b, set));
    }

    public f(WorkDatabase workDatabase) {
        this.f1433b = workDatabase;
        this.f1434c = new AbstractC0735k(workDatabase);
    }
}
