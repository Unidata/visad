//
// RemoteDisplay.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 1999 Bill Hibbard, Curtis Rueden, Tom
Rink, Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and
Tommy Jasmin.
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA 02111-1307, USA
*/

package visad;

import java.awt.event.MouseEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.Vector;

/**
   RemoteDisplay is the interface for Remote Display-s.<P>
*/
public interface RemoteDisplay extends Remote, Display {
  String getName() throws VisADException, RemoteException;
  String getDisplayClassName() throws RemoteException;
  int getDisplayAPI() throws VisADException, RemoteException;
  String getDisplayRendererClassName() throws RemoteException;
  Vector getMapVector() throws VisADException, RemoteException;
  Vector getConstantMapVector()
	throws VisADException, RemoteException;
  RemoteGraphicsModeControl getGraphicsModeControl()
	throws VisADException, RemoteException;
  Vector getReferenceLinks()
	throws VisADException, RemoteException;

  /* CTR 5 Oct 1999 - begin code for slaved displays */
  void addSlave(RemoteSlaveDisplay display)
        throws VisADException, RemoteException;
  void removeSlave(RemoteSlaveDisplay display)
        throws VisADException, RemoteException;
  void removeAllSlaves() throws VisADException, RemoteException;
  boolean hasSlaves() throws VisADException, RemoteException;
  void sendMouseEvent(MouseEvent e) throws VisADException, RemoteException;
  /* CTR 5 Oct 1999 - end code for slaved displays */

  RemoteDisplayMonitor getRemoteDisplayMonitor()
        throws RemoteException;
  DisplaySync getDisplaySync()
        throws RemoteException;
  RemoteDisplaySync getRemoteDisplaySync()
        throws RemoteException;
}
