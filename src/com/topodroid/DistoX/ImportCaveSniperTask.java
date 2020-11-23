/* @file ImportCaveSniperTask.java
 *
 * @author marco corvi
 * @date may 2012
 *
 * @brief TopoDroid CaveSniper import task (text file only)
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

// import java.lang.ref.WeakReference;

import java.util.ArrayList;
 
class ImportCaveSniperTask extends ImportTask
{
  ImportCaveSniperTask( MainWindow main )
  {
    super( main );
  }

  @Override
  protected Long doInBackground( String... str )
  {
    long sid = 0;
    try {
      ParserCaveSniper parser = new ParserCaveSniper( str[0] ); 
      if ( ! parser.isValid() ) return -2L;
      if ( mApp.get() == null ) return -1L;
      DataHelper app_data = TopoDroidApp.mData;
      if ( app_data.hasSurveyName( parser.mName ) ) {
        return -1L;
      }
      sid = mApp.get().setSurveyFromName( parser.mName, SurveyInfo.DATAMODE_NORMAL, false ); // IMPORT CaveSniper no update

      // app_data.updateSurveyDayAndComment( sid, parser.mDate, parser.mTitle );
      // // app_data.updateSurveyDeclination( sid, parser.mDeclination );
      // app_data.updateSurveyInitStation( sid, parser.initStation() );
      parser.updateSurveyMetadata( sid, app_data );

      ArrayList< ParserShot > shots  = parser.getShots();
      // ArrayList< ParserShot > splays = parser.getSplays();
      long id = app_data.insertImportShots( sid, 1, shots ); // start id = 1
      // app_data.insertImportShots( sid, id, splays );
    } catch ( ParserException e ) {
      // TDToast.makeBad( R.string.file_parse_fail );
    }
    return sid;
  }
}
