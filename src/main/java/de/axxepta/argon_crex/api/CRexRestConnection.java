package de.axxepta.argon_crex.api;

import de.axxepta.oxygen.api.*;
import org.basex.io.IOStream;
import org.basex.io.IOUrl;
import org.basex.util.http.HttpText;
import org.basex.util.http.MediaType;

import static de.axxepta.oxygen.api.ConnectionUtils.*;
import static org.basex.util.http.HttpMethod.POST;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CRexRestConnection extends RestConnection implements Connection {

    private final IOUrl cRexUrl = new IOUrl("https://c-rex.net/api/Token");
    private final URL getDOCXUrl = new URL("https://www.c-rex.net/api/XBot/Convert/Demo/docx2DITATopicOxygen");
    private final URL getXLSXUrl = new URL("https://www.c-rex.net/api/XBot/Convert/Demo/xlsx2DITATopicOxygen");
    private final URL getDITAfromDOCXUrl = new URL("https://www.c-rex.net/api/XBot/Convert/Demo/DITATopicOxygen2docx");
    private final URL getDITAfromXLSXUrl = new URL("https://www.c-rex.net/api/XBot/Convert/Demo/DITATopicOxygen2xlsx");
    private final String bearerUser = "demo@c-rex.net";
    private final String bearerPass = "demo$crex";
    private static String bearerToken = "";
    private static final String LINE_FEED = "\r\n";

    public CRexRestConnection(final String server, final int port, final String user,
                             final String password) throws MalformedURLException {
        super(server, port, user, password);
    }

    @Override
    public byte[] get(final BaseXSource source, final String path, boolean export) throws IOException {
        byte[] doc = request(getQuery("get-" + source), PATH, path);
        try {
            if (path.toUpperCase().endsWith("DOCX") && !export) {
                doc = getConvertedData(doc, getDOCXUrl, path);
            }
            if (path.toUpperCase().endsWith("XLSX") && !export) {
                doc = getConvertedData(doc, getXLSXUrl, path);
            }
        } catch (IOException ie) {
            ie.printStackTrace();
            throw new IOException(ie);
        }
        return doc;
    }

    @Override
    public void put(final BaseXSource source, final String path, final byte[] resource, boolean binary, String encoding,
                    String owner, String versionize, String versionUp)
            throws IOException {
        byte[] convertedResource;
        System.out.println("Encoding: " + encoding);
        if (path.toUpperCase().endsWith("DOCX") && !encoding.equals("")) {
            convertedResource = getConvertedData(resource, getDITAfromDOCXUrl, path);
        } else  if (path.toUpperCase().endsWith("XLSX") && !encoding.equals("")) {
            convertedResource = getConvertedData(resource, getDITAfromXLSXUrl, path);
        } else {
            convertedResource = resource;
        }
        request(getQuery("put-" + source), PATH, path, RESOURCE, prepare(convertedResource), BINARY, Boolean.toString(binary),
                ENCODING, encoding, OWNER, owner, VERSIONIZE, versionize, VERSION_UP, versionUp);
    }

    private byte[] getConvertedData(byte[] doc, URL cRexUrl, String path) throws IOException {
        String bearerToken = getBearerToken();
        if (cRexUrl.equals(getDITAfromDOCXUrl) || cRexUrl.equals(getDITAfromXLSXUrl)) {
            path = getNameFromPath(path) + ".dita";
        }
        final java.net.HttpURLConnection cRexConnection = (java.net.HttpURLConnection) cRexUrl.openConnection();
        try {
            final String boundary = "___" + System.currentTimeMillis() + "___";
            cRexConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);
            cRexConnection.setDoOutput(true);
            cRexConnection.setRequestMethod(POST.name());
            cRexConnection.setRequestProperty(HttpText.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA.toString() +
                    "; boundary=" + boundary);
            cRexConnection.setRequestProperty("User-Agent", "Argon-C-Rex");
            try (final OutputStream out = cRexConnection.getOutputStream()) {
                multipartWrite(out, doc, boundary, getFileFromPath(path));
            }
            byte[] docZIP = new IOStream(cRexConnection.getInputStream()).read();
            return unzipCRexPackage(docZIP, path);
        } finally {
            cRexConnection.disconnect();
        }
    }

    private byte[] unzipCRexPackage(byte[] docZIP, String path) throws IOException {
        String tempZIPFile = System.getProperty("user.home") + "/argon/" + CRexRestConnection.getNameFromPath(path) + ".zip";
        byte[] unzippedStream = null;
        try (FileOutputStream fos = new FileOutputStream(tempZIPFile)) {
            fos.write(docZIP);
        }
        try (ZipFile zf = new ZipFile(tempZIPFile)) {
            for (ZipEntry entry : Collections.list(zf.entries())) {
                if (entry.getName().endsWith(CRexRestConnection.getNameFromPath(path) + ".dita") ||
                        entry.getName().endsWith(CRexRestConnection.getNameFromPath(path) + ".docx") ||
                        entry.getName().endsWith(CRexRestConnection.getNameFromPath(path) + ".xlsx")) {
                    try {
                        int zipStreamLength = Math.toIntExact(entry.getSize());
                        unzippedStream = new byte[zipStreamLength];
                        try (InputStream is = new BufferedInputStream(zf.getInputStream(entry))) {
                            int readLength = is.read(unzippedStream, 0, zipStreamLength);
                            System.out.println(zipStreamLength - readLength);
                        }
                    } catch (ArithmeticException ae) {
                        throw new IOException("Zipped stream too long!");
                    }
                }
            }
        }
        //File f = new File(tempZIPFile);
        //f.delete();
        if (unzippedStream == null) {
            unzippedStream = new byte[0];
        }

        return unzippedStream;
    }

    private static String getNameFromPath(String path) {
        int startPos = path.lastIndexOf("/") + 1;
        int endPos = path.lastIndexOf(".");
        return path.substring(startPos, endPos);
    }

    private static String getFileFromPath(String path) {
        int startPos = path.lastIndexOf("/") + 1;
        return path.substring(startPos);
    }

    private String getBearerToken() throws IOException {
        if (CRexRestConnection.bearerToken.equals("")) {  // ToDo: or bearer token is not valid any more
            final java.net.HttpURLConnection cRexConnection = (java.net.HttpURLConnection) cRexUrl.connection();
            try {
                cRexConnection.setDoOutput(true);
                cRexConnection.setRequestMethod(POST.name());
                cRexConnection.setRequestProperty(HttpText.CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
/*                byte[] tokenRequest = ("grant_type=password&username=" + URLEncoder.encode(bearerUser, "UTF-8") +
                        "&password=" + URLEncoder.encode(bearerPass, "UTF-8")).getBytes("UTF-8");*/
                byte[] tokenRequest = ("grant_type=password&username=" + bearerUser + "&password=" + bearerPass).getBytes("UTF-8");
                try (final OutputStream out = cRexConnection.getOutputStream()) {
                    out.write(tokenRequest);
                    out.close();
                }
                CRexRestConnection.bearerToken = extractToken(new IOStream(cRexConnection.getInputStream()).read());
            } finally {
                cRexConnection.disconnect();
            }
        }
        return CRexRestConnection.bearerToken;
    }

    private String extractToken(byte[] responseArray) {
        String response;
        try {
            response = new String(responseArray, "UTF-8");
        } catch (UnsupportedEncodingException ue) {
            response = "";
        }
        int tokenIDpos = response.indexOf("access_token");
        if (tokenIDpos < 0) {
            return "";
        } else {
            int startPos = response.indexOf("\"", tokenIDpos + 13);
            int endPos = response.indexOf("\"", startPos + 1);
            return response.substring(startPos + 1, endPos);
        }
    }

    private static void multipartWrite(OutputStream os, byte[] content, String boundary, String fileName) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"),
                true);

        String contentType;
        if (fileName.endsWith("dita"))
            contentType = "application/xml";
        else
            contentType = Files.probeContentType(Paths.get(fileName));
        System.out.println(contentType);
        //
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"sourceFileName0\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: " + contentType).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        os.write(content);
        os.flush();
        writer.append(LINE_FEED);
        writer.flush();
        //

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
    }

}
