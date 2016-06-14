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
                for (int i = 1; i < 5; i++) {
                    MPI.COMM_WORLD.Send(d, 0, 1, MPI.INT, i, 0);
                    MPI.COMM_WORLD.Send(e, 0, 1, MPI.INT, i, 0);
                    MPI.COMM_WORLD.Send(MK, 0, MK.length, MPI.OBJECT, i, 0);
                    MPI.COMM_WORLD.Send(MM, 0, MM.length, MPI.OBJECT, i, 0);
                    MPI.COMM_WORLD.Send(arr, H * i, H, MPI.OBJECT, i, 0);
                    MPI.COMM_WORLD.Send(arr, H * i, H, MPI.OBJECT, i, 0);
                }
                for (int i = 0; i < H; i++) {
                    for(int j = 0;j<N;j++){
                        MAh[i][j] = (d[0]*MOh[i][j])+(e[0]*(Operations.multiplyMatr(Operations.multiplyMatr(MRh, MM), MK))[i][j]);
                    }
                }
                System.out.println(Arrays.deepToString(MAh));

                break;
            case 1:
            case 2:
            case 3:
            case 4:
                MPI.COMM_WORLD.Recv(d, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(e, 0, 1, MPI.INT, 0, 0);
                MPI.COMM_WORLD.Recv(MK, 0, MK.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MM, 0, MM.length, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MOh, 0, H, MPI.OBJECT, 0, 0);
                MPI.COMM_WORLD.Recv(MRh, 0, H, MPI.OBJECT, 0, 0);
                for (int i = 0; i < H; i++) {
                    for(int j = 0;j<N;j++){
                        MAh[i][j] = (d[0]*MOh[i][j])+(e[0]*(Operations.multiplyMatr(Operations.multiplyMatr(MRh, MM), MK))[i][j]);
                    }
                }
                System.out.println(Arrays.deepToString(MAh));
                break;
        }

        MPI.Finalize();
    }

}
