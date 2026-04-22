package z1;

import android.content.Context;
import android.content.pm.PackageManager;
import java.util.TreeMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class n {

    /* renamed from: a  reason: collision with root package name */
    public final Context f6561a;

    /* renamed from: b  reason: collision with root package name */
    public final String f6562b;

    /* renamed from: c  reason: collision with root package name */
    public final TreeMap f6563c = new TreeMap();

    /* renamed from: d  reason: collision with root package name */
    public String f6564d;

    /* renamed from: e  reason: collision with root package name */
    public String f6565e;
    public final String f;

    public n(Context context, String str) {
        String concat;
        this.f6561a = context.getApplicationContext();
        this.f6562b = str;
        String packageName = context.getPackageName();
        try {
            concat = packageName + "-" + b2.c.a(context).b(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e4) {
            E1.m.e("Unable to get package version name for reporting", e4);
            concat = String.valueOf(packageName).concat("-missing");
        }
        this.f = concat;
    }
}
