package C3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class G<T> extends AbstractC0151a<T> implements F<T> {
    @Override // C3.F
    public final T f() {
        V v4;
        T t3;
        T t4 = (T) G();
        if (!(t4 instanceof U)) {
            if (!(t4 instanceof C0162l)) {
                if (t4 instanceof V) {
                    v4 = (V) t4;
                } else {
                    v4 = null;
                }
                if (v4 != null && (t3 = (T) v4.f446a) != null) {
                    return t3;
                }
                return t4;
            }
            throw ((C0162l) t4).f490a;
        }
        throw new IllegalStateException("This job has not completed yet".toString());
    }
}
