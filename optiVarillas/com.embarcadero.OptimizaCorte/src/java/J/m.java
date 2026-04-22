package J;

import android.os.Process;
import java.util.concurrent.ThreadFactory;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class m implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    public String f1186a;

    /* renamed from: b  reason: collision with root package name */
    public int f1187b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a extends Thread {

        /* renamed from: j  reason: collision with root package name */
        public final int f1188j;

        public a(Runnable runnable, String str, int i4) {
            super(runnable, str);
            this.f1188j = i4;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            Process.setThreadPriority(this.f1188j);
            super.run();
        }
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        return new a(runnable, this.f1186a, this.f1187b);
    }
}
