package br.com.sankhya.frs.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.input.CloseShieldInputStream;

import br.com.sankhya.frs.utils.CreateBasicAutorization;

public class CallServiceBkp {
    private String uri;

    private String method;

    private String body;

    private String empresa;

    private String autorization;

    //private static HttpURLConnection connection;

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return this.method;
    }

    public String getBasicAutorization() {
        return this.autorization;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public void setBasicAutorization (String autorization) {
        this.autorization = autorization;
    }

    public Map<String,String> fire() throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        Map<String,String> retorno = new HashMap<>();
        setBasicAutorization(CreateBasicAutorization.getAutorization(this.empresa));
        try {
            StringBuffer responseContent = new StringBuffer();
            URL url = new URL(this.uri);
            connection = (HttpURLConnection)url.openConnection();
            if (this.empresa.equals(new String("200")))
                connection.setRequestProperty("Authorization", "Basic " + getBasicAutorization());
            if (this.empresa.equals(new String("1")))
                connection.setRequestProperty("Authorization", "Basic " + getBasicAutorization());
            connection.setRequestMethod(this.method);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            if (this.body != null) {
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(this.body);
                osw.flush();
                osw.close();
            }
            int status = connection.getResponseCode();
            retorno.put("status", Integer.toString(status));
            if (status > 299) {
                @SuppressWarnings("resource")
                CloseShieldInputStream closeShieldInputStream1 = new CloseShieldInputStream(connection.getErrorStream());
                reader = new BufferedReader(new InputStreamReader((InputStream)closeShieldInputStream1));
                String str;
                while ((str = reader.readLine()) != null)
                    responseContent.append(str);
                reader.close();
                retorno.put("response", responseContent.toString());
                return retorno;
            }
            @SuppressWarnings("resource")
            CloseShieldInputStream closeShieldInputStream = new CloseShieldInputStream(connection.getInputStream());
            reader = new BufferedReader(new InputStreamReader((InputStream)closeShieldInputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null)
                responseContent.append(line);
            reader.close();
            retorno.put("response", responseContent.toString());
            //return responseContent.toString();
            return retorno;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
}
