import java.io.File;

public class Wu4Image {
	private int Height, Width;
	private int[][] RGB;
	private byte[] SourceFile;

	public void setSourceFile(File srcf) {
		// SourceFile经过解压后得到YCbCr的DCT矩阵
		// 假设可以通过解压器得到图片的真实大小并存在Height和Width当中，以及三个矩阵信息(从压缩文件中提取的矩阵应该是带有16对齐的)
		int h_16, w_16;
		int[][] tempY, tempCr, tempCb, Y_DCT, Cr_DCT, Cb_DCT, tempDCT, Res_DCT,tempRGB;
		// 这里缺少了读取矩阵Y_DCT、Cr_DCT、Cb_DCT的步骤,这里用null代替
		Y_DCT = null;
		Cr_DCT = null;
		Cb_DCT = null;
		h_16 = Y_DCT.length;
		w_16 = Y_DCT[0].length;

		tempY = new int[h_16][w_16];
		tempCr = new int[h_16 / 2][w_16 / 2];
		tempCb = new int[h_16 / 2][w_16 / 2];
		Y_DCT = new int[h_16][w_16];
		Cr_DCT = new int[h_16 / 2][w_16 / 2];
		Cb_DCT = new int[h_16 / 2][w_16 / 2];
		tempDCT = new int[8][8];
		int i, j, k, s;
		
		// Y_DCT反变换，tempY保存的是还原后的矩阵
		for (i = 0; i < h_16; i += 8) {
			for (j = 0; j < w_16; j += 8) {
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempDCT[k][s] = Y_DCT[i + k][j + s];
					}
				}
				Res_DCT = DCTConverter.reDCT(tempDCT);
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempY[i + k][j + s] = Res_DCT[k][s];
					}
				}
			}
		}
		//Cb_DCT Cr_DCT反变换到tempCb tempCr
		for (i = 0; i < h_16 / 2; i += 8) {
			for (j = 0; j < w_16 / 2; j += 8) {
				// 处理tempCb->Cb_DCT
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempDCT[k][s] = Cb_DCT[i + k][j + s];
					}
				}
				Res_DCT = DCTConverter.reDCT(tempDCT);
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempCb[i + k][j + s] = Res_DCT[k][s];
					}
				}
				// 处理tempCr->Cr_DCT
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempDCT[k][s] = Cr_DCT[i + k][j + s];
					}
				}
				Res_DCT = DCTConverter.reDCT(tempDCT);
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempCr[i + k][j + s] = Res_DCT[k][s];
					}
				}
			}
		}

		YCbCrConverter YCCC = new YCbCrConverter(tempY,tempCb,tempCb);
		tempRGB=YCCC.getRGB();
		
		//减去对齐时造成的多余的部分
		for(i=0;i<Height;i++) {
			for(j=0;j<Width;j++) {
				RGB[i][j]=tempRGB[i][j];
			}
		}
	}

	public void setRGB(int[][] inputRGB) {
		int h, w, h_16, w_16;
		h = inputRGB.length;
		w = inputRGB[0].length;

		// 对原始图片的大小进行保存，方便生成文件头
		Height = h;
		Width = w;

		// 16-对齐操作
		h_16 = align_16(h);
		w_16 = align_16(w);

		RGB = new int[h][w];
		int[][] tempY, tempCr, tempCb, Y_DCT, Cr_DCT, Cb_DCT, tempDCT, Res_DCT;
		tempY = new int[h_16][w_16];
		tempCr = new int[h_16 / 2][w_16 / 2];
		tempCb = new int[h_16 / 2][w_16 / 2];
		Y_DCT = new int[h_16][w_16];
		Cr_DCT = new int[h_16 / 2][w_16 / 2];
		Cb_DCT = new int[h_16 / 2][w_16 / 2];
		tempDCT = new int[8][8];
		int i, j, k, s;

		// 读入外部的RGB矩阵
		for (i = 0; i < h; i++) {
			for (j = 0; j < w; j++) {
				RGB[i][j] = inputRGB[i][j];
			}
		}
		// RGB矩阵的分解
		YCbCrConverter YCCC = new YCbCrConverter(RGB);
		for (i = 0; i < h; i++) {
			for (j = 0; j < w; j++) {
				tempY[i][j] = YCCC.getY()[i][j];
				tempCb[i][j] = YCCC.getCb()[i][j];
				tempCr[i][j] = YCCC.getCr()[i][j];
			}
		}

		// tempY矩阵每个8*8分块经过DCT变换得到Y_DCT
		for (i = 0; i < h_16; i += 8) {
			for (j = 0; j < w_16; j += 8) {
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempDCT[k][s] = tempY[i + k][j + s];
					}
				}
				Res_DCT = DCTConverter.toDCT(tempDCT);
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						Y_DCT[i + k][j + s] = Res_DCT[k][s];
					}
				}
			}
		}
		// tempCb tempCr矩阵每个8*8分块经过DCT变换得到Cb_DCT Cr_DCT
		for (i = 0; i < h_16 / 2; i += 8) {
			for (j = 0; j < w_16 / 2; j += 8) {
				// 处理tempCb->Cb_DCT
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempDCT[k][s] = tempCb[i + k][j + s];
					}
				}
				Res_DCT = DCTConverter.toDCT(tempDCT);
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						Cb_DCT[i + k][j + s] = Res_DCT[k][s];
					}
				}
				// 处理tempCr->Cr_DCT
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						tempDCT[k][s] = tempCr[i + k][j + s];
					}
				}
				Res_DCT = DCTConverter.toDCT(tempDCT);
				for (k = 0; k < 8; k++) {
					for (s = 0; s < 8; s++) {
						Cr_DCT[i + k][j + s] = Res_DCT[k][s];
					}
				}
			}
		}
		// 通过压缩器得到压缩后的SourceFile
	}

	public byte[] getSourceFile() {
		return SourceFile;
	}

	public int[][] getRGB() {
		return RGB;
	}

	public int align_16(int size) {
		int r;
		if (size % 16 == 0) {
			r = size;
		} else {
			r = size + 16 - size % 16;
		}
		return r;
	}

}
