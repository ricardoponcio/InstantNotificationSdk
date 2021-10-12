package dev.poncio.SystemApps.InstantNotificationSdk.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import org.springframework.stereotype.Service;

import dev.poncio.SystemApps.InstantNotificationSdk.dto.ResponseEntity;

@Service
public class HttpService {

    public ResponseEntity makeReq(MetodoRequisicao metodo, String URL, Object body, Map<String, String> mapUrlParameters,
            Map<String, String> mapRequestParameters) throws Exception {
        HttpURLConnection connection = null;
        try {
            // Prepare parameters
            String urlParameters = "";
            if (mapUrlParameters != null) {
                for (Entry<String, String> entry : mapUrlParameters.entrySet()) {
                    if (urlParameters == null || urlParameters.trim().isEmpty())
                        urlParameters += "?";
                    else
                        urlParameters += "&";
                    urlParameters += entry.getKey().replaceAll(" ", "%20") + "="
                            + entry.getValue().replaceAll(" ", "%20");
                }
            }
            boolean hasPayload = body != null;
            byte[] payloadBytes = hasPayload ? this.payloadBytesFromObj(body) : new byte[] {};

            // Create connection
            String fullURL = URL;
            if (mapUrlParameters != null) {
                if (!fullURL.endsWith("/"))
                    fullURL += "/";
                fullURL += urlParameters;
            }
            URL url = new URL(fullURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(metodo.name());
            connection.setRequestProperty("Content-Type", "application/json");
            if (hasPayload) {
                connection.setRequestProperty("Content-Length", Integer.toString(payloadBytes.length));
            }
            connection.setRequestProperty("Content-Language", "en-US");
            if (mapRequestParameters != null) {
                for (Entry<String, String> entry : mapRequestParameters.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Send request
            if (hasPayload) {
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(payloadBytes);
                wr.close();
            }

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return new Gson().fromJson(response.toString(), ResponseEntity.class);
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private byte[] payloadBytesFromObj(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        return bos.toByteArray();
    }

    public static enum MetodoRequisicao {
        GET, POST
    };

}
