package b2;

import a2.g;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.Process;
import com.google.errorprone.annotations.ResultIgnorabilityUnspecified;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public final Context f2924a;

    public b(Context context) {
        this.f2924a = context;
    }

    @ResultIgnorabilityUnspecified
    public final ApplicationInfo a(String str, int i4) {
        return this.f2924a.getPackageManager().getApplicationInfo(str, i4);
    }

    @ResultIgnorabilityUnspecified
    public final PackageInfo b(String str, int i4) {
        return this.f2924a.getPackageManager().getPackageInfo(str, i4);
    }

    public final boolean c() {
        String nameForUid;
        boolean isInstantApp;
        int callingUid = Binder.getCallingUid();
        int myUid = Process.myUid();
        Context context = this.f2924a;
        if (callingUid == myUid) {
            return C0355a.c(context);
        }
        if (g.a() && (nameForUid = context.getPackageManager().getNameForUid(Binder.getCallingUid())) != null) {
            isInstantApp = context.getPackageManager().isInstantApp(nameForUid);
            return isInstantApp;
        }
        return false;
    }
}
