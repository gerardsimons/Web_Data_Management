import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import org.apache.log4j.BasicConfigurator;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;


public class MySimpleServer implements Container
{

	// Simple server settings
	final static int PORT = 1234;

	// eXist settings
	protected static String DRIVER = "org.exist.xmldb.DatabaseImpl";
	protected static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
	protected static String collectionPath = "/db/sandbox/";
	// protected static String resourceName = "movies.xml";

	private final String lilyDir = "lily";
	private final String script = lilyDir + "/convert.sh";
	private final String inputDir = lilyDir + "/input/";
	private final String outputDir = lilyDir + "/output/";
	private final String defaultHTML = "html/index.html";

	// Store the last result;
	private ResourceSet lastResult;
	//And store the filenames to converted when requested
	private ArrayList<String> files;

	private PrintStream body;

	public void handle(Request request, Response response)
	{
		try {
			body = response.getPrintStream();
			long time = System.currentTimeMillis();
			Query query = request.getQuery();

			String file = query.get("file");
			String queryValue = query.get("_query");

			System.out.println("file request : " + file);
			if (file != null && file != "") {
				// convert(file);
				convert(file);
				response.setValue("Content-Type", "application/pdf");
				response.setValue("Server", "HelloWorld/1.0 (Simple 4.0)");
				response.setDate("Date", time);
				response.setDate("Last-Modified", time);

				File pdfFile = new File(outputDir + file + ".pdf");

				FileInputStream fileInputStream = new FileInputStream(pdfFile);

				int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					body.write(bytes);
				}
				fileInputStream.close();
				// body.println("<a href=\"lily/" + file + "\">File</a>");
			} 
			else if (queryValue != null && queryValue != "") 
			{
				body.println("<html><body>");
				body.println("<h1>Results</h1>");
				response.setValue("Content-Type", "text/html");
				response.setValue("Server", "HelloWorld/1.0 (Simple 4.0)");
				response.setDate("Date", time);
				response.setDate("Last-Modified", time);

				System.out.println("Received _query=" + queryValue);

				if (queryValue != null && queryValue.trim() != "") {
					lastResult = documentsForSearchTerm(queryValue);

					if (lastResult != null) {
						files = new ArrayList<String>();
						ResourceIterator iterator = lastResult.getIterator();
						while (iterator.hasMoreResources()) {
							Resource r = iterator.nextResource();
							// System.out.println((String) r.getContent());
							// body.println((String) r.getContent());
							String fileName = normalizeId(r.getId());
							files.add(fileName);
						}
					}
					body.println("<ul>");
					for (String pdfFile : files) {
						body.println("<li><a target=\"_blank\" href=\"http://localhost:" + PORT
								+ "?file=" + pdfFile + "\">" + pdfFile
								+ "</a></li>");
					}
					body.println("</ul>");
				}

			} 
			else 
			{
				FileReader htmlFile = new FileReader(defaultHTML);
				BufferedReader br = new BufferedReader(htmlFile);
				String line;
				response.setValue("Content-Type", "text/html");
				response.setValue("Server", "HelloWorld/1.0 (Simple 4.0)");
				response.setDate("Date", time);
				response.setDate("Last-Modified", time);
				while ((line = br.readLine()) != null) {
					body.println(line);
				}
				br.close();
			}
			body.println("</body></html>");
			body.close();

		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String normalizeId(String id)
	{
		return id.substring(0, id.lastIndexOf(".") + 4);
	}

	/**
	 * This method matches any child node of an MusicXML document against the
	 * given search term using xQuery contains.
	 * 
	 * @param searchTerm
	 *            the term to be matched with
	 * @return the Set of results that match the term
	 */
	public ResourceSet documentsForSearchTerm(String searchTerm)
	{
		String xQuery = "let $col := collection() for $doc in $col where contains(lower-case($doc/score-partwise/.),'"
				+ searchTerm.toLowerCase() + "') return $doc";
		return query(xQuery);
	}

	public ResourceSet query(String xQuery)
	{
		ResourceSet result = null;
		try {
			// initialize database driver
			Class cl = Class.forName(DRIVER);
			Database database = (Database) cl.newInstance();
			DatabaseManager.registerDatabase(database);

			// get the collection
			Collection col = DatabaseManager
					.getCollection(URI + collectionPath);

			XQueryService service = (XQueryService) col.getService(
					"XQueryService", "1.0");

			System.out.println(xQuery);

			service.setProperty("indent", "yes");

			result = service.query(xQuery);
			System.out.println(result.getSize() + " results found.");
			return result;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void printLine(Object o)
	{
		if (body != null) {
			body.println(o);
		}
		System.out.println(o);
	}

	// Convert an XML file in the lily/input folder to a .PNG in the output.
	// Afterwards remove the input files.
	public void convert(String file)
	{

		String[] cmdCall = { script, file };
		System.out.println(script);
		try {
			Process p = Runtime.getRuntime().exec(cmdCall);
			p.waitFor();

			String line;

			BufferedReader error = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			while ((line = error.readLine()) != null) {
				System.out.println(line);
			}
			error.close();

			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}

			input.close();

			OutputStream outputStream = p.getOutputStream();
			PrintStream printStream = new PrintStream(outputStream);
			printStream.println();
			printStream.flush();
			printStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test()
	{
		// executeScript();

		ResourceSet result = query("sleep");
		try {
			ResourceIterator ri = result.getIterator();

			while (ri.hasMoreResources()) {
				System.out.println(ri.nextResource().getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();

		if (true) {
			Container container = new MySimpleServer();
			Server server = new ContainerServer(container);
			Connection connection = new SocketConnection(server);
			SocketAddress address = new InetSocketAddress(PORT);

			connection.connect(address);
		} else {

			MySimpleServer myServer = new MySimpleServer();

			// myServer.test();
			// convert("0.xml");
		}
	}
}