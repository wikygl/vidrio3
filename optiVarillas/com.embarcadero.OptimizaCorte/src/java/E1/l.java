package E1;

import android.util.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class l {

    /* renamed from: c  reason: collision with root package name */
    public static boolean f871c;

    /* renamed from: d  reason: collision with root package name */
    public static boolean f872d;

    /* renamed from: a  reason: collision with root package name */
    public final List f874a;

    /* renamed from: b  reason: collision with root package name */
    public static final Object f870b = new Object();

    /* renamed from: e  reason: collision with root package name */
    public static final HashSet f873e = new HashSet(Arrays.asList(new String[0]));

    public l() {
        List asList;
        if (!c()) {
            asList = new ArrayList();
        } else {
            asList = Arrays.asList("network_request_".concat(String.valueOf(UUID.randomUUID().toString())));
        }
        this.f874a = asList;
    }

    public static boolean c() {
        boolean z4;
        synchronized (f870b) {
            try {
                z4 = false;
                if (f871c && f872d) {
                    z4 = true;
                }
            } finally {
            }
        }
        return z4;
    }

    public static void e(JsonWriter jsonWriter, Map map) {
        if (map == null) {
            return;
        }
        jsonWriter.name("headers").beginArray();
        Iterator it = map.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry entry = (Map.Entry) it.next();
            String str = (String) entry.getKey();
            if (!f873e.contains(str)) {
                if (entry.getValue() instanceof List) {
                    for (String str2 : (List) entry.getValue()) {
                        jsonWriter.beginObject();
                        jsonWriter.name("name").value(str);
                        jsonWriter.name("value").value(str2);
                        jsonWriter.endObject();
                    }
                } else if (entry.getValue() instanceof String) {
                    jsonWriter.beginObject();
                    jsonWriter.name("name").value(str);
                    jsonWriter.name("value").value((String) entry.getValue());
                    jsonWriter.endObject();
                } else {
                    m.d("Connection headers should be either Map<String, String> or Map<String, List<String>>");
                    break;
                }
            }
        }
        jsonWriter.endArray();
    }

    public final void a(HttpURLConnection httpURLConnection, byte[] bArr) {
        HashMap hashMap;
        if (!c()) {
            return;
        }
        if (httpURLConnection.getRequestProperties() == null) {
            hashMap = null;
        } else {
            hashMap = new HashMap(httpURLConnection.getRequestProperties());
        }
        d("onNetworkRequest", new g(new String(httpURLConnection.getURL().toString()), new String(httpURLConnection.getRequestMethod()), hashMap, bArr));
    }

    public final void b(HttpURLConnection httpURLConnection, int i4) {
        HashMap hashMap;
        if (c()) {
            String str = null;
            if (httpURLConnection.getHeaderFields() == null) {
                hashMap = null;
            } else {
                hashMap = new HashMap(httpURLConnection.getHeaderFields());
            }
            d("onNetworkResponse", new j(i4, hashMap));
            if (i4 >= 200 && i4 < 300) {
                return;
            }
            try {
                str = httpURLConnection.getResponseMessage();
            } catch (IOException e4) {
                m.g("Can not get error message from error HttpURLConnection\n".concat(String.valueOf(e4.getMessage())));
            }
            d("onNetworkRequestError", new i(str));
        }
    }

    public final void d(String str, k kVar) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        try {
            jsonWriter.beginObject();
            jsonWriter.name("timestamp").value(System.currentTimeMillis());
            jsonWriter.name("event").value(str);
            jsonWriter.name("components").beginArray();
            for (String str2 : this.f874a) {
                jsonWriter.value(str2);
            }
            jsonWriter.endArray();
            kVar.b(jsonWriter);
            jsonWriter.endObject();
            jsonWriter.flush();
            jsonWriter.close();
        } catch (IOException e4) {
            m.e("unable to log", e4);
        }
        String stringWriter2 = stringWriter.toString();
        synchronized (l.class) {
            try {
                m.f("GMA Debug BEGIN");
                int i4 = 0;
                while (i4 < stringWriter2.length()) {
                    int i5 = i4 + 4000;
                    m.f("GMA Debug CONTENT ".concat(String.valueOf(stringWriter2.substring(i4, Math.min(i5, stringWriter2.length())))));
                    i4 = i5;
                }
                m.f("GMA Debug FINISH");
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
