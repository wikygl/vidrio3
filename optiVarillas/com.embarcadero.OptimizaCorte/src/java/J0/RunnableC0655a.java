package j0;

import java.io.Serializable;

/* renamed from: j0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final /* synthetic */ class RunnableC0655a implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ androidx.profileinstaller.b f4715j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ int f4716k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f4717l;

    public /* synthetic */ RunnableC0655a(androidx.profileinstaller.b bVar, int i4, Serializable serializable) {
        this.f4715j = bVar;
        this.f4716k = i4;
        this.f4717l = serializable;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f4715j.b.a(this.f4716k, (Serializable) this.f4717l);
    }
}
