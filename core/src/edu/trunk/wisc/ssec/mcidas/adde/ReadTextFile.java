//
// ReadTextFile.java
//

/*
The code in this file is Copyright(C) 1999 by Don
Murray.  It is designed to be used with the VisAD system for 
interactive analysis and visualization of numerical data.  
 
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 1, or (at your option)
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License in file NOTICE for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package edu.wisc.ssec.mcidas.adde;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.lang.*;
import java.util.*;

/** 
 * ReadTextFile interface for McIDAS ADDE data sets.  
 *
 * <pre>
 * URLs must all have the following format   
 *   adde://host/text?file=filename.ext
 *
 * there can be any valid combination of the following supported keywords:
 *
 *   file - name of text file on ADDE server
 *
 * the following keywords are required:
 *
 *   file
 *
 * an example URL might look like:
 *   adde://viper/text?file=filename.ext
 * </pre>
 *
 * @author Tom Whittaker (derived from DataSetInfo by Don Murray)
 * 
 */
public class ReadTextFile 
{

    // load protocol for ADDE URLs
    // See java.net.URL for explanation of URL handling
    static 
    {
        try 
        {
            String handlers = System.getProperty("java.protocol.handler.pkgs");
            String newProperty = null;
            if (handlers == null)
                newProperty = "edu.wisc.ssec.mcidas";
            else if (handlers.indexOf("edu.wisc.ssec.mcidas") < 0)
                newProperty = "edu.wisc.ssec.mcidas | " + handlers;
            if (newProperty != null)  // was set above
                System.setProperty("java.protocol.handler.pkgs", newProperty);
        }
        catch (Exception e)
        {
            System.out.println(
                "Unable to set System Property: java.protocol.handler.pkgs"); 
        }
    }

    private int status=0;                 // read status
    private String statusString = null;
    private boolean debug = false;        // debug
    private Vector linesOfText = null;
    private URLConnection urlc;           
    private DataInputStream dis;
    
    /**
     * creates a ReadTextFile object that allows reading an ADDE
     * text file or TEXT dataset
     *
     * @param request ADDE URL to read from.  See class javadoc.
     *
     * <pre>
     * an example URL might look like:
     *   adde://viper/datasetinfo?group=gvar&descr=FILE=RESOLV.SRV
     * </pre>
     *
     * @exception AddeURLException if there are no datasets of the particular
     *            type or there is an error reading data
     *
     */
    public ReadTextFile(String request) {
   
      status = 0;
      statusString = "OK";
      try {
          URL url = new URL(request);
          if (debug) System.out.println("Request: "+request);
          urlc = url.openConnection();
          InputStream is = urlc.getInputStream();
          dis = new DataInputStream(is);
      }
      catch (AddeURLException ae) 
      {
          status = -1;
          statusString = "No file found";
      }
      catch (Exception e) 
      {
          status = -2;
          statusString = "Error opening connection: "+e;
      }

     int numBytes;
     linesOfText = new Vector();

     if (status == 0) {

       try {
         numBytes = ((AddeURLConnection) urlc).getInitialRecordSize();
         if (debug) 
          System.out.println("ReadTextFile: initial numBytes = " + numBytes);

         numBytes = dis.readInt();
          while ((numBytes = dis.readInt()) != 0) {

            if (debug) 
               System.out.println("ReadTextFile: numBytes = " + numBytes);

              byte[] data = new byte[numBytes];
              dis.readFully(data,0,numBytes);
              String s = new String(data);
              if (debug) System.out.println(s);
              linesOfText.addElement(s);
         } 

       } catch (Exception iox) {
         statusString = " "+iox;
         System.out.println(" "+iox);
       }

     }
     if (linesOfText.size() < 1) statusString = "No data read";
    }

    public String getStatus() {
      return statusString;
    }

    public Vector getText() {

      return linesOfText;
    }
 



    /** test by running 'java edu.wisc.ssec.mcidas.adde.ReadTextFile' */
    public static void main (String[] args)
        throws Exception
    {
        String request = (args.length == 0)
          ? "adde://adde.ucar.edu/text?file=PUBLIC.SRV"
          : args[0];

        ReadTextFile rtf = new ReadTextFile(request);
        String status = rtf.getStatus();
        System.out.println("Status = "+status);
        if (status.equals("OK")) {
          Vector l = rtf.getText();
          for (int i=0; i<l.size(); i++) {
            System.out.println( (String)l.elementAt(i));
          }
        }
    }
}
