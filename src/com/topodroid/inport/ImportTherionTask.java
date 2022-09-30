/* @file ImportTherionTask.java
 *
 * @author marco corvi
 * @date march 2017
 *
 * @brief TopoDroid Therion import task
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.inport;

import com.topodroid.utils.TDLog;

import com.topodroid.TDX.DataHelper;
import com.topodroid.TDX.SurveyInfo;
import com.topodroid.TDX.TopoDroidApp;
import com.topodroid.TDX.MainWindow;

// import java.io.InputStreamReader;

import android.os.ParcelFileDescriptor;

// import java.lang.ref.WeakReference;

import java.util.ArrayList;

  
// NOTE survey name must be guaranteed not be in the db
public class ImportTherionTask extends ImportTask
{
  public ImportTherionTask( MainWindow main ) { super( main ); }

  // public ImportTherionTask( MainWindow main, InputStreamReader isr ) { super( main, isr ); }

  public ImportTherionTask( MainWindow main, ParcelFileDescriptor pfd ) { super( main, pfd ); }

  @Override
  protected Long doInBackground( String... str )
  {
    // TDLog.v( "import Therion task: " + str[0] + " survey " + str[1] );
    long sid = 0;
    try {
      // if fr == null, str[0] is the filename, otherwise it is the survey name
      ParserTherion parser = new ParserTherion( isr, str[0], true ); // apply_declination = true
      if ( ! parser.isValid() ) return -2L;
      if ( mApp.get() == null ) return -1L;
      if ( hasSurveyName( parser.mName ) ) {
        return -1L;
      }

      sid = mApp.get().setSurveyFromName( str[1], SurveyInfo.DATAMODE_NORMAL, false ); // IMPORT TH no update

      DataHelper app_data = TopoDroidApp.mData;
      // app_data.updateSurveyDayAndComment( sid, parser.mDate, parser.mTitle );
      // app_data.updateSurveyDeclination( sid, parser.surveyDeclination() );
      // app_data.updateSurveyInitStation( sid, parser.initStation() );
      updateSurveyMetadata( sid, parser );

      app_data.updateSurveyTeam( sid, parser.mTeam ); // TODO check return ( boolean )

      ArrayList< ParserShot > shots  = parser.getShots();
      long id = insertImportShots( sid, 1, shots ); // start id = 1

      ArrayList< ParserShot > splays = parser.getSplays();
      insertImportShots( sid, id, splays );

      // FIXME this suppose CS long-lat, ie, e==long, n==lat
      // WorldMagneticModel wmm = new WorldMagneticModel( mApp.get() );
      // ArrayList< ParserTherion.Fix > fixes = parser.getFixes();
      // for ( ParserTherion.Fix fix : fixes ) {
      //   // double asl = fix.z;
      //   double alt = wmm.geoidToEllipsoid( fix.n, fix.e, fix.z );
      //   app_data.insertFixed( sid, -1L, fix.name, fix.e, fix.n, alt, fix.z, "", 0 );
      // }

      ArrayList< ParserTherion.Station > stations = parser.getStations();
      for ( ParserTherion.Station st : stations ) {
        app_data.insertStation( sid, st.name, st.comment, st.flag, st.name ); // PRESENTATION
      }
    } catch ( ParserException e ) {
      // TDToast.makeBad( R.string.file_parse_fail );
    }
    return sid;
  }

}

