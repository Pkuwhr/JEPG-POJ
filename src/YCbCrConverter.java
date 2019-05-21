
public class YCbCrConverter {
	private int h,w;//Y MR MG MB RGB 的尺寸信息 [0~h-1][0~w-1]
	private int [][] Y;
	private int [][] Cb,Cr;//尺寸是Y的一半，即 h/2 w/2
	private int [][] MR,MG,MB;//RGB分解的矩阵
	private int [][] RGB;
	
	//三个构造函数，无参构造函数、以RGB为参数的构造函数和以YCbCr为参数的构造函数
	YCbCrConverter(){};
	YCbCrConverter(int[][] inputRGB){
		setRGB(inputRGB);
	}
	YCbCrConverter(int[][] inputY,int[][] inputCb,int[][] inputCr){
		setYCbCr(inputY,inputCb,inputCr);
	}
	
	//读入RGB矩阵 自动生成分解的各种信息 完成颜色空间转换
	void setRGB(int[][] inputRGB) {
		//获取读入的矩阵边长信息
		h=inputRGB.length;
		w=inputRGB[0].length;
		MR=new int[h][w];
		MG=new int[h][w];
		MB=new int[h][w];
		RGB=new int[h][w];
		Y=new int[h][w];
		Cb=new int[h/2][w/2];
		Cr=new int[h/2][w/2];
		int[][] tempCb=new int[h][w];
		int[][] tempCr=new int[h][w];
		int mask_r=0x00ff0000;
		int mask_g=0x0000ff00;
		int mask_b=0x0000000ff;
		//将读入的矩阵信息转换成三个矩阵
		int i,j;
		for(i=0;i<h;i++) {
			for(j=0;j<w;j++) {
				MR[i][j]=((mask_r&inputRGB[i][j])>>16);
				MG[i][j]=((mask_g&inputRGB[i][j])>>8);
				MB[i][j]=(mask_b&inputRGB[i][j]);
				RGB[i][j]=inputRGB[i][j];
				Y[i][j]=(int)(0.299*MR[i][j]+0.587*MG[i][j]+0.144*MB[i][j]);
				tempCb[i][j]=(int)(-0.1687*MR[i][j]-0.3313*MG[i][j]+0.5*MB[i][j]);
				tempCr[i][j]=(int)(0.5*MR[i][j]-0.4187*MG[i][j]-0.0813*MB[i][j]);
			}
		}
		
		for(i=0;i<h;i+=2) {
			for(j=0;j<w;j+=2) {
				Cb[i/2][j/2]=(tempCb[i][j]+tempCb[i+1][j]+tempCb[i][j+1]+tempCb[i+1][j+1])/4;
				Cr[i/2][j/2]=(tempCr[i][j]+tempCr[i+1][j]+tempCr[i][j+1]+tempCr[i+1][j+1])/4;
			}
		}
	}
	//读入YCbCr 生成其他成员变量信息 即完成颜色空间逆转换
	void setYCbCr(int[][] inputY,int[][] inputCb,int[][] inputCr) {
		h=inputY.length;
		w=inputY[0].length;
		MR=new int[h][w];
		MG=new int[h][w];
		MB=new int[h][w];
		RGB=new int[h][w];
		Y=new int[h][w];
		Cb=new int[h/2][w/2];
		Cr=new int[h/2][w/2];
		int[][] tempCb=new int[h][w];
		int[][] tempCr=new int[h][w];
		int i,j;
		
		for(i=0;i<h/2;i++) {
			for(j=0;j<w/2;j++) {
				Cb[i][j]=inputCb[i][j];
				Cr[i][j]=inputCr[i][j];
			}
		}
		
		for(i=0;i<h;i++) {
			for(j=0;j<w;j++) {
				Y[i][j]=inputY[i][j];
				tempCb[i][j]=Cb[i/2][j/2];
				tempCr[i][j]=Cr[i/2][j/2];
				MR[i][j]=Y[i][j] + (int)(1.402*tempCr[i][j]);
				MG[i][j]=Y[i][j] - (int)(0.714*tempCr[i][j]+0.344*tempCb[i][j]);
				MB[i][j]=Y[i][j] + (int)(1.772*tempCb[i][j]);
				RGB[i][j]=(MR[i][j]<<16)+(MG[i][j]<<8)+MB[i][j];
			}
		}
	}
	
	//用下列方法得到成员变量信息
	
	int getHeight() {
		return h;
	}
	
	int getWidth() {
		return w;
	}
	
	int[][] getY(){
		return Y;
	}
	
	int[][] getCb(){
		return Cb;
	}
	
	int[][] getCr(){
		return Cr;
	}
	
	int[][] getR(){
		return MR;
	}
	
	int[][] getG(){
		return MG;
	}
	
	int[][] getB(){
		return MB;
	}
	
	int[][] getRGB(){
		return RGB;
	}

}
