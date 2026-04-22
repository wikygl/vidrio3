package j$.time.format;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e implements g {

    /* renamed from: a  reason: collision with root package name */
    private final char f3928a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public e(char c4) {
        this.f3928a = c4;
    }

    @Override // j$.time.format.g
    public final boolean j(q qVar, StringBuilder sb) {
        sb.append(this.f3928a);
        return true;
    }

    public final String toString() {
        char c4 = this.f3928a;
        if (c4 == '\'') {
            return "''";
        }
        return "'" + c4 + "'";
    }
}
