package q0;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import r0.C0779a;

/* renamed from: q0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public interface InterfaceC0767b extends Closeable {

    /* renamed from: q0.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static abstract class a {

        /* renamed from: a  reason: collision with root package name */
        public final int f5598a = 12;

        public static void a(String str) {
            if (!str.equalsIgnoreCase(":memory:") && str.trim().length() != 0) {
                Log.w("SupportSQLite", "deleting the database file: ".concat(str));
                try {
                    SQLiteDatabase.deleteDatabase(new File(str));
                } catch (Exception e4) {
                    Log.w("SupportSQLite", "delete failed: ", e4);
                }
            }
        }

        public abstract void b(C0779a c0779a, int i4, int i5);
    }

    /* renamed from: q0.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class C0062b {

        /* renamed from: a  reason: collision with root package name */
        public final Context f5599a;

        /* renamed from: b  reason: collision with root package name */
        public final String f5600b;

        /* renamed from: c  reason: collision with root package name */
        public final a f5601c;

        /* renamed from: d  reason: collision with root package name */
        public final boolean f5602d;

        public C0062b(Context context, String str, a aVar, boolean z4) {
            this.f5599a = context;
            this.f5600b = str;
            this.f5601c = aVar;
            this.f5602d = z4;
        }
    }

    /* renamed from: q0.b$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface c {
        InterfaceC0767b a(C0062b c0062b);
    }

    InterfaceC0766a D();

    void setWriteAheadLoggingEnabled(boolean z4);
}
