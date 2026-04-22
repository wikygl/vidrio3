package e3;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import b3.InterfaceC0361f;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class f implements InterfaceC0360e {
    public static final Charset f = Charset.forName("UTF-8");

    /* renamed from: g  reason: collision with root package name */
    public static final C0358c f3363g;

    /* renamed from: h  reason: collision with root package name */
    public static final C0358c f3364h;

    /* renamed from: i  reason: collision with root package name */
    public static final e f3365i;

    /* renamed from: a  reason: collision with root package name */
    public OutputStream f3366a;

    /* renamed from: b  reason: collision with root package name */
    public final Map<Class<?>, InterfaceC0359d<?>> f3367b;

    /* renamed from: c  reason: collision with root package name */
    public final Map<Class<?>, InterfaceC0361f<?>> f3368c;

    /* renamed from: d  reason: collision with root package name */
    public final InterfaceC0359d<Object> f3369d;

    /* renamed from: e  reason: collision with root package name */
    public final i f3370e = new i(this);

    /* JADX WARN: Type inference failed for: r0v6, types: [java.lang.Object, e3.e] */
    static {
        C0409a c0409a = new C0409a(1);
        HashMap hashMap = new HashMap();
        hashMap.put(d.class, c0409a);
        f3363g = new C0358c("key", Collections.unmodifiableMap(new HashMap(hashMap)));
        C0409a c0409a2 = new C0409a(2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(d.class, c0409a2);
        f3364h = new C0358c("value", Collections.unmodifiableMap(new HashMap(hashMap2)));
        f3365i = new Object();
    }

    public f(ByteArrayOutputStream byteArrayOutputStream, Map map, Map map2, InterfaceC0359d interfaceC0359d) {
        this.f3366a = byteArrayOutputStream;
        this.f3367b = map;
        this.f3368c = map2;
        this.f3369d = interfaceC0359d;
    }

    public static int f(C0358c c0358c) {
        d dVar = (d) ((Annotation) c0358c.f2928b.get(d.class));
        if (dVar != null) {
            return ((C0409a) dVar).f3359a;
        }
        throw new RuntimeException("Field has no @Protobuf config");
    }

    @Override // b3.InterfaceC0360e
    public final InterfaceC0360e a(C0358c c0358c, long j4) {
        if (j4 != 0) {
            d dVar = (d) ((Annotation) c0358c.f2928b.get(d.class));
            if (dVar != null) {
                g(((C0409a) dVar).f3359a << 3);
                h(j4);
            } else {
                throw new RuntimeException("Field has no @Protobuf config");
            }
        }
        return this;
    }

    public final void b(C0358c c0358c, int i4, boolean z4) {
        if (z4 && i4 == 0) {
            return;
        }
        d dVar = (d) ((Annotation) c0358c.f2928b.get(d.class));
        if (dVar != null) {
            g(((C0409a) dVar).f3359a << 3);
            g(i4);
            return;
        }
        throw new RuntimeException("Field has no @Protobuf config");
    }

    public final void c(C0358c c0358c, Object obj, boolean z4) {
        if (obj == null) {
            return;
        }
        if (obj instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) obj;
            if (z4 && charSequence.length() == 0) {
                return;
            }
            g((f(c0358c) << 3) | 2);
            byte[] bytes = charSequence.toString().getBytes(f);
            g(bytes.length);
            this.f3366a.write(bytes);
        } else if (obj instanceof Collection) {
            for (Object obj2 : (Collection) obj) {
                c(c0358c, obj2, false);
            }
        } else if (obj instanceof Map) {
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                e(f3365i, c0358c, entry, false);
            }
        } else if (obj instanceof Double) {
            double doubleValue = ((Double) obj).doubleValue();
            if (!z4 || doubleValue != 0.0d) {
                g((f(c0358c) << 3) | 1);
                this.f3366a.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(doubleValue).array());
            }
        } else if (obj instanceof Float) {
            float floatValue = ((Float) obj).floatValue();
            if (!z4 || floatValue != 0.0f) {
                g((f(c0358c) << 3) | 5);
                this.f3366a.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatValue).array());
            }
        } else if (obj instanceof Number) {
            long longValue = ((Number) obj).longValue();
            if (!z4 || longValue != 0) {
                d dVar = (d) ((Annotation) c0358c.f2928b.get(d.class));
                if (dVar != null) {
                    g(((C0409a) dVar).f3359a << 3);
                    h(longValue);
                    return;
                }
                throw new RuntimeException("Field has no @Protobuf config");
            }
        } else if (obj instanceof Boolean) {
            b(c0358c, ((Boolean) obj).booleanValue() ? 1 : 0, z4);
        } else if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            if (z4 && bArr.length == 0) {
                return;
            }
            g((f(c0358c) << 3) | 2);
            g(bArr.length);
            this.f3366a.write(bArr);
        } else {
            InterfaceC0359d<?> interfaceC0359d = this.f3367b.get(obj.getClass());
            if (interfaceC0359d != null) {
                e(interfaceC0359d, c0358c, obj, z4);
                return;
            }
            InterfaceC0361f<?> interfaceC0361f = this.f3368c.get(obj.getClass());
            if (interfaceC0361f != null) {
                i iVar = this.f3370e;
                iVar.f3375a = false;
                iVar.f3377c = c0358c;
                iVar.f3376b = z4;
                interfaceC0361f.a(obj, iVar);
            } else if (obj instanceof c) {
                b(c0358c, ((c) obj).a(), true);
            } else if (obj instanceof Enum) {
                b(c0358c, ((Enum) obj).ordinal(), true);
            } else {
                e(this.f3369d, c0358c, obj, z4);
            }
        }
    }

    @Override // b3.InterfaceC0360e
    public final InterfaceC0360e d(C0358c c0358c, Object obj) {
        c(c0358c, obj, true);
        return this;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [java.io.OutputStream, e3.b] */
    public final void e(InterfaceC0359d interfaceC0359d, C0358c c0358c, Object obj, boolean z4) {
        ?? outputStream = new OutputStream();
        outputStream.f3360j = 0L;
        try {
            OutputStream outputStream2 = this.f3366a;
            this.f3366a = outputStream;
            interfaceC0359d.a(obj, this);
            this.f3366a = outputStream2;
            long j4 = outputStream.f3360j;
            outputStream.close();
            if (z4 && j4 == 0) {
                return;
            }
            g((f(c0358c) << 3) | 2);
            h(j4);
            interfaceC0359d.a(obj, this);
        } catch (Throwable th) {
            try {
                outputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public final void g(int i4) {
        while ((i4 & (-128)) != 0) {
            this.f3366a.write((i4 & 127) | 128);
            i4 >>>= 7;
        }
        this.f3366a.write(i4 & 127);
    }

    public final void h(long j4) {
        while (((-128) & j4) != 0) {
            this.f3366a.write((((int) j4) & 127) | 128);
            j4 >>>= 7;
        }
        this.f3366a.write(((int) j4) & 127);
    }
}
