package D0;

import C0.p;
import C0.q;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class f extends p {

    /* renamed from: i  reason: collision with root package name */
    public static final String f571i = C0.i.e("WorkContinuationImpl");

    /* renamed from: a  reason: collision with root package name */
    public final k f572a;

    /* renamed from: b  reason: collision with root package name */
    public final String f573b;

    /* renamed from: c  reason: collision with root package name */
    public final C0.e f574c;

    /* renamed from: d  reason: collision with root package name */
    public final List<? extends q> f575d;

    /* renamed from: e  reason: collision with root package name */
    public final ArrayList f576e;
    public final ArrayList f;

    /* renamed from: g  reason: collision with root package name */
    public final List<f> f577g;

    /* renamed from: h  reason: collision with root package name */
    public boolean f578h;

    public f() {
        throw null;
    }

    public f(k kVar, List<? extends q> list) {
        C0.e eVar = C0.e.f314j;
        this.f572a = kVar;
        this.f573b = null;
        this.f574c = eVar;
        this.f575d = list;
        this.f577g = null;
        this.f576e = new ArrayList(list.size());
        this.f = new ArrayList();
        for (int i4 = 0; i4 < list.size(); i4++) {
            String uuid = list.get(i4).f344a.toString();
            this.f576e.add(uuid);
            this.f.add(uuid);
        }
    }

    public static boolean b(f fVar, HashSet hashSet) {
        hashSet.addAll(fVar.f576e);
        HashSet c4 = c(fVar);
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            if (c4.contains((String) it.next())) {
                return true;
            }
        }
        List<f> list = fVar.f577g;
        if (list != null && !list.isEmpty()) {
            for (f fVar2 : list) {
                if (b(fVar2, hashSet)) {
                    return true;
                }
            }
        }
        hashSet.removeAll(fVar.f576e);
        return false;
    }

    public static HashSet c(f fVar) {
        HashSet hashSet = new HashSet();
        List<f> list = fVar.f577g;
        if (list != null && !list.isEmpty()) {
            for (f fVar2 : list) {
                hashSet.addAll(fVar2.f576e);
            }
        }
        return hashSet;
    }
}
