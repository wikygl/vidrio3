package j$.time.format;

import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class f implements g {

    /* renamed from: a  reason: collision with root package name */
    private final g[] f3929a;

    /* renamed from: b  reason: collision with root package name */
    private final boolean f3930b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(List list, boolean z4) {
        this((g[]) list.toArray(new g[list.size()]), z4);
    }

    f(g[] gVarArr, boolean z4) {
        this.f3929a = gVarArr;
        this.f3930b = z4;
    }

    public final f a() {
        return !this.f3930b ? this : new f(this.f3929a, false);
    }

    @Override // j$.time.format.g
    public final boolean j(q qVar, StringBuilder sb) {
        int length = sb.length();
        boolean z4 = this.f3930b;
        if (z4) {
            qVar.g();
        }
        try {
            for (g gVar : this.f3929a) {
                if (!gVar.j(qVar, sb)) {
                    sb.setLength(length);
                    return true;
                }
            }
            if (z4) {
                qVar.a();
            }
            return true;
        } finally {
            if (z4) {
                qVar.a();
            }
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        g[] gVarArr = this.f3929a;
        if (gVarArr != null) {
            boolean z4 = this.f3930b;
            sb.append(z4 ? "[" : "(");
            for (g gVar : gVarArr) {
                sb.append(gVar);
            }
            sb.append(z4 ? "]" : ")");
        }
        return sb.toString();
    }
}
