package K;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class h {

    /* renamed from: a  reason: collision with root package name */
    public static final d f1259a = new d(null, false);

    /* renamed from: b  reason: collision with root package name */
    public static final d f1260b = new d(null, true);

    /* renamed from: c  reason: collision with root package name */
    public static final d f1261c;

    /* renamed from: d  reason: collision with root package name */
    public static final d f1262d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a implements b {

        /* renamed from: a  reason: collision with root package name */
        public static final a f1263a = new Object();

        @Override // K.h.b
        public final int a(CharSequence charSequence, int i4) {
            int i5 = 2;
            for (int i6 = 0; i6 < i4 && i5 == 2; i6++) {
                byte directionality = Character.getDirectionality(charSequence.charAt(i6));
                d dVar = h.f1259a;
                if (directionality != 0) {
                    if (directionality != 1 && directionality != 2) {
                        switch (directionality) {
                            case 14:
                            case 15:
                                break;
                            case 16:
                            case 17:
                                break;
                            default:
                                i5 = 2;
                                break;
                        }
                    }
                    i5 = 0;
                }
                i5 = 1;
            }
            return i5;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface b {
        int a(CharSequence charSequence, int i4);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static abstract class c implements g {

        /* renamed from: a  reason: collision with root package name */
        public final b f1264a;

        public c(b bVar) {
            this.f1264a = bVar;
        }

        public abstract boolean a();

        public final boolean b(CharSequence charSequence, int i4) {
            if (charSequence != null && i4 >= 0 && charSequence.length() - i4 >= 0) {
                b bVar = this.f1264a;
                if (bVar == null) {
                    return a();
                }
                int a4 = bVar.a(charSequence, i4);
                if (a4 == 0) {
                    return true;
                }
                if (a4 != 1) {
                    return a();
                }
                return false;
            }
            throw new IllegalArgumentException();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class d extends c {

        /* renamed from: b  reason: collision with root package name */
        public final boolean f1265b;

        public d(b bVar, boolean z4) {
            super(bVar);
            this.f1265b = z4;
        }

        @Override // K.h.c
        public final boolean a() {
            return this.f1265b;
        }
    }

    static {
        a aVar = a.f1263a;
        f1261c = new d(aVar, false);
        f1262d = new d(aVar, true);
    }
}
