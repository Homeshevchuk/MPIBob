package com.company;

import java.util.Arrays;

/**
 * Created by Bogdan on 6/14/2016.
 */
public class Operations {
  static  int[][] createArr(int N, int M){
        int[][] arr = new int[N][M];
        for (int[] row:arr) {
            Arrays.fill(row,1);
        }
        return arr;
  }
    static int[][] copyMult(int[][] from, int offset,int count){
        int[][] arr = new int[count][Config.N];
        for(int i = 0;i<count;i++){
            arr[i]=from[offset+count].clone();
        }
        return arr;
    }
    static int[][] multiplyMatr(int[][] A, int[][] B){
        int[][] Result = new int[A.length][B.length];
        for(int i = 0;i<A.length;i++){
            for(int j = 0;j<B.length;j++){
                for(int k = 0;k<B.length;k++){
                    Result[i][j] +=  A[i][k]*B[k][j];
                }
            }
        }
        return Result;
    }
    static int[][] calculateMAh(int[] d, int[][]MOh,int[] e, int[][] MRh, int[][]MM, int[][] MK){
        int[][] MAh = new int[Config.H][Config.N];
        for (int i = 0; i < Config.H; i++) {
            for(int j = 0;j<Config.N;j++){
                MAh[i][j] = (d[0]*MOh[i][j])+(e[0]*(Operations.multiplyMatr(Operations.multiplyMatr(MRh, MM), MK))[i][j]);
            }
        }
        return MAh;
    }
    static void printArr(int[][] arr){
        for(int i = 0;i<arr.length;i++){
            for(int j = 0;j<arr[i].length;j++){
                System.out.print(arr[i][j]+" ");
            }
            System.out.println();
        }
    }
}
