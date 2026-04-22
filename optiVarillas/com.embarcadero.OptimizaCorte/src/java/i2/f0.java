package i2;

import android.app.Application;
import android.os.Handler;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.webkit.WebSettings;
import com.google.android.gms.internal.ads.Gf;
import com.google.android.gms.internal.ads.LA;
import com.google.android.gms.internal.ads.Xp;
import com.google.android.gms.internal.ads.d1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class f0 {

    /* renamed from: a  reason: collision with root package name */
    public final Application f3735a;

    /* renamed from: b  reason: collision with root package name */
    public final Handler f3736b;

    /* renamed from: c  reason: collision with root package name */
    public final Executor f3737c;

    /* renamed from: d  reason: collision with root package name */
    public final C0463i f3738d;

    /* renamed from: e  reason: collision with root package name */
    public final C0469o f3739e;
    public final L0.f f;

    /* renamed from: g  reason: collision with root package name */
    public final Xp f3740g;

    /* renamed from: h  reason: collision with root package name */
    public final a0 f3741h;

    public f0(Application application, Handler handler, F f, C0463i c0463i, C0469o c0469o, L0.f fVar, Xp xp, a0 a0Var) {
        this.f3735a = application;
        this.f3736b = handler;
        this.f3737c = f;
        this.f3738d = c0463i;
        this.f3739e = c0469o;
        this.f = fVar;
        this.f3740g = xp;
        this.f3741h = a0Var;
    }

    public final B a(C0479z c0479z) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://fundingchoicesmessages.google.com/a/consent").openConnection();
            httpURLConnection.setRequestProperty("User-Agent", WebSettings.getDefaultUserAgent(this.f3735a));
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(30000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
            JsonWriter jsonWriter = new JsonWriter(outputStreamWriter);
            jsonWriter.beginObject();
            String str = c0479z.f3823a;
            if (str != null) {
                jsonWriter.name("admob_app_id");
                jsonWriter.value(str);
            }
            d1 d1Var = c0479z.f3824b;
            if (d1Var != null) {
                jsonWriter.name("device_info");
                jsonWriter.beginObject();
                int i4 = d1Var.j;
                if (i4 != 1) {
                    jsonWriter.name("os_type");
                    int i5 = i4 - 1;
                    if (i5 != 0) {
                        if (i5 == 1) {
                            jsonWriter.value("ANDROID");
                        }
                    } else {
                        jsonWriter.value("UNKNOWN");
                    }
                }
                String str2 = (String) d1Var.k;
                if (str2 != null) {
                    jsonWriter.name("model");
                    jsonWriter.value(str2);
                }
                Integer num = (Integer) d1Var.l;
                if (num != null) {
                    jsonWriter.name("android_api_level");
                    jsonWriter.value(num);
                }
                jsonWriter.endObject();
            }
            String str3 = c0479z.f3825c;
            if (str3 != null) {
                jsonWriter.name("language_code");
                jsonWriter.value(str3);
            }
            Boolean bool = c0479z.f3826d;
            if (bool != null) {
                jsonWriter.name("tag_for_under_age_of_consent");
                jsonWriter.value(bool.booleanValue());
            }
            Map map = c0479z.f3827e;
            if (!map.isEmpty()) {
                jsonWriter.name("stored_infos_map");
                jsonWriter.beginObject();
                for (Map.Entry entry : map.entrySet()) {
                    jsonWriter.name((String) entry.getKey());
                    jsonWriter.value((String) entry.getValue());
                }
                jsonWriter.endObject();
            }
            Gf gf = c0479z.f;
            if (gf != null) {
                jsonWriter.name("screen_info");
                jsonWriter.beginObject();
                Integer num2 = (Integer) gf.a;
                if (num2 != null) {
                    jsonWriter.name("width");
                    jsonWriter.value(num2);
                }
                Integer num3 = (Integer) gf.b;
                if (num3 != null) {
                    jsonWriter.name("height");
                    jsonWriter.value(num3);
                }
                Double d4 = (Double) gf.c;
                if (d4 != null) {
                    jsonWriter.name("density");
                    jsonWriter.value(d4);
                }
                List<C0478y> list = (List) gf.d;
                if (!list.isEmpty()) {
                    jsonWriter.name("screen_insets");
                    jsonWriter.beginArray();
                    for (C0478y c0478y : list) {
                        jsonWriter.beginObject();
                        Integer num4 = c0478y.f3819a;
                        if (num4 != null) {
                            jsonWriter.name("top");
                            jsonWriter.value(num4);
                        }
                        Integer num5 = c0478y.f3820b;
                        if (num5 != null) {
                            jsonWriter.name("left");
                            jsonWriter.value(num5);
                        }
                        Integer num6 = c0478y.f3821c;
                        if (num6 != null) {
                            jsonWriter.name("right");
                            jsonWriter.value(num6);
                        }
                        Integer num7 = c0478y.f3822d;
                        if (num7 != null) {
                            jsonWriter.name("bottom");
                            jsonWriter.value(num7);
                        }
                        jsonWriter.endObject();
                    }
                    jsonWriter.endArray();
                }
                jsonWriter.endObject();
            }
            LA la = c0479z.f3828g;
            if (la != null) {
                jsonWriter.name("app_info");
                jsonWriter.beginObject();
                String str4 = (String) la.a;
                if (str4 != null) {
                    jsonWriter.name("package_name");
                    jsonWriter.value(str4);
                }
                String str5 = (String) la.b;
                if (str5 != null) {
                    jsonWriter.name("publisher_display_name");
                    jsonWriter.value(str5);
                }
                String str6 = (String) la.c;
                if (str6 != null) {
                    jsonWriter.name("version");
                    jsonWriter.value(str6);
                }
                jsonWriter.endObject();
            }
            R2.d dVar = c0479z.f3829h;
            if (dVar != null) {
                jsonWriter.name("sdk_info");
                jsonWriter.beginObject();
                String str7 = (String) dVar.f2063k;
                if (str7 != null) {
                    jsonWriter.name("version");
                    jsonWriter.value(str7);
                }
                jsonWriter.endObject();
            }
            List<EnumC0477x> list2 = c0479z.f3830i;
            if (!list2.isEmpty()) {
                jsonWriter.name("debug_params");
                jsonWriter.beginArray();
                for (EnumC0477x enumC0477x : list2) {
                    int ordinal = enumC0477x.ordinal();
                    if (ordinal != 0) {
                        if (ordinal != 1) {
                            if (ordinal != 2) {
                                if (ordinal != 3) {
                                    if (ordinal == 4) {
                                        jsonWriter.value("PREVIEWING_DEBUG_MESSAGES");
                                    }
                                } else {
                                    jsonWriter.value("GEO_OVERRIDE_NON_EEA");
                                }
                            } else {
                                jsonWriter.value("GEO_OVERRIDE_EEA");
                            }
                        } else {
                            jsonWriter.value("ALWAYS_SHOW");
                        }
                    } else {
                        jsonWriter.value("DEBUG_PARAM_UNKNOWN");
                    }
                }
                jsonWriter.endArray();
            }
            jsonWriter.endObject();
            jsonWriter.close();
            outputStreamWriter.close();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                String headerField = httpURLConnection.getHeaderField("x-ump-using-header");
                if (headerField != null) {
                    B a4 = B.a(new JsonReader(new StringReader(headerField)));
                    a4.f3668a = new Scanner(httpURLConnection.getInputStream()).useDelimiter("\\A").next();
                    return a4;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                bufferedReader.readLine();
                JsonReader jsonReader = new JsonReader(bufferedReader);
                B a5 = B.a(jsonReader);
                jsonReader.close();
                bufferedReader.close();
                return a5;
            }
            throw new IOException("Http error code - " + responseCode + ".\n" + new Scanner(httpURLConnection.getErrorStream()).useDelimiter("\\A").next());
        } catch (SocketTimeoutException e4) {
            throw new b0(4, "The server timed out.", e4);
        } catch (IOException e5) {
            throw new b0(2, "Error making request.", e5);
        }
    }
}
