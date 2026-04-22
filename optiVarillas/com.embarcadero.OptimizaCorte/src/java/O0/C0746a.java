package o0;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import r0.C0779a;

/* renamed from: o0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0746a {

    /* renamed from: a  reason: collision with root package name */
    public final String f5405a;

    /* renamed from: b  reason: collision with root package name */
    public final Map<String, C0061a> f5406b;

    /* renamed from: c  reason: collision with root package name */
    public final Set<b> f5407c;

    /* renamed from: d  reason: collision with root package name */
    public final Set<d> f5408d;

    /* renamed from: o0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class C0061a {

        /* renamed from: a  reason: collision with root package name */
        public final String f5409a;

        /* renamed from: b  reason: collision with root package name */
        public final String f5410b;

        /* renamed from: c  reason: collision with root package name */
        public final int f5411c;

        /* renamed from: d  reason: collision with root package name */
        public final boolean f5412d;

        /* renamed from: e  reason: collision with root package name */
        public final int f5413e;
        public final String f;

        /* renamed from: g  reason: collision with root package name */
        public final int f5414g;

        public C0061a(int i4, int i5, String str, String str2, String str3, boolean z4) {
            this.f5409a = str;
            this.f5410b = str2;
            this.f5412d = z4;
            this.f5413e = i4;
            int i6 = 5;
            if (str2 != null) {
                String upperCase = str2.toUpperCase(Locale.US);
                if (upperCase.contains("INT")) {
                    i6 = 3;
                } else if (!upperCase.contains("CHAR") && !upperCase.contains("CLOB") && !upperCase.contains("TEXT")) {
                    if (!upperCase.contains("BLOB")) {
                        i6 = (upperCase.contains("REAL") || upperCase.contains("FLOA") || upperCase.contains("DOUB")) ? 4 : 1;
                    }
                } else {
                    i6 = 2;
                }
            }
            this.f5411c = i6;
            this.f = str3;
            this.f5414g = i5;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || C0061a.class != obj.getClass()) {
                return false;
            }
            C0061a c0061a = (C0061a) obj;
            if (this.f5413e != c0061a.f5413e || !this.f5409a.equals(c0061a.f5409a) || this.f5412d != c0061a.f5412d) {
                return false;
            }
            String str = this.f;
            int i4 = this.f5414g;
            int i5 = c0061a.f5414g;
            String str2 = c0061a.f;
            if (i4 == 1 && i5 == 2 && str != null && !str.equals(str2)) {
                return false;
            }
            if (i4 == 2 && i5 == 1 && str2 != null && !str2.equals(str)) {
                return false;
            }
            if ((i4 == 0 || i4 != i5 || (str == null ? str2 == null : str.equals(str2))) && this.f5411c == c0061a.f5411c) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            int i4;
            int hashCode = ((this.f5409a.hashCode() * 31) + this.f5411c) * 31;
            if (this.f5412d) {
                i4 = 1231;
            } else {
                i4 = 1237;
            }
            return ((hashCode + i4) * 31) + this.f5413e;
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder("Column{name='");
            sb.append(this.f5409a);
            sb.append("', type='");
            sb.append(this.f5410b);
            sb.append("', affinity='");
            sb.append(this.f5411c);
            sb.append("', notNull=");
            sb.append(this.f5412d);
            sb.append(", primaryKeyPosition=");
            sb.append(this.f5413e);
            sb.append(", defaultValue='");
            return C.b.c(sb, this.f, "'}");
        }
    }

    /* renamed from: o0.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public final String f5415a;

        /* renamed from: b  reason: collision with root package name */
        public final String f5416b;

        /* renamed from: c  reason: collision with root package name */
        public final String f5417c;

        /* renamed from: d  reason: collision with root package name */
        public final List<String> f5418d;

        /* renamed from: e  reason: collision with root package name */
        public final List<String> f5419e;

        public b(String str, String str2, String str3, List<String> list, List<String> list2) {
            this.f5415a = str;
            this.f5416b = str2;
            this.f5417c = str3;
            this.f5418d = Collections.unmodifiableList(list);
            this.f5419e = Collections.unmodifiableList(list2);
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || b.class != obj.getClass()) {
                return false;
            }
            b bVar = (b) obj;
            if (!this.f5415a.equals(bVar.f5415a) || !this.f5416b.equals(bVar.f5416b) || !this.f5417c.equals(bVar.f5417c) || !this.f5418d.equals(bVar.f5418d)) {
                return false;
            }
            return this.f5419e.equals(bVar.f5419e);
        }

        public final int hashCode() {
            int hashCode = this.f5416b.hashCode();
            int hashCode2 = this.f5417c.hashCode();
            int hashCode3 = this.f5418d.hashCode();
            return this.f5419e.hashCode() + ((hashCode3 + ((hashCode2 + ((hashCode + (this.f5415a.hashCode() * 31)) * 31)) * 31)) * 31);
        }

        public final String toString() {
            return "ForeignKey{referenceTable='" + this.f5415a + "', onDelete='" + this.f5416b + "', onUpdate='" + this.f5417c + "', columnNames=" + this.f5418d + ", referenceColumnNames=" + this.f5419e + '}';
        }
    }

    /* renamed from: o0.a$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class c implements Comparable<c> {

        /* renamed from: j  reason: collision with root package name */
        public final int f5420j;

        /* renamed from: k  reason: collision with root package name */
        public final int f5421k;

        /* renamed from: l  reason: collision with root package name */
        public final String f5422l;

        /* renamed from: m  reason: collision with root package name */
        public final String f5423m;

        public c(int i4, int i5, String str, String str2) {
            this.f5420j = i4;
            this.f5421k = i5;
            this.f5422l = str;
            this.f5423m = str2;
        }

        @Override // java.lang.Comparable
        public final int compareTo(c cVar) {
            c cVar2 = cVar;
            int i4 = this.f5420j - cVar2.f5420j;
            if (i4 == 0) {
                return this.f5421k - cVar2.f5421k;
            }
            return i4;
        }
    }

    /* renamed from: o0.a$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d {

        /* renamed from: a  reason: collision with root package name */
        public final String f5424a;

        /* renamed from: b  reason: collision with root package name */
        public final boolean f5425b;

        /* renamed from: c  reason: collision with root package name */
        public final List<String> f5426c;

        public d(String str, boolean z4, List<String> list) {
            this.f5424a = str;
            this.f5425b = z4;
            this.f5426c = list;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || d.class != obj.getClass()) {
                return false;
            }
            d dVar = (d) obj;
            if (this.f5425b != dVar.f5425b || !this.f5426c.equals(dVar.f5426c)) {
                return false;
            }
            String str = this.f5424a;
            boolean startsWith = str.startsWith("index_");
            String str2 = dVar.f5424a;
            if (startsWith) {
                return str2.startsWith("index_");
            }
            return str.equals(str2);
        }

        public final int hashCode() {
            int hashCode;
            String str = this.f5424a;
            if (str.startsWith("index_")) {
                hashCode = -1184239155;
            } else {
                hashCode = str.hashCode();
            }
            return this.f5426c.hashCode() + (((hashCode * 31) + (this.f5425b ? 1 : 0)) * 31);
        }

        public final String toString() {
            return "Index{name='" + this.f5424a + "', unique=" + this.f5425b + ", columns=" + this.f5426c + '}';
        }
    }

    public C0746a(String str, HashMap hashMap, HashSet hashSet, HashSet hashSet2) {
        Set<d> unmodifiableSet;
        this.f5405a = str;
        this.f5406b = Collections.unmodifiableMap(hashMap);
        this.f5407c = Collections.unmodifiableSet(hashSet);
        if (hashSet2 == null) {
            unmodifiableSet = null;
        } else {
            unmodifiableSet = Collections.unmodifiableSet(hashSet2);
        }
        this.f5408d = unmodifiableSet;
    }

    public static C0746a a(C0779a c0779a, String str) {
        boolean z4;
        int i4;
        int i5;
        ArrayList arrayList;
        int i6;
        boolean z5;
        Cursor f = c0779a.f("PRAGMA table_info(`" + str + "`)");
        HashMap hashMap = new HashMap();
        try {
            if (f.getColumnCount() > 0) {
                int columnIndex = f.getColumnIndex("name");
                int columnIndex2 = f.getColumnIndex("type");
                int columnIndex3 = f.getColumnIndex("notnull");
                int columnIndex4 = f.getColumnIndex("pk");
                int columnIndex5 = f.getColumnIndex("dflt_value");
                while (f.moveToNext()) {
                    String string = f.getString(columnIndex);
                    String string2 = f.getString(columnIndex2);
                    if (f.getInt(columnIndex3) != 0) {
                        z5 = true;
                    } else {
                        z5 = false;
                    }
                    hashMap.put(string, new C0061a(f.getInt(columnIndex4), 2, string, string2, f.getString(columnIndex5), z5));
                }
            }
            f.close();
            HashSet hashSet = new HashSet();
            f = c0779a.f("PRAGMA foreign_key_list(`" + str + "`)");
            try {
                int columnIndex6 = f.getColumnIndex("id");
                int columnIndex7 = f.getColumnIndex("seq");
                int columnIndex8 = f.getColumnIndex("table");
                int columnIndex9 = f.getColumnIndex("on_delete");
                int columnIndex10 = f.getColumnIndex("on_update");
                ArrayList b4 = b(f);
                int count = f.getCount();
                int i7 = 0;
                while (i7 < count) {
                    f.moveToPosition(i7);
                    if (f.getInt(columnIndex7) != 0) {
                        i4 = columnIndex6;
                        i5 = columnIndex7;
                        arrayList = b4;
                        i6 = count;
                    } else {
                        int i8 = f.getInt(columnIndex6);
                        i4 = columnIndex6;
                        ArrayList arrayList2 = new ArrayList();
                        i5 = columnIndex7;
                        ArrayList arrayList3 = new ArrayList();
                        Iterator it = b4.iterator();
                        while (it.hasNext()) {
                            ArrayList arrayList4 = b4;
                            c cVar = (c) it.next();
                            int i9 = count;
                            if (cVar.f5420j == i8) {
                                arrayList2.add(cVar.f5422l);
                                arrayList3.add(cVar.f5423m);
                            }
                            b4 = arrayList4;
                            count = i9;
                        }
                        arrayList = b4;
                        i6 = count;
                        hashSet.add(new b(f.getString(columnIndex8), f.getString(columnIndex9), f.getString(columnIndex10), arrayList2, arrayList3));
                    }
                    i7++;
                    columnIndex6 = i4;
                    columnIndex7 = i5;
                    b4 = arrayList;
                    count = i6;
                }
                f.close();
                f = c0779a.f("PRAGMA index_list(`" + str + "`)");
                try {
                    int columnIndex11 = f.getColumnIndex("name");
                    int columnIndex12 = f.getColumnIndex("origin");
                    int columnIndex13 = f.getColumnIndex("unique");
                    HashSet hashSet2 = null;
                    if (columnIndex11 != -1 && columnIndex12 != -1 && columnIndex13 != -1) {
                        HashSet hashSet3 = new HashSet();
                        while (f.moveToNext()) {
                            if ("c".equals(f.getString(columnIndex12))) {
                                String string3 = f.getString(columnIndex11);
                                if (f.getInt(columnIndex13) == 1) {
                                    z4 = true;
                                } else {
                                    z4 = false;
                                }
                                d c4 = c(c0779a, string3, z4);
                                if (c4 != null) {
                                    hashSet3.add(c4);
                                }
                            }
                        }
                        f.close();
                        hashSet2 = hashSet3;
                        return new C0746a(str, hashMap, hashSet, hashSet2);
                    }
                    return new C0746a(str, hashMap, hashSet, hashSet2);
                } finally {
                }
            } finally {
            }
        } finally {
        }
    }

    public static ArrayList b(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex("id");
        int columnIndex2 = cursor.getColumnIndex("seq");
        int columnIndex3 = cursor.getColumnIndex("from");
        int columnIndex4 = cursor.getColumnIndex("to");
        int count = cursor.getCount();
        ArrayList arrayList = new ArrayList();
        for (int i4 = 0; i4 < count; i4++) {
            cursor.moveToPosition(i4);
            arrayList.add(new c(cursor.getInt(columnIndex), cursor.getInt(columnIndex2), cursor.getString(columnIndex3), cursor.getString(columnIndex4)));
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    /* JADX WARN: Finally extract failed */
    public static d c(C0779a c0779a, String str, boolean z4) {
        Cursor f = c0779a.f("PRAGMA index_xinfo(`" + str + "`)");
        try {
            int columnIndex = f.getColumnIndex("seqno");
            int columnIndex2 = f.getColumnIndex("cid");
            int columnIndex3 = f.getColumnIndex("name");
            if (columnIndex != -1 && columnIndex2 != -1 && columnIndex3 != -1) {
                TreeMap treeMap = new TreeMap();
                while (f.moveToNext()) {
                    if (f.getInt(columnIndex2) >= 0) {
                        int i4 = f.getInt(columnIndex);
                        treeMap.put(Integer.valueOf(i4), f.getString(columnIndex3));
                    }
                }
                ArrayList arrayList = new ArrayList(treeMap.size());
                arrayList.addAll(treeMap.values());
                d dVar = new d(str, z4, arrayList);
                f.close();
                return dVar;
            }
            f.close();
            return null;
        } catch (Throwable th) {
            f.close();
            throw th;
        }
    }

    public final boolean equals(Object obj) {
        Set<d> set;
        if (this == obj) {
            return true;
        }
        if (obj == null || C0746a.class != obj.getClass()) {
            return false;
        }
        C0746a c0746a = (C0746a) obj;
        String str = c0746a.f5405a;
        String str2 = this.f5405a;
        if (str2 == null ? str != null : !str2.equals(str)) {
            return false;
        }
        Map<String, C0061a> map = c0746a.f5406b;
        Map<String, C0061a> map2 = this.f5406b;
        if (map2 == null ? map != null : !map2.equals(map)) {
            return false;
        }
        Set<b> set2 = c0746a.f5407c;
        Set<b> set3 = this.f5407c;
        if (set3 == null ? set2 != null : !set3.equals(set2)) {
            return false;
        }
        Set<d> set4 = this.f5408d;
        if (set4 == null || (set = c0746a.f5408d) == null) {
            return true;
        }
        return set4.equals(set);
    }

    public final int hashCode() {
        int i4;
        int i5;
        int i6 = 0;
        String str = this.f5405a;
        if (str != null) {
            i4 = str.hashCode();
        } else {
            i4 = 0;
        }
        int i7 = i4 * 31;
        Map<String, C0061a> map = this.f5406b;
        if (map != null) {
            i5 = map.hashCode();
        } else {
            i5 = 0;
        }
        int i8 = (i7 + i5) * 31;
        Set<b> set = this.f5407c;
        if (set != null) {
            i6 = set.hashCode();
        }
        return i8 + i6;
    }

    public final String toString() {
        return "TableInfo{name='" + this.f5405a + "', columns=" + this.f5406b + ", foreignKeys=" + this.f5407c + ", indices=" + this.f5408d + '}';
    }
}
