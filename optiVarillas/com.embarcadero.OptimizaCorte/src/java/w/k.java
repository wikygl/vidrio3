package w;

import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class k {

    /* renamed from: a  reason: collision with root package name */
    public m f6360a;

    /* renamed from: b  reason: collision with root package name */
    public ArrayList<m> f6361b;

    public static long a(f fVar, long j4) {
        m mVar = fVar.f6341d;
        if (mVar instanceof i) {
            return j4;
        }
        ArrayList arrayList = fVar.f6347k;
        int size = arrayList.size();
        long j5 = j4;
        for (int i4 = 0; i4 < size; i4++) {
            d dVar = (d) arrayList.get(i4);
            if (dVar instanceof f) {
                f fVar2 = (f) dVar;
                if (fVar2.f6341d != mVar) {
                    j5 = Math.min(j5, a(fVar2, fVar2.f + j4));
                }
            }
        }
        if (fVar == mVar.f6371i) {
            long j6 = mVar.j();
            f fVar3 = mVar.f6370h;
            long j7 = j4 - j6;
            return Math.min(Math.min(j5, a(fVar3, j7)), j7 - fVar3.f);
        }
        return j5;
    }

    public static long b(f fVar, long j4) {
        m mVar = fVar.f6341d;
        if (mVar instanceof i) {
            return j4;
        }
        ArrayList arrayList = fVar.f6347k;
        int size = arrayList.size();
        long j5 = j4;
        for (int i4 = 0; i4 < size; i4++) {
            d dVar = (d) arrayList.get(i4);
            if (dVar instanceof f) {
                f fVar2 = (f) dVar;
                if (fVar2.f6341d != mVar) {
                    j5 = Math.max(j5, b(fVar2, fVar2.f + j4));
                }
            }
        }
        if (fVar == mVar.f6370h) {
            long j6 = mVar.j();
            f fVar3 = mVar.f6371i;
            long j7 = j4 + j6;
            return Math.max(Math.max(j5, b(fVar3, j7)), j7 - fVar3.f);
        }
        return j5;
    }
}
