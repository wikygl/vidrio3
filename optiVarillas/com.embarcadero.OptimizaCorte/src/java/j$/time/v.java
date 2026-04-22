package j$.time;

import java.io.Externalizable;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class v implements Externalizable {
    private static final long serialVersionUID = -7683839454370182990L;

    /* renamed from: a  reason: collision with root package name */
    private byte f4036a;

    /* renamed from: b  reason: collision with root package name */
    private Object f4037b;

    public v() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public v(byte b4, Object obj) {
        this.f4036a = b4;
        this.f4037b = obj;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Serializable a(ObjectInput objectInput) {
        return b(objectInput.readByte(), objectInput);
    }

    private static Serializable b(byte b4, ObjectInput objectInput) {
        switch (b4) {
            case 1:
                Duration duration = Duration.f3846c;
                return Duration.u(objectInput.readLong(), objectInput.readInt());
            case 2:
                g gVar = g.f3966c;
                return g.H(objectInput.readLong(), objectInput.readInt());
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                i iVar = i.f3971d;
                return i.O(objectInput.readInt(), objectInput.readByte(), objectInput.readByte());
            case 4:
                return m.S(objectInput);
            case 5:
                k kVar = k.f3977c;
                i iVar2 = i.f3971d;
                return k.L(i.O(objectInput.readInt(), objectInput.readByte(), objectInput.readByte()), m.S(objectInput));
            case 6:
                return E.H(objectInput);
            case 7:
                int i4 = C.f3842c;
                return A.E(objectInput.readUTF());
            case 8:
                return B.O(objectInput);
            case 9:
                return t.F(objectInput);
            case 10:
                return s.F(objectInput);
            case 11:
                int i5 = x.f4040b;
                return x.D(objectInput.readInt());
            case 12:
                return z.H(objectInput);
            case 13:
                return q.D(objectInput);
            case 14:
                return u.a(objectInput);
            default:
                throw new StreamCorruptedException("Unknown serialized type");
        }
    }

    private Object readResolve() {
        return this.f4037b;
    }

    @Override // java.io.Externalizable
    public final void readExternal(ObjectInput objectInput) {
        byte readByte = objectInput.readByte();
        this.f4036a = readByte;
        this.f4037b = b(readByte, objectInput);
    }

    @Override // java.io.Externalizable
    public final void writeExternal(ObjectOutput objectOutput) {
        byte b4 = this.f4036a;
        Object obj = this.f4037b;
        objectOutput.writeByte(b4);
        switch (b4) {
            case 1:
                ((Duration) obj).v(objectOutput);
                return;
            case 2:
                ((g) obj).L(objectOutput);
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((i) obj).a0(objectOutput);
                return;
            case 4:
                ((m) obj).X(objectOutput);
                return;
            case 5:
                ((k) obj).U(objectOutput);
                return;
            case 6:
                ((E) obj).K(objectOutput);
                return;
            case 7:
                ((C) obj).J(objectOutput);
                return;
            case 8:
                ((B) obj).P(objectOutput);
                return;
            case 9:
                ((t) obj).writeExternal(objectOutput);
                return;
            case 10:
                ((s) obj).writeExternal(objectOutput);
                return;
            case 11:
                ((x) obj).H(objectOutput);
                return;
            case 12:
                ((z) obj).K(objectOutput);
                return;
            case 13:
                ((q) obj).E(objectOutput);
                return;
            case 14:
                ((u) obj).b(objectOutput);
                return;
            default:
                throw new InvalidClassException("Unknown serialized type");
        }
    }
}
