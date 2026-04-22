package w2;

import M.Z;
import M.e0;
import android.view.View;
import java.util.Iterator;
import java.util.List;
import r2.C0783a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class d extends Z.b {

    /* renamed from: c  reason: collision with root package name */
    public final View f6409c;

    /* renamed from: d  reason: collision with root package name */
    public int f6410d;

    /* renamed from: e  reason: collision with root package name */
    public int f6411e;
    public final int[] f = new int[2];

    public d(View view) {
        this.f6409c = view;
    }

    @Override // M.Z.b
    public final e0 a(e0 e0Var, List<Z> list) {
        Iterator<Z> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Z next = it.next();
            if ((next.f1552a.c() & 8) != 0) {
                int i4 = this.f6411e;
                this.f6409c.setTranslationY(C0783a.c(next.f1552a.b(), i4, 0));
                break;
            }
        }
        return e0Var;
    }
}
