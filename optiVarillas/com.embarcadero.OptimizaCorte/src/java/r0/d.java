package r0;

import android.database.sqlite.SQLiteProgram;
import java.io.Closeable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class d implements Closeable {

    /* renamed from: j  reason: collision with root package name */
    public final SQLiteProgram f5705j;

    public d(SQLiteProgram sQLiteProgram) {
        this.f5705j = sQLiteProgram;
    }

    public final void a(int i4, byte[] bArr) {
        this.f5705j.bindBlob(i4, bArr);
    }

    public final void b(int i4, double d4) {
        this.f5705j.bindDouble(i4, d4);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        this.f5705j.close();
    }

    public final void d(int i4, long j4) {
        this.f5705j.bindLong(i4, j4);
    }

    public final void f(int i4) {
        this.f5705j.bindNull(i4);
    }

    public final void g(String str, int i4) {
        this.f5705j.bindString(i4, str);
    }
}
