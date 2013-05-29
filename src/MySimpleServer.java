import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import org.exist.xmldb.XQueryService; 
import org.xmldb.api.DatabaseManager; 
import org.xmldb.api.base.Collection; 
import org.xmldb.api.base.CompiledExpression; 
import org.xmldb.api.base.Database; 
import org.xmldb.api.base.Resource; 
import org.xmldb.api.base.ResourceIterator; 
import org.xmldb.api.base.ResourceSet; 
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;	

public class MySimpleServer implements Container {
	
	//Simple server settings
	final static int PORT = 1234;
	
	//eXist settings
	protected static String DRIVER = "org.exist.xmldb.DatabaseImpl"; 
    protected static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc"; 
    protected static String collectionPath = "/db/project/music"; 
    //protected static String resourceName = "movies.xml";
    
    private PrintStream body;

   public void handle(Request request, Response response) {
      try {
          body = response.getPrintStream();
         long time = System.currentTimeMillis();

         Query query = request.getQuery();
         String value = query.get("_query"); 
         
         
         System.out.println("Received _query=" + value);
         
  
         
         response.setValue("Content-Type", "text/html");
         response.setValue("Server", "HelloWorld/1.0 (Simple 4.0)");
         response.setDate("Date", time);
         response.setDate("Last-Modified", time);
         body.println("<html><body>");
         body.println("<h1>Results</h1>");
         
         
         if(value != null && value.trim() != "")
         {
        	 ResourceSet result = query(value);
        	 if(result != null)
        	 {
	        	 ResourceIterator i = result.getIterator(); 
	        	 while (i.hasMoreResources()) { 
	                 Resource r = i.nextResource(); 
	                 System.out.println((String) r.getContent());
	                 body.println((String) r.getContent());
	        	 }
        	 }
         }
         body.println("</body></html>");
         body.close();
         
      } catch(Exception e) {
         e.printStackTrace();
      }
   } 
   
   public ResourceSet query(String title)
   {
	   ResourceSet result = null;
	   title = title.toLowerCase();
	   try
	   {
		   // initialize database driver 
	       Class cl = Class.forName(DRIVER); 
	       Database database = (Database) cl.newInstance(); 
	       DatabaseManager.registerDatabase(database); 
	
	       // get the collection 
	       Collection col = DatabaseManager.getCollection(URI + collectionPath); 
	       
	       String[] xmlFiles = col.listResources();
	       if(xmlFiles.length == 0) printLine(URI + collectionPath + " contains no children.");
	       // query a document 
	       
	       //String query = "doc("/db/project/music/john_lennon-eleano_rigby.xml')[contains(lower-case(score-partwise/movement-title),"rigby")]"
	       
	       for(int i = 0 ; i < xmlFiles.length ; i++)
	       {
	    	   String xmlFile = xmlFiles[i];
	    	   String xQuery = "doc('" + xmlFile + "')/score-partwise[contains(lower-case(.),'" + title + "')]";
	    	   System.out.println("Execute xQuery = " + xQuery); 
		        
			   // Instantiate a XQuery service 
			   XQueryService service = (XQueryService) col.getService("XQueryService", 
			                   "1.0"); 
			   service.setProperty("indent", "yes"); 
			
			   // Execute the query, print the result 
			   	 ResourceSet res = service.query(xQuery);
			   if(result != null)
			   {
				   ResourceIterator iter = res.getIterator(); 
		        	 while (iter.hasMoreResources()) { 
		                 Resource r = iter.nextResource(); 
		                 result.addResource(r);
		        	 }
			   }
			   else
			   {
				   result = res;
			   }
	       }
	       /*
	       String xQuery = "for $x in doc('" + resourceName + "')//title where $x='" + title  
                   + "' return data($x)";
                   */ 
		  
	   }
	   catch(ClassNotFoundException e)
	   {
		   e.printStackTrace();
	   	} 
   		catch (InstantiationException e) {
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
	   if(body != null)
	   {
		   body.println(o);
	   }
	   System.out.println(o);
   }
   
   public static void executeScript() {
       try {
           Process proc = Runtime.getRuntime().exec("lily/convert.sh"); //Whatever you want to execute
           BufferedReader read = new BufferedReader(new InputStreamReader(
                   proc.getInputStream()));
           try {
               proc.waitFor();
           } catch (InterruptedException e) {
               System.out.println(e.getMessage());
           }
           while (read.ready()) {
               System.out.println(read.readLine());
           }
       } catch (IOException e) {
           System.out.println(e.getMessage());
       }
   }

   public static void main(String[] args) throws Exception {
	   
	  //executeScript();
	   MySimpleServer myServer = new MySimpleServer();
	   myServer.query("rigby");
	   if(true)
	   {
		   Container container = new MySimpleServer();
		   Server server = new ContainerServer(container);
		   Connection connection = new SocketConnection(server);
		   SocketAddress address = new InetSocketAddress(PORT);

		   connection.connect(address);
	   }
   }
}