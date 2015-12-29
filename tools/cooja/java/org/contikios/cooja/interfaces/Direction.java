/*
 * Copyright (c) 2015, Vishwesh Rege.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package org.contikios.cooja.interfaces;

import org.contikios.cooja.interfaces.Position;

//import java.lang.math;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.jdom.Element;

import org.contikios.cooja.*;

/**
 * Antenna direction.
 *
 * <p>
 * This observable notifies when the direction is changed.
 *
 * @author Vishwesh Rege
 */
@ClassDescription("Direction")
public class Direction extends MoteInterface {
  private static Logger logger = Logger.getLogger(Direction.class);
  private Mote mote = null;
  private double orientationDegrees;
  private double beamwidthDegrees;
  private boolean omni;

  public Direction(Mote mote) {
    this.mote = mote;

    beamwidthDegrees = 90.0;
    orientationDegrees = 0.0;
    omni = true;
  }

  public void setAntennaType (int type) {
    if(type == 0) {
	omni = true;
    }
    else {
	omni = false;
    }

    this.setChanged();
    this.notifyObservers(mote);
  }

  public boolean getAntennaType () {
	return omni;
  }

  public void setOrientation (double orientationDegrees) {
    this.orientationDegrees = orientationDegrees;

    this.setChanged();
    this.notifyObservers(mote);
  }

  public double getOrientation () {
	return orientationDegrees;
  }

  public double getGain (Position destPos) {
	Position sourcePos = this.mote.getInterfaces().getPosition();
	double beamwidthRadians = beamwidthDegrees*Math.PI/180;
	double cosin = Math.cos(beamwidthRadians/4);
	double logan = Math.log10(cosin);
	double exp = -3/(20*logan);
	double gain = Math.pow(Math.cos(getAngle(destPos)), exp);
	if (omni) {
		return 1.0;
	}
	else {
		return gain;
	}
  }

  public double getAngle (Position destPos) {	// get angle of dest w.r.t mySelf
	Position sourcePos = this.mote.getInterfaces().getPosition();
	double x = destPos.getXCoordinate() - sourcePos.getXCoordinate();
	double y = destPos.getYCoordinate() - sourcePos.getYCoordinate();
	double angle = Math.atan2(y,x);	// returns angle between -PI/2 to PI/2???
	return angle - getOrientation()*Math.PI/180;
  }

  public JPanel getInterfaceVisualizer() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    final NumberFormat form = NumberFormat.getNumberInstance();

    final JLabel directionLabel = new JLabel();
    directionLabel.setText("phi=" + form.format(getOrientation()));

    panel.add(directionLabel);

    Observer observer;
    this.addObserver(observer = new Observer() {
      public void update(Observable obs, Object obj) {
        directionLabel.setText("phi=" + form.format(getOrientation()));
      }
    });

    // Saving observer reference for releaseInterfaceVisualizer
    panel.putClientProperty("intf_obs", observer);

    return panel;
  }

  public void releaseInterfaceVisualizer(JPanel panel) {
    Observer observer = (Observer) panel.getClientProperty("intf_obs");
    if (observer == null) {
      logger.fatal("Error when releasing panel, observer is null");
      return;
    }

    this.deleteObserver(observer);
  }

  public Collection<Element> getConfigXML() {
    Vector<Element> config = new Vector<Element>();
    Element element;

    // X coordinate
    element = new Element("orientationDegrees");
    element.setText(Double.toString(getOrientation()));
    config.add(element);

    return config;
  }

  public void setConfigXML(Collection<Element> configXML, boolean visAvailable) {
    double orientationDegrees = 0;

    for (Element element : configXML) {
      if (element.getName().equals("orientationDegrees")) {
        orientationDegrees = Double.parseDouble(element.getText());
      }
    }

    setOrientation(orientationDegrees);
  }

  public String toString() {
    return "Mote interface : Direction";
  }

}




