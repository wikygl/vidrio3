package d3;

import android.util.Base64;
import android.util.JsonWriter;
import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import b3.InterfaceC0361f;
import b3.InterfaceC0362g;
import java.io.BufferedWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* renamed from: d3.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0391e implements InterfaceC0360e, InterfaceC0362g {

    /* renamed from: a  reason: collision with root package name */
    public final boolean f3169a = true;

    /* renamed from: b  reason: collision with root package name */
    public final JsonWriter f3170b;

    /* renamed from: c  reason: collision with root package name */
    public final Map<Class<?>, InterfaceC0359d<?>> f3171c;

    /* renamed from: d  reason: collision with root package name */
    public final Map<Class<?>, InterfaceC0361f<?>> f3172d;

    /* renamed from: e  reason: collision with root package name */
    public final InterfaceC0359d<Object> f3173e;
    public final boolean f;

    public C0391e(BufferedWriter bufferedWriter, HashMap hashMap, HashMap hashMap2, C0387a c0387a, boolean z4) {
        this.f3170b = new JsonWriter(bufferedWriter);
        this.f3171c = hashMap;
        this.f3172d = hashMap2;
        this.f3173e = c0387a;
        this.f = z4;
    }

    @Override // b3.InterfaceC0360e
    public final InterfaceC0360e a(C0358c c0358c, long j4) {
        String str = c0358c.f2927a;
        g();
        JsonWriter jsonWriter = this.f3170b;
        jsonWriter.name(str);
        g();
        jsonWriter.value(j4);
        return this;
    }

    @Override // b3.InterfaceC0362g
    public final InterfaceC0362g b(String str) {
        g();
        this.f3170b.value(str);
        return this;
    }

    @Override // b3.InterfaceC0362g
    public final InterfaceC0362g c(boolean z4) {
        g();
        this.f3170b.value(z4);
        return this;
    }

    @Override // b3.InterfaceC0360e
    public final InterfaceC0360e d(C0358c c0358c, Object obj) {
        f(obj, c0358c.f2927a);
        return this;
    }

    public final C0391e e(Object obj) {
        int i4 = 0;
        JsonWriter jsonWriter = this.f3170b;
        if (obj == null) {
            jsonWriter.nullValue();
            return this;
        } else if (obj instanceof Number) {
            jsonWriter.value((Number) obj);
            return this;
        } else if (obj.getClass().isArray()) {
            if (obj instanceof byte[]) {
                g();
                jsonWriter.value(Base64.encodeToString((byte[]) obj, 2));
                return this;
            }
            jsonWriter.beginArray();
            if (obj instanceof int[]) {
                int[] iArr = (int[]) obj;
                int length = iArr.length;
                while (i4 < length) {
                    jsonWriter.value(iArr[i4]);
                    i4++;
                }
            } else if (obj instanceof long[]) {
                long[] jArr = (long[]) obj;
                int length2 = jArr.length;
                while (i4 < length2) {
                    long j4 = jArr[i4];
                    g();
                    jsonWriter.value(j4);
                    i4++;
                }
            } else if (obj instanceof double[]) {
                double[] dArr = (double[]) obj;
                int length3 = dArr.length;
                while (i4 < length3) {
                    jsonWriter.value(dArr[i4]);
                    i4++;
                }
            } else if (obj instanceof boolean[]) {
                boolean[] zArr = (boolean[]) obj;
                int length4 = zArr.length;
                while (i4 < length4) {
                    jsonWriter.value(zArr[i4]);
                    i4++;
                }
            } else if (obj instanceof Number[]) {
                Number[] numberArr = (Number[]) obj;
                int length5 = numberArr.length;
                while (i4 < length5) {
                    e(numberArr[i4]);
                    i4++;
                }
            } else {
                Object[] objArr = (Object[]) obj;
                int length6 = objArr.length;
                while (i4 < length6) {
                    e(objArr[i4]);
                    i4++;
                }
            }
            jsonWriter.endArray();
            return this;
        } else if (obj instanceof Collection) {
            jsonWriter.beginArray();
            for (Object obj2 : (Collection) obj) {
                e(obj2);
            }
            jsonWriter.endArray();
            return this;
        } else if (obj instanceof Map) {
            jsonWriter.beginObject();
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                Object key = entry.getKey();
                try {
                    f(entry.getValue(), (String) key);
                } catch (ClassCastException e4) {
                    throw new RuntimeException(String.format("Only String keys are currently supported in maps, got %s of type %s instead.", key, key.getClass()), e4);
                }
            }
            jsonWriter.endObject();
            return this;
        } else {
            InterfaceC0359d<?> interfaceC0359d = this.f3171c.get(obj.getClass());
            if (interfaceC0359d != null) {
                jsonWriter.beginObject();
                interfaceC0359d.a(obj, this);
                jsonWriter.endObject();
                return this;
            }
            InterfaceC0361f<?> interfaceC0361f = this.f3172d.get(obj.getClass());
            if (interfaceC0361f != null) {
                interfaceC0361f.a(obj, this);
                return this;
            } else if (obj instanceof Enum) {
                String name = ((Enum) obj).name();
                g();
                jsonWriter.value(name);
                return this;
            } else {
                jsonWriter.beginObject();
                this.f3173e.a(obj, this);
                jsonWriter.endObject();
                return this;
            }
        }
    }

    public final C0391e f(Object obj, String str) {
        boolean z4 = this.f;
        JsonWriter jsonWriter = this.f3170b;
        if (z4) {
            if (obj != null) {
                g();
                jsonWriter.name(str);
                e(obj);
            }
            return this;
        }
        g();
        jsonWriter.name(str);
        if (obj == null) {
            jsonWriter.nullValue();
        } else {
            e(obj);
        }
        return this;
    }

    public final void g() {
        if (this.f3169a) {
            return;
        }
        throw new IllegalStateException("Parent context used since this context was created. Cannot use this context anymore.");
    }
}
