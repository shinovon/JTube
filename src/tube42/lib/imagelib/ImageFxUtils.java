/*
 * This file is a part of the TUBE42 imagelib, released under the LGPL license.
 *
 * Development page: https://github.com/tube42/imagelib
 * License:          http://www.gnu.org/copyleft/lesser.html
 */

package tube42.lib.imagelib;

import javax.microedition.lcdui.*;

/**
 * This class contains some simple image effects
 */

public final class ImageFxUtils
{   
    
    /**
     * my generic filter function
     * 
     * override _one_ of these function to work on a line or a single pixel
     */
    public static class ImageFilter
    {
        public void apply(int [][]pixels, int []output, int offset, int count, int y)
        {
            for(int i = 0; i < count; i++)  
                output[i] = apply(pixels, offset + i, i, y);
        }
        public int apply(int [][]pixels, int offset, int x, int y)
        {
            return 0;
        }
    }
    
    /**
     * my generic pixel modifier.
     * override _one_ of these function to work on a line or a single pixel
     */
    public static class PixelModifier
    {
        public void apply(int [] pixel, int [] output, int count, int y)
        {
            for(int i = 0; i < count; i++)
                output[i] = apply(pixel[i], i, y);
        }
        public int apply(int pixel, int x, int y) 
        { 
            return pixel; 
        }
    }
    
    
    // ---------------------------------------------    
    /**
     * apply filter of size filter_w * filter_h to the image.
     * 
     * This function will process one line at a time
     */
    public static Image applyFilter(Image image, int filter_w, int filter_h, ImageFilter filter)
    {
        // 0. sanity check: filter dimensions must be odd
        if((filter_w & filter_h & 1) == 0) return null; // boolean haxory ftw
        
        int fw2 = filter_w / 2;
        int fh2 = filter_h / 2;
        
        // 1. allocate needed buffers & output image
        final int w = image.getWidth();
        final int h = image.getHeight();
        final int scanlength = w + filter_w;
        
        
        Image ret = Image.createImage(w, h);        
        Graphics g = ret.getGraphics();
        int [] buffer_out = new int[w];
        int [][]buffer_in = new int[filter_h][]; 
        
        for(int i = 0; i < buffer_in.length; i++)     
            buffer_in[i] = new int[scanlength];
        
        int next_line = 0;
        // 2. get initial pixels and empty surroundings
        for(int i = 0; i < filter_h; i++) {
            for(int j = 0; j < scanlength; j++) buffer_in[i][j] = 0;                        
            if(i >= fh2)
                image.getRGB(buffer_in[i], fw2, w, 0, next_line++, w, 1);            
        }
        
        // 3. for each row, compute new line, write it to image and then get the next line
        for(int y = 0; y < h; y++) {       
            filter.apply(buffer_in, buffer_out, fw2, w, y);
            g.drawRGB(buffer_out, 0, w, 0, y, w, 1, true);
                  
            
            // buffer rotation instead of copying
            int [] tmp = buffer_in[0];
            for(int i = 1; i < filter_h; i++) buffer_in[i-1] = buffer_in[i];
            buffer_in[filter_h-1] = tmp;
            
            // and add the next line (if any)
            if(next_line < h) 
                image.getRGB(tmp, fw2, w, 0, next_line++, w, 1); 
            else
                for(int i = 0; i < scanlength; i++) tmp[i] = 0;
        }        
        return ret;
    }
    
    // ---------------------------------------------    
    /**
     * apply 2D filter
     * 
     * NOTE: this function is currently quite slow!
     * 
     */
    public static Image applyFilter(Image image, final int [][] multiplier, final int divider)
    {
        
        final int w = multiplier[0].length;
        final int h = multiplier.length;
                
        return applyFilter(image, w, h, new ImageFxUtils.ImageFilter() {              
                  public void apply(int [][]pixels, int [] output, int offset,  int count, int y_) {                         
                  for(int i = 0; i < count; i++) {                      
                  int sum_a = 0, sum_r = 0, sum_g = 0, sum_b = 0;                      
                  
                  // TODO 1: speed up computation by using the SIMD trick here
                  // TODO 2: speed up computation by extracting each point only once?
                  for(int y = 0; y < h; y++) {
                  final int [] line = pixels[y];
                  final int [] mul = multiplier[y];
                  for(int x = 0; x < w; x++) {
                  sum_a +=  mul[x] * ((line[x + i] >> 24) & 0xFF);
                  sum_r +=  mul[x] * ((line[x + i] >> 16) & 0xFF);
                  sum_g +=  mul[x] * ((line[x + i] >>  8) & 0xFF);
                  sum_b +=  mul[x] * ((line[x + i] >>  0) & 0xFF);
              }
              }
                  output[i] = 
                  Math.min(255, Math.max(0, sum_a / divider)) << 24 |
                  Math.min(255, Math.max(0, sum_r / divider)) << 16 |
                  Math.min(255, Math.max(0, sum_g / divider)) << 8 |
                  Math.min(255, Math.max(0, sum_b / divider));
              }
              }
              });
    }    
    
