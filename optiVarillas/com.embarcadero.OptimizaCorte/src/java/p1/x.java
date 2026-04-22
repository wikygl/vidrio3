package p1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Arrays;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class x extends SQLiteOpenHelper {

    /* renamed from: l  reason: collision with root package name */
    public static final String f5546l = "INSERT INTO global_log_event_state VALUES (" + System.currentTimeMillis() + ")";

    /* renamed from: m  reason: collision with root package name */
    public static final int f5547m = 5;

    /* renamed from: n  reason: collision with root package name */
    public static final List<a> f5548n = Arrays.asList(new Object(), new Object(), new Object(), new Object(), new Object());

    /* renamed from: j  reason: collision with root package name */
    public final int f5549j;

    /* renamed from: k  reason: collision with root package name */
    public boolean f5550k;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface a {
        void a(SQLiteDatabase sQLiteDatabase);
    }

    public x(int i4, Context context, String str) {
        super(context, str, (SQLiteDatabase.CursorFactory) null, i4);
        this.f5550k = false;
        this.f5549j = i4;
    }

    public static void a(SQLiteDatabase sQLiteDatabase, int i4, int i5) {
        List<a> list = f5548n;
        if (i5 <= list.size()) {
            while (i4 < i5) {
                list.get(i4).a(sQLiteDatabase);
                i4++;
            }
            return;
        }
        throw new IllegalArgumentException("Migration from " + i4 + " to " + i5 + " was requested, but cannot be performed. Only " + list.size() + " migrations are provided");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onConfigure(SQLiteDatabase sQLiteDatabase) {
        this.f5550k = true;
        sQLiteDatabase.rawQuery("PRAGMA busy_timeout=0;", new String[0]).close();
        sQLiteDatabase.setForeignKeyConstraintsEnabled(true);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onCreate(SQLiteDatabase sQLiteDatabase) {
        if (!this.f5550k) {
            onConfigure(sQLiteDatabase);
        }
        a(sQLiteDatabase, 0, this.f5549j);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onDowngrade(SQLiteDatabase sQLiteDatabase, int i4, int i5) {
        sQLiteDatabase.execSQL("DROP TABLE events");
        sQLiteDatabase.execSQL("DROP TABLE event_metadata");
        sQLiteDatabase.execSQL("DROP TABLE transport_contexts");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS event_payloads");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS log_event_dropped");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS global_log_event_state");
        if (!this.f5550k) {
            onConfigure(sQLiteDatabase);
        }
        a(sQLiteDatabase, 0, i5);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onOpen(SQLiteDatabase sQLiteDatabase) {
        if (!this.f5550k) {
            onConfigure(sQLiteDatabase);
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i4, int i5) {
        if (!this.f5550k) {
            onConfigure(sQLiteDatabase);
        }
        a(sQLiteDatabase, i4, i5);
    }
}
