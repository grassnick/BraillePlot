package de.tudresden.inf.mci.brailleplot.rendering;

import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;

import java.awt.image.BufferedImage;

import static java.lang.Math.*;

/**
 * A rasterizer that is able to re-raster a raster graphics onto a canvas.
 */
public class ImageRasterizer implements Rasterizer<Image> {

    // This Rasterizer is a basic example on how to create a rasterizer.
    // Each rasterizer is the implementation of the Rasterizer interface. To create a rasterizer, implement the
    // Rasterizer interface by overriding the rasterize method. Since Rasterizer is a functional interface,
    // it is technically also possible to implement a rasterizer as a method independent from a class, as long
    // as it takes a renderable and a canvas as parameters.

    // Switches
    private boolean mPreventOverStretch;
    private boolean mPreserveAspectRatio;
    private boolean mQuantifiedPositions;

    // Output threshold: A dot will be set for gray scale values below (darker than) the threshold.
    private int mLowThreshold;

    public ImageRasterizer() {
        mPreventOverStretch = true;
        mPreserveAspectRatio = true;
        mQuantifiedPositions = true;
        mLowThreshold = 80;
    }

    public ImageRasterizer(
            final boolean preventOverStretch,
            final boolean preserveAspectRatio,
            final boolean useQuantifiedPositions,
            int threshold) {
        mPreventOverStretch = preventOverStretch;
        mPreserveAspectRatio = preserveAspectRatio;
        mQuantifiedPositions = useQuantifiedPositions;
        mLowThreshold = threshold;
    }

    @Override
    public void rasterize(Image imgData, AbstractRasterCanvas canvas) throws InsufficientRenderingAreaException {

        // Each rasterizer essentially works by taking an instance of a Renderable (in this case Image) and then
        // creating a graphical representation of the object on the raster canvas.

        // In this example a raster image will be rasterized onto the canvas by two selectable methods:
        // - linear mapping
        // - quantified positions

        // Both methods are similar, since the given image is already represented on a raster, which means that the
        // basic question is just how to map from one raster to the other. This is were the methods differ.
        // Another question is when to set a dot, which in this example is done by a simple threshold for
        // the grey scale value of each pixel.
        // A more sophisticated implementation could utilize an edge finding algorithm.

        // First, a readable representation of the is retrieved.
        BufferedImage imgBuf = imgData.getBufferedImage();
        System.out.println(imgBuf.getWidth() + "x" + imgBuf.getHeight());

        // Then the selected method is applied.
        // Implementing the rasterizer as class comes in handy. Subtasks can be splitted into help
        // methods and different modular rasterizers can be reused by calling them inside the rasterize method.
        if (mQuantifiedPositions) {
            quantifiedPositionMapping(imgBuf, canvas);
        } else {
            linearMapping(imgBuf, canvas);
        }


        /*
        original: 431 x 251
        raster: 90 x 87

        hRatio = 430 / 90 = 4.78
        vRatio = 250 / 87 = 2.87

        raster[0,0]:
        x1,y1 ..........
        |               |
        |               |
        |.......... x2,y2
        x2 = -1 // because we set next x1 to be x2's neighbor (avoid double scan).
        scanX = 0
        x1 = x2 + 1 -> 0
        scanX += hRatio -> 4.78
        x2 = floor(scanX) -> 4

        raster[1,0]:
        x1 -> 5
        scanX += hRatio -> 9.56
        x2 -> 9

        raster[2,0]:
        x1 -> 10
        scanX += hRatio -> 14.34
        x2 -> 14

        raster[3,0]:
        x1 -> 15
        scanX += hRatio -> 19.12
        x2 -> 19

        raster[4,0]:
        x1 -> 20
        scanX += hRatio -> 23.9 (hRatio * x)
        x2 -> 23

        raster[90,0]:
        x1 -> 426
        scanX += hRatio -> 430
        x2 -> 430




        original: 50 x 50
        raster: 90 x 87

        hRatio = 49 / 90 = 0.54444...
        vRatio = 49 / 87 = 0.56321...

        x2 = -1

        x1 = 0
        scanX = 0.544444
        x2 = 0

        x1 = 1
        scanX = 1.0888888
        x2 = 1

        x1 = 2
        scanX = 1.6333333
        x2 =

         */

        /*

        // NON FIXED RATIO - PARTIAL SCAN STEPS

        double scanX = 0;
        for (int outX = 0; outX < availableArea.getWidth(); outX++) {
            int x1 = (int) round(scanX);
            //int x2 = (int) min(round(scanX += scanStepSize), imgBuf.getWidth() - 1); // 4 * 100 = 400
            int x2 = (int) min(round(scanX += hRatio), imgBuf.getWidth() - 1);
            double scanY = 0;
            for (int outY = 0; outY < availableArea.getHeight(); outY++) {
                int y1 = (int) round(scanY);
                //int y2 = (int) min(round(scanY += scanStepSize), imgBuf.getHeight() - 1); // 4 * 100 = 400
                int y2 = (int) min(round(scanY += vRatio), imgBuf.getHeight() - 1);
                System.out.println(x1 + "," + y1 + " " + x2 + "," + y2);
                int value = minFilter(imgBuf, x1, y1, x2, y2);
                data.setValue(outY, outX, value <= mLowThreshold);
                if (scanY >= (imgBuf.getHeight() - 1)) {
                    break;
                }
            }
            if (scanX >= (imgBuf.getWidth() - 1)) {
                break;
            }
        }

         */

        /*

        // FIXED RATIO - FULL SCAN STEPS

        int inX = 0;
        for (int outX = 0; outX < floor(imgBuf.getWidth() / scanStepSize); outX++) {
            int inY = 0;
            for (int outY = 0; outY < floor(imgBuf.getHeight() / scanStepSize); outY++) {
                boolean setDot = false;
                for (int offsetX = 0; offsetX < scanStepSize; offsetX++) {
                    for (int offsetY = 0; offsetY < scanStepSize; offsetY++) {
                        int readX = min(inX + offsetX, imgBuf.getWidth() - 1);
                        int readY = min(inY + offsetY, imgBuf.getHeight() - 1);
                        //System.out.println(readX + "," + readY);
                        int grey = toGrayScaleValue(imgBuf.getRGB(readX, readY));
                        if (grey <= mLowThreshold) {
                            setDot = true;
                        }
                    }
                }
                inY += scanStepSize;

                data.setValue(outY, outX, setDot);

            }
            inX += scanStepSize;
        }
         */

        //availableArea.getWidth();
    }

