package j0;

import M.C;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.profileinstaller.ProfileInstallerInitializer;
import java.util.Random;

/* renamed from: j0.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final /* synthetic */ class RunnableC0658d implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ ProfileInstallerInitializer f4726j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Context f4727k;

    public /* synthetic */ RunnableC0658d(ProfileInstallerInitializer profileInstallerInitializer, Context context) {
        this.f4726j = profileInstallerInitializer;
        this.f4727k = context;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Handler handler;
        this.f4726j.getClass();
        if (Build.VERSION.SDK_INT >= 28) {
            handler = ProfileInstallerInitializer.b.a(Looper.getMainLooper());
        } else {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.postDelayed(new C(6, this.f4727k), new Random().nextInt(Math.max(1000, 1)) + 5000);
    }
}
