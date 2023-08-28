package edu.coursera.distributed;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {

        	Socket s = socket.accept();
        	InputStream iStream = s.getInputStream();
        	InputStreamReader ISReader = new InputStreamReader(iStream);
        	BufferedReader br = new BufferedReader(ISReader);

        	String line = br.readLine();
        	assert line != null;
        	assert line.startsWith("GET");
        	final String path = line.split(" ")[1];
        	
        	final String contents = fs.readFile(new PCDPPath(path));

        	OutputStream out = s.getOutputStream();
        	PrintWriter pw = new PrintWriter(out);
        	
        	if(contents != null) {
        		pw.write("HTTP/1.0 200 OK\r\n");
                pw.write("Server: FileServer\r\n");
        		pw.write("\r\n");
        		pw.write("\r\n");
        		pw.write(contents);
        		pw.write("\r\n");
        	} else {
        		pw.write("HTTP/1.0 404 Not Found\r\n");
        		pw.write("Server: FileServer\\r\\n");
        		pw.write("\r\n");
        		pw.write("\r\n");
        	}
        	pw.close();
        	out.close();
        	s.close();
        }
    }
}
