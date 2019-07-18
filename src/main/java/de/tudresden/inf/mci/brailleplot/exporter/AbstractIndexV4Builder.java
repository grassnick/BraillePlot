package de.tudresden.inf.mci.brailleplot.exporter;

/**
 * Abstract Class for Documents, that need special Escape Sequences for the Index Everest-V4.
 * All special Documents (i.e. Floating Dot Area) should implement this class. All information taken
 * from the Index PrinterCapability Interface Protocol V5_V4 2ß16-05-13. All the Variables with the respective Values have no
 * particular order (except mStartTemporaryDoc, which must be at the beginning). All the Variable Names are set to
 * final, these are PrinterCapability specific Values that should not be changed.
 * @author Andrey Ruzhanskiy
 * @version 31.05.2019
 */

@SuppressWarnings("checkstyle:MagicNumber")
public class AbstractIndexV4Builder extends AbstractDocumentBuilder {



    /**
     * Standard Byte Sequence to announce StartTemporary Document ([ESC]D).
     * Must be always at the beginning.
     */

    protected final byte[] mStartTemporaryDoc = new byte[] {0x1B, 0x44};

    /**
     * Standard Variable Name for Binding Margin (BI).
     */

    protected final byte[] mBindingMarginName = new byte[] {0x42, 0x49};

    /**
     * Standard Value for Everest-V4 A4 Paper Size(4). Possible Values:
     * 0
     * to
     * 10,
     * whereas the number encodes the #charracters to leave empty on the line to the
     * left and right.
     */

    protected byte[] mBindingMarginValue = new byte[] {0x34};

    /**
     * Standard Variable Name for Characters per Line (CH).
     */

    protected final byte[] mCharsPerLineName = new byte[] {0x43, 0x48};

    /**
     * Standard Value for Everest-V4 A4 Paper Size(30).If used, there must be also
     * a corresponding Binding Margin. Possible Values are:
     * 0: may not be usefull
     * to
     * 48:Absolute maximum due to printer heads total movement and only applicable to the printers
     * that can use papers that big.
     */

    protected byte[] mCharsPerLineValue = new byte[] {0x33, 0x30};

    /**
     * Standard Variable Name for PageMode (Duplex) (DP).
     */

    protected final byte[] mPageModeName = new byte[] {0x44, 0x50};

    /**
     * Standard Value for Page Mode (2, Double sided).
     * Possible Values are:
     * 1 Single sided (normal horizontal printing)
     * 2 Double sided (normal horizontal printing)
     * 3 Double sided z-folding (normal horizontal printing)
     * 5 Single sided z-folding (normal horizontal printing)
     * 6 Double sided sideways z-folding (vertical printing)
     * 7 Single sided sideways z-folding (vertical printing)
     */

    protected byte[] mPageModeValue = new byte[] {0x32};

    /**
     * Standard Variable Name for Braille Table (BT).
     */

    protected final byte[] mBrailleTableName = new byte[] {0x42, 0x54};

    /**
     * Currently, there is no standard for a Braille Table.
     */
    protected byte[] mBrailleTableValue = null;

    /**
     * Standard Variable Name for Driver Version (DV).
     */

    protected final byte[] mDriverVersionName = new byte[] {0x44, 0x56};

    /**
     * Standard Value for Driver Version (not used currently TODO).
     */

    protected byte[] mDriverVersion = null;

    /**
     * Standard Variable Name for First Line Offset (FO).
     */

    protected final byte[] mFirstLineOffsetName = new byte[] {0x46, 0x4F};

    /**
     * Standard Value for First Line Offset (~50/60).
     * Possible Values: equal or greater then 0.
     * The value is specified in tenths of mm.
     *
     */

    protected byte[] mFirstLineOffsetValue = null;

    /**
     * Standard VariableName for Graphic Dot Distance (GD).
     */

    protected final byte[] mGraphicDotDistanceName = new byte[] {0x47, 0x44};

