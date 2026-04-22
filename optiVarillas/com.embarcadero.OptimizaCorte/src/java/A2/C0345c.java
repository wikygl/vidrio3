package a2;

import android.os.SystemClock;

/* renamed from: a2.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0345c implements InterfaceC0343a {

    /* renamed from: a  reason: collision with root package name */
    public static final C0345c f2861a = new Object();

    @Override // a2.InterfaceC0343a
    public final long a() {
        return System.currentTimeMillis();
    }

    @Override // a2.InterfaceC0343a
    public final long b() {
        return SystemClock.elapsedRealtime();
    }
}
