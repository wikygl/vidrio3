package p1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import java.util.ArrayList;
import p1.q;
import s1.C0795a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class j implements q.a {
    @Override // p1.q.a
    public final Object apply(Object obj) {
        byte[] decode;
        Cursor rawQuery = ((SQLiteDatabase) obj).rawQuery("SELECT distinct t._id, t.backend_name, t.priority, t.extras FROM transport_contexts AS t, events AS e WHERE e.context_id = t._id", new String[0]);
        try {
            ArrayList arrayList = new ArrayList();
            while (rawQuery.moveToNext()) {
                String string = rawQuery.getString(1);
                if (string != null) {
                    f1.d b4 = C0795a.b(rawQuery.getInt(2));
                    String string2 = rawQuery.getString(3);
                    if (string2 == null) {
                        decode = null;
                    } else {
                        decode = Base64.decode(string2, 0);
                    }
                    arrayList.add(new i1.j(string, decode, b4));
                } else {
                    throw new NullPointerException("Null backendName");
                }
            }
            return arrayList;
        } finally {
            rawQuery.close();
        }
    }
}
