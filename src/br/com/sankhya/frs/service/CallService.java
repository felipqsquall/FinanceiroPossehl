package br.com.sankhya.frs.service;

import br.com.sankhya.frs.utils.CreateBasicAutorization;
import org.apache.commons.io.input.CloseShieldInputStream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CallService {
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";
    private static final int TIMEOUT = 30000;

    private String uri;
    private String method;
    private String body;
    private String empresa;
    private String authorization;

    public static class Builder {
        private final CallService service;

        public Builder() {
            this.service = new CallService();
        }

        public Builder uri(String uri) {
            service.uri = uri;
            return this;
        }

        public Builder method(String method) {
            service.method = method;
            return this;
        }

        public Builder body(String body) {
            service.body = body;
            return this;
        }

        public Builder empresa(String empresa) {
            service.empresa = empresa;
            return this;
        }

        public CallService build() {
            return service;
        }
    }

    private CallService() {
        // Construtor privado para forçar uso do Builder
    }

    public Map<String, String> fire() throws Exception {
        validateRequiredFields();

        this.authorization = CreateBasicAutorization.getAutorization(this.empresa);

        HttpURLConnection connection = null;
        try {
            connection = createConnection();
            configureConnection(connection);
            writeBody(connection);

            return processResponse(connection);
        } finally {
            Optional.ofNullable(connection).ifPresent(HttpURLConnection::disconnect);
        }
    }

    private void validateRequiredFields() throws IllegalArgumentException {
        if (uri == null || uri.trim().isEmpty()) {
            throw new IllegalArgumentException("URI não pode ser nula ou vazia");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Método HTTP não pode ser nulo ou vazio");
        }
        if (empresa == null || empresa.trim().isEmpty()) {
            throw new IllegalArgumentException("Empresa não pode ser nula ou vazia");
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        URL url = new URL(this.uri);
        return (HttpURLConnection) url.openConnection();
    }

    private void configureConnection(HttpURLConnection connection) throws IOException {
        connection.setRequestMethod(this.method);
        connection.setRequestProperty("Accept", CONTENT_TYPE_JSON);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // Configura a autorização apenas para empresas específicas
        if ("200".equals(empresa) || "1".equals(empresa)) {
            connection.setRequestProperty("Authorization", "Basic " + this.authorization);
        }
    }

    private void writeBody(HttpURLConnection connection) throws IOException {
        if (body != null) {
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(body);
                writer.flush();
            }
        }
    }

    private Map<String, String> processResponse(HttpURLConnection connection) throws IOException {
        Map<String, String> response = new HashMap<>();
        int status = connection.getResponseCode();
        response.put("status", String.valueOf(status));

        InputStream inputStream = status > 299 ? connection.getErrorStream() : connection.getInputStream();
        String responseContent = readInputStream(inputStream);
        response.put("response", responseContent);

        return response;
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) return "";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new CloseShieldInputStream(inputStream), StandardCharsets.UTF_8))) {
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            return responseContent.toString();
        }
    }
}