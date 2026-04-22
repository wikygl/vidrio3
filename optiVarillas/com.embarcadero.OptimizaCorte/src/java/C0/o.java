package C0;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class o {

    /* renamed from: j  reason: collision with root package name */
    public static final o f337j;

    /* renamed from: k  reason: collision with root package name */
    public static final o f338k;

    /* renamed from: l  reason: collision with root package name */
    public static final o f339l;

    /* renamed from: m  reason: collision with root package name */
    public static final o f340m;

    /* renamed from: n  reason: collision with root package name */
    public static final o f341n;

    /* renamed from: o  reason: collision with root package name */
    public static final o f342o;

    /* renamed from: p  reason: collision with root package name */
    public static final /* synthetic */ o[] f343p;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v1, types: [C0.o, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r11v1, types: [C0.o, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r6v0, types: [C0.o, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r7v1, types: [C0.o, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r8v1, types: [C0.o, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r9v1, types: [C0.o, java.lang.Enum] */
    static {
        ?? r6 = new Enum("ENQUEUED", 0);
        f337j = r6;
        ?? r7 = new Enum("RUNNING", 1);
        f338k = r7;
        ?? r8 = new Enum("SUCCEEDED", 2);
        f339l = r8;
        ?? r9 = new Enum("FAILED", 3);
        f340m = r9;
        ?? r10 = new Enum("BLOCKED", 4);
        f341n = r10;
        ?? r11 = new Enum("CANCELLED", 5);
        f342o = r11;
        f343p = new o[]{r6, r7, r8, r9, r10, r11};
    }

    public o() {
        throw null;
    }

    public static o valueOf(String str) {
        return (o) Enum.valueOf(o.class, str);
    }

    public static o[] values() {
        return (o[]) f343p.clone();
    }

    public final boolean a() {
        if (this != f339l && this != f340m && this != f342o) {
            return false;
        }
        return true;
    }
}
