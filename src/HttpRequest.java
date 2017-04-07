import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class HttpRequest {

    private String method;
    private String url;
    private String protocol;
    Map<String, String> headers = new LinkedHashMap<>();
    List<String> body = new ArrayList<>();

    private HttpRequest() {}

    static HttpRequest parseAsHttp(InputStream in) {
        try {
            HttpRequest request = new HttpRequest();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            System.out.println(line);
            if (line == null) {
                throw new IOException("Server accepts only HTTP requests.");
            }
            String[] requestLine = line.split(" ", 3);
            if (requestLine.length != 3) {
                throw new IOException("Cannot parse request line from \"" + line + "\"");
            }
            if (!requestLine[2].startsWith("HTTP/")) {
                throw new IOException("Server accepts only HTTP requests.");
            }
            request.method = requestLine[0];
            request.url = requestLine[1];
            request.protocol = requestLine[2];

            line = reader.readLine();
            //System.out.println(line);
            while(line != null && !line.equals("")) {
                //System.out.println(line);
                String[] header = line.split(": ", 2);
                if (header.length != 2)
                    throw new IOException("Cannot parse header from \"" + line + "\"");
                else
                    request.headers.put(header[0], header[1]);
                System.out.println(header[0]);
                if(header[0].equals("Accept-Language")) break;
                line = reader.readLine();
            }

            if(request.method.equals("POST")) {
                reader.readLine();
                reader.readLine();
                reader.readLine();

                reader.readLine();
                while ((line = reader.readLine()) != null && !line.contains("WebKit")) {
                    System.out.println(line);
                    request.body.add(line);
                }
            }
            return request;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    String getMethod() {
        return method;
    }

    String getUrl() {
        return url;
    }
    //own code starts
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(method + " " + url + " " + protocol + "\n");
        for (Map.Entry<String,String> e: headers.entrySet()) {
            result.append(e.getKey()).append(": ").append(headers.get(e.getValue())).append("\n");
        }
        result.append("\r\n");
        for (String line : body) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
    //own code ends
    static class HttpMethod {
        static final String GET = "GET";
        static final String HEAD = "HEAD";
        static final String POST = "POST";
        static final String OPTIONS = "OPTIONS";
        static final String PUT = "PUT";
        static final String DELETE = "DELETE";
    }
}