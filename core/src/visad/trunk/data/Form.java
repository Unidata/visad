/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 2000 Bill Hibbard, Curtis Rueden, Tom
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

$Id: Form.java,v 1.5 2000-04-26 15:44:42 dglo Exp $
*/

package visad.data;


import visad.MathType;


/**
 * A leaf-node in the data form hierarchy for the storage of
 * persistent data objects.
 */
public abstract class
Form
    extends FormNode
{
    /**
     * The MathType of an existing data object.  Set by the
     * getForms(Data data) method.
     */
    protected MathType	mathType;


    /**
     * Construct a data form of the given name.
     */
    public Form(String name)
    {
	super(name);
    }


    /**
     * Get the MathType.
     */
    public MathType
    getMathType()
    {
	return mathType;
    }
}
