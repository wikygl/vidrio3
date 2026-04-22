package f2;

import U1.a;
import U1.c;
import V1.H;
import android.app.PendingIntent;
import android.content.Context;
import com.google.android.gms.common.api.Status;
import p2.AbstractC0757f;
import p2.q;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class j extends U1.c<a.c.C0028c> implements Q1.a {

    /* renamed from: k  reason: collision with root package name */
    public static final U1.a<a.c.C0028c> f3405k = new U1.a<>("AppSet.API", new a.AbstractC0026a(), new Object());

    /* renamed from: i  reason: collision with root package name */
    public final Context f3406i;

    /* renamed from: j  reason: collision with root package name */
    public final T1.f f3407j;

    public j(Context context, T1.f fVar) {
        super(context, f3405k, a.c.f2376a, c.a.f2385b);
        this.f3406i = context;
        this.f3407j = fVar;
    }

    /* JADX WARN: Type inference failed for: r1v4, types: [java.lang.Object, V1.k$a] */
    /* JADX WARN: Type inference failed for: r3v3, types: [V1.j, java.lang.Object] */
    @Override // Q1.a
    public final AbstractC0757f<Q1.b> a() {
        if (this.f3407j.c(this.f3406i, 212800000) == 0) {
            ?? obj = new Object();
            T1.d[] dVarArr = {Q1.g.f2019a};
            obj.f2592a = new Object();
            return c(0, new H(obj, dVarArr, false, 27601));
        }
        U1.b bVar = new U1.b(new Status(17, (String) null, (PendingIntent) null, (T1.b) null));
        q qVar = new q();
        qVar.l(bVar);
        return qVar;
    }
}