    private void linearMapping(BufferedImage imgBuf, AbstractRasterCanvas canvas) {

        // A canvas is basically a wrapper for multiple representations of printable data, each representing a page.
        // These representations can be acquired by either requesting the current page or creating a new page.
        MatrixData<Boolean> data = canvas.getNewPage();

        // The raster canvas delivers meta information about the underlying format and raster geometry.
        // A vital information are the cell- and dot-rectangle of the canvas. Every rasterizer should take care of at
        // least these rectangles which represent the portion of the raster that the rasterizer is expected to work on.
        Rectangle availableArea = canvas.getDotRectangle();
        // A rasterizer trying to set values outside of the dot rectangle will cause an exception.
        // Other information about the grid (spacing of cells and dots, ...) is available but must not always be
        // regarded, depending on the use case.

        // Calculate the ratios between original image and target raster. (resolution 'shrink' factor)
        double hRatio =  (availableArea.getWidth() - 1) / imgBuf.getWidth();
        double vRatio = (availableArea.getHeight() - 1) / imgBuf.getHeight();

        if(mPreventOverStretch) {
            // In case that the given images resolution is smaller than the grid on at least one dimension
            // this prevents it to be 'stretched' on the output, leading to 'cuts' in former solid lines.
            // The maximum ratio is 1 for the linear mapping, meaning that the pixel position would be the
            // exact dot position in the output.
            // A ratio smaller than 1 would mean that some pixels will be mapped to the same dot to fit the output,
            // but without any regard to the spacing between the single dots.
            hRatio = min(hRatio, 1);
            vRatio = min(hRatio, 1);
        }
        if(mPreserveAspectRatio) {
            // This selects the smaller of both ratios for both dimensions to keep aspect ratio the same in the output.
            hRatio = min(hRatio, vRatio);
            vRatio = min(vRatio, vRatio);
        }

        // Linear Mapping: The pixel position of the original image is linearly mapped to the dot position.
        // This can lead to distortions because the original pixel raster is equidistant, but the output raster
        // does not have to be equidistant.

        // Scan through each pixel of the original image
        for (int x = 0; x < imgBuf.getWidth(); x++) {
            // Convert from original pixel x-position to braille dot x-position.
            // Linear mapping: The conversion happens disregarding the grid spacing (dot and cell distances)
            int column = (int) round(hRatio * (x + 1));
            for (int y = 0; y < imgBuf.getHeight(); y++) {
                // Convert from original pixel y-position to braille dot x-position.
                int row = (int) round(vRatio * (y + 1));
                // Calculate gray scale value and compare against threshold.
                int value = toGrayScaleValue(imgBuf.getRGB(x, y));
                if (value <= mLowThreshold) {
                    data.setValue(row, column, true);
                }
            }
        }
    }

