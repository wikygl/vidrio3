package C3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class g0 extends AbstractC0171v {
    public abstract g0 H();

    @Override // C3.AbstractC0171v
    public String toString() {
        g0 g0Var;
        String str;
        G3.c cVar = K.f431a;
        g0 g0Var2 = F3.q.f944a;
        if (this == g0Var2) {
            str = "Dispatchers.Main";
        } else {
            try {
                g0Var = g0Var2.H();
            } catch (UnsupportedOperationException unused) {
                g0Var = null;
            }
            if (this == g0Var) {
                str = "Dispatchers.Main.immediate";
            } else {
                str = null;
            }
        }
        if (str == null) {
            return getClass().getSimpleName() + '@' + C.c(this);
        }
        return str;
    }
}
