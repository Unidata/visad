//
// HdfeosFile.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 1998 Bill Hibbard, Curtis Rueden, Tom
Rink and Dave Glowacki.

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

package visad.data.hdfeos; 

import java.util.*;
import java.lang.*;
import visad.data.hdfeos.hdfeosc.HdfeosLib;

  public class HdfeosFile {

    String filename;
    int  file_id;
    int  n_structs;

    Vector Structs;

    static Vector openedFiles = new Vector();          // all opened file objects

    static int DFACC_READ = 1;
    static int HDFE_mode = 4;
 

    HdfeosFile( String filename ) 
    throws HdfeosException
    {

      this.filename = filename;

      String[] swath_list = {"empty"};
      int n_swaths = Library.Lib.SWinqswath( filename, swath_list );
      n_structs = 0;
      Structs = new Vector();

      if ( n_swaths > 0 )  {

         file_id = Library.Lib.SWopen( filename, DFACC_READ );

            System.out.println( "file_id: "+file_id);

         StringTokenizer swaths = new StringTokenizer( swath_list[0], ",", false );
         while ( swaths.hasMoreElements() )
         {
           String swath = (String) swaths.nextElement();
           EosSwath obj = new EosSwath( file_id, swath );
           Structs.addElement( (EosStruct)obj );
           n_structs++;
         }
      } 

      String[] grid_list = {"empty"};
      int n_grids = Library.Lib.GDinqgrid( filename, grid_list );

      if ( n_grids > 0 ) {

         file_id = Library.Lib.GDopen( filename, DFACC_READ );

            System.out.println( "file_id: "+file_id);

         StringTokenizer grids = new StringTokenizer( grid_list[0], ",", false );
 
         while ( grids.hasMoreElements() ) 
         {
           String grid = (String) grids.nextElement();
           EosGrid g_obj = new EosGrid( file_id, grid );
           Structs.addElement( (EosStruct)g_obj );
           n_structs++;
         }

      }

      openedFiles.addElement( this );
    }

    public int getNumberOfStructs() {

      return n_structs; 
    }

    public EosStruct getStruct( int ii ) {

      return (EosStruct) Structs.elementAt(ii);
    }

    public String getFileName()
    {
      return filename;
    }

    public static void close() throws HdfeosException {

      for ( Enumeration e = openedFiles.elements(); e.hasMoreElements(); ) 
      {
        int status = Library.Lib.EHclose( ((HdfeosFile) e.nextElement()).file_id );

        if ( status < 0 ) 
        {
          throw new HdfeosException(" trouble closing file, status: "+status );
        }
      }
    }
}
