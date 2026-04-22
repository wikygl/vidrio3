package W1;

import android.app.PendingIntent;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class E extends L {

    /* renamed from: d  reason: collision with root package name */
    public final int f2641d;

    /* renamed from: e  reason: collision with root package name */
    public final Bundle f2642e;
    public final /* synthetic */ AbstractC0314b f;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public E(AbstractC0314b abstractC0314b, int i4, Bundle bundle) {
        super(abstractC0314b);
        this.f = abstractC0314b;
        this.f2641d = i4;
        this.f2642e = bundle;
    }

    @Override // W1.L
    public final /* bridge */ /* synthetic */ void a() {
        AbstractC0314b abstractC0314b = this.f;
        PendingIntent pendingIntent = null;
        int i4 = this.f2641d;
        if (i4 == 0) {
            if (!e()) {
                abstractC0314b.C(1, null);
                d(new T1.b(8, null));
                return;
            }
            return;
        }
        abstractC0314b.C(1, null);
        Bundle bundle = this.f2642e;
        if (bundle != null) {
            pendingIntent = (PendingIntent) bundle.getParcelable("pendingIntent");
        }
        d(new T1.b(i4, pendingIntent));
    }

    public abstract void d(T1.b bVar);

    public abstract boolean e();
}
