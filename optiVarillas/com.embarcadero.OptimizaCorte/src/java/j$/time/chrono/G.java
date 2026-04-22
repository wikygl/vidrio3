package j$.time.chrono;

import java.io.Externalizable;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class G implements Externalizable {
    private static final long serialVersionUID = -6103370247208168577L;

    /* renamed from: a  reason: collision with root package name */
    private byte f3864a;

    /* renamed from: b  reason: collision with root package name */
    private Object f3865b;

    public G() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public G(byte b4, Object obj) {
        this.f3864a = b4;
        this.f3865b = obj;
    }

    private Object readResolve() {
        return this.f3865b;
    }

    @Override // java.io.Externalizable
    public final void readExternal(ObjectInput objectInput) {
        Object j4;
        byte readByte = objectInput.readByte();
        this.f3864a = readByte;
        switch (readByte) {
            case 1:
                int i4 = AbstractC0483a.f3873c;
                j4 = AbstractC0483a.j(objectInput.readUTF());
                break;
            case 2:
                j4 = ((InterfaceC0484b) objectInput.readObject()).t((j$.time.m) objectInput.readObject());
                break;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                j4 = ((InterfaceC0487e) objectInput.readObject()).o((j$.time.B) objectInput.readObject()).h((j$.time.A) objectInput.readObject());
                break;
            case 4:
                j$.time.i iVar = z.f3911d;
                int readInt = objectInput.readInt();
                byte readByte2 = objectInput.readByte();
                byte readByte3 = objectInput.readByte();
                x.f3909d.getClass();
                j4 = new z(j$.time.i.O(readInt, readByte2, readByte3));
                break;
            case 5:
                A a4 = A.f3854d;
                j4 = A.y(objectInput.readByte());
                break;
            case 6:
                q qVar = (q) objectInput.readObject();
                int readInt2 = objectInput.readInt();
                byte readByte4 = objectInput.readByte();
                byte readByte5 = objectInput.readByte();
                qVar.getClass();
                j4 = s.L(qVar, readInt2, readByte4, readByte5);
                break;
            case 7:
                int readInt3 = objectInput.readInt();
                byte readByte6 = objectInput.readByte();
                byte readByte7 = objectInput.readByte();
                C.f3860d.getClass();
                j4 = new E(j$.time.i.O(readInt3 + 1911, readByte6, readByte7));
                break;
            case 8:
                int readInt4 = objectInput.readInt();
                byte readByte8 = objectInput.readByte();
                byte readByte9 = objectInput.readByte();
                I.f3867d.getClass();
                j4 = new K(j$.time.i.O(readInt4 - 543, readByte8, readByte9));
                break;
            case 9:
                int i5 = C0490h.f3878e;
                j4 = new C0490h(AbstractC0483a.j(objectInput.readUTF()), objectInput.readInt(), objectInput.readInt(), objectInput.readInt());
                break;
            default:
                throw new StreamCorruptedException("Unknown serialized type");
        }
        this.f3865b = j4;
    }

    @Override // java.io.Externalizable
    public final void writeExternal(ObjectOutput objectOutput) {
        byte b4 = this.f3864a;
        Object obj = this.f3865b;
        objectOutput.writeByte(b4);
        switch (b4) {
            case 1:
                objectOutput.writeUTF(((AbstractC0483a) obj).i());
                return;
            case 2:
                ((C0489g) obj).writeExternal(objectOutput);
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((m) obj).writeExternal(objectOutput);
                return;
            case 4:
                z zVar = (z) obj;
                zVar.getClass();
                objectOutput.writeInt(j$.time.temporal.n.a(zVar, j$.time.temporal.a.YEAR));
                objectOutput.writeByte(j$.time.temporal.n.a(zVar, j$.time.temporal.a.MONTH_OF_YEAR));
                objectOutput.writeByte(j$.time.temporal.n.a(zVar, j$.time.temporal.a.DAY_OF_MONTH));
                return;
            case 5:
                ((A) obj).E(objectOutput);
                return;
            case 6:
                ((s) obj).writeExternal(objectOutput);
                return;
            case 7:
                E e4 = (E) obj;
                e4.getClass();
                objectOutput.writeInt(j$.time.temporal.n.a(e4, j$.time.temporal.a.YEAR));
                objectOutput.writeByte(j$.time.temporal.n.a(e4, j$.time.temporal.a.MONTH_OF_YEAR));
                objectOutput.writeByte(j$.time.temporal.n.a(e4, j$.time.temporal.a.DAY_OF_MONTH));
                return;
            case 8:
                K k4 = (K) obj;
                k4.getClass();
                objectOutput.writeInt(j$.time.temporal.n.a(k4, j$.time.temporal.a.YEAR));
                objectOutput.writeByte(j$.time.temporal.n.a(k4, j$.time.temporal.a.MONTH_OF_YEAR));
                objectOutput.writeByte(j$.time.temporal.n.a(k4, j$.time.temporal.a.DAY_OF_MONTH));
                return;
            case 9:
                ((C0490h) obj).a(objectOutput);
                return;
            default:
                throw new InvalidClassException("Unknown serialized type");
        }
    }
}
