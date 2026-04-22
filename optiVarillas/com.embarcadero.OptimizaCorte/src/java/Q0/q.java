package Q0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class q {

    /* renamed from: a  reason: collision with root package name */
    public int f1994a;

    /* renamed from: b  reason: collision with root package name */
    public final Object f1995b;

    public /* synthetic */ q(int i4, Object obj) {
        this.f1995b = obj;
        this.f1994a = i4;
    }

    public Object a() {
        int i4 = this.f1994a;
        if (i4 <= 0) {
            return null;
        }
        int i5 = i4 - 1;
        Object[] objArr = (Object[]) this.f1995b;
        Object obj = objArr[i5];
        objArr[i5] = null;
        this.f1994a = i4 - 1;
        return obj;
    }

    public void b(u.b bVar) {
        int i4 = this.f1994a;
        Object[] objArr = (Object[]) this.f1995b;
        if (i4 < objArr.length) {
            objArr[i4] = bVar;
            this.f1994a = i4 + 1;
        }
    }

    public q() {
        this.f1995b = new Object[256];
    }
}
