/**
 * Copyright 2013 Tridium, Inc. All Rights Reserved.
 */
package com.tridium.customLogin;

//import javax.baja.io.HtmlWriter;
import javax.baja.naming.BOrd;
//import javax.baja.sys.BObject;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.baja.web.BLoginTemplate;
import javax.baja.web.BWebService;
import javax.baja.web.IStateLoginTemplate; //implement for 3.7
import javax.baja.web.LoginState;//implement for 3.7
import javax.baja.util.*;
import javax.baja.file.*;
//import com.tridium.web.BHttpAuthAgent;
import java.io.*;

/**
 * Custom Login Template Example for Niagara-Central Blog
 */
public class BCustomLoginTemplate
  extends BLoginTemplate
  implements IStateLoginTemplate // 3.7 and newer versions
{
//
  /**
   * 3.7 and newer versions
   */
   public void write(BWebService service, HttpServletRequest req, HttpServletResponse resp, LoginState state)
     throws Exception
   {
     write(service, req, resp, state.getState() != 0);
   }
   
   
//    
  /**
   * Write the full HTML login page.
   *
   * @param sys The SysServlet which is the main servlet for accessing the
   *    Niagara runtime through the web server.
   * @param req The request object.
   * @param resp The response object.
   * @param retry Indicates that a login attempt has been submitted and failed.
   *   The gives custom pages an opportunity to display an error message
   *   before a retry.
   */
  public void write(BWebService service,
                             HttpServletRequest req,
                             HttpServletResponse resp,
                             boolean retry)
                             
    throws Exception

  {
    
    PrintWriter out = resp.getWriter();
    
    try
    {
      // Read the HTML into a buffer
      String html = FileUtil.readString((BIFile)BOrd.make("file:^login/login.html").get()); 
      // Find an replace the necessary information in the template so it works properly
      if (retry)
    {
        //if retry due to credential failure html will = loginFailed.html
        html = FileUtil.readString((BIFile)BOrd.make("file:^login/loginFailed.html").get()); 
    }
      else
        html = TextUtil.replace(html, FAILED, "");
      
      html = TextUtil.replace(html, STATION_NAME, Sys.getStation().getStationName());
      html = TextUtil.replace(html, AUTHENTICATION, service.getAuthenticationScheme().getTag());
      
           
      // Write out the HTML code to the response
        out.print(html);

    // for tunneling, set base to the tunnel base...

    }
    catch(Exception e)
    {
      System.out.println("Error attempting to load the custom HTML template...");
      e.printStackTrace();
      
      out.println("Error creating custom log in page...");
      out.println("");
      out.println("login.html not found.");
      out.println("");
      out.println("Confirm the html resources have been installed on the station at file:^login");
      out.println("");
      out.println("");
      e.printStackTrace(out);
    }
  }
  
  /**
   * Convert a resource path to an ord that can be resolved to locate
   * the resource.  This can be used to included images, etc. on the
   * login page.  The actual path for accessing a resource is
   * /login/path.  The path that is passed to this method has "/login/"
   * stripped off.
   *
   * For example: /login/a/b/c => a/b/c
   */
  public BOrd resourceToOrd(String path)
  {
    if (path.startsWith("custom/"))
    {
      // return custom resources:
      path=path.substring("custom/".length(), path.length());
      BOrd ord = BOrd.make("module://customLogin/rc/" + path);
      return ord;
    }
    else
    {
      // return resources from the web modules such as login.js & auth.min.js
      return BOrd.make("module://web/com/tridium/web/rc/" + path);
    }
  }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  public static final Type TYPE = Sys.loadType(BCustomLoginTemplate.class);
  public Type getType() { return TYPE; }
  //following added for custom template
  private static final String STATION_NAME = "<TEMPLATE_STATION_NAME>"; 
  private static final String AUTHENTICATION = "<TEMPLATE_AUTHENTICATION>";
  private static final String FAILED = "<TEMPLATE_FAILED>";
//  private static final String FAILED_TEXT = "<div class='failed'> <b>Login Failed</b><br/> Username and/or password invalid.</div><br/>";
  
}