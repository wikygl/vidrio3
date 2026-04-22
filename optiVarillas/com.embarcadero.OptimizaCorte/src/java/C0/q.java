package C0;

import C0.k;
import android.os.Build;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class q {

    /* renamed from: a  reason: collision with root package name */
    public UUID f344a;

    /* renamed from: b  reason: collision with root package name */
    public L0.p f345b;

    /* renamed from: c  reason: collision with root package name */
    public Set<String> f346c;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static abstract class a<B extends a<?, ?>, W extends q> {

        /* renamed from: a  reason: collision with root package name */
        public UUID f347a;

        /* renamed from: b  reason: collision with root package name */
        public L0.p f348b;

        /* renamed from: c  reason: collision with root package name */
        public HashSet f349c;

        /* JADX WARN: Type inference failed for: r0v9, types: [L0.p, java.lang.Object] */
        /* JADX WARN: Type inference failed for: r1v0, types: [C0.k, java.lang.Object, C0.q] */
        /* JADX WARN: Type inference failed for: r5v10, types: [java.lang.Object, C0.c] */
        public final k a() {
            boolean z4;
            k.a aVar = (k.a) this;
            UUID uuid = aVar.f347a;
            L0.p pVar = aVar.f348b;
            HashSet hashSet = aVar.f349c;
            ?? obj = new Object();
            obj.f344a = uuid;
            obj.f345b = pVar;
            obj.f346c = hashSet;
            c cVar = this.f348b.f1458j;
            int i4 = Build.VERSION.SDK_INT;
            if ((i4 < 24 || cVar.f310h.f311a.size() <= 0) && !cVar.f307d && !cVar.f305b && (i4 < 23 || !cVar.f306c)) {
                z4 = false;
            } else {
                z4 = true;
            }
            if (this.f348b.f1465q && z4) {
                throw new IllegalArgumentException("Expedited jobs only support network and storage constraints");
            }
            this.f347a = UUID.randomUUID();
            L0.p pVar2 = this.f348b;
            ?? obj2 = new Object();
            obj2.f1451b = o.f337j;
            androidx.work.b bVar = androidx.work.b.c;
            obj2.f1454e = bVar;
            obj2.f = bVar;
            obj2.f1458j = c.f303i;
            obj2.f1460l = C0.a.f298j;
            obj2.f1461m = 30000L;
            obj2.f1464p = -1L;
            obj2.f1466r = m.f334j;
            obj2.f1450a = pVar2.f1450a;
            obj2.f1452c = pVar2.f1452c;
            obj2.f1451b = pVar2.f1451b;
            obj2.f1453d = pVar2.f1453d;
            obj2.f1454e = new androidx.work.b(pVar2.f1454e);
            obj2.f = new androidx.work.b(pVar2.f);
            obj2.f1455g = pVar2.f1455g;
            obj2.f1456h = pVar2.f1456h;
            obj2.f1457i = pVar2.f1457i;
            c cVar2 = pVar2.f1458j;
            ?? obj3 = new Object();
            obj3.f304a = j.f324j;
            obj3.f = -1L;
            obj3.f309g = -1L;
            obj3.f310h = new d();
            obj3.f305b = cVar2.f305b;
            obj3.f306c = cVar2.f306c;
            obj3.f304a = cVar2.f304a;
            obj3.f307d = cVar2.f307d;
            obj3.f308e = cVar2.f308e;
            obj3.f310h = cVar2.f310h;
            obj2.f1458j = obj3;
            obj2.f1459k = pVar2.f1459k;
            obj2.f1460l = pVar2.f1460l;
            obj2.f1461m = pVar2.f1461m;
            obj2.f1462n = pVar2.f1462n;
            obj2.f1463o = pVar2.f1463o;
            obj2.f1464p = pVar2.f1464p;
            obj2.f1465q = pVar2.f1465q;
            obj2.f1466r = pVar2.f1466r;
            this.f348b = obj2;
            obj2.f1450a = this.f347a.toString();
            return obj;
        }
    }

    public q() {
        throw null;
    }
}
