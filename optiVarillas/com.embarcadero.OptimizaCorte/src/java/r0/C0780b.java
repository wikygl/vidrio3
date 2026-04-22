package r0;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.List;
import m0.C0732h;
import q0.InterfaceC0766a;
import q0.InterfaceC0767b;

/* renamed from: r0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0780b implements InterfaceC0767b {

    /* renamed from: j  reason: collision with root package name */
    public final Context f5693j;

    /* renamed from: k  reason: collision with root package name */
    public final String f5694k;

    /* renamed from: l  reason: collision with root package name */
    public final InterfaceC0767b.a f5695l;

    /* renamed from: m  reason: collision with root package name */
    public final boolean f5696m;

    /* renamed from: n  reason: collision with root package name */
    public final Object f5697n = new Object();

    /* renamed from: o  reason: collision with root package name */
    public a f5698o;

    /* renamed from: p  reason: collision with root package name */
    public boolean f5699p;

    /* renamed from: r0.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a extends SQLiteOpenHelper {

        /* renamed from: j  reason: collision with root package name */
        public final C0779a[] f5700j;

        /* renamed from: k  reason: collision with root package name */
        public final InterfaceC0767b.a f5701k;

        /* renamed from: l  reason: collision with root package name */
        public boolean f5702l;

        /* renamed from: r0.b$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public class C0065a implements DatabaseErrorHandler {

            /* renamed from: a  reason: collision with root package name */
            public final /* synthetic */ InterfaceC0767b.a f5703a;

            /* renamed from: b  reason: collision with root package name */
            public final /* synthetic */ C0779a[] f5704b;

            public C0065a(InterfaceC0767b.a aVar, C0779a[] c0779aArr) {
                this.f5703a = aVar;
                this.f5704b = c0779aArr;
            }

            @Override // android.database.DatabaseErrorHandler
            public final void onCorruption(SQLiteDatabase sQLiteDatabase) {
                C0779a a4 = a.a(this.f5704b, sQLiteDatabase);
                this.f5703a.getClass();
                Log.e("SupportSQLite", "Corruption reported by sqlite on database: " + a4.f5691j.getPath());
                SQLiteDatabase sQLiteDatabase2 = a4.f5691j;
                if (!sQLiteDatabase2.isOpen()) {
                    InterfaceC0767b.a.a(sQLiteDatabase2.getPath());
                    return;
                }
                List<Pair<String, String>> list = null;
                try {
                    try {
                        list = sQLiteDatabase2.getAttachedDbs();
                    } finally {
                        if (list != null) {
                            for (Pair<String, String> next : list) {
                                InterfaceC0767b.a.a((String) next.second);
                            }
                        } else {
                            InterfaceC0767b.a.a(sQLiteDatabase2.getPath());
                        }
                    }
                } catch (SQLiteException unused) {
                }
                try {
                    a4.close();
                } catch (IOException unused2) {
                }
            }
        }

        public a(Context context, String str, C0779a[] c0779aArr, InterfaceC0767b.a aVar) {
            super(context, str, null, aVar.f5598a, new C0065a(aVar, c0779aArr));
            this.f5701k = aVar;
            this.f5700j = c0779aArr;
        }

        public static C0779a a(C0779a[] c0779aArr, SQLiteDatabase sQLiteDatabase) {
            C0779a c0779a = c0779aArr[0];
            if (c0779a == null || c0779a.f5691j != sQLiteDatabase) {
                c0779aArr[0] = new C0779a(sQLiteDatabase);
            }
            return c0779aArr[0];
        }

        public final synchronized InterfaceC0766a b() {
            this.f5702l = false;
            SQLiteDatabase writableDatabase = getWritableDatabase();
            if (this.f5702l) {
                close();
                return b();
            }
            return a(this.f5700j, writableDatabase);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper, java.lang.AutoCloseable
        public final synchronized void close() {
            super.close();
            this.f5700j[0] = null;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onConfigure(SQLiteDatabase sQLiteDatabase) {
            a(this.f5700j, sQLiteDatabase);
            this.f5701k.getClass();
        }

        /* JADX WARN: Removed duplicated region for block: B:13:0x0030  */
        /* JADX WARN: Removed duplicated region for block: B:20:0x005c  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x006f A[ORIG_RETURN, RETURN] */
        @Override // android.database.sqlite.SQLiteOpenHelper
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final void onCreate(android.database.sqlite.SQLiteDatabase r7) {
            /*
                r6 = this;
                r0 = 1
                r0.a[] r1 = r6.f5700j
                r0.a r7 = a(r1, r7)
                q0.b$a r1 = r6.f5701k
                m0.h r1 = (m0.C0732h) r1
                r1.getClass()
                java.lang.String r2 = "SELECT count(*) FROM sqlite_master WHERE name != 'android_metadata'"
                android.database.Cursor r2 = r7.f(r2)
                boolean r3 = r2.moveToFirst()     // Catch: java.lang.Throwable -> L23
                r4 = 0
                if (r3 == 0) goto L25
                int r3 = r2.getInt(r4)     // Catch: java.lang.Throwable -> L23
                if (r3 != 0) goto L25
                r3 = 1
                goto L26
            L23:
                r7 = move-exception
                goto L70
            L25:
                r3 = 0
            L26:
                r2.close()
                m0.h$a r2 = r1.f5327c
                r2.a(r7)
                if (r3 != 0) goto L4f
                m0.h$b r3 = r2.b(r7)
                boolean r5 = r3.f5328a
                if (r5 == 0) goto L39
                goto L4f
            L39:
                java.lang.IllegalStateException r7 = new java.lang.IllegalStateException
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                java.lang.String r1 = "Pre-packaged database has an invalid schema: "
                r0.<init>(r1)
                java.lang.String r1 = r3.f5329b
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                r7.<init>(r0)
                throw r7
            L4f:
                r1.c(r7)
                androidx.work.impl.WorkDatabase_Impl$a r2 = (androidx.work.impl.WorkDatabase_Impl.a) r2
                int r7 = androidx.work.impl.WorkDatabase_Impl.s
                androidx.work.impl.WorkDatabase_Impl r7 = r2.a
                java.util.List<m0.g$b> r1 = r7.f5308g
                if (r1 == 0) goto L6f
                int r1 = r1.size()
            L60:
                if (r4 >= r1) goto L6f
                java.util.List<m0.g$b> r2 = r7.f5308g
                java.lang.Object r2 = r2.get(r4)
                m0.g$b r2 = (m0.AbstractC0731g.b) r2
                r2.getClass()
                int r4 = r4 + r0
                goto L60
            L6f:
                return
            L70:
                r2.close()
                throw r7
            */
            throw new UnsupportedOperationException("Method not decompiled: r0.C0780b.a.onCreate(android.database.sqlite.SQLiteDatabase):void");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onDowngrade(SQLiteDatabase sQLiteDatabase, int i4, int i5) {
            this.f5702l = true;
            ((C0732h) this.f5701k).b(a(this.f5700j, sQLiteDatabase), i4, i5);
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x0031  */
        /* JADX WARN: Removed duplicated region for block: B:32:0x006a  */
        /* JADX WARN: Removed duplicated region for block: B:63:0x008b A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // android.database.sqlite.SQLiteOpenHelper
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final void onOpen(android.database.sqlite.SQLiteDatabase r10) {
            /*
                Method dump skipped, instructions count: 249
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: r0.C0780b.a.onOpen(android.database.sqlite.SQLiteDatabase):void");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i4, int i5) {
            this.f5702l = true;
            this.f5701k.b(a(this.f5700j, sQLiteDatabase), i4, i5);
        }
    }

    public C0780b(Context context, String str, InterfaceC0767b.a aVar, boolean z4) {
        this.f5693j = context;
        this.f5694k = str;
        this.f5695l = aVar;
        this.f5696m = z4;
    }

    @Override // q0.InterfaceC0767b
    public final InterfaceC0766a D() {
        return a().b();
    }

    public final a a() {
        a aVar;
        synchronized (this.f5697n) {
            try {
                if (this.f5698o == null) {
                    C0779a[] c0779aArr = new C0779a[1];
                    if (Build.VERSION.SDK_INT >= 23 && this.f5694k != null && this.f5696m) {
                        this.f5698o = new a(this.f5693j, new File(this.f5693j.getNoBackupFilesDir(), this.f5694k).getAbsolutePath(), c0779aArr, this.f5695l);
                    } else {
                        this.f5698o = new a(this.f5693j, this.f5694k, c0779aArr, this.f5695l);
                    }
                    this.f5698o.setWriteAheadLoggingEnabled(this.f5699p);
                }
                aVar = this.f5698o;
            } catch (Throwable th) {
                throw th;
            }
        }
        return aVar;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        a().close();
    }

    @Override // q0.InterfaceC0767b
    public final void setWriteAheadLoggingEnabled(boolean z4) {
        synchronized (this.f5697n) {
            try {
                a aVar = this.f5698o;
                if (aVar != null) {
                    aVar.setWriteAheadLoggingEnabled(z4);
                }
                this.f5699p = z4;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
