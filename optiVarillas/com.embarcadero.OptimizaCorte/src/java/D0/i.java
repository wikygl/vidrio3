package D0;

import android.content.Context;
import android.os.Build;
import java.io.File;
import java.util.HashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class i {

    /* renamed from: a  reason: collision with root package name */
    public static final String f580a = C0.i.e("WrkDbPathHelper");

    /* renamed from: b  reason: collision with root package name */
    public static final String[] f581b = {"-journal", "-shm", "-wal"};

    public static void a(Context context) {
        String format;
        File file;
        String[] strArr;
        File databasePath = context.getDatabasePath("androidx.work.workdb");
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 23 && databasePath.exists()) {
            String str = f580a;
            C0.i.c().a(str, "Migrating WorkDatabase to the no-backup directory", new Throwable[0]);
            HashMap hashMap = new HashMap();
            if (i4 >= 23) {
                File databasePath2 = context.getDatabasePath("androidx.work.workdb");
                if (i4 < 23) {
                    file = context.getDatabasePath("androidx.work.workdb");
                } else {
                    file = new File(context.getNoBackupFilesDir(), "androidx.work.workdb");
                }
                hashMap.put(databasePath2, file);
                for (String str2 : f581b) {
                    hashMap.put(new File(databasePath2.getPath() + str2), new File(file.getPath() + str2));
                }
            }
            for (File file2 : hashMap.keySet()) {
                File file3 = (File) hashMap.get(file2);
                if (file2.exists() && file3 != null) {
                    if (file3.exists()) {
                        C0.i.c().f(str, String.format("Over-writing contents of %s", file3), new Throwable[0]);
                    }
                    if (file2.renameTo(file3)) {
                        format = String.format("Migrated %s to %s", file2, file3);
                    } else {
                        format = String.format("Renaming %s to %s failed", file2, file3);
                    }
                    C0.i.c().a(str, format, new Throwable[0]);
                }
            }
        }
    }
}
