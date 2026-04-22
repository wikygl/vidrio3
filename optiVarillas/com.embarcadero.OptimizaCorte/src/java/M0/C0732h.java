package m0;

import androidx.work.impl.WorkDatabase_Impl;
import q0.InterfaceC0767b;
import r0.C0779a;

/* renamed from: m0.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0732h extends InterfaceC0767b.a {

    /* renamed from: b  reason: collision with root package name */
    public C0725a f5326b;

    /* renamed from: c  reason: collision with root package name */
    public final a f5327c;

    /* renamed from: m0.h$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class a {
        public abstract void a(C0779a c0779a);

        public abstract b b(C0779a c0779a);
    }

    /* renamed from: m0.h$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public final boolean f5328a;

        /* renamed from: b  reason: collision with root package name */
        public final String f5329b;

        public b(String str, boolean z4) {
            this.f5328a = z4;
            this.f5329b = str;
        }
    }

    public C0732h(C0725a c0725a, WorkDatabase_Impl.a aVar) {
        this.f5326b = c0725a;
        this.f5327c = aVar;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0038  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0036 A[EDGE_INSN: B:86:0x0036->B:17:0x0036 ?: BREAK  , SYNTHETIC] */
    @Override // q0.InterfaceC0767b.a
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void b(r0.C0779a r13, int r14, int r15) {
        /*
            Method dump skipped, instructions count: 358
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: m0.C0732h.b(r0.a, int, int):void");
    }

    public final void c(C0779a c0779a) {
        c0779a.d("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        c0779a.d("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c103703e120ae8cc73c9248622f3cd1e')");
    }
}
