/*
 * This file is a part of the TUBE42 imagelib, released under the LGPL license.
 *
 * Development page: https://github.com/tube42/imagelib
 * License:          http://www.gnu.org/copyleft/lesser.html
 */

package tube42.lib.imagelib;

/**
 * This class contains some functions
 * that are common during color manipulation
 */
public final class ColorUtils
{
    public static final int blend(final int c1, final int c2, final int value256)
    {
                
        final int v1 = value256 & 0xFF;        
        final int c1_RB = c1 & 0x00FF00FF;
        final int c2_RB = c2 & 0x00FF00FF;
        
        final int c1_AG = (c1 >>> 8) & 0x00FF00FF;
        
        final int c2_AG_org = c2 & 0xFF00FF00;
        final int c2_AG = (c2_AG_org) >>> 8;
        
        // the world-famous tube42 blend with one mult per two components:      
        final int rb = (c2_RB + (((c1_RB - c2_RB) * v1) >> 8)) & 0x00FF00FF;
        final int ag = (c2_AG_org + ((c1_AG - c2_AG) * v1)) & 0xFF00FF00;                
        return ag | rb;
        
    }
    
        
    public static int darker(int c)
    {
        int a = (c >> 24) & 0xFF;
        int r = (c >> 16) & 0xFF;
        int g = (c >>  8) & 0xFF;
        int b = (c >>  0) & 0xFF;
        
        r = (r * 15) >> 4;
        g = (g * 15) >> 4;
        b = (b * 15) >> 4;
        
        return (a << 24) | (r << 16) | (g << 8) | b;        
    }
    
    public static int lighter(int c)
    {
        int a = (c >> 24) & 0xFF;
        int r = (c >> 16) & 0xFF;
        int g = (c >>  8) & 0xFF;
        int b = (c >>  0) & 0xFF;
        
        r = Math.max(1, Math.min(255, (r * 17) >> 4));
        g = Math.max(1, Math.min(255, (g * 17) >> 4));
        b = Math.max(1, Math.min(255, (b * 17) >> 4));
        
        
        return (a << 24) | (r << 16) | (g << 8) | b;        
    }
    
    // ----------------------------------------------
    public static int mix(int c1, int c2)
    {
        // return blend(c1, c2, 0x7f);
        
        int c_RB = (((c1 & 0x00FF00FF) + (c2 & 0x00FF00FF)) >> 1) & 0x00FF00FF;
        int c_AG = (((c1 & 0xFF00FF00) >>> 1) + ((c2 & 0xFF00FF00) >>> 1)) & 0xFF00FF00;
        return c_RB | c_AG;
    }
    
    public static int mix(int c1, int c2, int c3, int c4)
    {
        // return blend(c1, c2, 0x7f);
        
        int c_RB = (
                  ((c1 & 0x00FF00FF) + (c2 & 0x00FF00FF) + (c3 & 0x00FF00FF) + (c4 & 0x00FF00FF)) >> 2
                  ) & 0x00FF00FF;
        
        int c_AG = (
                  ((c1 & 0xFF00FF00) >>> 2) + ((c2 & 0xFF00FF00) >>> 2) + 
                  ((c3 & 0xFF00FF00) >>> 2) + ((c4 & 0xFF00FF00) >>> 2) 
                  ) & 0xFF00FF00;
        return c_RB | c_AG;
    }
    
    // --------------------------------------
    // colorspace conversion functions
    
    /*
     * ARGB_8 to A_8 + YCbCr_8
     * 
     * (CCIR Recommendation 601, normalized to 0-255)
     */
    public final static void ARGB2AYCbCr(int argb, int [] result)
    {
        int a = argb >>> 24;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >>  8) & 0xFF;
        int b =  argb        & 0xFF;
        
        result[0] = a;
        result[1] = (         r *  77 + g * 150 + b *  29) >> 8;
        result[2] = (0x8000 - r *  43 - g *  85 + b * 128) >> 8;
        result[3] = (0x8000 + r * 128 - g * 107 - b *  21) >> 8;
    }
    
    /*
     * A_8 + YCbCr_8 to ARGB_8
     * 
     * (CCIR Recommendation 601, normalized to 0-255)
     * 
     * NOTE: the current implmentation is very inefficient!
     */    
    public final static int AYCbCr2ARGB(int [] components)
    {
        int Y8 = components[1] << 8;
        
        // XXX: I can't figure out why these number don't map to 0-255, hence the min/max for now :(
        int r = Math.max(0, Math.min(255,  (Y8 + (components[3]-128) * 359) >> 8));
        int g = Math.max(0, Math.min(255,  (Y8 - (components[3]-128) * 183 - (components[2]-128) * 88) >> 8));
        int b = Math.max(0, Math.min(255,  (Y8 + (components[2]-128) * 454) >> 8));
        
        return (components[0] << 24) | (r << 16) | (g << 8) | b;
    }
    
    
}
