package v3;

import u3.q;
import u3.r;
import u3.s;
import u3.t;
import u3.u;
import u3.v;
import u3.w;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class p {
    public static void a(Object obj) {
        int i4;
        if (obj != null) {
            if (obj instanceof l3.a) {
                if (obj instanceof e) {
                    i4 = ((e) obj).e();
                } else if (obj instanceof u3.a) {
                    i4 = 0;
                } else if (obj instanceof u3.l) {
                    i4 = 1;
                } else if (obj instanceof u3.p) {
                    i4 = 2;
                } else if (obj instanceof q) {
                    i4 = 3;
                } else if (obj instanceof r) {
                    i4 = 4;
                } else if (obj instanceof s) {
                    i4 = 5;
                } else if (obj instanceof t) {
                    i4 = 6;
                } else if (obj instanceof u) {
                    i4 = 7;
                } else if (obj instanceof v) {
                    i4 = 8;
                } else if (obj instanceof w) {
                    i4 = 9;
                } else if (obj instanceof u3.b) {
                    i4 = 10;
                } else if (obj instanceof u3.c) {
                    i4 = 11;
                } else if (obj instanceof u3.d) {
                    i4 = 12;
                } else if (obj instanceof u3.e) {
                    i4 = 13;
                } else if (obj instanceof u3.f) {
                    i4 = 14;
                } else if (obj instanceof u3.g) {
                    i4 = 15;
                } else if (obj instanceof u3.h) {
                    i4 = 16;
                } else if (obj instanceof u3.i) {
                    i4 = 17;
                } else if (obj instanceof u3.j) {
                    i4 = 18;
                } else if (obj instanceof u3.k) {
                    i4 = 19;
                } else if (obj instanceof u3.m) {
                    i4 = 20;
                } else if (obj instanceof u3.n) {
                    i4 = 21;
                } else if (obj instanceof u3.o) {
                    i4 = 22;
                } else {
                    i4 = -1;
                }
                if (i4 == 2) {
                    return;
                }
            }
            ClassCastException classCastException = new ClassCastException(obj.getClass().getName().concat(" cannot be cast to kotlin.jvm.functions.Function2"));
            h.f(classCastException, p.class.getName());
            throw classCastException;
        }
    }
}
