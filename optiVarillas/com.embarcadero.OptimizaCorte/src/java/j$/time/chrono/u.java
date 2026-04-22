package j$.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class u extends AbstractC0483a implements Serializable {

    /* renamed from: d  reason: collision with root package name */
    public static final u f3906d = new u();
    private static final long serialVersionUID = -1440403870442975015L;

    private u() {
    }

    public static boolean m(long j4) {
        return (3 & j4) == 0 && (j4 % 100 != 0 || j4 % 400 == 0);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    @Override // j$.time.chrono.n
    public final o B(int i4) {
        if (i4 != 0) {
            if (i4 == 1) {
                return v.CE;
            }
            throw new RuntimeException("Invalid era: " + i4);
        }
        return v.BCE;
    }

    @Override // j$.time.chrono.n
    public final String i() {
        return "ISO";
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0484b k(j$.time.temporal.o oVar) {
        return j$.time.i.F(oVar);
    }

    @Override // j$.time.chrono.AbstractC0483a, j$.time.chrono.n
    public final InterfaceC0487e n(j$.time.k kVar) {
        return j$.time.k.E(kVar);
    }

    @Override // j$.time.chrono.n
    public final String q() {
        return "iso8601";
    }

    Object writeReplace() {
        return new G((byte) 1, this);
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0493k y(j$.time.g gVar, j$.time.A a4) {
        return j$.time.E.E(gVar, a4);
    }
}
