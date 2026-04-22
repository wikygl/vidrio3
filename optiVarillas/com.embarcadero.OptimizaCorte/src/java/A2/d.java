package a2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class d {

    /* renamed from: a  reason: collision with root package name */
    public static Boolean f2862a;

    /* renamed from: b  reason: collision with root package name */
    public static Boolean f2863b;

    /* renamed from: c  reason: collision with root package name */
    public static Boolean f2864c;

    /* renamed from: d  reason: collision with root package name */
    public static Boolean f2865d;

    /* renamed from: e  reason: collision with root package name */
    public static Boolean f2866e;
    public static Boolean f;

    public static boolean a(Context context) {
        if (f2864c == null) {
            PackageManager packageManager = context.getPackageManager();
            boolean z4 = false;
            if (packageManager.hasSystemFeature("com.google.android.feature.services_updater") && packageManager.hasSystemFeature("cn.google.services")) {
                z4 = true;
            }
            f2864c = Boolean.valueOf(z4);
        }
        return f2864c.booleanValue();
    }

    @TargetApi(26)
    public static boolean b(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (f2862a == null) {
            f2862a = Boolean.valueOf(packageManager.hasSystemFeature("android.hardware.type.watch"));
        }
        if (!f2862a.booleanValue() || Build.VERSION.SDK_INT >= 24) {
            if (c(context)) {
                if (!g.a() || g.b()) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return true;
    }

    @TargetApi(21)
    public static boolean c(Context context) {
        if (f2863b == null) {
            f2863b = Boolean.valueOf(context.getPackageManager().hasSystemFeature("cn.google"));
        }
        return f2863b.booleanValue();
    }
}
