package r0;

import E1.i;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import q0.InterfaceC0766a;
import q0.InterfaceC0768c;

/* renamed from: r0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0779a implements InterfaceC0766a {

    /* renamed from: k  reason: collision with root package name */
    public static final String[] f5690k = new String[0];

    /* renamed from: j  reason: collision with root package name */
    public final SQLiteDatabase f5691j;

    /* renamed from: r0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class C0064a implements SQLiteDatabase.CursorFactory {

        /* renamed from: a  reason: collision with root package name */
        public final /* synthetic */ InterfaceC0768c f5692a;

        public C0064a(InterfaceC0768c interfaceC0768c) {
            this.f5692a = interfaceC0768c;
        }

        @Override // android.database.sqlite.SQLiteDatabase.CursorFactory
        public final Cursor newCursor(SQLiteDatabase sQLiteDatabase, SQLiteCursorDriver sQLiteCursorDriver, String str, SQLiteQuery sQLiteQuery) {
            this.f5692a.a(new d(sQLiteQuery));
            return new SQLiteCursor(sQLiteCursorDriver, str, sQLiteQuery);
        }
    }

    public C0779a(SQLiteDatabase sQLiteDatabase) {
        this.f5691j = sQLiteDatabase;
    }

    public final void a() {
        this.f5691j.beginTransaction();
    }

    public final void b() {
        this.f5691j.endTransaction();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        this.f5691j.close();
    }

    public final void d(String str) {
        this.f5691j.execSQL(str);
    }

    public final Cursor f(String str) {
        return g(new i(str));
    }

    public final Cursor g(InterfaceC0768c interfaceC0768c) {
        return this.f5691j.rawQueryWithFactory(new C0064a(interfaceC0768c), interfaceC0768c.d(), f5690k, null);
    }

    public final void i() {
        this.f5691j.setTransactionSuccessful();
    }
}
