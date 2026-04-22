package B;

import android.app.Activity;
import android.content.pm.PackageManager;

/* renamed from: B.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class RunnableC0146b implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ String[] f216j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Activity f217k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ int f218l;

    public RunnableC0146b(Activity activity, String[] strArr, int i4) {
        this.f216j = strArr;
        this.f217k = activity;
        this.f218l = i4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        String[] strArr = this.f216j;
        int[] iArr = new int[strArr.length];
        Activity activity = this.f217k;
        PackageManager packageManager = activity.getPackageManager();
        String packageName = activity.getPackageName();
        int length = strArr.length;
        for (int i4 = 0; i4 < length; i4++) {
            iArr[i4] = packageManager.checkPermission(strArr[i4], packageName);
        }
        ((d) activity).onRequestPermissionsResult(this.f218l, strArr, iArr);
    }
}
