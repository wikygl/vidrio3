package r0;

import android.database.sqlite.SQLiteStatement;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class e extends d {

    /* renamed from: k  reason: collision with root package name */
    public final SQLiteStatement f5706k;

    public e(SQLiteStatement sQLiteStatement) {
        super(sQLiteStatement);
        this.f5706k = sQLiteStatement;
    }

    public final void i() {
        this.f5706k.executeUpdateDelete();
    }
}
