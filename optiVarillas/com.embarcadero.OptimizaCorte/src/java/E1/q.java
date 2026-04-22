package E1;

import A1.C0124p;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class q implements e {

    /* renamed from: j  reason: collision with root package name */
    public final String f876j;

    public q() {
        this.f876j = null;
    }

    /* JADX WARN: Finally extract failed */
    @Override // E1.e
    public final boolean i(String str) {
        HttpURLConnection httpURLConnection;
        int responseCode;
        boolean z4 = false;
        try {
            m.b("Pinging URL: " + str);
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                f fVar = C0124p.f.f161a;
                String str2 = this.f876j;
                httpURLConnection.setConnectTimeout(60000);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setReadTimeout(60000);
                if (str2 != null) {
                    httpURLConnection.setRequestProperty("User-Agent", str2);
                }
                httpURLConnection.setUseCaches(false);
                l lVar = new l();
                lVar.a(httpURLConnection, null);
                responseCode = httpURLConnection.getResponseCode();
                lVar.b(httpURLConnection, responseCode);
            } catch (Throwable th) {
                httpURLConnection.disconnect();
                throw th;
            }
        } catch (IOException e4) {
            e = e4;
            String message = e.getMessage();
            m.g("Error while pinging URL: " + str + ". " + message);
        } catch (IndexOutOfBoundsException e5) {
            String message2 = e5.getMessage();
            m.g("Error while parsing ping URL: " + str + ". " + message2);
        } catch (RuntimeException e6) {
            e = e6;
            String message3 = e.getMessage();
            m.g("Error while pinging URL: " + str + ". " + message3);
        } finally {
        }
        if (responseCode >= 200 && responseCode < 300) {
            z4 = true;
            httpURLConnection.disconnect();
            return z4;
        }
        m.g("Received non-success response code " + responseCode + " from pinging URL: " + str);
        httpURLConnection.disconnect();
        return z4;
    }

    public q(String str) {
        this.f876j = str;
    }
}
