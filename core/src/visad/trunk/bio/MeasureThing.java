//
// MeasureThing.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 2002 Bill Hibbard, Curtis Rueden, Tom
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

package visad.bio;

import java.awt.Color;

/**
 * MeasureThing is the base class for measurement
 * constructs such as lines and points.
 */
public abstract class MeasureThing {

  /** Color of this measurement. */
  Color color;

  /** Group of this measurement. */
  MeasureGroup group;

  /** Standard id tag. -1 if not a standard line. */
  int stdId = -1;

  /** Whether this measurement is part of the pilot line. */
  boolean pilot;

  /** Sets the color of this measurement to match the given color. */
  public abstract void setColor(Color color);

  /** Sets the standard id of this measurement to match the given id. */
  public abstract void setStdId(int stdId);

}
