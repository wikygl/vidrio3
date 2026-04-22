package C3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class M implements U {

    /* renamed from: j  reason: collision with root package name */
    public final boolean f433j;

    public M(boolean z4) {
        this.f433j = z4;
    }

    @Override // C3.U
    public final boolean a() {
        return this.f433j;
    }

    @Override // C3.U
    public final h0 h() {
        return null;
    }

    public final String toString() {
        String str;
        StringBuilder sb = new StringBuilder("Empty{");
        if (this.f433j) {
            str = "Active";
        } else {
            str = "New";
        }
        sb.append(str);
        sb.append('}');
        return sb.toString();
    }
}
