package de.tudresden.inf.mci.brailleplot.rendering;

/**
 * SixDotBrailleRaster. A representation of a raster with regularly spaced groups of two columns and three rows.
 * @author Leonard Kupper
 * @version 2019.06.28
 */
public class SixDotBrailleRaster extends Raster {

    public SixDotBrailleRaster(final int cellsWide, final int cellsHigh, final double verticalDotDistance, final double horizontalDotDistance,
                               final double verticalCellDistance, final double horizontalCellDistance
    ) {
        super(cellsWide, cellsHigh, 3, 2, verticalDotDistance, horizontalDotDistance,
                verticalCellDistance, horizontalCellDistance);
    }
}
