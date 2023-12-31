package edu.coursera.distributed;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;

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
     * @param ncores The number of cores that are available to your
     *               multi-threaded file server. Using this argument is entirely
     *               optional. You are free to use this information to change
     *               how you create your threads, or ignore it.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs,
            final int ncores) throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {

        	Socket s = socket.accept();

        	Thread thread = new Thread(()-> {
        		InputStream iStream = null;
				try {
					iStream = s.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
        		InputStreamReader ISReader = new InputStreamReader(iStream);
        		BufferedReader br = new BufferedReader(ISReader);

        		String line = null;
				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
        		assert line != null;
        		assert line.startsWith("GET");
        		final String path = line.split(" ")[1];
        	
        		final String contents = fs.readFile(new PCDPPath(path));

        		OutputStream out = null;
				try {
					out = s.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
        		try {
            		out.close();
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	});
        	thread.start();
        }
    }
}
