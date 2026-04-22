package n1;

import e0.C0405a;
import i1.h;
import i1.j;
import i1.n;
import i1.s;
import i1.v;
import j1.InterfaceC0667e;
import j1.k;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import o1.t;
import q1.InterfaceC0770b;

/* renamed from: n1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0743c implements d {
    public static final Logger f = Logger.getLogger(v.class.getName());

    /* renamed from: a  reason: collision with root package name */
    public final t f5373a;

    /* renamed from: b  reason: collision with root package name */
    public final Executor f5374b;

    /* renamed from: c  reason: collision with root package name */
    public final InterfaceC0667e f5375c;

    /* renamed from: d  reason: collision with root package name */
    public final p1.d f5376d;

    /* renamed from: e  reason: collision with root package name */
    public final InterfaceC0770b f5377e;

    public C0743c(Executor executor, InterfaceC0667e interfaceC0667e, t tVar, p1.d dVar, InterfaceC0770b interfaceC0770b) {
        this.f5374b = executor;
        this.f5375c = interfaceC0667e;
        this.f5373a = tVar;
        this.f5376d = dVar;
        this.f5377e = interfaceC0770b;
    }

    @Override // n1.d
    public final void a(final j jVar, final h hVar, final C0405a c0405a) {
        this.f5374b.execute(new Runnable() { // from class: n1.a
            @Override // java.lang.Runnable
            public final void run() {
                s sVar = jVar;
                C0405a c0405a2 = c0405a;
                n nVar = hVar;
                final C0743c c0743c = C0743c.this;
                c0743c.getClass();
                Logger logger = C0743c.f;
                try {
                    k a4 = c0743c.f5375c.a(sVar.a());
                    if (a4 == null) {
                        String str = "Transport backend '" + sVar.a() + "' is not registered";
                        logger.warning(str);
                        new IllegalArgumentException(str);
                        c0405a2.getClass();
                    } else {
                        final h b4 = a4.b((h) nVar);
                        final j jVar2 = (j) sVar;
                        c0743c.f5377e.a(new InterfaceC0770b.a() { // from class: n1.b
                            @Override // q1.InterfaceC0770b.a
                            public final Object a() {
                                C0743c c0743c2 = C0743c.this;
                                p1.d dVar = c0743c2.f5376d;
                                n nVar2 = b4;
                                s sVar2 = jVar2;
                                dVar.h((j) sVar2, nVar2);
                                c0743c2.f5373a.b(sVar2, 1);
                                return null;
                            }
                        });
                        c0405a2.getClass();
                    }
                } catch (Exception e4) {
                    logger.warning("Error scheduling event " + e4.getMessage());
                    c0405a2.getClass();
                }
            }
        });
    }
}
