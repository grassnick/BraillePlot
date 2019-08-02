package de.tudresden.inf.mci.brailleplot.printerbackend;

/**
 * Abstract class for Documents, that need special escape sequences for the Index Everest-V4.
 * All special documents (i.e. Floating Dot Area) should implement this class. All information taken
 * from the Index PrinterCapability Interface Protocol V5_V4 2ß16-05-13. All the variables with the respective values have no
 * particular order (except mStartTemporaryDoc, which must be at the beginning). All the variable names are set to
 * final, these are PrinterCapability specific values that should not be changed.
 * @author Andrey Ruzhanskiy
 * @version 31.05.2019
 */

@SuppressWarnings("checkstyle:MagicNumber")
public abstract class AbstractIndexV4Builder extends AbstractDocumentBuilder {



    /**
     * Standard byte Sequence to announce StartTemporary document ([ESC]D).
     * Must be always at the beginning.
     */

    final byte[] mStartTemporaryDoc = new byte[] {0x1B, 0x44};

    /**
     * Standard variable name for binding margin (BI).
     */

    final byte[] mBindingMarginName = new byte[] {0x42, 0x49};

    /**
     * Standard value for Everest-V4 A4 Paper Size(4). Possible Values:
     * 0
     * to
     * 10,
     * whereas the number encodes the #charracters to leave empty on the line to the
     * left and right.
     */

    byte[] mBindingMarginValue = new byte[] {0x34};

    /**
     * Standard variable name for characters per line (CH).
     */

    final byte[] mCharsPerLineName = new byte[] {0x43, 0x48};

    /**
     * Standard value for Everest-V4 A4 Paper Size(30).If used, there must be also
     * a corresponding binding margin. Possible values are:
     * 0: may not be usefull
     * to
     * 48:Absolute maximum due to printer heads total movement and only applicable to the printers
     * that can use papers that big.
     */

    byte[] mCharsPerLineValue = new byte[] {0x33, 0x30};

    /**
     * Standard variable name for PageMode (Duplex) (DP).
     */

    final byte[] mPageModeName = new byte[] {0x44, 0x50};

    /**
     * Standard value for page mode (2, double sided).
     * Possible values are:
     * 1 Single sided (normal horizontal printing)
     * 2 Double sided (normal horizontal printing)
     * 3 Double sided z-folding (normal horizontal printing)
     * 5 Single sided z-folding (normal horizontal printing)
     * 6 Double sided sideways z-folding (vertical printing)
     * 7 Single sided sideways z-folding (vertical printing)
     */

    byte[] mPageModeValue = new byte[] {0x32};

    /**
     * Standard variable name for braille table (BT).
     */

    final byte[] mBrailleTableName = new byte[] {0x42, 0x54};

    /**
     * Currently, there is no standard braille table.
     */
    byte[] mBrailleTableValue = null;

    /**
     * Standard variable name for Driver Version (DV).
     */

    final byte[] mDriverVersionName = new byte[] {0x44, 0x56};

    /**
     * Standard value for driver version (not used currently TODO).
     */

    byte[] mDriverVersion = null;

    /**
     * Standard variable name for first line offset (FO).
     */

    final byte[] mFirstLineOffsetName = new byte[] {0x46, 0x4F};

    /**
     * Standard value for first line offset (~50/60).
     * Possible values: equal or greater then 0.
     * The value is specified in tenths of mm.
     *
     */

    byte[] mFirstLineOffsetValue = null;

    /**
     * Standard variable name for graphic dot distance (GD).
     */

    final byte[] mGraphicDotDistanceName = new byte[] {0x47, 0x44};

    /**
     * Standard value for graphic dot distance. Possible values are 0, 1, 2.
     * 0 = 2.0 mm
     * 1 = 2.5 mm
     * 2 = 1.6 mm
     * Standard is 1 (2.5 mm).
     */

    byte[] mGraphicDotDistanceValue = new byte[] {0x31};

    /**
     * Standard variable name for lines per page (LP).
     */

    final byte[] mLinesPerPageName = new byte[] {0x4C, 0x50};

    /**
     * Standard value for lines per page (28). If this parameter is set, one must include
     * the TM parameter as well.
     */

    byte[] mLinesPerPageValue = new byte[] {0x32, 0x38};

    /**
     * Standard variable name for line spacing (LS).
     */

    final byte[] mLineSpacingName = new byte[] {0x4C, 0x53};

    /**
     * Standard value for line spacing (GD). This parameter can only be specified in the beginning of a document
     * and is ignored if a row has been added to the first page. Line spacing is ignored if DP is set to DP4 and DP8.
     * Possible values are:
     * 50: 5.0 mm (single -normal)
     * to
     * 100: 10.0 mm (double)
     */

    byte[] mLineSpacingValue = new byte[] {0x35, 0x30};

    /**
     * Standard variable name for multiple copies (MC).
     */

    final byte[] mMultipleCopiesName = new byte[] {0x4D, 0x43};

    /**
     * Standard value for multiple copies (1). Possible values are:
     * 1
     * to
     * 10000
     */

    byte[] mMultipleCopiesValue = new byte[] {0x31};

    /**
     * Standard variable name for multiple impact (MI).
     */

    final byte[] mMultipleImpactName = new byte[] {0x4D, 0x49};

    /**
     * Standard value for multiple impact (1).To impact the hammers multiple times
     * for a braille row this parameter can be specified.  Possible values are:
     * 1
     * to
     * 3.
     */

    byte[] mMultipleImpactValue = new byte[] {0x31};

    /**
     * Standard variable name for page number (PN).
     */

    final byte[] mPageNumberName = new byte[] {0x50, 0x4E};

    /**
     * Standard value for page number (0). This parameter needs to be specified before the first row is added
     * to the first page. Possible values are:
     * 0: None
     * 1: Top (require top margin greater than 0)
     * 2: Top-left (require top margin greater than 0)
     * 3: Top-right (require top margin greater than 0)
     * 4: Bottom (require bottom margin greater than 0)
     * 5: Bottom left (require bottom margin greater than 0)
     * 6: Bottom right (require bottom margin greater than 0)
     */

    byte[] mPageNumberValue = new byte[] {0x30};

    /**
     * Standard variable name for braille cell size (TD).
     */

    final byte[] mBrailleCellSizeName = new byte[] {0x54, 0x44};

    /**
     * Standard value for braille cell size (0). Possible values are:
     * 0: 2.5 mm
     * 1: 2.2 mm
     * 2: 3.2 mm
     */

    byte[] mBrailleCellSizeValue = new byte[] {0x30};

    /**
     * Standard variable name for top margin (TM).
     */

    final byte[] mTopMarginName = new byte[] {0x54, 0x4D};

    /**
     * Standard value for top margin(not researched). The top margin is
     * always specified in lines. Possible values are:
     * 0
     * to
     * 10
     */

    byte[] mTopMarginValue = null;

    /**
     * Separator for values (,).
     */
    final byte[] mComma = new byte[] {0x2C};


    /**
     * Colon character.
     */
    final byte[] mColon = new byte[] {0x3A};



    /**
     * End of escape sequence (;). Must be always at the end.
     */

    final byte[] mSemicolon = new byte[] {0x3B};
}
