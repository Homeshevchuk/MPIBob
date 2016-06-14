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
}
