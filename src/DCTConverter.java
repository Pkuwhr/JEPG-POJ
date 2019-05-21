public class DCTConverter {

    final static double PI = 3.14159265;
    final static double MS = 0.70710678;

    public static int[][] toDCT(int[][] org) {
        int dct[][] = new int[8][8];
        for (int u = 0; u < 8; u++) {
            for (int v = 0; v < 8; v++) {
                double sum = 0;
                for (int x = 0; x < 8; x++)
                    for (int y = 0; y < 8; y++)
                        sum += (org[x][y] - 128) * cos((2 * x + 1) * u * PI / 16)
                                * cos((2 * y + 1) * v * PI / 16);
                dct[u][v] = (int) (sum * C(u) * C(v) / 4);
            }
        }
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                dct[i][j] /= kernel(i, j);
        return dct;
    }

    public static int[][] reDCT(int[][] dct) {
        int rec[][] = new int[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                dct[i][j] *= kernel(i, j);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                double sum = 0;
                for (int u = 0; u < 8; u++)
                    for (int v = 0; v < 8; v++)
                        sum += C(u) * C(v) * dct[u][v]
                                * cos((2 * x + 1) * u * PI / 16)
                                * cos((2 * y + 1) * v * PI / 16);
                rec[x][y] = (int) (sum / 4) + 128;
                if (rec[x][y] > 255)
                    rec[x][y] = 255;
                if (rec[x][y] < 0)
                    rec[x][y] = 0;
            }
        }
        return rec;
    }

    public static double cos(double a) {
        return Math.cos(a);
    }

    public static double C(int c) {
        return c > 0 ? 1.0 : 0.70710678;
    }

    public static int kernel(int i, int j) {
        int ker[] = new int[]{
            1, 1, 2, 2, 16, 32, 64, 64,
            1, 1, 2, 2, 16, 32, 64, 64,
            2, 2, 2, 2, 16, 32, 64, 64,
            2, 2, 2, 2, 16, 32, 64, 64,
            16, 16, 16, 16, 16, 32, 64, 64,
            32, 32, 32, 32, 32, 32, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64
        };
        return ker[i * 8 + j];
    }
}
