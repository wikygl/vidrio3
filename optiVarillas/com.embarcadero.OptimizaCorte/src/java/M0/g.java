package M0;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class g {

    /* renamed from: a  reason: collision with root package name */
    public static final String f1671a = C0.i.e("PackageManagerHelper");

    public static void a(Context context, Class<?> cls, boolean z4) {
        int i4;
        String str;
        String str2 = "disabled";
        String str3 = f1671a;
        try {
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, cls.getName());
            if (z4) {
                i4 = 1;
            } else {
                i4 = 2;
            }
            packageManager.setComponentEnabledSetting(componentName, i4, 1);
            C0.i c4 = C0.i.c();
            String name = cls.getName();
            if (!z4) {
                str = "disabled";
            } else {
                str = "enabled";
            }
            c4.a(str3, name + " " + str, new Throwable[0]);
        } catch (Exception e4) {
            C0.i c5 = C0.i.c();
            String name2 = cls.getName();
            if (z4) {
                str2 = "enabled";
            }
            c5.a(str3, X1.b.e(name2, " could not be ", str2), e4);
        }
    }
}
