/*
 * This file is a part of the TUBE42 imagelib, released under the LGPL license.
 *
 * Development page: https://github.com/tube42/imagelib
 * License:          http://www.gnu.org/copyleft/lesser.html
 */

package tube42.lib.imagelib;



import javax.microedition.lcdui.*;


/**
 * This class contains some functions
 * for image analysis
 */

public final class ImageAnalysisUtils
{   
    
    /**
     * Create a histogram over the ARGB component distribution.
     * 
     * Note that this function returns pixel counters, so
     * the accumulative total for each component is w * h, not 1.0
     */
    public static int [][] getARGBHistogram(Image image)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        
        return getARGBHistogram(image, 0, 0, w, h);
    }
    
    /**
     * Create a histogram over the ARGB component distribution
     * of selected parts in an image.
     *      
     * Note that this function returns pixel counters, so
     * the accumulative total for each component is w * h, not 1.0
     */
    public static int [][] getARGBHistogram(Image image, int x, int y, int w, int h)
    {
        // 1. create initial empty histogram
        int [][]ret = new int[4][];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = new int[256];
            for(int j = 0; j < 256; j++) ret[i][j] = 0;
        }
        
        // 2. build histogram
        int [] buffer = new int[w];
        for(int i = 0; i < h; i++) {
            image.getRGB(buffer, 0, w, 0, i, w, 1);
            
            for(int j = 0; j < w; j++) {
                int c = buffer[j];
                
                ret[0][(c >> 24) & 0xFF]++;
                ret[1][(c >> 16) & 0xFF]++;
                ret[2][(c >>  8) & 0xFF]++;
                ret[3][(c >>  0) & 0xFF]++;
            }
        }
        
        // 3. and we are done
        return ret;
    }
}
