package com.example.myapplication;

/**
 * Created by 성규 on 2017-05-25.
 */
// 화면 비율 설정
public class ScreenConfig {

    public static int ScreenWidth; // 실제 좌표
    public static int ScreenHeight;
    public static int VirtualWidth; // 가상 좌표
    public static int VirtualHeight;

    public void setScreenSize(int width, int height){
        ScreenWidth = width;
        ScreenHeight = height;
    }
    // 가상좌표 설정
    public void setVirtualSize(int width, int height){
        VirtualWidth = width;
        VirtualHeight = height;
    }

    public void coodinates(float x, float y){

    }

    public static int getScreenWidth(){
        return ScreenWidth;
    }

    public static int getScreenHeight(){
        return ScreenHeight;
    }
    //가상좌표를 넣어 현재좌표를 반환함
    public static int getX(int x){
        return (int)(x * ScreenWidth/VirtualWidth);
    }
    public static int getY(int y){  return (int)(y * ScreenHeight/VirtualHeight);}

    //현재좌표를 넣어 가상좌표를 반환함
    public static int getVX(float x){
        return (int)(x * VirtualWidth/ScreenWidth);
    }
    public static int getVY(float y){
        return (int)(y * VirtualWidth/ScreenWidth);
    }
}
