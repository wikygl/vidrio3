package P2;

import P2.l;
import android.graphics.Canvas;
import android.graphics.Matrix;
import java.util.ArrayList;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class k extends l.f {

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ List f1894c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ Matrix f1895d;

    public k(ArrayList arrayList, Matrix matrix) {
        this.f1894c = arrayList;
        this.f1895d = matrix;
    }

    @Override // P2.l.f
    public final void a(Matrix matrix, O2.a aVar, int i4, Canvas canvas) {
        for (l.f fVar : this.f1894c) {
            fVar.a(this.f1895d, aVar, i4, canvas);
        }
    }
}
