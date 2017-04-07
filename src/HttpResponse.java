import java.io.*;
import java.util.*;

class HttpResponse {

    private static final String protocol = "HTTP/1.1";

    private String status;
    private Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = null;

    HttpResponse(String status) {
        this.status = status;
        setDate(new Date());
    }

    HttpResponse withFile(File f) {
        if (f.isFile()) {
            try {
                FileInputStream reader = new FileInputStream(f);
                //own code starts
                body = new byte[reader.available()];
                int read = reader.read(body);
                reader.close();
                if(read != -1) {
                    setContentLength(read);
                } else {
                    setContentLength(0);
                }
                //own code ends
                if (f.getName().endsWith(".htm") || f.getName().endsWith(".html")) {
                    setContentType(ContentType.HTML);
                } else {
                    setContentType(ContentType.TEXT);
                }
            } catch (IOException e) {
                System.err.println("Error while reading " + f);
            }
            return this;
        } else {
            String msg = "<html><body>File " + f + " not found.</body></html>";
            return new HttpResponse(StatusCode.NOT_FOUND)
                    .withHtmlBody(msg);
        }
    }
    //own code starts
    //Functions deleteFile, createFile, updateFile, getOptions are written by me
    HttpResponse deleteFile(File f) {
        boolean deleted;
        if (f.isFile()) {
            String msg="";
            try {
                deleted = f.delete();
                if(deleted) {
                    msg = "<html><body>File " + f + " deleted.</body></html>";
                    return new HttpResponse(StatusCode.OK)
                            .withHtmlBody(msg);
                } else {
                    msg = "<html><body>File " + f + " not deleted.</body></html>";
                    return new HttpResponse(StatusCode.OK)
                            .withHtmlBody(msg);
                }

            } catch (Exception e) {
                System.err.println("Error while deleting " + f);
            }
            return new HttpResponse(StatusCode.NOT_FOUND)
                    .withHtmlBody(msg);
        } else {
            String msg = "<html><body>File " + f + " not found.</body></html>";
            return new HttpResponse(StatusCode.NOT_FOUND)
                    .withHtmlBody(msg);
        }
    }
    HttpResponse getOptions(HttpRequest request) {
        String msg = "<html><body>GET, HEAD, PUT, POST and DELETE are implemented</body></html>";
        headers.put("Allow","GET, HEAD, PUT, POST, DELETE, OPTIONS");
        return new HttpResponse(StatusCode.OK)
                .withHtmlBody(msg);
    }
    HttpResponse createFile(String f, List<String> body) {
        File file  = new File(f);
        boolean created = false;
        try {
            created = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int i=0;i<body.size();i++) {
                String s = body.get(i);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(!created) {
            String msg;
            try {
                msg = "<html><body>File " + f + " exists.</body></html>";
                return new HttpResponse(StatusCode.OK)
                        .withHtmlBody(msg);

            } catch (Exception e) {
                System.err.println("Error while reading " + f);
            }
        }
        if (file.isFile()) {
            String msg="";
            try {
                msg = "<html><body>File " + f + " created.</body></html>";
                return new HttpResponse(StatusCode.OK)
                        .withHtmlBody(msg);

            } catch (Exception e) {
                System.err.println("Error while reading " + f);
            }
            return new HttpResponse(StatusCode.NOT_FOUND)
                    .withHtmlBody(msg);
        } else {
            String msg = "<html><body>File " + f + " not created.</body></html>";
            return new HttpResponse(StatusCode.NOT_FOUND)
                    .withHtmlBody(msg);
        }
    }
    HttpResponse updateFile(String f, List<String> body) {
        File file  = new File(f);
        System.out.println(body);
        if (file.isFile()) {
            try (PrintWriter pw = new PrintWriter(file)) {
                for (int i=0;i<body.size();i++) {
                    String s = body.get(i);
                    pw.append(s).append("\n");
                }
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String msg="";
            try {
                msg = "<html><body>File " + f + " updated.</body></html>";
                return new HttpResponse(StatusCode.OK)
                        .withHtmlBody(msg);

            } catch (Exception e) {
                System.err.println("Error while reading " + f);
            }
            return new HttpResponse(StatusCode.NOT_FOUND)
                    .withHtmlBody(msg);
        } else {
            try {
                boolean created = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (PrintWriter pw = new PrintWriter(file)) {
                for (int i=0;i<body.size();i++) {
                    String s = body.get(i);
                    pw.append(s).append("\n");
                }
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String msg = "<html><body>File " + f + " created.</body></html>";
            return new HttpResponse(StatusCode.NOT_FOUND)
                    .withHtmlBody(msg);
        }
    }
    //own code ends
    private HttpResponse withHtmlBody(String msg) {
        setContentLength(msg.getBytes().length);
        setContentType(ContentType.HTML);
        body = msg.getBytes();
        return this;
    }

    private void setDate(Date date) {
        headers.put("Date", date.toString());
    }

    private void setContentLength(long value) {
        headers.put("Content-Length", String.valueOf(value));
    }

    private void setContentType(String value) {
        headers.put("Content-Type", value);
    }

    void removeBody() {
        body = null;
    }
    //own code starts
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(protocol + " " + status + "\n");
        for (Map.Entry<String,String>  e: headers.entrySet()) {
            result.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        result.append("\r\n");
        if (body != null) {
            result.append(new String(body));
        }
        return result.toString();
    }
    //own code ends
    static class StatusCode {
        static final String OK = "200 OK";
        static final String NOT_FOUND = "404 Not Found";
        static final String NOT_IMPLEMENTED = "501 Not Implemented";
    }

    static class ContentType {
        static final String TEXT = "text/plain";
        static final String HTML = "text/html";
    }

}
