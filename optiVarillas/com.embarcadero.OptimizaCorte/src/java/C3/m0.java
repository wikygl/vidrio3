package C3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class m0 {

    /* renamed from: a  reason: collision with root package name */
    public static final ThreadLocal<N> f493a = new ThreadLocal<>();

    public static N a() {
        ThreadLocal<N> threadLocal = f493a;
        N n4 = threadLocal.get();
        if (n4 == null) {
            C0153c c0153c = new C0153c(Thread.currentThread());
            threadLocal.set(c0153c);
            return c0153c;
        }
        return n4;
    }
}
