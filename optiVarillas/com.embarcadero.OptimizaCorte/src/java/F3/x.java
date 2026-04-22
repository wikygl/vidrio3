package F3;

import C3.l0;
import n3.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class x {

    /* renamed from: a  reason: collision with root package name */
    public static final C1.A f947a = new C1.A(1, "NO_THREAD_ELEMENTS");

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class a extends v3.i implements u3.p<Object, f.b, Object> {

        /* renamed from: k  reason: collision with root package name */
        public static final a f948k = new v3.i(2);

        @Override // u3.p
        public final Object f(Object obj, f.b bVar) {
            Integer num;
            int i4;
            f.b bVar2 = bVar;
            if (bVar2 instanceof l0) {
                if (obj instanceof Integer) {
                    num = (Integer) obj;
                } else {
                    num = null;
                }
                if (num != null) {
                    i4 = num.intValue();
                } else {
                    i4 = 1;
                }
                if (i4 == 0) {
                    return bVar2;
                }
                return Integer.valueOf(i4 + 1);
            }
            return obj;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class b extends v3.i implements u3.p<l0<?>, f.b, l0<?>> {

        /* renamed from: k  reason: collision with root package name */
        public static final b f949k = new v3.i(2);

        @Override // u3.p
        public final l0<?> f(l0<?> l0Var, f.b bVar) {
            l0<?> l0Var2 = l0Var;
            f.b bVar2 = bVar;
            if (l0Var2 == null) {
                if (bVar2 instanceof l0) {
                    return (l0) bVar2;
                }
                return null;
            }
            return l0Var2;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class c extends v3.i implements u3.p<A, f.b, A> {

        /* renamed from: k  reason: collision with root package name */
        public static final c f950k = new v3.i(2);

        @Override // u3.p
        public final A f(A a4, f.b bVar) {
            A a5 = a4;
            f.b bVar2 = bVar;
            if (bVar2 instanceof l0) {
                l0<Object> l0Var = (l0) bVar2;
                String b4 = l0Var.b(a5.f906a);
                int i4 = a5.f909d;
                a5.f907b[i4] = b4;
                a5.f909d = i4 + 1;
                a5.f908c[i4] = l0Var;
            }
            return a5;
        }
    }

    public static final void a(n3.f fVar, Object obj) {
        if (obj == f947a) {
            return;
        }
        if (obj instanceof A) {
            A a4 = (A) obj;
            l0<Object>[] l0VarArr = a4.f908c;
            int length = l0VarArr.length - 1;
            if (length < 0) {
                return;
            }
            while (true) {
                int i4 = length - 1;
                l0<Object> l0Var = l0VarArr[length];
                v3.h.b(l0Var);
                l0Var.p(a4.f907b[length]);
                if (i4 >= 0) {
                    length = i4;
                } else {
                    return;
                }
            }
        } else {
            Object B4 = fVar.B(null, b.f949k);
            v3.h.c(B4, "null cannot be cast to non-null type kotlinx.coroutines.ThreadContextElement<kotlin.Any?>");
            ((l0) B4).p(obj);
        }
    }

    public static final Object b(n3.f fVar, Object obj) {
        if (obj == null) {
            obj = fVar.B(0, a.f948k);
            v3.h.b(obj);
        }
        if (obj == 0) {
            return f947a;
        }
        if (obj instanceof Integer) {
            return fVar.B(new A(fVar, ((Number) obj).intValue()), c.f950k);
        }
        return ((l0) obj).b(fVar);
    }
}
