package E;

import D.e;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.InputStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class l {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface a<T> {
        int a(T t3);

        boolean b(T t3);
    }

    public l() {
        new ConcurrentHashMap();
    }

    public Typeface a(Context context, e.c cVar, Resources resources, int i4) {
        throw null;
    }

    public Typeface b(Context context, J.l[] lVarArr, int i4) {
        throw null;
    }

    public Typeface c(Context context, InputStream inputStream) {
        File d4 = m.d(context);
        if (d4 == null) {
            return null;
        }
        try {
            if (!m.c(d4, inputStream)) {
                return null;
            }
            return Typeface.createFromFile(d4.getPath());
        } catch (RuntimeException unused) {
            return null;
        } finally {
            d4.delete();
        }
    }

    public Typeface d(Context context, Resources resources, int i4, String str, int i5) {
        File d4 = m.d(context);
        if (d4 == null) {
            return null;
        }
        try {
            if (!m.b(d4, resources, i4)) {
                return null;
            }
            return Typeface.createFromFile(d4.getPath());
        } catch (RuntimeException unused) {
            return null;
        } finally {
            d4.delete();
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [E.l$a, java.lang.Object] */
    public J.l e(int i4, J.l[] lVarArr) {
        int i5;
        boolean z4;
        int i6;
        ?? obj = new Object();
        if ((i4 & 1) == 0) {
            i5 = 400;
        } else {
            i5 = 700;
        }
        if ((i4 & 2) != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        J.l lVar = null;
        int i7 = Integer.MAX_VALUE;
        for (J.l lVar2 : lVarArr) {
            int abs = Math.abs(obj.a(lVar2) - i5) * 2;
            if (obj.b(lVar2) == z4) {
                i6 = 0;
            } else {
                i6 = 1;
            }
            int i8 = abs + i6;
            if (lVar == null || i7 > i8) {
                lVar = lVar2;
                i7 = i8;
            }
        }
        return lVar;
    }
}
