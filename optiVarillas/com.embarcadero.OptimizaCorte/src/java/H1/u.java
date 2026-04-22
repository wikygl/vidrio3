package h1;

import android.util.SparseArray;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class u {

    /* renamed from: j  reason: collision with root package name */
    public static final u f3582j;

    /* renamed from: k  reason: collision with root package name */
    public static final /* synthetic */ u[] f3583k;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [h1.u, java.lang.Enum, java.lang.Object] */
    static {
        ?? r6 = new Enum("DEFAULT", 0);
        f3582j = r6;
        Enum r7 = new Enum("UNMETERED_ONLY", 1);
        Enum r8 = new Enum("UNMETERED_OR_DAILY", 2);
        Enum r9 = new Enum("FAST_IF_RADIO_AWAKE", 3);
        Enum r10 = new Enum("NEVER", 4);
        Enum r11 = new Enum("UNRECOGNIZED", 5);
        f3583k = new u[]{r6, r7, r8, r9, r10, r11};
        SparseArray sparseArray = new SparseArray();
        sparseArray.put(0, r6);
        sparseArray.put(1, r7);
        sparseArray.put(2, r8);
        sparseArray.put(3, r9);
        sparseArray.put(4, r10);
        sparseArray.put(-1, r11);
    }

    public u() {
        throw null;
    }

    public static u valueOf(String str) {
        return (u) Enum.valueOf(u.class, str);
    }

    public static u[] values() {
        return (u[]) f3583k.clone();
    }
}