    private void quantifiedPositionMapping(BufferedImage imgBuf, AbstractRasterCanvas canvas) {

        MatrixData<Boolean> data = canvas.getNewPage();

        // Instead of using the dot rectangle a rectangle representing the target printing space in millimeters
        // is built from the canvas information.
        Rectangle availableArea = new Rectangle(0, 0, canvas.getPrintableWidth(), canvas.getPrintableHeight());

        // Calculate the ratios between original image and target printable area. (mm / pixel)
        double hRatio =  (availableArea.getWidth() / imgBuf.getWidth());
        double vRatio = (availableArea.getHeight() / imgBuf.getHeight());

        if(mPreventOverStretch) {
            // Here, the maximum ratio is not 1 as in the linear mapping but instead equal to the regarding dot
            // distances. This is because the ratios are not measured in dots/pixel but mm/pixel.
            hRatio = min(hRatio, canvas.getHorizontalDotDistance());
            vRatio = min(vRatio, canvas.getVerticalDotDistance());
        }
        if(mPreserveAspectRatio) {
            hRatio = min(hRatio, vRatio);
            vRatio = min(hRatio, vRatio);
        }

        System.out.println("Available area: " + availableArea.getWidth() + "mm x " + availableArea.getHeight() + "mm");

        // Quantified Positions: The pixel position of the original image is linearly mapped to the respective
        // millimeter position on the printed area of the page. This step preserves the original distance ratios.
        // In a second step, the calculated exact position is quantified to fit a dot position on the raster.
        // Distortions can still be introduced but are minimized.

        // Scan through all pixels of the original image
        for (int x = 0; x < imgBuf.getWidth(); x++) {
            // Convert from original pixel x-position to printed dot x-position in millimeters.
            // In contrast to the linear mapping, this will try to preserve the original distance ratios.
            double columnMM = hRatio * (x + 1);
            for (int y = 0; y < imgBuf.getHeight(); y++) {
                // Convert from original pixel y-position to printed dot y-position in millimeters.
                double rowMM = vRatio * (y + 1);
                // Calculate gray scale value and compare against threshold.
                int value = toGrayScaleValue(imgBuf.getRGB(x, y));
                if (value <= mLowThreshold) {
                    // The target dot position in millimeters has to be quantified regarding the raster.
                    int row = canvas.quantifyY(rowMM);
                    int column = canvas.quantifyX(columnMM);
                    data.setValue(row, column, true);
                }
            }
        }
    }

    /*
    private int minFilter(BufferedImage imgBuf, int x1, int y1, int x2, int y2) {
        int min = 255;
        for (int x = min(x1, x2); x <= max(x1, x2); x++) {
            for (int y = min(y1, y2); y <= max(y1, y2); y++) {
                min = min(min, toGrayScaleValue(imgBuf.getRGB(x,y)));
            }
        }
        return min;
    }
     */

    private int toGrayScaleValue(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        return ((r+g+b) / 3);
    }
}
