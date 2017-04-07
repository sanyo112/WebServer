import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements Runnable {

    private Server server;
    private Socket client;
    private OutputStream out;

    Connection(Socket cl, Server s) {
        client = cl;
        server = s;
    }

    @Override
    public void run() {
        try {
            InputStream in = client.getInputStream();
            out = client.getOutputStream();
            System.out.println("hello");
            HttpRequest request = HttpRequest.parseAsHttp(in);

            if (request != null) {
                System.out.println("Request for " + request.getUrl() + " is being processed " +
                        "by socket at " + client.getInetAddress() +":"+ client.getPort());

                HttpResponse response;
                String method;
                if ((method = request.getMethod()).equals(HttpRequest.HttpMethod.GET)
                        || method.equals(HttpRequest.HttpMethod.HEAD)) {
                    File f = new File(server.getWebRoot() + request.getUrl());
                    response = new HttpResponse(HttpResponse.StatusCode.OK).withFile(f);
                    if (method.equals(HttpRequest.HttpMethod.HEAD)) {
                        response.removeBody();
                    }
                    //own code starts
                } else if (method.equals(HttpRequest.HttpMethod.POST)) {
                    String f = server.getWebRoot() + request.getUrl();
                    response = new HttpResponse(HttpResponse.StatusCode.OK).updateFile(f, request.body);
                } else if (method.equals(HttpRequest.HttpMethod.PUT)) {
                    String f = server.getWebRoot() + request.getUrl();
                    response = new HttpResponse(HttpResponse.StatusCode.OK).createFile(f, request.body);
                } else if (method.equals(HttpRequest.HttpMethod.DELETE)) {
                    File f = new File(server.getWebRoot() + request.getUrl());
                    response = new HttpResponse(HttpResponse.StatusCode.OK).deleteFile(f);
                } else if (method.equals(HttpRequest.HttpMethod.OPTIONS)) {
                    response = new HttpResponse(HttpResponse.StatusCode.OK).getOptions(request);
                    //own code ends
                }else {
                    response = new HttpResponse(HttpResponse.StatusCode.NOT_IMPLEMENTED);
                }

                respond(response);

            } else {
                System.err.println("Server accepts only HTTP protocol.");
            }

            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Error in client's IO.");
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("Error while closing client socket.");
            }
        }
    }
    //own code starts
    private void respond(HttpResponse response) {
        PrintWriter writer = new PrintWriter(out);
        writer.write(response.toString());
        writer.close();
    }
    //own code ends
}