    /**
     * Standard Value for Graphic Dot Distance. Possible Values are 0, 1, 2.
     * 0 = 2.0 mm
     * 1 = 2.5 mm
     * 2 = 1.6 mm
     * Standard is 1 (2.5 mm).
     */

    protected byte[] mGraphicDotDistanceValue = new byte[] {0x31};

    /**
     * Standard VariableName for Lines per Page (LP).
     */

    protected final byte[] mLinesPerPageName = new byte[] {0x4C, 0x50};

    /**
     * Standard Value for Lines per Page (28). If this parameter is set, one must include
     * the TM parameter as well.
     */

    protected byte[] mLinesPerPageValue = new byte[] {0x32, 0x38};

    /**
     * Standard VariableName for Line Spacing (LS).
     */

    protected final byte[] mLineSpacingName = new byte[] {0x4C, 0x53};

    /**
     * Standard Value for Line Spacing (GD). This parameter can only be specified in the beginning of a document
     * and is ignored if a row has been added to the first page. Line spacing is ignored if DP is set to DP4 and DP8.
     * Possible Values are:
     * 50: 5.0 mm (single -normal)
     * to
     * 100: 10.0 mm (double)
     */

    protected byte[] mLineSpacingValue = new byte[] {0x35, 0x30};

    /**
     * Standard VariableName for Multiple Copies (MC).
     */

    protected final byte[] mMultipleCopiesName = new byte[] {0x4D, 0x43};

    /**
     * Standard Value for Multiple Copies (1). Possible Values are:
     * 1
     * to
     * 10000
     */

    protected byte[] mMultipleCopiesValue = new byte[] {0x31};

    /**
     * Standard VariableName for Multiple Impact (MI).
     */

    protected final byte[] mMultipleImpactName = new byte[] {0x4D, 0x49};

    /**
     * Standard Value for Multiple Impact (1).To impact the hammers multiple times
     * for a Braille row this parameter can be specified  Possible Values are:
     * 1
     * to
     * 3.
     */

    protected byte[] mMultipleImpactValue = new byte[] {0x31};

    /**
     * Standard VariableName for Page Number (PN).
     */

    protected final byte[] mPageNumberName = new byte[] {0x50, 0x4E};

    /**
     * Standard Value for Page Number (0). This parameter needs to be specified before the first row is added
     * to the first page. Possible Values are:
     * 0: None
     * 1: Top (require top margin > 0)
     * 2: Top-left (require top margin > 0)
     * 3: Top-right (require top margin > 0)
     * 4: Bottom (require bottom margin > 0)
     * 5: Bottom left (require bottom margin > 0)
     * 6: Bottom right (require bottom margin > 0)
     */

    protected byte[] mPageNumberValue = new byte[] {0x30};

    /**
     * Standard VariableName for Braille Cell Size (TD).
     */

    protected final byte[] mBrailleCellSizeName = new byte[] {0x54, 0x44};

    /**
     * Standard Value for Braille Cell Size (0). Possible Values are:
     * 0: 2.5 mm
     * 1: 2.2 mm
     * 2: 3.2 mm
     */

    protected byte[] mBrailleCellSizeValue = new byte[] {0x30};

    /**
     * Standard VariableName for Top Margin (TM).
     */

    protected final byte[] mTopMarginName = new byte[] {0x54, 0x4D};

    /**
     * Standard Value for Top Margin(not researched). The top margin is
     * always specified in lines. Possible Values are:
     * 0
     * to
     * 10
     */

    protected byte[] mTopMarginValue = null;

    /**
     * Separator for values (,).
     */
    protected final byte[] mComma = new byte[] {0x2C};


    /**
     * Colon Character.
     */
    protected final byte[] mColon = new byte[] {0x3A};



    /**
     * End of Escape Sequence (;). Must be always at the end.
     */

    protected final byte[] mSemicolon = new byte[] {0x3B};
}
