import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
public class test {
	public static void printMatrix(int[][] input) {
		int h;
		int w;
		int j,i;
		h=input.length;
		w=input[0].length;
		for(i=0;i<h;i++) {
			for(j=0;j<w;j++) {
				System.out.printf("%d ", input[i][j]);
			}
			System.out.printf("\n");
		}
	}
	public static void main(String [] args) {
		BufferedImage img,oimg;
		try {
			int i,j;
            img = ImageIO.read(new File("src\\Image\\test.bmp"));
            int w=img.getWidth();
            int h=img.getHeight();
            int[][] inputRGB=new int[h][w];
            for(i=0;i<h;i++) {
            	for(j=0;j<w;j++) {
            		inputRGB[i][j]=img.getRGB(j, i);
            	}
            }
            YCbCrConverter M=new YCbCrConverter(inputRGB);
            YCbCrConverter M1=new YCbCrConverter(M.getY(),M.getCb(),M.getCr());
            oimg=new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB);
            for(i=0;i<h;i++) {
            	for(j=0;j<w;j++) {
            		oimg.setRGB(j, i, M.getR()[i][j]<<16);
            	}
            }
            File outputfile=new File("src\\Image\\testMR.bmp");
            ImageIO.write(oimg, "bmp", outputfile);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}
}
