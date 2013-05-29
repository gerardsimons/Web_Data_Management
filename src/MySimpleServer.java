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
import org.xmldb.api.modules.XMLResource;

public class MySimpleServer implements Container {
	
	final static int PORT = 1234;

   public void handle(Request request, Response response) {
      try {
         PrintStream body = response.getPrintStream();
         long time = System.currentTimeMillis();

         Query query = request.getQuery();
         String value = query.get("_query"); 
         
         System.out.println("Received _query=" + value);
         
         
         
         response.setValue("Content-Type", "text/plain");
         response.setValue("Server", "HelloWorld/1.0 (Simple 4.0)");
         response.setDate("Date", time);
         response.setDate("Last-Modified", time);
         
         body.println("OK");
         body.close();
      
     
         
      } catch(Exception e) {
         e.printStackTrace();
      }
   } 
   


   public static void main(String[] list) throws Exception {
      Container container = new MySimpleServer();
      Server server = new ContainerServer(container);
      Connection connection = new SocketConnection(server);
      SocketAddress address = new InetSocketAddress(PORT);

      connection.connect(address);
   }
}