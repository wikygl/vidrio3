package o1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import i1.h;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import p1.q;
import q1.InterfaceC0770b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class i implements InterfaceC0770b.a, q.a {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ i1.s f5448j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f5449k;

    public /* synthetic */ i(Object obj, i1.j jVar) {
        this.f5449k = obj;
        this.f5448j = jVar;
    }

    @Override // q1.InterfaceC0770b.a
    public Object a() {
        return Boolean.valueOf(((p) this.f5449k).f5466c.m((i1.j) this.f5448j));
    }

    @Override // p1.q.a
    public Object apply(Object obj) {
        f1.d[] values;
        SQLiteDatabase sQLiteDatabase = (SQLiteDatabase) obj;
        p1.q qVar = (p1.q) this.f5449k;
        p1.e eVar = qVar.f5537m;
        int c4 = eVar.c();
        i1.s sVar = this.f5448j;
        ArrayList l2 = qVar.l(sQLiteDatabase, (i1.j) sVar, c4);
        for (f1.d dVar : f1.d.values()) {
            if (dVar != sVar.c()) {
                int c5 = eVar.c() - l2.size();
                if (c5 <= 0) {
                    break;
                }
                String a4 = sVar.a();
                if (a4 != null) {
                    if (dVar != null) {
                        l2.addAll(qVar.l(sQLiteDatabase, new i1.j(a4, sVar.b(), dVar), c5));
                    } else {
                        throw new NullPointerException("Null priority");
                    }
                } else {
                    throw new NullPointerException("Null backendName");
                }
            }
        }
        HashMap hashMap = new HashMap();
        StringBuilder sb = new StringBuilder("event_id IN (");
        for (int i4 = 0; i4 < l2.size(); i4++) {
            sb.append(((p1.i) l2.get(i4)).b());
            if (i4 < l2.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(')');
        Cursor query = sQLiteDatabase.query("event_metadata", new String[]{"event_id", "name", "value"}, sb.toString(), null, null, null, null);
        while (query.moveToNext()) {
            try {
                long j4 = query.getLong(0);
                Set set = (Set) hashMap.get(Long.valueOf(j4));
                if (set == null) {
                    set = new HashSet();
                    hashMap.put(Long.valueOf(j4), set);
                }
                set.add(new q.b(query.getString(1), query.getString(2)));
            } catch (Throwable th) {
                query.close();
                throw th;
            }
        }
        query.close();
        ListIterator listIterator = l2.listIterator();
        while (listIterator.hasNext()) {
            p1.i iVar = (p1.i) listIterator.next();
            if (hashMap.containsKey(Long.valueOf(iVar.b()))) {
                h.a i5 = iVar.a().i();
                for (q.b bVar : (Set) hashMap.get(Long.valueOf(iVar.b()))) {
                    i5.a(bVar.f5539a, bVar.f5540b);
                }
                listIterator.set(new p1.b(iVar.b(), iVar.c(), i5.b()));
            }
        }
        return l2;
    }
}
