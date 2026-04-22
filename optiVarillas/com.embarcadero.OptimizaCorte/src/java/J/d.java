package J;

import S0.C0284u0;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class d {

    /* renamed from: a  reason: collision with root package name */
    public static final J.c f1155a = new J.c(0);

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface a {
        Cursor a(Uri uri, String[] strArr, String[] strArr2);

        void close();
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class b implements a {

        /* renamed from: a  reason: collision with root package name */
        public final ContentProviderClient f1156a;

        public b(Context context, Uri uri) {
            this.f1156a = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }

        @Override // J.d.a
        public final Cursor a(Uri uri, String[] strArr, String[] strArr2) {
            ContentProviderClient contentProviderClient = this.f1156a;
            if (contentProviderClient == null) {
                return null;
            }
            try {
                return contentProviderClient.query(uri, strArr, "query = ?", strArr2, null, null);
            } catch (RemoteException e4) {
                Log.w("FontsProvider", "Unable to query the content provider", e4);
                return null;
            }
        }

        @Override // J.d.a
        public final void close() {
            ContentProviderClient contentProviderClient = this.f1156a;
            if (contentProviderClient != null) {
                contentProviderClient.release();
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class c implements a {

        /* renamed from: a  reason: collision with root package name */
        public final ContentProviderClient f1157a;

        public c(Context context, Uri uri) {
            this.f1157a = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }

        @Override // J.d.a
        public final Cursor a(Uri uri, String[] strArr, String[] strArr2) {
            ContentProviderClient contentProviderClient = this.f1157a;
            if (contentProviderClient == null) {
                return null;
            }
            try {
                return contentProviderClient.query(uri, strArr, "query = ?", strArr2, null, null);
            } catch (RemoteException e4) {
                Log.w("FontsProvider", "Unable to query the content provider", e4);
                return null;
            }
        }

        @Override // J.d.a
        public final void close() {
            ContentProviderClient contentProviderClient = this.f1157a;
            if (contentProviderClient != null) {
                contentProviderClient.release();
            }
        }
    }

    public static k a(Context context, e eVar) {
        Cursor cursor;
        a cVar;
        int i4;
        int i5;
        Uri withAppendedId;
        int i6;
        boolean z4;
        PackageManager packageManager = context.getPackageManager();
        Resources resources = context.getResources();
        String str = eVar.f1158a;
        ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(str, 0);
        if (resolveContentProvider != null) {
            String str2 = resolveContentProvider.packageName;
            String str3 = eVar.f1159b;
            if (str2.equals(str3)) {
                Signature[] signatureArr = packageManager.getPackageInfo(resolveContentProvider.packageName, 64).signatures;
                ArrayList arrayList = new ArrayList();
                for (Signature signature : signatureArr) {
                    arrayList.add(signature.toByteArray());
                }
                J.c cVar2 = f1155a;
                Collections.sort(arrayList, cVar2);
                List<List<byte[]>> list = eVar.f1161d;
                if (list == null) {
                    list = D.e.b(resources, 0);
                }
                int i7 = 0;
                loop1: while (true) {
                    cursor = null;
                    if (i7 < list.size()) {
                        ArrayList arrayList2 = new ArrayList(list.get(i7));
                        Collections.sort(arrayList2, cVar2);
                        if (arrayList.size() == arrayList2.size()) {
                            for (int i8 = 0; i8 < arrayList.size(); i8++) {
                                if (!Arrays.equals((byte[]) arrayList.get(i8), (byte[]) arrayList2.get(i8))) {
                                    break;
                                }
                            }
                            break loop1;
                        }
                        i7++;
                    } else {
                        resolveContentProvider = null;
                        break;
                    }
                }
                if (resolveContentProvider == null) {
                    return new k(1, null);
                }
                String str4 = resolveContentProvider.authority;
                ArrayList arrayList3 = new ArrayList();
                Uri build = new Uri.Builder().scheme("content").authority(str4).build();
                Uri build2 = new Uri.Builder().scheme("content").authority(str4).appendPath("file").build();
                if (Build.VERSION.SDK_INT < 24) {
                    cVar = new b(context, build);
                } else {
                    cVar = new c(context, build);
                }
                try {
                    cursor = cVar.a(build, new String[]{"_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code"}, new String[]{eVar.f1160c});
                    if (cursor != null && cursor.getCount() > 0) {
                        int columnIndex = cursor.getColumnIndex("result_code");
                        arrayList3 = new ArrayList();
                        int columnIndex2 = cursor.getColumnIndex("_id");
                        int columnIndex3 = cursor.getColumnIndex("file_id");
                        int columnIndex4 = cursor.getColumnIndex("font_ttc_index");
                        int columnIndex5 = cursor.getColumnIndex("font_weight");
                        int columnIndex6 = cursor.getColumnIndex("font_italic");
                        while (cursor.moveToNext()) {
                            if (columnIndex != -1) {
                                i4 = cursor.getInt(columnIndex);
                            } else {
                                i4 = 0;
                            }
                            if (columnIndex4 != -1) {
                                i5 = cursor.getInt(columnIndex4);
                            } else {
                                i5 = 0;
                            }
                            if (columnIndex3 == -1) {
                                withAppendedId = ContentUris.withAppendedId(build, cursor.getLong(columnIndex2));
                            } else {
                                withAppendedId = ContentUris.withAppendedId(build2, cursor.getLong(columnIndex3));
                            }
                            Uri uri = withAppendedId;
                            if (columnIndex5 != -1) {
                                i6 = cursor.getInt(columnIndex5);
                            } else {
                                i6 = 400;
                            }
                            if (columnIndex6 != -1 && cursor.getInt(columnIndex6) == 1) {
                                z4 = true;
                            } else {
                                z4 = false;
                            }
                            arrayList3.add(new l(uri, i5, i6, z4, i4));
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    cVar.close();
                    return new k(0, (l[]) arrayList3.toArray(new l[0]));
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    cVar.close();
                    throw th;
                }
            }
            throw new PackageManager.NameNotFoundException("Found content provider " + str + ", but package was not " + str3);
        }
        throw new PackageManager.NameNotFoundException(C0284u0.c("No package found for authority: ", str));
    }
}
