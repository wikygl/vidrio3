package o1;

import S0.M0;
import android.content.Context;
import f1.C0412b;
import j$.util.Objects;
import j1.C0663a;
import j1.C0664b;
import j1.InterfaceC0667e;
import j1.g;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import l1.C0717a;
import l1.C0719c;
import m1.C0736a;
import q1.InterfaceC0770b;
import r1.InterfaceC0782a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class p {

    /* renamed from: a  reason: collision with root package name */
    public final Context f5464a;

    /* renamed from: b  reason: collision with root package name */
    public final InterfaceC0667e f5465b;

    /* renamed from: c  reason: collision with root package name */
    public final p1.d f5466c;

    /* renamed from: d  reason: collision with root package name */
    public final t f5467d;

    /* renamed from: e  reason: collision with root package name */
    public final Executor f5468e;
    public final InterfaceC0770b f;

    /* renamed from: g  reason: collision with root package name */
    public final InterfaceC0782a f5469g;

    /* renamed from: h  reason: collision with root package name */
    public final InterfaceC0782a f5470h;

    /* renamed from: i  reason: collision with root package name */
    public final p1.c f5471i;

    public p(Context context, InterfaceC0667e interfaceC0667e, p1.d dVar, t tVar, Executor executor, InterfaceC0770b interfaceC0770b, InterfaceC0782a interfaceC0782a, InterfaceC0782a interfaceC0782a2, p1.c cVar) {
        this.f5464a = context;
        this.f5465b = interfaceC0667e;
        this.f5466c = dVar;
        this.f5467d = tVar;
        this.f5468e = executor;
        this.f = interfaceC0770b;
        this.f5469g = interfaceC0782a;
        this.f5470h = interfaceC0782a2;
        this.f5471i = cVar;
    }

    /* JADX WARN: Type inference failed for: r5v3, types: [java.lang.Object, i1.h$a] */
    public final void a(final i1.j jVar, int i4) {
        C0664b a4;
        j1.k a5 = this.f5465b.a(jVar.f3640a);
        g.a aVar = g.a.f4748j;
        new C0664b(aVar, 0L);
        final long j4 = 0;
        while (true) {
            i iVar = new i(this, jVar);
            InterfaceC0770b interfaceC0770b = this.f;
            if (((Boolean) interfaceC0770b.a(iVar)).booleanValue()) {
                final Iterable<p1.i> iterable = (Iterable) interfaceC0770b.a(new InterfaceC0770b.a() { // from class: o1.j
                    @Override // q1.InterfaceC0770b.a
                    public final Object a() {
                        return p.this.f5466c.o((i1.j) jVar);
                    }
                });
                if (!iterable.iterator().hasNext()) {
                    return;
                }
                if (a5 == null) {
                    C0736a.a(jVar, "Uploader", "Unknown backend for %s, deleting event batch for it...");
                    a4 = new C0664b(g.a.f4750l, -1L);
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (p1.i iVar2 : iterable) {
                        arrayList.add(iVar2.a());
                    }
                    if (jVar.b() != null) {
                        p1.c cVar = this.f5471i;
                        Objects.requireNonNull(cVar);
                        C0717a c0717a = (C0717a) interfaceC0770b.a(new M0(cVar));
                        ?? obj = new Object();
                        obj.f = new HashMap();
                        obj.f3633d = Long.valueOf(this.f5469g.a());
                        obj.f3634e = Long.valueOf(this.f5470h.a());
                        obj.f3630a = "GDT_CLIENT_METRICS";
                        C0412b c0412b = new C0412b("proto");
                        c0717a.getClass();
                        e3.h hVar = i1.p.f3653a;
                        hVar.getClass();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            hVar.a(c0717a, byteArrayOutputStream);
                        } catch (IOException unused) {
                        }
                        obj.f3632c = new i1.m(c0412b, byteArrayOutputStream.toByteArray());
                        arrayList.add(a5.b(obj.b()));
                    }
                    a4 = a5.a(new C0663a(arrayList, jVar.f3641b));
                }
                g.a aVar2 = g.a.f4749k;
                g.a aVar3 = a4.f4742a;
                if (aVar3 == aVar2) {
                    interfaceC0770b.a(new InterfaceC0770b.a() { // from class: o1.k
                        @Override // q1.InterfaceC0770b.a
                        public final Object a() {
                            p pVar = p.this;
                            p1.d dVar = pVar.f5466c;
                            dVar.A(iterable);
                            dVar.j(pVar.f5469g.a() + j4, (i1.j) jVar);
                            return null;
                        }
                    });
                    this.f5467d.a(jVar, i4 + 1, true);
                    return;
                }
                interfaceC0770b.a(new InterfaceC0770b.a() { // from class: o1.l
                    @Override // q1.InterfaceC0770b.a
                    public final Object a() {
                        p.this.f5466c.e(iterable);
                        return null;
                    }
                });
                if (aVar3 == aVar) {
                    long max = Math.max(j4, a4.f4743b);
                    if (jVar.b() != null) {
                        interfaceC0770b.a(new InterfaceC0770b.a() { // from class: o1.m
                            @Override // q1.InterfaceC0770b.a
                            public final Object a() {
                                p.this.f5471i.f();
                                return null;
                            }
                        });
                    }
                    j4 = max;
                } else if (aVar3 == g.a.f4751m) {
                    final HashMap hashMap = new HashMap();
                    for (p1.i iVar3 : iterable) {
                        String g4 = iVar3.a().g();
                        if (!hashMap.containsKey(g4)) {
                            hashMap.put(g4, 1);
                        } else {
                            hashMap.put(g4, Integer.valueOf(((Integer) hashMap.get(g4)).intValue() + 1));
                        }
                    }
                    interfaceC0770b.a(new InterfaceC0770b.a() { // from class: o1.n
                        @Override // q1.InterfaceC0770b.a
                        public final Object a() {
                            p pVar = p.this;
                            pVar.getClass();
                            for (Map.Entry entry : hashMap.entrySet()) {
                                C0719c.a aVar4 = C0719c.a.f5253p;
                                pVar.f5471i.b(((Integer) entry.getValue()).intValue(), (String) entry.getKey(), aVar4);
                            }
                            return null;
                        }
                    });
                }
            } else {
                interfaceC0770b.a(new InterfaceC0770b.a() { // from class: o1.o
                    @Override // q1.InterfaceC0770b.a
                    public final Object a() {
                        p pVar = p.this;
                        pVar.f5466c.j(pVar.f5469g.a() + j4, (i1.j) jVar);
                        return null;
                    }
                });
                return;
            }
        }
    }
}
