package J;

import J.j;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class i implements L.a<j.a> {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ String f1172a;

    public i(String str) {
        this.f1172a = str;
    }

    @Override // L.a
    public final void a(j.a aVar) {
        j.a aVar2 = aVar;
        synchronized (j.f1175c) {
            try {
                r.j<String, ArrayList<L.a<j.a>>> jVar = j.f1176d;
                ArrayList<L.a<j.a>> orDefault = jVar.getOrDefault(this.f1172a, null);
                if (orDefault != null) {
                    jVar.remove(this.f1172a);
                    for (int i4 = 0; i4 < orDefault.size(); i4++) {
                        orDefault.get(i4).a(aVar2);
                    }
                }
            } finally {
            }
        }
    }
}