    /**
     * apply pixel modifier to the image.
     * This function also demonstrates use of in-place image modification
     * which consumes less memory.
     *      
     */
    public static Image applyModifier(Image image, PixelModifier modifier)
    {
        return applyModifier(image, modifier, false);
    }
    public static Image applyModifier(Image image, PixelModifier modifier, 
                  boolean modify_original)
    {
        // 1. allocate needed buffers        
        final int w = image.getWidth();
        final int h = image.getHeight();
        int [] buffer1 = new int[w];
        int [] buffer2 = new int[w];
        
        // 2. get image for in-place modification or create new
        Image ret = modify_original ? image : Image.createImage(w, h);
        Graphics g = ret.getGraphics();
        
        // 3. for each row, get the pixels
        for(int y = 0; y < h; y++) {
            image.getRGB(buffer1, 0, w, 0, y, w, 1);            
            modifier.apply( buffer1, buffer2, w, y);
            g.drawRGB(buffer2, 0, w, 0, y, w, 1, true);            
        }
        
        return ret;
    }
    
    // --------------------------------------
    
    /**
     * transform the components in each pixel according to the 
     * transfrom tables.
     * 
     * If a table is null, the original value will be used
     */
    public static Image transformARGB(Image image, byte [] ta, byte [] tr, byte [] tg, byte [] tb)
    {
        // 1. get blended image:
        int w = image.getWidth();
        int h = image.getHeight();
        
        int [] buffer = new int[w * h];
        
        image.getRGB(buffer, 0, w, 0, 0, w, h);
        image = null; // not needed anymore
        
        int [] tmp = new int[256];
        // apply A, R, G and B tables when available
        
        if(ta != null) {
            for(int i = 0; i < 256; i++) 
                tmp[i] = (((int)ta[i]) & 0xFF) << 24;
            
            for(int i = 0; i < buffer.length; i++)                 
                buffer[i] = (buffer[i] & 0x00FFFFFF) | tmp[(buffer[i] >> 24) & 0xFF ];
        }
        
        if(tr != null) {
            for(int i = 0; i < 256; i++) 
                tmp[i] = (((int)tr[i]) & 0xFF) << 16;
            
            for(int i = 0; i < buffer.length; i++)                 
                buffer[i] = (buffer[i] & 0xFF00FFFF) | tmp[(buffer[i] >> 16) & 0xFF ];
        }
        
        if(tg != null) {
            for(int i = 0; i < 256; i++) 
                tmp[i] = (((int)tg[i]) & 0xFF) << 8;
            
            for(int i = 0; i < buffer.length; i++)                 
                buffer[i] = (buffer[i] & 0xFFFF00FF) | tmp[(buffer[i] >> 8) & 0xFF ];
        }
        if(tb != null) {
            for(int i = 0; i < 256; i++) 
                tmp[i] = (((int)tb[i]) & 0xFF) << 0;
            
            for(int i = 0; i < buffer.length; i++)                 
                buffer[i] = (buffer[i] & 0xFFFFFF00) | tmp[(buffer[i] >> 0) & 0xFF ];
        }
        
        return Image.createRGBImage(buffer, w, h, true);
    }
    
    /*
     * Image transform colors by adding a delta to the linear mapping
     */
    public static Image transformARGB(Image image, int delta_alpha, int delta_red, int delta_green, int delta_blue)
    {
        // build transformation tables
        byte [] ta = new byte[256];
        byte [] tr = new byte[256];
        byte [] tg = new byte[256];
        byte [] tb = new byte[256];
        for(int i = 0; i < 256; i++) {
            ta[i] = (byte) Math.min(255, Math.max(0, i + delta_alpha));
            tr[i] = (byte) Math.min(255, Math.max(0, i + delta_red));
            tg[i] = (byte) Math.min(255, Math.max(0, i + delta_green));
            tb[i] = (byte) Math.min(255, Math.max(0, i + delta_blue));
        }
        // now, transform it according to the tables
        return transformARGB(image, ta, tr, tg, tb);
    }

    
}
