package T1;

import java.lang.ref.WeakReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class u extends s {

    /* renamed from: l  reason: collision with root package name */
    public static final WeakReference f2366l = new WeakReference(null);

    /* renamed from: k  reason: collision with root package name */
    public WeakReference f2367k;

    public u(byte[] bArr) {
        super(bArr);
        this.f2367k = f2366l;
    }

    public abstract byte[] L1();

    @Override // T1.s
    public final byte[] p0() {
        byte[] bArr;
        synchronized (this) {
            try {
                bArr = (byte[]) this.f2367k.get();
                if (bArr == null) {
                    bArr = L1();
                    this.f2367k = new WeakReference(bArr);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return bArr;
    }
}
