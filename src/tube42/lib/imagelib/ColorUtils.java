/*
 * This file is a part of the TUBE42 imagelib, released under the LGPL license.
 *
 * Development page: https://github.com/tube42/imagelib
 * License:          http://www.gnu.org/copyleft/lesser.html
 */

package tube42.lib.imagelib;

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
    
}
