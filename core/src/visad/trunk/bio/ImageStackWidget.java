//
// ImageStackWidget.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 2001 Bill Hibbard, Curtis Rueden, Tom
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

import java.rmi.RemoteException;
import visad.*;

/** ImageStackWidget is a GUI component for stepping through an image stack. */
public class ImageStackWidget extends StepWidget
  implements ControlListener, ScalarMapListener
{

  private static final int NORMAL_BRIGHTNESS = 50;

  private ScalarMap smap;
  private AnimationControl control;
  private MeasureMatrix mm;
  private boolean grayscale = false;
  private int brightness = NORMAL_BRIGHTNESS;

  /** Constructs a new ImageStackWidget. */
  public ImageStackWidget(boolean horizontal) { super(horizontal); }

  /** Links the widget with the given measurement matrix. */
  public void setMatrix(MeasureMatrix mm) { this.mm = mm; }

  /** Links the widget with the given scalar map. */
  public void setMap(ScalarMap smap) throws VisADException, RemoteException {
    // verify scalar map
    if (smap != null && !Display.Animation.equals(smap.getDisplayScalar())) {
      throw new DisplayException("ImageStackWidget: " +
        "ScalarMap must be to Display.Animation");
    }

    // remove old listeners
    if (this.smap != null) smap.removeScalarMapListener(this);
    if (control != null) control.removeControlListener(this);

    // get control values
    this.smap = smap;
    control = (AnimationControl) smap.getControl();
    updateSlider();

    // add listeners
    if (control != null) control.addControlListener(this);
    if (smap != null) smap.addScalarMapListener(this);
  }

  /** Toggles grayscale color mode. */
  public void setGrayscale(boolean grayscale) {
    this.grayscale = grayscale;
    doColorTable();
  }

  /** Sets image brightness. */
  public void setBrightness(int brightness) {
    this.brightness = brightness;
    doColorTable();
  }

  private void doColorTable() {
    float[][] table = grayscale ?
      ColorControl.initTableGreyWedge(new float[3][256]) :
      ColorControl.initTableVis5D(new float[3][256]);

    // apply brightness (actually gamma correction)
    double gamma = 1.0 -
      (1.0 / NORMAL_BRIGHTNESS) * (brightness - NORMAL_BRIGHTNESS);
    for (int i=0; i<256; i++) {
      table[0][i] = (float) Math.pow(table[0][i], gamma);
      table[1][i] = (float) Math.pow(table[1][i], gamma);
      table[2][i] = (float) Math.pow(table[2][i], gamma);
    }

    // get color controls
    DisplayImpl display2 = mm.getDisplay();
    DisplayImpl display3 = mm.getDisplay3d();
    ColorControl cc2 = (ColorControl) display2.getControl(ColorControl.class);
    ColorControl cc3 = display3 == null ? null :
      (ColorControl) display3.getControl(ColorControl.class);

    // set color tables
    try {
      if (cc2 != null) cc2.setTable(table);
      if (cc3 != null) cc3.setTable(table);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

  private void updateSlider() {
    int max = 1;
    int cur = 1;
    if (control == null) setEnabled(false);
    else {
      setEnabled(true);
      try {
        Set set = control.getSet();
        if (set != null) max = set.getLength();
      }
      catch (VisADException exc) { if (DEBUG) exc.printStackTrace(); }
      cur = control.getCurrent() + 1;
      if (cur < 1) cur = 1;
      else if (cur > max) cur = max;
    }
    setBounds(1, max, cur);
  }

  /** Updates the current image of the image stack. */
  public void updateStep() {
    if (control != null && cur != control.getCurrent() + 1) {
      try {
        control.setCurrent(cur - 1);
        mm.setSlice(cur - 1);
      }
      catch (VisADException exc) { if (DEBUG) exc.printStackTrace(); }
      catch (RemoteException exc) { if (DEBUG) exc.printStackTrace(); }
    }
  }

  /** ControlListener method used for programmatically moving JSlider. */
  public void controlChanged(ControlEvent e) {
    if (control != null) {
      int val = control.getCurrent() + 1;
      if (step.getValue() != val) step.setValue(val);
    }
  }

  /** ScalarMapListener method used to recompute JSlider bounds. */
  public void mapChanged(ScalarMapEvent e) {
    updateSlider();
  }

  /** ScalarMapListener method used to detect new AnimationControl. */
  public void controlChanged(ScalarMapControlEvent evt) {
    int id = evt.getId();
    if (id == ScalarMapEvent.CONTROL_REMOVED ||
      id == ScalarMapEvent.CONTROL_REPLACED)
    {
      evt.getControl().removeControlListener(this);
      if (id == ScalarMapEvent.CONTROL_REMOVED) control = null;
    }

    if (id == ScalarMapEvent.CONTROL_REPLACED ||
      id == ScalarMapEvent.CONTROL_ADDED)
    {
      control = (AnimationControl) evt.getScalarMap().getControl();
      updateSlider();
      if (control != null) control.addControlListener(this);
    }
  }

}
