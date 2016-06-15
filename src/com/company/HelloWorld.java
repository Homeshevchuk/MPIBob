package com.company;

import mpi.Datatype;
import mpi.MPI;
import mpi.Op;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.Arrays;

public class HelloWorld {

    public static void main(String args[]) throws Exception {
        MPI.Init(args);
        int id = MPI.COMM_WORLD.Rank();
        System.out.println("T" + id + " started");
        int N = Config.N;
        int P = Config.P;
        int H = Config.H;
        int[][] MOh = new int[H][N];
        int[][] MK = new int[N][N];
        int[][] MRh = new int[H][N];
        int[][] MM = new int[N][N];
        int[][] MAh = new int[H][N];
        int[][] MA = new int[N][N];
        int[] e = new int[1];
        int[] d = new int[1];
        switch (id) {
            case 0:
                int[][] arr = Operations.createArr(N, N);
                e[0] = 1;
                d[0] = 1;

                MRh = Operations.copyMult(arr, 0, H);
                MK = Operations.createArr(N, N);
                MOh = Operations.copyMult(arr, 0, H);
                MM = Operations.createArr(N, N);

                for (int i = 1; i < 3; i++) {
                    MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, i, 0);
                    MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, i, 0);
                    MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, i, 0);
                    MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, i, 0);
                    MPI.COMM_WORLD.Send(arr, H * i, H, MPI.OBJECT, i, 0);
                    MPI.COMM_WORLD.Send(arr, H * i, H, MPI.OBJECT, i, 0);
                }
                //Send to T4
                MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, 3, 0);
                MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, 3, 0);
                MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, 3, 0);
                MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, 3, 0);
                MPI.COMM_WORLD.Send(arr, H * 3, 4 * H, MPI.OBJECT, 3, 0);
                MPI.COMM_WORLD.Send(arr, H * 3, 4 * H, MPI.OBJECT, 3, 0);

                //Sent to T5
                MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, 4, 0);
                MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, 4, 0);
                MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Send(arr, H * 7, 3 * H, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Send(arr, H * 7, 3 * H, MPI.OBJECT, 4, 0);

                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);

                for(int i = 0;i<H;i++){
                    MA[i] = MAh[i];
                }

                MPI.COMM_WORLD.Recv(MA,H,H,MPI.OBJECT,1,0);
                MPI.COMM_WORLD.Recv(MA,2*H,H,MPI.OBJECT,2,0);
                MPI.COMM_WORLD.Recv(MA,3*H,4*H,MPI.OBJECT,3,0);
                MPI.COMM_WORLD.Recv(MA,7*H,3*H,MPI.OBJECT,4,0);
                Operations.printArr(MA);

                break;
            case 1:
            case 2:
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MOh, 0, H, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MRh, 0, H, MPI.OBJECT, 0, 0);
                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);
                MPI.COMM_WORLD.Send(MAh, 0, MAh.length, MPI.OBJECT, 0, 0);

                break;
            case 3:
                int[][] T4MO4h = new int[4 * H][N];
                int[][] T4MR4h = new int[4 * H][N];
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(T4MO4h, 0, 4 * H, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(T4MR4h, 0, 4 * H, MPI.OBJECT, 0, 0);

                MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, 7, 0);
                MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, 7, 0);
                MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, 7, 0);
                MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, 7, 0);
                MPI.COMM_WORLD.Send(T4MR4h, H, 3 * H, MPI.OBJECT, 7, 0);
                MPI.COMM_WORLD.Send(T4MO4h, H, 3 * H, MPI.OBJECT, 7, 0);

                MOh = Operations.copyMult(T4MO4h, 0, H);
                MRh = Operations.copyMult(T4MR4h, 0, H);
                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);
                int[][] T4MA4h = new int[4*H][N];
                for(int i = 0;i<H;i++){
                    T4MA4h[i] = MAh[i];
                }

                MPI.COMM_WORLD.Recv(T4MA4h,H,4*H,MPI.OBJECT,7,0);
                MPI.COMM_WORLD.Send(T4MA4h,0,4*H,MPI.OBJECT,0,0);
                break;
            case 4:
                int[][] T5MO3h = new int[3 * H][N];
                int[][] T5MR3h = new int[3 * H][N];
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(T5MO3h, 0, 3 * H, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(T5MR3h, 0, 3 * H, MPI.OBJECT, 0, 0);
                //Send to T6

                MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, 5, 0);
                MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, 5, 0);
                MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, 5, 0);
                MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, 5, 0);
                MPI.COMM_WORLD.Send(T5MR3h, H, H, MPI.OBJECT, 5, 0);
                MPI.COMM_WORLD.Send(T5MO3h, H, H, MPI.OBJECT, 5, 0);
                //Send to T7

                MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, 6, 0);
                MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, 6, 0);
                MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, 6, 0);
                MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, 6, 0);
                MPI.COMM_WORLD.Send(T5MR3h, 2 * H, H, MPI.OBJECT, 6, 0);
                MPI.COMM_WORLD.Send(T5MO3h, 2 * H, H, MPI.OBJECT, 6, 0);
                MOh = Operations.copyMult(T5MO3h, 0, H);
                MRh = Operations.copyMult(T5MR3h, 0, H);
                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);

                int[][] T5MA3h = new int[3*H][N];
                for(int i = 0;i<H;i++){
                    T5MA3h[i] = MAh[i];
                }
                MPI.COMM_WORLD.Recv(T5MA3h,H,H,MPI.OBJECT,5,0);
                MPI.COMM_WORLD.Recv(T5MA3h,2*H,3*H,MPI.OBJECT,6,0);
                MPI.COMM_WORLD.Send(T5MA3h,0,3*H,MPI.OBJECT,0,0);
                break;
            case 5:
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 4, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 4, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Recv(MOh, 0, H, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Recv(MRh, 0, H, MPI.OBJECT, 4, 0);
                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);
                MPI.COMM_WORLD.Send(MAh, 0, MAh.length, MPI.OBJECT, 4, 0);
                break;
            case 6:
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 4, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 4, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Recv(MOh, 0, H, MPI.OBJECT, 4, 0);
                MPI.COMM_WORLD.Recv(MRh, 0, H, MPI.OBJECT, 4, 0);

                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);
                MPI.COMM_WORLD.Send(MAh, 0, MAh.length, MPI.OBJECT, 4, 0);
                break;
            case 7:
                int[][] T8MO3h = new int[3 * H][N];
                int[][] T8MR3h = new int[3 * H][N];
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 3, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 3, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 3, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 3, 0);
                MPI.COMM_WORLD.Recv(T8MO3h, 0, 3 * H, MPI.OBJECT, 3, 0);
                MPI.COMM_WORLD.Recv(T8MR3h, 0, 3 * H, MPI.OBJECT, 3, 0);
                //Send to T9

                MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, 8, 0);
                MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, 8, 0);
                MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, 8, 0);
                MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, 8, 0);
                MPI.COMM_WORLD.Send(T8MR3h, H, 2 * H, MPI.OBJECT, 8, 0);
                MPI.COMM_WORLD.Send(T8MO3h, H, 2 * H, MPI.OBJECT, 8, 0);

                MOh = Operations.copyMult(T8MO3h, 0, H);
                MRh = Operations.copyMult(T8MR3h, 0, H);
                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);

                int[][] T8MA3h = new int[3*H][N];
                for(int i = 0;i<H;i++){
                    T8MA3h[i] = MAh[i];
                }

                MPI.COMM_WORLD.Recv(T8MA3h,H,2*H,MPI.OBJECT,8,0);

                MPI.COMM_WORLD.Send(T8MA3h,0,3*H,MPI.OBJECT,3,0);

                break;
            case 8:
                int[][] T9MO2h = new int[2 * H][N];
                int[][] T9MR2h = new int[2 * H][N];
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 7, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 7, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 7, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 7, 0);
                MPI.COMM_WORLD.Recv(T9MO2h, 0, 3 * H, MPI.OBJECT, 7, 0);
                MPI.COMM_WORLD.Recv(T9MR2h, 0, 3 * H, MPI.OBJECT, 7, 0);
                //Send to T9

                MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, 9, 0);
                MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, 9, 0);
                MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, 9, 0);
                MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, 9, 0);
                MPI.COMM_WORLD.Send(T9MR2h, H, H, MPI.OBJECT, 9, 0);
                MPI.COMM_WORLD.Send(T9MO2h, H, H, MPI.OBJECT, 9, 0);

                MOh = Operations.copyMult(T9MO2h, 0, H);
                MRh = Operations.copyMult(T9MR2h, 0, H);
                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);
                int[][] MA2h = new int[2*H][N];
                for(int i = 0;i<H;i++){
                    MA2h[i] = MAh[i];
                }

                MPI.COMM_WORLD.Recv(MA2h,H,H,MPI.OBJECT,9,0);

                MPI.COMM_WORLD.Send(MA2h,0,2*H,MPI.OBJECT,7,0);
                break;
            case 9:
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 8, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 8, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 8, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 8, 0);
                MPI.COMM_WORLD.Recv(MOh, 0, H, MPI.OBJECT, 8, 0);
                MPI.COMM_WORLD.Recv(MRh, 0, H, MPI.OBJECT, 8, 0);
                MAh = Operations.calculateMAh(d, MOh, e, MRh, MM, MK);
                MPI.COMM_WORLD.Send(MAh, 0, MAh.length, MPI.OBJECT, 8, 0);
                break;
        }


        MPI.Finalize();
    }

}
