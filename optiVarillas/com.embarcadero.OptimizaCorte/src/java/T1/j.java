package T1;

import W1.C0324l;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.util.Log;
import com.google.errorprone.annotations.RestrictedInheritance;

@RestrictedInheritance(allowedOnPath = ".*java.*/com/google/android/gms/common/testing/.*", explanation = "Sub classing of GMS Core's APIs are restricted to testing fakes.", link = "go/gmscore-restrictedinheritance")
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class j {

    /* renamed from: a  reason: collision with root package name */
    public static j f2358a;

    /* JADX WARN: Type inference failed for: r1v1, types: [java.lang.Object, T1.j] */
    public static void a(Context context) {
        C0324l.d(context);
        synchronized (j.class) {
            try {
                if (f2358a == null) {
                    w.a(context);
                    ?? obj = new Object();
                    context.getApplicationContext();
                    f2358a = obj;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static final s b(PackageInfo packageInfo, s... sVarArr) {
        Signature[] signatureArr = packageInfo.signatures;
        if (signatureArr != null) {
            if (signatureArr.length != 1) {
                Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
                return null;
            }
            t tVar = new t(packageInfo.signatures[0].toByteArray());
            for (int i4 = 0; i4 < sVarArr.length; i4++) {
                if (sVarArr[i4].equals(tVar)) {
                    return sVarArr[i4];
                }
            }
        }
        return null;
    }

    public static final boolean c(PackageInfo packageInfo) {
        PackageInfo packageInfo2;
        boolean z4;
        s b4;
        boolean z5;
        if (packageInfo != null) {
            if (!"com.android.vending".equals(packageInfo.packageName) && !"com.google.android.gms".equals(packageInfo.packageName)) {
                z4 = true;
            } else {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if (applicationInfo == null || (applicationInfo.flags & 129) == 0) {
                    z5 = false;
                } else {
                    z5 = true;
                }
                z4 = z5;
            }
            packageInfo2 = packageInfo;
        } else {
            packageInfo2 = null;
            z4 = true;
        }
        if (packageInfo != null && packageInfo2.signatures != null) {
            if (z4) {
                b4 = b(packageInfo2, v.f2368a);
            } else {
                b4 = b(packageInfo2, v.f2368a[0]);
            }
            if (b4 != null) {
                return true;
            }
        }
        return false;
    }
}
