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

package org.contikios.cooja.plugins.skins;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import org.contikios.cooja.ClassDescription;
import org.contikios.cooja.Mote;
import org.contikios.cooja.Simulation;
import org.contikios.cooja.SimEventCentral.MoteCountListener;
import org.contikios.cooja.interfaces.Direction;
import org.contikios.cooja.interfaces.Position;
import org.contikios.cooja.plugins.Visualizer;
import org.contikios.cooja.plugins.VisualizerSkin;

/**
 * Visualizer skin for mote direction.
 *
 * Paints the three dimensional mote direction on the right-hand side of the mote.
 *
 * @author Fredrik Osterlind
 */
@ClassDescription("Directions")
public class DirectionVisualizerSkin implements VisualizerSkin {
  private static Logger logger = Logger.getLogger(DirectionVisualizerSkin.class);

  private Simulation simulation = null;
  private Visualizer visualizer = null;

  private Observer directionObserver = new Observer() {
    public void update(Observable obs, Object obj) {
      visualizer.repaint();
    }
  };
  private MoteCountListener simObserver = new MoteCountListener() {
    public void moteWasAdded(Mote mote) {
      Direction p = mote.getInterfaces().getDirection();
		//JOptionPane.showMessageDialog(null, "Himote0", "Title", JOptionPane.PLAIN_MESSAGE);
      if (p != null) {
		//JOptionPane.showMessageDialog(null, "Himote1", "Title", JOptionPane.PLAIN_MESSAGE);
        p.addObserver(directionObserver);
      }
    }
    public void moteWasRemoved(Mote mote) {
      Direction p = mote.getInterfaces().getDirection();
      if (p != null) {
        p.deleteObserver(directionObserver);
      }
    }
  };

  public void setActive(Simulation simulation, Visualizer vis) {
    this.simulation = simulation;
    this.visualizer = vis;

    simulation.getEventCentral().addMoteCountListener(simObserver);
    for (Mote m: simulation.getMotes()) {
      simObserver.moteWasAdded(m);
    }
  }

  public void setInactive() {
    simulation.getEventCentral().removeMoteCountListener(simObserver);
    for (Mote m: simulation.getMotes()) {
      simObserver.moteWasRemoved(m);
    }
  }

  public Color[] getColorOf(Mote mote) {
    return null;
  }

  public void paintBeforeMotes(Graphics g) {
  }

  public void paintAfterMotes(Graphics g) {
    g.setColor(Color.BLACK);

    /* Paint direction right of motes */
    Mote[] allMotes = simulation.getMotes();
    for (Mote mote: allMotes) {
      Direction dir = mote.getInterfaces().getDirection();
      Position pos = mote.getInterfaces().getPosition();
      Point pixel = visualizer.transformPositionToPixel(pos);

      String msg = "";
      String dirString;
      String[] parts;

      /* X */
      dirString = String.valueOf(dir.getOrientation());
        //msg += dirString;
        //msg += " deg";


      g.drawString(dirString, pixel.x + Visualizer.MOTE_RADIUS + 4, pixel.y + 4);
    }
  }

  public Visualizer getVisualizer() {
    return visualizer;
  }
}
