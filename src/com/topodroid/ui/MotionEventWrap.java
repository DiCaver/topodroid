/*
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
 *
 * This class has been taken from "Hello, Android" source code
***/
package com.topodroid.ui;

import android.os.Build;
import android.view.MotionEvent;

public class MotionEventWrap
{
   protected MotionEvent event;

   public MotionEventWrap( MotionEvent event )
   {
      this.event = event;
   }

   public static MotionEventWrap wrap(MotionEvent event)
   {
      // FIXME NFE 

      // Use Build.VERSION.SDK_INT if you don't have to support Cupcake
      if (Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.ECLAIR) { // DEPRECATED API-16
         // TDLog.d("MotionEventWrap using Eclair version");
         return new MotionEventEclair(event);
      } else {
         // TDLog.d("MotionEventWrap using Cupcake/Donut version");
         return new MotionEventWrap(event);
      }
   }
   
   public int getAction() { return event.getAction(); }

   // this is the X coord (pixels, from the center ?) adjusted for containing window and views
   // to get the real X use getRawX()
   //
   public float getX() { return event.getX(); }
   public float getX(int pointerIndex)
   {
      verifyPointerIndex(pointerIndex);
      return getX();
   }
   public float getY() { return event.getY(); }
   public float getY(int pointerIndex)
   {
      verifyPointerIndex(pointerIndex);
      return getY();
   }
   public int getPointerCount() { return 1; }
   public int getPointerId(int pointerIndex)
   {
      verifyPointerIndex(pointerIndex);
      return 0;
   }
   private void verifyPointerIndex(int pointerIndex) 
   {
      if (pointerIndex > 0) {
         throw new IllegalArgumentException(
               "Invalid pointer index for Donut/Cupcake");
      }
   }
   
}

