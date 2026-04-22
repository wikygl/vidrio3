package V1;

import W1.C0324l;
import android.util.Base64OutputStream;
import com.google.android.gms.internal.ads.KV;
import com.google.android.gms.internal.ads.W8;
import com.google.android.gms.internal.ads.X8;
import com.google.android.gms.internal.ads.Z8;
import com.google.android.gms.internal.ads.fn;
import com.google.android.gms.internal.ads.p20;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.PriorityQueue;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class N {

    /* renamed from: a  reason: collision with root package name */
    public final int f2557a;

    /* renamed from: b  reason: collision with root package name */
    public final Object f2558b;

    public N(int i4) {
        this.f2558b = new KV();
        this.f2557a = i4;
    }

    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v1, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r3v9 */
    public String a(ArrayList arrayList) {
        StringBuilder sb = new StringBuilder();
        int size = arrayList.size();
        ?? r32 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            sb.append(((String) arrayList.get(i4)).toLowerCase(Locale.US));
            sb.append('\n');
        }
        String[] split = sb.toString().split("\n");
        String str = "";
        if (split.length == 0) {
            return "";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
        Base64OutputStream base64OutputStream = new Base64OutputStream(byteArrayOutputStream, 10);
        PriorityQueue priorityQueue = new PriorityQueue(this.f2557a, new p20(1));
        int i5 = 0;
        while (i5 < split.length) {
            String[] b4 = W8.b(split[i5], (boolean) r32);
            if (b4.length != 0) {
                int length = b4.length;
                int i6 = this.f2557a;
                if (length < 6) {
                    fn.r(i6, fn.t(length, b4), fn.q(b4, (int) r32, length), length, priorityQueue);
                } else {
                    long t3 = fn.t(6, b4);
                    String q4 = fn.q(b4, (int) r32, 6);
                    int i7 = 6;
                    fn.r(i6, t3, q4, 6, priorityQueue);
                    int i8 = 1;
                    while (true) {
                        int length2 = b4.length;
                        if (i8 < length2 - 5) {
                            String q5 = fn.q(b4, i8, i7);
                            long a4 = (W8.a(b4[i8 + 5]) + 2147483647L) % 1073807359;
                            t3 = (a4 + (((((t3 + 1073807359) - ((((W8.a(b4[i8 - 1]) + 2147483647L) % 1073807359) * fn.l(5, 16785407L)) % 1073807359)) % 1073807359) * 16785407) % 1073807359)) % 1073807359;
                            fn.r(i6, t3, q5, length2, priorityQueue);
                            i8++;
                            str = str;
                            split = split;
                            i7 = 6;
                        }
                    }
                }
            }
            i5++;
            str = str;
            split = split;
            r32 = 0;
        }
        String str2 = str;
        Iterator it = priorityQueue.iterator();
        while (it.hasNext()) {
            try {
                base64OutputStream.write(((X8) this.f2558b).k(((Z8) it.next()).b));
            } catch (IOException e4) {
                E1.m.e("Error while writing hash to byteStream", e4);
            }
        }
        try {
            base64OutputStream.close();
        } catch (IOException e5) {
            E1.m.e("HashManager: Unable to convert to Base64.", e5);
        }
        try {
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toString();
        } catch (IOException e6) {
            E1.m.e("HashManager: Unable to convert to Base64.", e6);
            return str2;
        }
    }

    public N(T1.b bVar, int i4) {
        C0324l.d(bVar);
        this.f2558b = bVar;
        this.f2557a = i4;
    }
}
